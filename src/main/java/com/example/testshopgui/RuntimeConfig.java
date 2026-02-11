package com.example.testshopgui;

/**
 * Runtime Configuration for Shopee API
 * Allows dynamic changes to configuration values during runtime
 */
public class RuntimeConfig {
    // Runtime values (can be changed via GUI)
    private static long partnerId = ShopeeConfig.PARTNER_ID;
    private static String partnerKey = ShopeeConfig.PARTNER_KEY;
    private static long defaultShopId = ShopeeConfig.SHOP_ID;
    private static String apiHost = ShopeeConfig.HOST;
    private static String redirectUrl = ShopeeConfig.REDIRECT_URL;
    private static String backendCallbackUrl = ShopeeConfig.BACKEND_CALLBACK_URL;

    // Getters
    public static long getPartnerId() {
        return partnerId;
    }

    public static String getPartnerKey() {
        return partnerKey;
    }

    public static long getDefaultShopId() {
        return defaultShopId;
    }

    public static String getApiHost() {
        return apiHost;
    }

    public static String getRedirectUrl() {
        return redirectUrl;
    }

    public static String getBackendCallbackUrl() {
        return backendCallbackUrl;
    }

    // Setters
    public static void setPartnerId(long partnerId) {
        RuntimeConfig.partnerId = partnerId;
        System.out.println("✓ Partner ID updated to: " + partnerId);
    }

    public static void setPartnerKey(String partnerKey) {
        RuntimeConfig.partnerKey = partnerKey;
        System.out.println("✓ Partner Key updated");
    }

    public static void setDefaultShopId(long shopId) {
        RuntimeConfig.defaultShopId = shopId;
        System.out.println("✓ Default Shop ID updated to: " + shopId);
    }

    public static void setApiHost(String host) {
        RuntimeConfig.apiHost = host;
        System.out.println("✓ API Host updated to: " + host);
    }

    public static void setRedirectUrl(String url) {
        RuntimeConfig.redirectUrl = url;
        System.out.println("✓ Redirect URL updated to: " + url);
    }

    public static void setBackendCallbackUrl(String url) {
        RuntimeConfig.backendCallbackUrl = url;
        System.out.println("✓ Backend Callback URL updated to: " + url);
    }

    // Reset to defaults
    public static void resetToDefaults() {
        partnerId = ShopeeConfig.PARTNER_ID;
        partnerKey = ShopeeConfig.PARTNER_KEY;
        defaultShopId = ShopeeConfig.SHOP_ID;
        apiHost = ShopeeConfig.HOST;
        redirectUrl = ShopeeConfig.REDIRECT_URL;
        backendCallbackUrl = ShopeeConfig.BACKEND_CALLBACK_URL;
        System.out.println("✓ Configuration reset to defaults");
    }
}
