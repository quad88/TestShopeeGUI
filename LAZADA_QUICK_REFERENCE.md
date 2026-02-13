# Lazada Integration - Quick Reference

## Setup Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                   STEP 1: REGISTER APPLICATION                  │
├─────────────────────────────────────────────────────────────────┤
│  1. Visit https://open.lazada.com/                             │
│  2. Create developer account                                    │
│  3. Create new app → Get App Key & App Secret                  │
│  4. Configure redirect URL in app settings                      │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                   STEP 2: UPDATE CONFIGURATION                  │
├─────────────────────────────────────────────────────────────────┤
│  File: LazadaConfig.java                                        │
│  • Replace APP_KEY with your actual app key                     │
│  • Replace APP_SECRET with your actual app secret               │
│  • Set API_GATEWAY to your region's endpoint                    │
│  • Set REDIRECT_URL to match app settings                       │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                   STEP 3: AUTHORIZATION FLOW                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  A. Generate Auth URL                                           │
│     LazadaAuth.generateAuthUrl(redirectUrl)                     │
│           ↓                                                      │
│     Returns: https://auth.lazada.com/oauth/authorize?...        │
│                                                                 │
│  B. Seller Authorizes                                           │
│     • Open URL in browser                                       │
│     • Seller logs in and approves                               │
│           ↓                                                      │
│     Redirect: https://your-url.com/callback?code=ABC123         │
│                                                                 │
│  C. Exchange Code for Token                                     │
│     LazadaAuth.createAccessToken(code)                          │
│           ↓                                                      │
│     Returns JSON with:                                          │
│       • access_token                                            │
│       • refresh_token                                           │
│       • expires_in                                              │
│       • refresh_expires_in                                      │
│                                                                 │
│  D. Save Tokens                                                 │
│     LazadaTokenStorage.saveToken(tokenData)                     │
│           ↓                                                      │
│     Saved to: ~/.testshopgui/lazada_tokens.properties           │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                   STEP 4: MAKE API CALLS                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  A. Load Token                                                  │
│     tokenData = LazadaTokenStorage.loadToken()                  │
│                                                                 │
│  B. Check Expiry & Refresh if Needed                            │
│     if (tokenData.isAccessTokenExpired()) {                     │
│         if (!tokenData.isRefreshTokenExpired()) {               │
│             LazadaAuth.refreshAccessToken(refresh_token)        │
│         } else {                                                │
│             // Re-authorize (back to Step 3)                    │
│         }                                                       │
│     }                                                           │
│                                                                 │
│  C. Call API                                                    │
│     LazadaOrderAPI.getOrders(access_token)                      │
│           ↓                                                      │
│     Returns JSON with order data                                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## API Request Signature Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    REQUEST SIGNING PROCESS                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. Prepare Parameters                                          │
│     params = {                                                  │
│       "app_key": "123456",                                      │
│       "timestamp": "1676275200000",                             │
│       "access_token": "abc...",                                 │
│       ...other params...                                        │
│     }                                                           │
│                                                                 │
│  2. Sort Parameters Alphabetically                              │
│     sorted_params = TreeMap(params)                             │
│                                                                 │
│  3. Build String to Sign                                        │
│     string = API_PATH                                           │
│     for each (key, value) in sorted_params:                     │
│         string += key + value                                   │
│                                                                 │
│     Example:                                                    │
│     /orders/getaccess_tokenabc...app_key123456timestamp...      │
│                                                                 │
│  4. Generate HMAC-SHA256 Signature                              │
│     signature = HMAC_SHA256(string, APP_SECRET)                 │
│     signature = ToUpperHex(signature)                           │
│                                                                 │
│  5. Build Final URL                                             │
│     url = API_GATEWAY + API_PATH + "?"                          │
│     url += all_params + "&sign=" + signature                    │
│                                                                 │
│  6. Make HTTP Request                                           │
│     GET or POST to signed URL                                   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Token Lifecycle

```
┌─────────────────────────────────────────────────────────────────┐
│                        TOKEN LIFECYCLE                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Authorization Code (10 minutes validity)                       │
│  └─> Exchange for Access Token                                  │
│                                                                 │
│  Access Token (~7 days / 604,800 seconds)                       │
│  ├─> Use for all API calls                                      │
│  ├─> Check expiry before each use                               │
│  └─> Refresh when expired (if refresh token valid)              │
│                                                                 │
│  Refresh Token (~60 days)                                       │
│  ├─> Used to get new access token                               │
│  └─> When this expires → re-authorize from step 1               │
│                                                                 │
│  Timeline:                                                      │
│  ┌─────────┬──────────────────────────┬────────────────────────┐│
│  │ Day 0   │ Day 7 (access expires)   │ Day 60 (refresh exp)   ││
│  │ Authorize│ Refresh token           │ Re-authorize needed    ││
│  └─────────┴──────────────────────────┴────────────────────────┘│
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Class Structure

```
┌──────────────────────┐
│   LazadaConfig       │  Configuration constants
│                      │  • APP_KEY, APP_SECRET
│                      │  • API endpoints
└──────────────────────┘

┌──────────────────────┐
│  LazadaSignature     │  Request signing
│                      │  • HMAC-SHA256 generation
│                      │  • URL building
└──────────────────────┘

┌──────────────────────┐
│  LazadaHttpClient    │  HTTP communication
│                      │  • GET requests
│                      │  • POST requests
└──────────────────────┘

┌──────────────────────┐
│    LazadaAuth        │  Authentication
│                      │  • Generate auth URL
│                      │  • Create access token
│                      │  • Refresh token
└──────────────────────┘

┌──────────────────────┐
│  LazadaOrderAPI      │  Order operations
│                      │  • Get orders
│                      │  • Get order items
└──────────────────────┘

┌──────────────────────┐
│ LazadaTokenStorage   │  Token persistence
│                      │  • Save tokens
│                      │  • Load tokens
│                      │  • Check expiry
└──────────────────────┘
```

---

## Region Endpoints

| Region      | API Gateway URL                    |
|-------------|-------------------------------------|
| Singapore   | https://api.lazada.sg/rest         |
| Thailand    | https://api.lazada.co.th/rest      |
| Malaysia    | https://api.lazada.com.my/rest     |
| Vietnam     | https://api.lazada.vn/rest         |
| Philippines | https://api.lazada.com.ph/rest     |
| Indonesia   | https://api.lazada.co.id/rest      |

---

## Common API Endpoints

| Endpoint                | Purpose                          |
|-------------------------|----------------------------------|
| /auth/token/create      | Exchange code for access token   |
| /auth/token/refresh     | Refresh expired access token     |
| /orders/get             | Get order list                   |
| /order/items/get        | Get items for specific orders    |
| /product/get            | Get product details              |
| /products/get           | Get product list                 |

---

## Next Steps Checklist

- [ ] Register app on Lazada Open Platform
- [ ] Get App Key and App Secret
- [ ] Update LazadaConfig.java with credentials
- [ ] Choose and set correct API_GATEWAY for region
- [ ] Configure redirect URL in app settings
- [ ] Test authorization flow in sandbox (if available)
- [ ] Implement GUI integration
- [ ] Add JSON parsing library (Gson/Jackson)
- [ ] Implement automatic token refresh
- [ ] Add error handling and retry logic
- [ ] Test in production environment

---

## Important Security Notes

⚠️ **NEVER commit APP_SECRET to Git repository**
⚠️ **Store credentials securely (environment variables recommended)**
⚠️ **Token storage location: ~/.testshopgui/lazada_tokens.properties**
⚠️ **Always use HTTPS for API calls**
⚠️ **Implement proper error handling for token expiry**

---

For detailed documentation, see: **LAZADA_SETUP_GUIDE.md**
