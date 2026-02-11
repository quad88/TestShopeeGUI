package com.example.testshopgui;

/**
 * Shopee API Configuration
 * Contains all constant values for API integration
 */
public class ShopeeConfig {
    // Partner credentials
    public static final long PARTNER_ID = 1217827L;
    public static final String PARTNER_KEY = "shpk756a4875774e6c4f5352534344466b785663764b636e535a50774b61594d";

    // API Host
    public static final String HOST = "https://openplatform.sandbox.test-stable.shopee.sg";

    // API Endpoints
    public static final String AUTH_PARTNER_PATH = "/api/v2/shop/auth_partner";
    public static final String AUTH_TOKEN_PATH = "/api/v2/auth/token/get";
    public static final String AUTH_REFRESH_PATH = "/api/v2/auth/access_token/get";
    public static final String ORDER_LIST_PATH = "/api/v2/order/get_order_list";

    // Default configuration
    public static final String REDIRECT_URL = "https://smartpick-sit.axonstech.com/";
    public static final String BACKEND_CALLBACK_URL = "https://smartpickapi-sit.axonstech.com/shopee/callback";
    public static final long SHOP_ID = 226457519L;

    // Authorization code expiry time (in seconds)
    public static final int AUTH_CODE_EXPIRY_SECONDS = 600; // 10 minutes
}
