package com.example.testshopgui;

/**
 * Lazada API Configuration
 * Contains all constant values for Lazada API integration
 */
public class LazadaConfig {
    // App credentials (REPLACE WITH YOUR ACTUAL CREDENTIALS)
    public static final String APP_KEY = "136803";
    public static final String APP_SECRET = "sU1R3v9vR9yxIA3bJKxaMDjMxVHZtN0r";

    // API Gateway URL
    // Choose the correct URL based on your region:
    // Singapore: https://api.lazada.sg/rest
    // Thailand: https://api.lazada.co.th/rest
    // Malaysia: https://api.lazada.com.my/rest
    // Vietnam: https://api.lazada.vn/rest
    // Philippines: https://api.lazada.com.ph/rest
    // Indonesia: https://api.lazada.co.id/rest
    public static final String API_GATEWAY = "https://api.lazada.co.th/rest";

    // API Endpoints (relative paths)
    public static final String AUTH_URL_PATH = "/auth/authorize";
    public static final String TOKEN_CREATE_PATH = "/auth/token/create";
    public static final String TOKEN_REFRESH_PATH = "/auth/token/refresh";
    public static final String ORDERS_GET_PATH = "/orders/get";
    public static final String ORDER_ITEMS_GET_PATH = "/order/items/get";

    // Redirect URL (configure this in your Lazada app settings)
    public static final String REDIRECT_URL = "https://smartpick-sit.axonstech.com/lazada/callback";
//    public static final String REDIRECT_URL = "https://smartpick-uat.axonstech.com/login";

    // Default configuration
    public static final int TOKEN_EXPIRY_BUFFER_SECONDS = 300; // 5 minutes buffer before expiry
}
