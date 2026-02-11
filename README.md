# Shopee API Testing Tool - Multi-Shop Manager

A JavaFX GUI application for testing Shopee API integration with multiple shops. This tool allows you to authorize shops, manage tokens, and fetch orders through a user-friendly interface.

## Features

✅ **Multi-Shop Management**
- Manage multiple Shopee shops from one interface
- View token status and expiration for each shop
- Add, remove, and refresh shop data

✅ **OAuth Authorization**
- Generate authorization URLs
- Complete OAuth flow with visual feedback
- Automatic token storage and management

✅ **Order Management**
- Fetch orders from authorized shops
- Auto-refresh expired tokens
- Display order data in readable format

✅ **Token Management**
- Automatic token refresh when expired
- In-memory token storage (easily replaceable with database)
- Token expiration monitoring

## Project Structure

```
src/main/java/com/example/testshopgui/
├── ShopeeGuiApp.java           # Main JavaFX application
├── Launcher.java                # Application entry point
├── ShopManagerPanel.java        # Shop management UI panel
├── AuthPanel.java               # Authorization UI panel
├── OrderPanel.java              # Order fetching UI panel
├── ShopeeConfig.java            # API configuration constants
├── ShopeeAuth.java              # Authentication service
├── ShopeeOrderAPI.java          # Order API service
├── ShopeeHttpClient.java        # HTTP client for API calls
├── ShopeeSignature.java         # HMAC-SHA256 signature generation
├── ShopeeTokenStorage.java      # Token storage (in-memory)
├── ShopeeBackendOnlyService.java # Backend-only flow service
└── ShopeeCallbackHandler.java   # OAuth callback handler
```

## Configuration

Update `ShopeeConfig.java` with your Shopee Partner credentials:

```java
public static final long PARTNER_ID = YOUR_PARTNER_ID;
public static final String PARTNER_KEY = "YOUR_PARTNER_KEY";
public static final String HOST = "https://openplatform.sandbox.test-stable.shopee.sg";
```

## How to Run

### Using Maven:
```bash
mvnw clean javafx:run
```

### Using IntelliJ IDEA:
1. Open the project in IntelliJ IDEA
2. Ensure JDK 17 is configured (File → Project Structure → Project → SDK)
3. Right-click on `Launcher.java` → Run 'Launcher.main()'

## Usage Guide

### 1. Shop Management Tab
- View all authorized shops
- Monitor token status and expiration
- Add test shops for development
- Remove shops from storage

### 2. Authorization Tab
**Step-by-step authorization process:**

1. Enter Shop ID (or use default from config)
2. Click "Generate Auth URL" to create authorization link
3. Click "Copy URL" or "Open in Browser"
4. Login to Shopee and authorize your app
5. After redirect, copy the `code` parameter from the URL
6. Paste the code in the "Authorization Code" field
7. Click "Complete Authorization"
8. Tokens are automatically saved

**Example redirect URL:**
```
https://your-callback-url.com/?code=AUTHORIZATION_CODE&shop_id=226457519
```
Copy only the `AUTHORIZATION_CODE` part.

### 3. Orders Tab
- Select a shop ID
- Click "Fetch Orders" to retrieve order list
- Use "Refresh Token" if token is expired
- Orders are displayed in JSON format

## API Endpoints Used

- **Auth Partner**: `/api/v2/shop/auth_partner` - Generate authorization URL
- **Get Token**: `/api/v2/auth/token/get` - Exchange code for access token
- **Refresh Token**: `/api/v2/auth/access_token/get` - Refresh expired tokens
- **Get Orders**: `/api/v2/order/get_order_list` - Fetch order list

## Token Storage

Current implementation uses in-memory storage (`ShopeeTokenStorage.java`).

**For production, replace with:**
- Database (MySQL, PostgreSQL)
- Redis cache
- Secure key-value store

## Security Notes

⚠️ **Important Security Considerations:**

1. **Never commit credentials** - Store Partner ID and Key in environment variables
2. **Use HTTPS** - Always use secure connections for API calls
3. **Token encryption** - Encrypt tokens before storing in database
4. **Rate limiting** - Implement rate limiting for API calls
5. **Sandbox vs Production** - Currently using sandbox; update HOST for production

## Development

### Dependencies
- JavaFX 17.0.14 (Controls, FXML)
- JDK 17
- Maven

### Module Configuration
The project uses Java modules. See `module-info.java`:
```java
module com.example.testshopgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    
    opens com.example.testshopgui to javafx.fxml;
    exports com.example.testshopgui;
}
```

## Troubleshooting

### JDK Not Configured
**Error:** "JDK isn't specified for module 'TestShopGUI'"

**Solution:**
1. File → Project Structure → Project
2. Set SDK to JDK 17
3. Set Language Level to "17"

### Module Not Found
**Error:** "module not found: XXX"

**Solution:**
- Check `module-info.java` includes all required modules
- Verify dependencies in `pom.xml`

### Token Expired
**Error:** "Access token expired"

**Solution:**
- Use "Refresh Token" button in Orders tab
- Or re-authorize the shop in Authorization tab

## Backend-Only Flow

For server-to-server integration without GUI:

```java
// 1. Initiate authorization
AuthorizationRequest request = ShopeeBackendOnlyService.initiateAuthorization(shopId);
// Send authUrl to shop owner via email/notification

// 2. Handle callback (automatic)
ShopeeBackendOnlyService.handleAuthorizationCallback(code, shopId);

// 3. Fetch orders (automatic)
String orders = ShopeeBackendOnlyService.fetchOrders(shopId);
```

## Testing

### Test Shops
Use "Add Test Shops" button to add sample shops with dummy tokens for UI testing.

### Sandbox Environment
Currently configured for Shopee sandbox:
```
https://openplatform.sandbox.test-stable.shopee.sg
```

## Future Enhancements

- [ ] Database integration for token storage
- [ ] Order details view
- [ ] Product management
- [ ] Webhook handling
- [ ] Multi-language support
- [ ] Export order data to CSV/Excel
- [ ] Automated token refresh scheduler
- [ ] Dark mode theme

## License

This is a testing tool for Shopee API integration. Use in accordance with Shopee Partner Platform terms and conditions.

## Support

For Shopee API documentation:
https://open.shopee.com/documents

---

**Built with ❤️ for Shopee API Testing**
