# 🚀 Quick Start Guide - Shopee API Testing Tool

## Prerequisites

✅ **JDK 17** installed (Download from: https://adoptium.net/)
✅ **IntelliJ IDEA** (or any Java IDE)
✅ **Shopee Partner Account** with credentials

## Step 1: Configure JDK in IntelliJ IDEA

1. Open IntelliJ IDEA
2. Go to **File → Project Structure** (or press `Ctrl+Alt+Shift+S`)
3. In the left panel, select **Project**
4. Configure:
   - **SDK**: Click dropdown → **Add SDK → Download JDK**
   - Choose **Version: 17**, **Vendor: Eclipse Temurin**
   - Click **Download**
   - Set **Language level**: "17 - Sealed types, always-strict floating-point semantics"
5. Click **Apply** and **OK**

## Step 2: Configure Your Shopee Credentials

Edit `src/main/java/com/example/testshopgui/ShopeeConfig.java`:

```java
public static final long PARTNER_ID = YOUR_PARTNER_ID;  // Replace with your Partner ID
public static final String PARTNER_KEY = "YOUR_KEY";    // Replace with your Partner Key
public static final long SHOP_ID = YOUR_SHOP_ID;        // Replace with your Shop ID
```

## Step 3: Run the Application

### Method 1: Using IntelliJ Run Configuration
1. Right-click on `Launcher.java`
2. Select **Run 'Launcher.main()'**
3. Application window will open

### Method 2: Using Maven (if JAVA_HOME is set)
```bash
mvnw clean javafx:run
```

### Method 3: Create Run Configuration
1. **Run → Edit Configurations**
2. Click **+** → **Application**
3. Configure:
   - **Name**: Shopee GUI App
   - **Main class**: `com.example.testshopgui.Launcher`
   - **Module**: TestShopGUI
   - **JRE**: 17
4. Click **Apply** and **OK**
5. Click the green **Run** button

## Step 4: Authorize Your First Shop

### In the Application:

1. **Go to "Authorization" Tab**
   
2. **Enter Shop ID**
   - Default is from `ShopeeConfig.SHOP_ID`
   - Or enter a different shop ID

3. **Click "Generate Auth URL"**
   - Authorization URL will appear

4. **Open URL in Browser**
   - Click "Open in Browser" button
   - OR copy URL and paste in browser manually

5. **Login to Shopee**
   - Use shop owner credentials
   - Authorize the application

6. **Copy Authorization Code**
   - After authorization, you'll be redirected
   - URL will look like: `https://your-url.com/?code=XXXXXXXXXXXX&shop_id=12345`
   - Copy the `code` value (the part after `code=` and before `&`)

7. **Complete Authorization**
   - Paste the code in "Authorization Code" field
   - Click "Complete Authorization"
   - ✅ Success! Shop is now authorized

## Step 5: Fetch Orders

1. **Go to "Orders" Tab**

2. **Enter Shop ID**
   - Same shop ID you just authorized

3. **Click "Fetch Orders"**
   - Orders will be fetched and displayed
   - Token is automatically refreshed if expired

## Application Tabs

### 📋 Shop Manager
- View all authorized shops
- See token status and expiration
- Add test shops for development
- Remove shops

### 🔐 Authorization
- Generate authorization URLs
- Complete OAuth flow
- Save tokens automatically

### 📦 Orders
- Fetch orders from shops
- Auto-refresh expired tokens
- View order data

### ⚙️ Settings
- View API configuration
- Check partner credentials
- See storage information

## Troubleshooting

### ❌ "JDK isn't specified for module 'TestShopGUI'"
**Solution**: Follow Step 1 above to configure JDK 17

### ❌ "Module not found: XXX"
**Solution**: 
1. Right-click on `pom.xml`
2. Select **Maven → Reload Project**
3. Wait for dependencies to download

### ❌ "JAVA_HOME not found"
**Solution**: 
1. Set JAVA_HOME environment variable
2. OR use IntelliJ's built-in Maven in **View → Tool Windows → Maven**
3. OR just use IntelliJ run configuration (recommended)

### ❌ "Access token expired"
**Solution**: 
- Click "Refresh Token" in Orders tab
- OR re-authorize in Authorization tab

### ❌ "Shop not authorized"
**Solution**: 
- Complete authorization in Authorization tab first
- Make sure you copied the correct code from redirect URL

## Testing Without Real Authorization

You can add test shops with dummy tokens for UI testing:

1. Go to **Shop Manager** tab
2. Click **"Add Test Shops"**
3. Three test shops will be added
4. You can see them in the table

**Note**: These test tokens won't work for real API calls.

## API Endpoints

The application uses these Shopee API endpoints:

- **Sandbox**: `https://openplatform.sandbox.test-stable.shopee.sg`
- **Production**: `https://partner.shopeemobile.com` (update in config)

## Security Best Practices

⚠️ **Important**:
1. Never commit `ShopeeConfig.java` with real credentials
2. Use environment variables for production
3. Encrypt tokens before storing in database
4. Use HTTPS for all API calls
5. Implement rate limiting

## Next Steps

✅ Authorize your shops
✅ Fetch orders
✅ Explore the code
✅ Customize for your needs
✅ Add database integration
✅ Implement webhook handling

## Need Help?

- Check `README.md` for detailed documentation
- Review Shopee API docs: https://open.shopee.com/documents
- Check console output for detailed logs

---

**Happy Testing! 🎉**
