# Lazada Integration Setup and Flow

## Overview
This document explains the setup process and authentication flow for integrating Lazada API into the TestShopGUI application.

---

## 1. Prerequisites

### Register Lazada Developer Account
1. Go to [Lazada Open Platform](https://open.lazada.com/)
2. Sign up for a developer account
3. Create a new application in the developer console
4. Note down your credentials:
   - **App Key** (Client ID)
   - **App Secret** (Client Secret)
5. Configure your application settings:
   - Set **Redirect URL** (e.g., `https://smartpick-sit.axonstech.com/lazada/callback`)
   - Select required API permissions/scopes

### Choose API Region
Lazada operates in different regions with different API gateways:
- **Singapore**: `https://api.lazada.sg/rest`
- **Thailand**: `https://api.lazada.co.th/rest`
- **Malaysia**: `https://api.lazada.com.my/rest`
- **Vietnam**: `https://api.lazada.vn/rest`
- **Philippines**: `https://api.lazada.com.ph/rest`
- **Indonesia**: `https://api.lazada.co.id/rest`

---

## 2. Configuration Setup

### Update LazadaConfig.java
Replace placeholder values with your actual credentials:

```java
public static final String APP_KEY = "YOUR_ACTUAL_APP_KEY";
public static final String APP_SECRET = "YOUR_ACTUAL_APP_SECRET";
public static final String API_GATEWAY = "https://api.lazada.sg/rest"; // Choose your region
public static final String REDIRECT_URL = "https://your-callback-url.com/lazada/callback";
```

---

## 3. Authentication Flow

### Step 1: Generate Authorization URL
```
User clicks "Connect Lazada Shop" in GUI
    ↓
LazadaAuth.generateAuthUrl() creates authorization URL
    ↓
Open URL in browser (seller sees Lazada login page)
```

**What happens:**
- App generates a URL with your App Key
- Seller visits the URL and logs into their Lazada seller account
- Seller grants permissions to your app

### Step 2: Authorization Callback
```
Seller approves authorization
    ↓
Lazada redirects to your callback URL with authorization code
    ↓
URL format: https://your-callback-url.com/lazada/callback?code=AUTHORIZATION_CODE
```

**What happens:**
- Lazada sends the seller back to your redirect URL
- URL contains an authorization `code` parameter
- This code is valid for **10 minutes**

### Step 3: Exchange Code for Access Token
```
Extract code from callback URL
    ↓
LazadaAuth.createAccessToken(code) exchanges code for tokens
    ↓
Receive access_token, refresh_token, and expiry information
    ↓
LazadaTokenStorage.saveToken() stores tokens locally
```

**What happens:**
- Your app calls Lazada API with the authorization code
- Lazada returns:
  - `access_token`: Used to call API (valid for ~7 days)
  - `refresh_token`: Used to renew access token (valid for ~2 months)
  - `expires_in`: Seconds until access token expires
  - `refresh_expires_in`: Seconds until refresh token expires

### Step 4: Use Access Token for API Calls
```
Need to fetch orders?
    ↓
Check if access token is expired
    ↓
If expired: LazadaAuth.refreshAccessToken(refresh_token)
    ↓
LazadaOrderAPI.getOrders(access_token) calls API
    ↓
Receive order data in JSON format
```

**What happens:**
- Before each API call, check if token is still valid
- If token expired but refresh token valid: refresh the token
- If refresh token expired: seller must re-authorize (go back to Step 1)
- Use valid access token in all API requests

---

## 4. API Request Signing Flow

Lazada requires all API requests to be signed with HMAC-SHA256:

```
1. Prepare API parameters (app_key, timestamp, access_token, etc.)
    ↓
2. Sort parameters alphabetically by key
    ↓
3. Concatenate: API_PATH + sorted parameters (key+value pairs)
    ↓
4. Generate HMAC-SHA256 signature using APP_SECRET
    ↓
5. Convert signature to uppercase hex string
    ↓
6. Append signature to request URL as 'sign' parameter
    ↓
7. Make HTTP request
```

**Example:**
```
Path: /orders/get
Parameters: app_key=123456, timestamp=1676275200000, access_token=abc...
String to sign: /orders/getapp_key123456access_tokenabc...timestamp1676275200000
Signature: HMAC-SHA256(string_to_sign, APP_SECRET)
Final URL: https://api.lazada.sg/rest/orders/get?app_key=123456&timestamp=1676275200000&access_token=abc...&sign=SIGNATURE
```

---

## 5. Key Components Created

### Core Classes

1. **LazadaConfig.java**
   - Stores app credentials and API endpoints
   - Configure your App Key, App Secret, and API gateway here

2. **LazadaSignature.java**
   - Generates HMAC-SHA256 signatures for API requests
   - Builds signed URLs with proper parameter ordering

3. **LazadaHttpClient.java**
   - Handles HTTP GET and POST requests
   - Manages connections to Lazada API

4. **LazadaAuth.java**
   - Generates authorization URLs
   - Creates access tokens from authorization codes
   - Refreshes expired access tokens

5. **LazadaOrderAPI.java**
   - Fetches order lists with filters
   - Retrieves order items by order IDs
   - Handles pagination

6. **LazadaTokenStorage.java**
   - Saves tokens to local file system
   - Loads tokens from storage
   - Checks token expiry status

---

## 6. Usage Examples

### Connect a Lazada Shop
```java
// Generate authorization URL
String authUrl = LazadaAuth.generateAuthUrl();
System.out.println("Visit this URL to authorize: " + authUrl);

// Seller visits URL and authorizes, you receive code in callback
String code = "AUTHORIZATION_CODE_FROM_CALLBACK";

// Exchange code for tokens
String tokenResponse = LazadaAuth.createAccessToken(code);
// Parse JSON response to extract tokens and save them
```

### Fetch Orders
```java
// Load saved tokens
LazadaTokenStorage.TokenData tokenData = LazadaTokenStorage.loadToken();

// Check if token needs refresh
if (tokenData.isAccessTokenExpired()) {
    if (!tokenData.isRefreshTokenExpired()) {
        // Refresh the token
        String refreshResponse = LazadaAuth.refreshAccessToken(tokenData.refreshToken);
        // Parse and save new tokens
    } else {
        // Need to re-authorize (go back to Step 1)
        System.out.println("Refresh token expired. Please re-authorize.");
    }
}

// Fetch orders
String ordersResponse = LazadaOrderAPI.getOrders(tokenData.accessToken);
System.out.println(ordersResponse);
```

---

## 7. Development vs Production

### Sandbox Environment
- Some regions provide sandbox environments for testing
- Use sandbox credentials during development
- API responses may contain mock data

### Production Environment
- Use production credentials from Lazada Open Platform
- Test thoroughly before going live
- Monitor API rate limits and quotas

---

## 8. Important Notes

### Token Expiry
- **Access Token**: Valid for ~7 days (604,800 seconds)
- **Refresh Token**: Valid for ~60 days
- Always check expiry before making API calls
- Implement automatic token refresh logic

### Rate Limits
- Lazada enforces API rate limits per app
- Check your app's quota in developer console
- Implement retry logic with exponential backoff

### Error Handling
- Always check API response for error codes
- Common errors:
  - Invalid signature
  - Expired token
  - Missing permissions
  - Rate limit exceeded

### Security
- **Never commit APP_SECRET to version control**
- Store credentials securely (environment variables or secure vault)
- Tokens are stored in user's home directory: `~/.testshopgui/lazada_tokens.properties`

---

## 9. Next Steps

1. **Update LazadaConfig.java** with your actual credentials
2. **Test authorization flow** in sandbox environment
3. **Implement UI components** to trigger Lazada operations
4. **Add JSON parsing** to handle API responses (consider using Gson or Jackson)
5. **Implement error handling** and retry logic
6. **Add more API endpoints** as needed (products, inventory, etc.)

---

## 10. API Documentation

For complete API reference, visit:
- [Lazada Open Platform Documentation](https://open.lazada.com/doc/doc.htm)
- [API Authentication Guide](https://open.lazada.com/doc/api.htm?spm=a2o9m.11193531.0.0.33e537bbqNJJJJ#/api/auth)
- [Order API Reference](https://open.lazada.com/doc/api.htm?spm=a2o9m.11193531.0.0.33e537bbqNJJJJ#/api/order)

---

## Troubleshooting

### "Invalid Signature" Error
- Check that parameters are sorted correctly
- Verify APP_SECRET is correct
- Ensure timestamp is in milliseconds
- Check that all parameter values are included in signature

### "Access Token Expired" Error
- Token expired, use refresh token to get new access token
- If refresh token also expired, re-authorize

### "Invalid Authorization Code" Error
- Code expired (valid for 10 minutes only)
- Code already used (one-time use)
- Generate new authorization URL and try again
