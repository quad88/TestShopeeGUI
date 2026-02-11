# 🎯 YOUR NEXT STEPS - Start Using the Application

## ✅ Everything is Ready! Here's What to Do:

---

## STEP 1: Verify JDK is Configured ⏱️ 30 seconds

### In IntelliJ IDEA:

1. **Open File → Project Structure** (or press `Ctrl+Alt+Shift+S`)

2. **Click "Project" in the left panel**

3. **Check these settings:**
   ```
   SDK: 17 (java version "17.x.x")
   Language level: 17 - Sealed types, always-strict...
   ```

4. **If SDK is not set:**
   - Click SDK dropdown
   - Select "Add SDK → Download JDK"
   - Choose: Version 17, Vendor: Eclipse Temurin
   - Click Download
   - Wait for it to finish
   - Click Apply → OK

✅ **Done? Continue to Step 2**

---

## STEP 2: Update Your Shopee Credentials ⏱️ 1 minute

### Find the file:
```
src/main/java/com/example/testshopgui/ShopeeConfig.java
```

### Edit these lines:
```java
// REPLACE THESE VALUES WITH YOUR CREDENTIALS:
public static final long PARTNER_ID = 1216215L;  // ← Your Partner ID
public static final String PARTNER_KEY = "shpk...";  // ← Your Partner Key
public static final long SHOP_ID = 226457519L;  // ← Your Shop ID (optional)
```

### Where to get credentials:
- Go to: https://partner.shopeemobile.com
- Login to Partner Portal
- Find: Partner ID and Partner Key in settings

### Save the file (Ctrl+S)

✅ **Done? Continue to Step 3**

---

## STEP 3: Run the Application ⏱️ 10 seconds

### Method 1 - Quick Run (Easiest):

1. **In Project panel (left side), navigate to:**
   ```
   src → main → java → com.example.testshopgui → Launcher.java
   ```

2. **Right-click on `Launcher.java`**

3. **Select: "Run 'Launcher.main()'"**

4. **Wait for the window to open** ⏳

### Method 2 - Using Maven:

1. **Open Maven panel** (View → Tool Windows → Maven)
2. **Expand: TestShopGUI → Plugins → javafx**
3. **Double-click: javafx:run**

✅ **Application window opened? Continue to Step 4**

---

## STEP 4: Test the Application ⏱️ 2 minutes

### Quick Test (No API calls needed):

1. **Window should show:**
   ```
   Title: "Shopee API Testing Tool - Multi-Shop Manager"
   4 Tabs: Shop Manager | Authorization | Orders | Settings
   ```

2. **Click "Settings" tab**
   - Should show your Partner ID
   - Should show API Host
   - No errors

3. **Click "Shop Manager" tab**
   - Click "Add Test Shops" button
   - Should see 3 shops in table
   - Shop IDs: 226457519, 123456789, 987654321

4. **Click "Authorization" tab**
   - Enter a shop ID (or use default)
   - Click "Generate Auth URL"
   - URL should appear in text area

✅ **Everything works? You're ready!**

---

## STEP 5: Authorize a Real Shop ⏱️ 5 minutes

### Complete OAuth Flow:

1. **In "Authorization" tab:**
   - Enter your Shop ID
   - Click "Generate Auth URL"
   - Click "Open in Browser" (or copy URL)

2. **In Browser:**
   - Login with shop owner credentials
   - Authorize the application
   - Wait for redirect

3. **After Redirect:**
   - URL will look like:
     ```
     https://your-url.com/?code=ABC123XYZ&shop_id=12345
     ```
   - **Copy only the CODE part** (ABC123XYZ)

4. **Back in Application:**
   - Paste code in "Authorization Code" field
   - Click "Complete Authorization"
   - Wait for success message

5. **Success!** ✅
   - You should see: "Authorization completed successfully!"
   - Go to "Shop Manager" tab
   - Click "Refresh"
   - Your shop should appear with "✅ Active" status

✅ **Shop authorized? Continue to Step 6**

---

## STEP 6: Fetch Orders ⏱️ 1 minute

### Get Real Order Data:

1. **Go to "Orders" tab**

2. **Enter the Shop ID you just authorized**

3. **Click "Fetch Orders"**

4. **Wait for response** ⏳
   - Status will show: "⏳ Fetching orders..."
   - Then: "✓ Orders fetched successfully"

5. **View Orders:**
   - JSON response appears in text area
   - Shows order list from last 15 days

✅ **Orders fetched successfully? YOU'RE DONE!** 🎉

---

## 🎉 CONGRATULATIONS!

You've successfully:
- ✅ Configured the project
- ✅ Run the application
- ✅ Authorized a shop
- ✅ Fetched orders via GUI

### Now You Can:
- Test multiple shops
- Manage tokens easily
- Fetch orders anytime
- Extend the application

---

## 🆘 Troubleshooting

### Problem: Window doesn't open

**Solutions:**
1. Check "Run" panel at bottom for errors
2. Verify JDK 17 is selected
3. Try: Build → Rebuild Project
4. Try: File → Invalidate Caches → Restart

### Problem: "Module not found" error

**Solution:**
1. Right-click pom.xml
2. Maven → Reload Project
3. Wait for dependencies to download

### Problem: Authorization fails

**Check:**
- Partner ID and Key are correct
- Code was copied correctly (no extra spaces)
- Shop ID matches the one in redirect URL

### Problem: Orders fetch fails

**Check:**
- Shop is authorized first
- Token isn't expired (or click Refresh Token)
- Network connection is working

### Problem: "Desktop not supported"

**This is OK!**
- Just means browser can't auto-open
- Copy URL manually and paste in browser

---

## 📚 Documentation Reference

**Need more details?**

1. **README.md** - Full documentation
2. **QUICKSTART.md** - Detailed setup guide
3. **RUN_CHECKLIST.md** - Pre-flight checks
4. **MERGE_SUMMARY.md** - What was built

All files are in project root folder.

---

## 🔧 Common Tasks

### Add Another Shop:
```
1. Go to Authorization tab
2. Enter new Shop ID
3. Generate URL and authorize
4. Complete authorization
5. Done!
```

### Refresh Expired Token:
```
1. Go to Orders tab
2. Enter Shop ID
3. Click "Refresh Token"
4. Done!
```

### View All Shops:
```
1. Go to Shop Manager tab
2. Click "Refresh"
3. See all authorized shops
```

### Remove a Shop:
```
1. Go to Shop Manager tab
2. Click on shop in table
3. Click "Remove Selected"
4. Confirm
```

---

## 🎯 Mission Success Criteria

✅ Application starts without errors
✅ Can authorize shops
✅ Can fetch orders
✅ UI is responsive
✅ All tabs work

### **YOU HAVE ACHIEVED ALL CRITERIA!** 🏆

---

## 🚀 You're All Set!

The application is:
- ✅ Fully configured
- ✅ Fully functional  
- ✅ Ready to use
- ✅ Well documented

**Start testing your Shopee shops now!** 🎉

---

*Created: February 12, 2026*
*Status: Ready for Production Testing*
*Next: Authorize shops and fetch orders!*
