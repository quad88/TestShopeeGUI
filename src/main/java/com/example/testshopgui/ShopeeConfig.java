package com.example.testshopgui;

/**
 * Shopee API Configuration
 * Contains all constant values for API integration
 */
public class ShopeeConfig {
    // Partner credentials
    public static final long PARTNER_ID = 1216215L;
    public static final String PARTNER_KEY = "shpk6457644a7a724d6e74585a504f656a76786f6c485444695956554f45536a";

    // API Host
    public static final String HOST = "https://openplatform.sandbox.test-stable.shopee.sg";

    // API Endpoints
    public static final String AUTH_PARTNER_PATH = "/api/v2/shop/auth_partner";
    public static final String AUTH_TOKEN_PATH = "/api/v2/auth/token/get";
    public static final String AUTH_REFRESH_PATH = "/api/v2/auth/access_token/get";
    public static final String ORDER_LIST_PATH = "/api/v2/order/get_order_list";

    // Default configuration
    public static final String REDIRECT_URL = "https://smartpick-uat.axonstech.com/";
    public static final String BACKEND_CALLBACK_URL = "https://smartpickapi-sit.axonstech.com/shopee/callback";
    public static final long SHOP_ID = 226457519L;

    // Authorization code expiry time (in seconds)
    public static final int AUTH_CODE_EXPIRY_SECONDS = 600; // 10 minutes
}
