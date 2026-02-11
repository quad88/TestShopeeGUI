# ✅ Run Checklist - Before Starting the Application

## Pre-Flight Checks

### 1. JDK Configuration ✓
- [x] JDK 17 is installed
- [x] IntelliJ IDEA project SDK is set to JDK 17
- [x] Language level is set to 17

**How to verify:**
1. File → Project Structure → Project
2. Check SDK shows "17" or "openjdk-17"
3. Check Language level shows "17"

### 2. Project Configuration ✓
- [x] `module-info.java` is configured
- [x] `pom.xml` points to correct main class
- [x] No compilation errors

**How to verify:**
1. Build → Rebuild Project
2. Check for errors in "Problems" panel
3. Should see "Build completed successfully"

### 3. Shopee API Credentials ⚠️
- [ ] Partner ID is set in `ShopeeConfig.java`
- [ ] Partner Key is set in `ShopeeConfig.java`
- [ ] Shop ID is set (optional, can be entered in UI)

**Action Required:**
Edit `src/main/java/com/example/testshopgui/ShopeeConfig.java`:
```java
public static final long PARTNER_ID = YOUR_PARTNER_ID;  // ← Change this
public static final String PARTNER_KEY = "YOUR_KEY";    // ← Change this
public static final long SHOP_ID = YOUR_SHOP_ID;        // ← Change this (optional)
```

### 4. Dependencies Downloaded ✓
- [x] JavaFX dependencies
- [x] Maven dependencies

**How to verify:**
1. Right-click `pom.xml`
2. Maven → Reload Project
3. Wait for download to complete

## How to Run

### Option 1: Run from IntelliJ (Recommended) ✅

1. **Navigate to Launcher**
   - Open: `src/main/java/com/example/testshopgui/Launcher.java`

2. **Right-click on the file**
   - Select: "Run 'Launcher.main()'"

3. **Application should start**
   - Window title: "Shopee API Testing Tool - Multi-Shop Manager"
   - Should see 4 tabs: Shop Manager, Authorization, Orders, Settings

### Option 2: Using Maven Goal ✅

1. **Open Maven panel**
   - View → Tool Windows → Maven

2. **Navigate to:**
   - TestShopGUI → Plugins → javafx → javafx:run

3. **Double-click to run**

### Option 3: Create Run Configuration ✅

1. **Run → Edit Configurations**
2. **Click + → Application**
3. **Configure:**
   - Name: `Shopee GUI`
   - Main class: `com.example.testshopgui.Launcher`
   - Module: `TestShopGUI`
   - JRE: `17`
4. **Click Apply → OK**
5. **Click green Run button** ▶️

## First Time Usage

### Step 1: Verify Application Started ✅

You should see:
- ✅ Window opens with title "Shopee API Testing Tool"
- ✅ Four tabs visible
- ✅ Settings tab shows your Partner ID
- ✅ No error dialogs

### Step 2: Add Test Shops (Optional) ✅

1. Go to **"Shop Manager"** tab
2. Click **"Add Test Shops"**
3. Should see 3 test shops in table
4. These have dummy tokens (won't work for real API calls)

### Step 3: Authorize Real Shop ✅

1. Go to **"Authorization"** tab
2. Enter your Shop ID (or use default)
3. Click **"Generate Auth URL"**
4. Click **"Open in Browser"** OR copy URL manually
5. Login to Shopee and authorize
6. Copy the `code` from redirect URL
7. Paste in "Authorization Code" field
8. Click **"Complete Authorization"**
9. Should see success message

### Step 4: Fetch Orders ✅

1. Go to **"Orders"** tab
2. Enter authorized Shop ID
3. Click **"Fetch Orders"**
4. Orders should appear in text area

## Troubleshooting

### ❌ Window doesn't open

**Check:**
1. Console for error messages
2. JDK 17 is selected
3. JavaFX dependencies are downloaded

**Fix:**
```bash
# In Maven panel
Maven → Reload Project
Build → Rebuild Project
```

### ❌ "JavaFX runtime components are missing"

**Fix:**
1. File → Project Structure → Libraries
2. Should see javafx-controls, javafx-fxml
3. If missing, Maven → Reload Project

### ❌ "Module not found"

**Fix:**
1. Check `module-info.java` exists
2. Should contain:
   ```java
   module com.example.testshopgui {
       requires javafx.controls;
       requires javafx.fxml;
       requires java.desktop;
       opens com.example.testshopgui to javafx.fxml;
       exports com.example.testshopgui;
   }
   ```

### ❌ "Cannot access Desktop"

**Fix:**
- Add `requires java.desktop;` to module-info.java (already done ✓)

### ❌ Authorization fails

**Check:**
1. Partner ID and Key are correct
2. Using correct Shopee environment (sandbox vs production)
3. Shop ID is correct
4. Code was copied correctly (no extra spaces)

### ❌ Orders fetch fails

**Check:**
1. Shop is authorized first (Authorization tab)
2. Token hasn't expired (or use Refresh Token button)
3. Network connection is working
4. API endpoint is reachable

## Success Indicators ✅

When everything is working correctly:

1. **Application Starts**
   - ✅ Window opens without errors
   - ✅ All tabs are visible and clickable
   - ✅ Settings tab shows configuration

2. **Authorization Works**
   - ✅ URL is generated
   - ✅ Browser opens to Shopee login
   - ✅ After login, redirect happens
   - ✅ Code can be completed successfully
   - ✅ Success message appears
   - ✅ Shop appears in Shop Manager tab

3. **Orders Fetch Works**
   - ✅ Shop ID is recognized
   - ✅ No "not authorized" error
   - ✅ JSON response appears
   - ✅ No API errors in console

## Console Output

**Normal output should show:**
```
Request Details:
  Endpoint: /api/v2/...
  Shop ID: ...
  Timestamp: ...
  Signature: ...
  
Response Code: 200

✓ Tokens saved for shop_id: ...
```

**Error output would show:**
```
❌ Failed to ...
Error: ...
```

## Quick Test Sequence

**5-Minute Smoke Test:**

1. ✅ Start application (30 seconds)
2. ✅ Add test shops (10 seconds)
3. ✅ Check they appear in table (5 seconds)
4. ✅ Go to Authorization tab (5 seconds)
5. ✅ Generate auth URL (5 seconds)
6. ✅ Verify URL is displayed (5 seconds)
7. ✅ Go to Settings tab (5 seconds)
8. ✅ Verify configuration is shown (5 seconds)

**Total: Application is working! ✅**

## Next Steps

Once checklist is complete:

1. **Test with real credentials**
   - Use your Shopee Partner credentials
   - Authorize a test shop
   - Fetch real orders

2. **Explore the code**
   - Understand the API flow
   - Modify for your needs
   - Add custom features

3. **Customize**
   - Change UI styling
   - Add new panels
   - Integrate with database
   - Add webhook handling

## Support Files

- 📖 **README.md** - Detailed documentation
- 🚀 **QUICKSTART.md** - Setup guide
- ✅ **MERGE_SUMMARY.md** - What was built
- 📋 **RUN_CHECKLIST.md** - This file

---

**Ready to test your Shopee shops! 🎉**

Last updated: February 12, 2026
