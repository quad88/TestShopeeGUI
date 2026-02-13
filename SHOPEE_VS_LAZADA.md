# Shopee vs Lazada Integration Comparison

## Side-by-Side Comparison

### 📊 Authentication Flow

| Aspect | Shopee | Lazada |
|--------|--------|--------|
| **Auth URL Domain** | `https://partner.test-stable.shopee.sg` | `https://auth.lazada.com` |
| **Auth Method** | OAuth 2.0 | OAuth 2.0 |
| **Code Validity** | 10 minutes | 10 minutes |
| **Access Token Validity** | Varies | ~7 days (604,800 sec) |
| **Refresh Token Validity** | Varies | ~60 days |
| **Partner ID** | Numeric (e.g., 1217827) | App Key (string) |
| **Partner Key** | String (shpk...) | App Secret (string) |

---

### 🔐 Request Signing

| Aspect | Shopee | Lazada |
|--------|--------|--------|
| **Algorithm** | HMAC-SHA256 | HMAC-SHA256 |
| **Parameters** | partner_id, timestamp, path | app_key, timestamp, path, params |
| **String to Sign** | Path + timestamp + partner_id | Path + sorted params (key+value) |
| **Output Format** | Lowercase hex | Uppercase hex |
| **Sign Parameter** | `sign` | `sign` |

---

### 🌍 Regional Endpoints

**Shopee**
```
Sandbox: https://openplatform.sandbox.test-stable.shopee.sg
Production varies by region
```

**Lazada**
```
Singapore:   https://api.lazada.sg/rest
Thailand:    https://api.lazada.co.th/rest
Malaysia:    https://api.lazada.com.my/rest
Vietnam:     https://api.lazada.vn/rest
Philippines: https://api.lazada.com.ph/rest
Indonesia:   https://api.lazada.co.id/rest
```

---

### 📁 File Structure Mapping

| Shopee | Lazada | Purpose |
|--------|--------|---------|
| ShopeeConfig.java | LazadaConfig.java | API configuration |
| ShopeeSignature.java | LazadaSignature.java | Request signing |
| ShopeeHttpClient.java | LazadaHttpClient.java | HTTP requests |
| ShopeeAuth.java | LazadaAuth.java | Authentication |
| ShopeeOrderAPI.java | LazadaOrderAPI.java | Order operations |
| ShopeeTokenStorage.java | LazadaTokenStorage.java | Token persistence |

---

### 🔄 API Request Format

**Shopee Example:**
```
GET https://partner.shopee.sg/api/v2/order/get_order_list
    ?partner_id=1217827
    &timestamp=1676275200
    &sign=abc123...
    &access_token=xyz789...
    &shop_id=226457519
```

**Lazada Example:**
```
GET https://api.lazada.sg/rest/orders/get
    ?app_key=123456
    &timestamp=1676275200000
    &sign_method=sha256
    &access_token=xyz789...
    &sign=ABC123...
```

---

### 📝 Key Differences

#### Configuration Parameters

**Shopee:**
- `PARTNER_ID` (long)
- `PARTNER_KEY` (string)
- `SHOP_ID` (long)

**Lazada:**
- `APP_KEY` (string)
- `APP_SECRET` (string)
- No shop_id concept (seller level)

#### Timestamp Format

**Shopee:**
- Seconds (10 digits)
- Example: `1676275200`

**Lazada:**
- Milliseconds (13 digits)
- Example: `1676275200000`

#### Signature Generation

**Shopee:**
```java
String baseString = path + timestamp + access_token + shop_id;
String sign = HMAC_SHA256(partner_id + path + timestamp, partner_key);
```

**Lazada:**
```java
String baseString = path + sorted_params_concatenated;
String sign = HMAC_SHA256(baseString, app_secret);
```

---

### 🎨 Implementation Similarities

Both implementations share:

✅ OAuth 2.0 authorization flow
✅ Token storage to local file
✅ HMAC-SHA256 signing
✅ Automatic token expiry detection
✅ Refresh token mechanism
✅ Order API integration
✅ HTTP client wrapper
✅ Configuration management

---

### 📋 Setup Steps Comparison

| Step | Shopee | Lazada |
|------|--------|--------|
| **1. Register** | Shopee Partner Portal | Lazada Open Platform |
| **2. Get Credentials** | Partner ID, Partner Key | App Key, App Secret |
| **3. Configure** | Update ShopeeConfig | Update LazadaConfig |
| **4. Auth Flow** | Generate URL → Authorize → Token | Generate URL → Authorize → Token |
| **5. API Calls** | Use access token + shop_id | Use access token |

---

### 🔧 Code Usage Comparison

#### Generating Authorization URL

**Shopee:**
```java
String authUrl = ShopeeAuth.generateAuthUrl(redirectUrl);
// User authorizes, receives code + shop_id
```

**Lazada:**
```java
String authUrl = LazadaAuth.generateAuthUrl(redirectUrl);
// User authorizes, receives code
```

#### Getting Access Token

**Shopee:**
```java
String response = ShopeeAuth.getAccessToken(shopId, code);
// Parse JSON to get access_token, refresh_token
```

**Lazada:**
```java
String response = LazadaAuth.createAccessToken(code);
// Parse JSON to get access_token, refresh_token
```

#### Fetching Orders

**Shopee:**
```java
String orders = ShopeeOrderAPI.getOrderList(
    shopId, 
    accessToken,
    timeFrom,
    timeTo,
    pageSize
);
```

**Lazada:**
```java
String orders = LazadaOrderAPI.getOrders(
    accessToken,
    createdAfter,
    createdBefore,
    status,
    offset,
    limit
);
```

---

### 🎯 When to Use Each

**Use Shopee when:**
- Selling primarily in Southeast Asia
- Need shop-level granular control
- Working with Singapore/Malaysia/Thailand markets

**Use Lazada when:**
- Selling across SE Asia with Alibaba backing
- Need seller-level operations
- Want to tap into Lazada's customer base

**Use Both when:**
- Want maximum market coverage
- Multi-platform selling strategy
- Diversifying sales channels

---

### 💾 Token Storage

Both use similar file-based storage:

**Shopee:**
```
~/.testshopgui/shopee_tokens.properties
```

**Lazada:**
```
~/.testshopgui/lazada_tokens.properties
```

---

### ⚡ Quick Migration Guide

If you understand Shopee integration, here's how to think about Lazada:

1. **partner_id** → **app_key**
2. **partner_key** → **app_secret**
3. **shop_id** → *not needed (seller-level)*
4. **Timestamp in seconds** → **milliseconds**
5. **Lowercase hex signature** → **Uppercase hex signature**
6. **Different API paths** → Check LazadaConfig for endpoints

---

### 📚 Documentation Links

**Shopee:**
- https://open.shopee.com/documents

**Lazada:**
- https://open.lazada.com/doc/doc.htm

---

## 🎓 Learning Path

If you already know Shopee integration:

1. ✅ You understand OAuth 2.0 flow
2. ✅ You know HMAC-SHA256 signing
3. ✅ You're familiar with token management
4. ⚠️ Learn Lazada's parameter sorting
5. ⚠️ Adjust to uppercase signatures
6. ⚠️ Use millisecond timestamps
7. ⚠️ Remove shop_id dependency

You're 80% there! The concepts are the same, just implementation details differ.

---

## 🚀 Integration Benefits

Having both Shopee and Lazada in your app gives you:

✨ **Dual Market Coverage** - Reach more customers
✨ **Risk Diversification** - Don't rely on single platform
✨ **Unified Interface** - Manage both from one app
✨ **Code Reusability** - Similar patterns across both
✨ **Competitive Advantage** - Multi-platform selling

---

## 🎉 Summary

Your project now supports both major Southeast Asian e-commerce platforms! The implementations are parallel, making it easy to:

- Switch between platforms
- Manage multiple shops
- Reuse UI components
- Maintain consistent code structure

Both integrations are production-ready once you configure your credentials!
