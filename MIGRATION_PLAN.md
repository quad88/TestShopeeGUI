# Migration Plan: TestShopGUI POC → SmartPick Spring Boot Production

> **Date:** May 8, 2026  
> **Source Project:** `TestShopGUI` (JavaFX/Swing POC)  
> **Target Project:** `smartpick-api` (Spring Boot 3.x production backend)  
> **Target Package:** `com.axonstech.smartpick`

---

## Table of Contents

1. [Overview](#overview)
2. [Keep vs. Discard](#1-keep-vs-discard)
3. [Target Package Structure](#2-target-package-structure)
4. [Production Improvements](#3-production-improvements)
5. [Database Schema](#4-database-schema)
6. [Maven Dependencies](#5-maven-dependencies)
7. [Migration Phases](#6-migration-phases)
8. [Key Risks & Mitigations](#7-key-risks--mitigations)

---

## Overview

Strip the JavaFX GUI shell, extract the proven Shopee/Lazada signing, auth, and order logic,
and wire it into a clean, production-grade Spring Boot backend.

All static state, raw HTTP clients, string-based JSON hacks, in-memory/file token storage,
and JavaFX coupling will be replaced. GUI classes are discarded entirely.

---

## 1. Keep vs. Discard

| Category | ✅ Keep → Refactor | ❌ Discard Entirely |
|---|---|---|
| **Shopee** | `ShopeeSignature`, `ShopeeAuth`, `ShopeeOrderAPI`, `ShopeeCallbackHandler` (logic only) | `ShopeeConfig`, `RuntimeConfig`, `ShopeeTokenStorage` (in-memory), `ShopeeHttpClient`, `ShopeeBackendOnlyService` |
| **Lazada** | `LazadaSignature`, `LazadaAuth`, `LazadaOrderAPI` | `LazadaConfig`, `LazadaTokenStorage` (file-based/single-user), `LazadaHttpClient` |
| **Shared** | `ApiRequestLogger.ApiRequest` (data class shape only) | `ApiRequestLogger` itself (has JavaFX `Platform.runLater` coupling) |
| **GUI** | — | **Everything:** `*Panel`, `*Gui`, `*App`, `HelloApplication`, `HelloController`, `Launcher`, `InspectPanel*` |

---

## 2. Target Package Structure

```
com.axonstech.smartpick
├── config/
│   ├── ShopeeProperties           (@ConfigurationProperties("shopee"))
│   ├── LazadaProperties           (@ConfigurationProperties("lazada"))
│   └── MarketplaceClientConfig    (RestTemplate @Bean — timeouts, logging interceptor)
│
├── marketplace/
│   ├── common/
│   │   ├── MarketplaceAuthService  (interface: generateAuthUrl, exchangeCode, refreshToken)
│   │   └── MarketplaceOrderService (interface: getOrders, getOrderItems)
│   ├── shopee/
│   │   ├── ShopeeSignatureService  (@Component — partner & shop HMAC-SHA256)
│   │   ├── ShopeeAuthService       (@Service implements MarketplaceAuthService)
│   │   └── ShopeeOrderService      (@Service implements MarketplaceOrderService)
│   └── lazada/
│       ├── LazadaSignatureService  (@Component — sorted-param HMAC-SHA256)
│       ├── LazadaAuthService       (@Service implements MarketplaceAuthService)
│       └── LazadaOrderService      (@Service implements MarketplaceOrderService)
│
├── dto/
│   ├── shopee/   (ShopeeTokenResponse, ShopeeOrderListResponse — Jackson POJOs)
│   └── lazada/   (LazadaTokenResponse, LazadaOrderListResponse — Jackson POJOs)
│
├── token/
│   ├── MarketplaceToken           (JPA @Entity)
│   ├── MarketplaceTokenRepository (JpaRepository)
│   └── TokenService               (@Service — save/load/auto-refresh, all platforms & sellers)
│
├── web/
│   ├── ShopeeAuthController       (@RestController — /shopee/auth, /shopee/callback)
│   ├── LazadaAuthController       (@RestController — /lazada/auth, /lazada/callback)
│   └── OrderController            (@RestController — /orders/{platform}/{shopId})
│
├── scheduler/
│   └── TokenRefreshScheduler      (@Scheduled — proactively refresh near-expiry tokens)
│
└── exception/
    ├── MarketplaceApiException    (custom RuntimeException)
    └── GlobalExceptionHandler     (@ControllerAdvice — structured JSON errors)
```

---

## 3. Production Improvements

### 3.1 Configuration: Hardcoded Credentials → `application.yml`

**Problem:** `ShopeeConfig` and `LazadaConfig` have hardcoded partner keys, secrets, and URLs committed to source code.

**Fix:** Externalize all values into environment-specific YAML files bound via `@ConfigurationProperties`.

```yaml
# application-sit.yml
shopee:
  partner-id: 1217827
  partner-key: ${SHOPEE_PARTNER_KEY}          # injected from env/Vault — never in Git
  api-host: https://openplatform.sandbox.test-stable.shopee.sg
  redirect-url: https://smartpick-sit.axonstech.com/
  callback-url: https://smartpickapi-sit.axonstech.com/shopee/callback
  order:
    default-page-size: 20
    default-logistics-channel-id: 71001        # was hardcoded in ShopeeOrderAPI
    default-order-status: READY_TO_SHIP
    time-range-days: 15

lazada:
  app-key: ${LAZADA_APP_KEY}
  app-secret: ${LAZADA_APP_SECRET}
  api-gateway: https://api.lazada.co.th/rest
  redirect-url: https://smartpick-sit.axonstech.com/lazada/callback
  token-expiry-buffer-seconds: 300
  gateways:                                    # multi-region support
    TH: https://api.lazada.co.th/rest
    SG: https://api.lazada.sg/rest
    MY: https://api.lazada.com.my/rest
    PH: https://api.lazada.com.ph/rest
    VN: https://api.lazada.vn/rest
    ID: https://api.lazada.co.id/rest
```

```java
// ShopeeProperties.java
@Configuration
@ConfigurationProperties(prefix = "shopee")
@Data  // Lombok
public class ShopeeProperties {
    private long partnerId;
    private String partnerKey;
    private String apiHost;
    private String redirectUrl;
    private String callbackUrl;
    private OrderDefaults order = new OrderDefaults();

    @Data
    public static class OrderDefaults {
        private int defaultPageSize = 20;
        private long defaultLogisticsChannelId;
        private String defaultOrderStatus;
        private int timeRangeDays = 15;
    }
}
```

---

### 3.2 HTTP Client: Raw `HttpURLConnection` → `RestTemplate`

**Problem:** Two near-identical raw HTTP client classes (`ShopeeHttpClient`, `LazadaHttpClient`) with no connection pooling, no retry, and duplicated boilerplate.

**Fix:** Single shared `RestTemplate` bean with:
- Configured connect/read timeouts
- `ClientHttpRequestInterceptor` for structured request/response logging (replaces `ApiRequestLogger`)
- `@Retryable` (spring-retry) on API call methods for transient failures
- Optional: Resilience4j circuit breaker around marketplace calls

```java
// MarketplaceClientConfig.java
@Configuration
public class MarketplaceClientConfig {

    @Bean
    public RestTemplate marketplaceRestTemplate() {
        HttpComponentsClientHttpRequestFactory factory =
            new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(30_000);
        factory.setReadTimeout(30_000);

        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(List.of(new MarketplaceLoggingInterceptor()));
        return restTemplate;
    }
}
```

---

### 3.3 JSON Parsing: `String.indexOf()` hacks → Jackson `ObjectMapper`

**Problem:** `extractJsonValue()` is duplicated in 4 classes (`ShopeeTokenStorage`, `ShopeeBackendOnlyService`, `ShopeeCallbackHandler`, `LazadaTokenStorage`) and is fragile — breaks on nested JSON, escaped characters, and numeric fields.

**Fix:** Define Jackson DTO records per API response:

```java
// ShopeeTokenResponse.java
@JsonIgnoreProperties(ignoreUnknown = true)
public record ShopeeTokenResponse(
    @JsonProperty("access_token")  String accessToken,
    @JsonProperty("refresh_token") String refreshToken,
    @JsonProperty("expire_in")     long expireIn,
    String error,
    String message
) {
    public boolean isSuccess() {
        return accessToken != null && !accessToken.isBlank()
            && (error == null || error.isBlank());
    }
}

// ShopeeOrderListResponse.java
@JsonIgnoreProperties(ignoreUnknown = true)
public record ShopeeOrderListResponse(
    @JsonProperty("response") OrderListData response,
    String error,
    String message
) {}
```

Use: `objectMapper.readValue(rawJson, ShopeeTokenResponse.class)` — no more string surgery.

---

### 3.4 Token Storage: In-memory / File → JPA Database

**Problem:**
- Shopee tokens stored in a `HashMap` — lost on every restart
- Lazada tokens stored in a single `.properties` file — only supports one seller
- Neither works in a multi-instance (horizontally scaled) deployment

**Fix:** `marketplace_tokens` table backed by `MarketplaceTokenRepository` (JPA).
`TokenService` provides:

```java
@Service
@RequiredArgsConstructor
public class TokenService {
    private final MarketplaceTokenRepository repo;
    private final ObjectMapper objectMapper;

    // Returns a valid (non-expired) access token, auto-refreshing if needed.
    // Uses SELECT FOR UPDATE to prevent concurrent refresh race conditions.
    @Transactional
    public String getValidToken(String platform, String sellerId) { ... }

    public void saveTokens(String platform, String sellerId, String shopId,
                           String accessToken, String refreshToken,
                           long accessExpiresIn, String region) { ... }
}
```

> ⚠️ **Race Condition Risk:** When multiple threads request an expired token for the same seller simultaneously, all may trigger a concurrent refresh. Use `SELECT FOR UPDATE` (JPA `@Lock(LockModeType.PESSIMISTIC_WRITE)`) or a Redis distributed lock inside `getValidToken()`.

---

### 3.5 Logging: `System.out.println` → SLF4J + MDC

**Problem:** Verbose `System.out.println` and `System.err.println` spread across every class. No structured log format, no correlation IDs, secrets printed to console (access tokens, partner keys).

**Fix:**

```java
// Replace every println with:
private static final Logger log = LoggerFactory.getLogger(ShopeeAuthService.class);

// Add per-request context via MDC:
MDC.put("platform", "SHOPEE");
MDC.put("shopId", String.valueOf(shopId));
MDC.put("requestId", requestId);
log.info("Fetching order list");
log.debug("Using access token ending in: ...{}", token.substring(token.length() - 6));
// Never log full tokens at INFO level
```

---

### 3.6 Lazada Multi-Region Support

**Problem:** `LazadaConfig` hardcodes `API_GATEWAY = https://api.lazada.co.th/rest`. Onboarding sellers from SG, MY, PH will silently use the wrong regional endpoint.

**Fix:** Store `region` per seller in `marketplace_tokens`. `LazadaProperties` exposes a `Map<String, String> gateways`. `LazadaOrderService` resolves the correct gateway from the stored region at call time.

---

## 4. Database Schema

```sql
-- flyway/migrations/V1__create_marketplace_tokens.sql

CREATE TABLE marketplace_tokens (
    id                        BIGSERIAL     PRIMARY KEY,
    platform                  VARCHAR(20)   NOT NULL,        -- 'SHOPEE' | 'LAZADA'
    seller_id                 VARCHAR(100)  NOT NULL,        -- shop_id (Shopee) or seller_id (Lazada) as string
    shop_id                   BIGINT,                        -- Shopee-specific; nullable for Lazada
    access_token              TEXT          NOT NULL,        -- store encrypted (AES/KMS)
    refresh_token             TEXT          NOT NULL,        -- store encrypted (AES/KMS)
    access_token_expires_at   TIMESTAMP     NOT NULL,        -- absolute expiry instant (UTC)
    refresh_token_expires_at  TIMESTAMP,                     -- for refresh token lifecycle tracking
    region                    VARCHAR(10),                   -- 'TH', 'SG', 'MY', etc. (Lazada multi-region)
    created_at                TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at                TIMESTAMP     NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_platform_seller UNIQUE (platform, seller_id)
);

-- Index for TokenRefreshScheduler: find soon-to-expire tokens efficiently
CREATE INDEX idx_token_expiry ON marketplace_tokens (platform, access_token_expires_at);
```

### JPA Entity

```java
// MarketplaceToken.java
@Entity
@Table(name = "marketplace_tokens")
@Data
@NoArgsConstructor
public class MarketplaceToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String platform;                // "SHOPEE" | "LAZADA"

    @Column(nullable = false, length = 100)
    private String sellerId;

    private Long shopId;                    // Shopee only

    @Column(nullable = false, columnDefinition = "TEXT")
    private String accessToken;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String refreshToken;

    @Column(nullable = false)
    private Instant accessTokenExpiresAt;

    private Instant refreshTokenExpiresAt;

    @Column(length = 10)
    private String region;                  // Lazada multi-region

    @CreationTimestamp private Instant createdAt;
    @UpdateTimestamp  private Instant updatedAt;
}
```

---

## 5. Maven Dependencies

Add the following to the real project's `pom.xml` (Spring Boot 3.x parent):

```xml
<!-- ── Core Web & REST ─────────────────────────────────────── -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- ── Database / JPA ──────────────────────────────────────── -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>   <!-- or mysql-connector-j -->
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>

<!-- ── Validation ──────────────────────────────────────────── -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- ── Observability ───────────────────────────────────────── -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- ── Boilerplate Reduction ───────────────────────────────── -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>

<!-- ── Retry & Resilience ──────────────────────────────────── -->
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aspects</artifactId>  <!-- required by spring-retry -->
</dependency>
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
    <version>2.2.0</version>
</dependency>

<!-- ── API Documentation ───────────────────────────────────── -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>

<!-- ── Testing ─────────────────────────────────────────────── -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.wiremock.integrations</groupId>
    <artifactId>wiremock-spring-boot</artifactId>
    <version>3.1.0</version>
    <scope>test</scope>
</dependency>
```

---

## 6. Migration Phases

### Phase 1 — Project Setup
**Goal:** New Spring Boot project skeleton is runnable with proper config binding.

- [ ] Create `smartpick-api` Maven project with `spring-boot-starter-parent 3.x`, Java 17
- [ ] Add all dependencies from Section 5
- [ ] Create `application.yml` with SIT/UAT/PROD profiles
- [ ] Move all credentials to environment variable placeholders (`${SHOPEE_PARTNER_KEY}`, etc.)
- [ ] Create `ShopeeProperties` and `LazadaProperties` `@ConfigurationProperties` classes
- [ ] Verify app starts and properties bind correctly

---

### Phase 2 — Core Logic Extraction
**Goal:** Signature and auth services work and are unit-tested.

> ⚡ Start here — signature correctness is the foundation for everything else.

- [ ] Port `ShopeeSignature` → `ShopeeSignatureService` (`@Component`, injects `ShopeeProperties`, removes all `RuntimeConfig` / `ShopeeConfig` static references)
- [ ] Port `LazadaSignature` → `LazadaSignatureService` (`@Component`, injects `LazadaProperties`)
- [ ] Define Jackson DTOs: `ShopeeTokenResponse`, `LazadaTokenResponse`, `ShopeeOrderListResponse`, `LazadaOrderListResponse`
- [ ] Port `ShopeeAuth` → `ShopeeAuthService` (`@Service`, injects `RestTemplate`, `ShopeeSignatureService`)
- [ ] Port `LazadaAuth` → `LazadaAuthService` (`@Service`, injects `RestTemplate`, `LazadaSignatureService`)
- [ ] **Write unit tests for both signature services** with known-good input/output pairs from official API docs
- [ ] Replace all `System.out.println` with SLF4J

---

### Phase 3 — Token DB Integration
**Goal:** Tokens persist across restarts, support multiple sellers, auto-refresh works safely.

- [ ] Create Flyway migration `V1__create_marketplace_tokens.sql`
- [ ] Create `MarketplaceToken` JPA entity
- [ ] Create `MarketplaceTokenRepository` (JPA)
- [ ] Implement `TokenService.saveTokens()` and `TokenService.getValidToken()` with `@Transactional` + `@Lock(PESSIMISTIC_WRITE)` on the refresh path
- [ ] Wire `TokenService` into `ShopeeAuthService` and `LazadaAuthService`
- [ ] Write repository tests (H2 in-memory)
- [ ] Write concurrency test for the token refresh race condition

---

### Phase 4 — REST Controllers & Callback Endpoints
**Goal:** Full OAuth flow is reachable via HTTP; orders can be fetched via API.

- [ ] Create `ShopeeAuthController`:
  - `GET /shopee/auth` → generates and returns Shopee auth URL
  - `GET /shopee/callback?code={code}&shop_id={shopId}` → exchanges code, saves tokens, redirects to frontend
- [ ] Create `LazadaAuthController`:
  - `GET /lazada/auth` → generates and returns Lazada auth URL
  - `GET /lazada/callback?code={code}` → exchanges code, saves tokens, redirects to frontend
- [ ] Port `ShopeeOrderAPI` → `ShopeeOrderService` (inject `RestTemplate`, `ShopeeSignatureService`, `TokenService`, `ShopeeProperties` — make `logistics_channel_id`, `order_status`, and time range configurable)
- [ ] Port `LazadaOrderAPI` → `LazadaOrderService`
- [ ] Create `OrderController`:
  - `GET /orders/shopee/{shopId}`
  - `GET /orders/lazada/{sellerId}`
- [ ] Create `GlobalExceptionHandler` (`@ControllerAdvice`) returning `{"error": "...", "message": "..."}` JSON
- [ ] Add Swagger UI (`springdoc-openapi`)
- [ ] Integration test the callback → token-save → order-fetch flow end-to-end

---

### Phase 5 — Hardening & Production Readiness
**Goal:** The service is stable, observable, secure, and survives transient failures.

- [ ] Add `@Retryable(maxAttempts=3, backoff=@Backoff(delay=1000, multiplier=2))` on all marketplace HTTP calls
- [ ] Add Resilience4j circuit breaker around Shopee/Lazada calls
- [ ] Implement `TokenRefreshScheduler`: `@Scheduled(fixedDelay=60_000)` — query tokens expiring within next 10 minutes and refresh proactively
- [ ] Encrypt access/refresh tokens at rest (column-level AES or DB TDE)
- [ ] Add secrets management (Vault / AWS Secrets Manager / Spring Cloud Config)
- [ ] Integration tests with WireMock mocking Shopee/Lazada API responses
- [ ] Load/smoke test in SIT environment
- [ ] Add `@PreAuthorize` / API key guard on order and auth endpoints

---

## 7. Key Risks & Mitigations

| # | Risk | Impact | Mitigation |
|---|---|---|---|
| 1 | **Signature algorithm regression** | All API calls silently return 401/invalid_sign | ✅ Unit test `ShopeeSignatureService` and `LazadaSignatureService` with known-good inputs from official docs **before** wiring to any other class |
| 2 | **Credential leak** | Security incident / API key abuse | ✅ Never commit secrets; use `${ENV_VAR}` in YAML; add `.gitignore` + `git-secrets` pre-commit hook; scan with TruffleHog in CI |
| 3 | **Token refresh race condition** | Double-refresh / rate-limit hit / data corruption | ✅ `SELECT FOR UPDATE` (`@Lock(PESSIMISTIC_WRITE)`) or Redis `SETNX` distributed lock inside `TokenService.getValidToken()` |
| 4 | **Lazada multi-region breakage** | Orders fetched from wrong country endpoint | ✅ Store `region` per token record from Day 1; use `LazadaProperties.gateways` lookup map |
| 5 | **`logistics_channel_id=71001` hardcode** | Wrong orders returned (or missing orders) after migration | ✅ Move to `application.yml` as `shopee.order.default-logistics-channel-id`; document what this ID means in README |
| 6 | **Business data gap during cutover** | Missed orders between old and new system | ✅ Run old POC and new service in parallel for one business day; compare order counts before switching traffic |
| 7 | **Token loss on first deploy** | Re-auth required for all sellers | ✅ Before cutover, export existing tokens from POC's in-memory store / file into the new DB via a one-time migration script |

---

## Appendix: POC Issues Summary

| Issue | Affected Classes | Fix |
|---|---|---|
| Hardcoded credentials | `ShopeeConfig`, `LazadaConfig` | Move to `application.yml` + env vars |
| Static global state | `RuntimeConfig` | Replace with `@ConfigurationProperties` + DI |
| In-memory token storage | `ShopeeTokenStorage` | JPA `MarketplaceToken` entity + DB |
| File-based token storage (single-user) | `LazadaTokenStorage` | Same JPA entity, multi-seller support |
| String `indexOf()` JSON parsing | `ShopeeTokenStorage`, `ShopeeBackendOnlyService`, `ShopeeCallbackHandler`, `LazadaTokenStorage` | Jackson `ObjectMapper` + DTO records |
| Duplicate HTTP client code | `ShopeeHttpClient`, `LazadaHttpClient` | Single `RestTemplate` bean |
| `System.out.println` everywhere | All classes | SLF4J + Logback + MDC |
| JavaFX coupling | `ApiRequestLogger` | Replace with `ClientHttpRequestInterceptor` |
| No interfaces / abstractions | All service classes | `MarketplaceAuthService`, `MarketplaceOrderService` interfaces |
| No error handling / retry | All HTTP calls | `@Retryable`, Resilience4j circuit breaker |
| Hardcoded order filter values | `ShopeeOrderAPI` (line 90: `logistics_channel_id=71001`) | Move to `ShopeeProperties.order` config |
| No security on endpoints | `ShopeeCallbackHandler` | `@PreAuthorize` / API key guard on controllers |

