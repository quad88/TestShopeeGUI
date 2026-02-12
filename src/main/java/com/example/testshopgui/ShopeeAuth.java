package com.example.testshopgui;

/**
 * Shopee Authentication Service
 * Handles authorization URL generation, token generation, and token refresh
 */
public class ShopeeAuth {

    /**
     * Generate shop authorization URL
     * Shop owner needs to visit this URL to authorize the app
     * @param redirectUrl The URL to redirect after authorization
     * @return Authorization URL
     */
    public static String generateAuthUrl(String redirectUrl) throws Exception {
        long timestamp = ShopeeSignature.getCurrentTimestamp();
        String sign = ShopeeSignature.generatePartnerSignature(ShopeeConfig.AUTH_PARTNER_PATH, timestamp);

        return ShopeeConfig.HOST + ShopeeConfig.AUTH_PARTNER_PATH +
                "?partner_id=" + ShopeeConfig.PARTNER_ID +
                "&timestamp=" + timestamp +
                "&sign=" + sign +
                "&redirect=" + redirectUrl;
    }

    /**
     * Generate shop authorization URL with backend callback
     * This is the recommended method for backend-only flow
     * @return Authorization URL that redirects to backend callback
     */
    public static String generateAuthUrlWithBackendCallback() throws Exception {
        return generateAuthUrl(RuntimeConfig.getBackendCallbackUrl());
    }


    /**
     * Get access token using authorization code
     * Exchange the authorization code for an access token
     * @param shopId The shop ID from redirect
     * @param code The authorization code from redirect
     * @return JSON response containing access_token and refresh_token
     */
    public static String getAccessToken(long shopId, String code) throws Exception {
        long timestamp = ShopeeSignature.getCurrentTimestamp();
        String sign = ShopeeSignature.generatePartnerSignature(ShopeeConfig.AUTH_TOKEN_PATH, timestamp);

        // Build URL (only signature parameters in URL)
        String urlString = ShopeeConfig.HOST + ShopeeConfig.AUTH_TOKEN_PATH +
                "?partner_id=" + ShopeeConfig.PARTNER_ID +
                "&timestamp=" + timestamp +
                "&sign=" + sign;

        // Build JSON body with code and shop_id
        String jsonBody = String.format("{\"code\":\"%s\",\"shop_id\":%d,\"partner_id\":%d}",
                code, shopId, ShopeeConfig.PARTNER_ID);

        // Log request details
        System.out.println("Request Details:");
        System.out.println("  Endpoint: " + ShopeeConfig.AUTH_TOKEN_PATH);
        System.out.println("  Shop ID: " + shopId);
        System.out.println("  Code: " + code);
        System.out.println("  Timestamp: " + timestamp);
        System.out.println("  Signature: " + sign);
        System.out.println("  Request Body: " + jsonBody);
        System.out.println("  (Partner-level API: partner_id + path + timestamp)\n");

        // Make API call with JSON body
        String response = ShopeeHttpClient.post(urlString, jsonBody);

        // Display response
        System.out.println("Token Response:");
        System.out.println(response);
        System.out.println();

        return response;
    }

    /**
     * Refresh access token using refresh token
     * @param shopId The shop ID
     * @param refreshToken The refresh token from previous token response
     * @return JSON response containing new access_token and refresh_token
     */
    public static String refreshAccessToken(long shopId, String refreshToken) throws Exception {
        long timestamp = ShopeeSignature.getCurrentTimestamp();
        String baseString = String.format("%s%s%s", RuntimeConfig.getPartnerId(), ShopeeConfig.AUTH_REFRESH_PATH, timestamp);
        String sign = ShopeeSignature.generatePartnerSignature(ShopeeConfig.AUTH_REFRESH_PATH, timestamp);

        // Build URL (only signature parameters in URL)
        String urlString = RuntimeConfig.getApiHost() + ShopeeConfig.AUTH_REFRESH_PATH +
                "?partner_id=" + RuntimeConfig.getPartnerId() +
                "&timestamp=" + timestamp +
                "&sign=" + sign;

        // Build JSON body with refresh_token and shop_id
        String jsonBody = String.format("{\"refresh_token\":\"%s\",\"shop_id\":%d,\"partner_id\":%d}",
                refreshToken, shopId, RuntimeConfig.getPartnerId());

        // Log request details
        System.out.println("Request Details:");
        System.out.println("  Endpoint: " + ShopeeConfig.AUTH_REFRESH_PATH);
        System.out.println("  Shop ID: " + shopId);
        System.out.println("  Refresh Token: " + refreshToken);
        System.out.println("  Timestamp: " + timestamp);
        System.out.println("  Signature: " + sign);
        System.out.println("  Request Body: " + jsonBody + "\n");

        // Make API call with JSON body
        String response = ShopeeHttpClient.post(urlString, jsonBody);

        // Display response
        System.out.println("Refresh Token Response:");
        System.out.println(response);
        System.out.println();

        return response;
    }

    /**
     * Extract authorization code from redirect URL
     * @param url The redirect URL containing the code parameter
     * @return The authorization code
     */
    public static String extractCodeFromUrl(String url) {
        try {
            // Handle both formats: with ? or after existing parameters
            String[] parts = url.split("[?&]");
            for (String part : parts) {
                if (part.startsWith("code=")) {
                    return part.substring(5); // Remove "code=" prefix
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing URL: " + e.getMessage());
        }
        return null;
    }

    /**
     * Check if token response is successful
     * @param tokenResponse JSON response from token API
     * @return true if successful, false if error
     */
    public static boolean isTokenResponseSuccess(String tokenResponse) {
        boolean hasAccessToken = tokenResponse.contains("\"access_token\"");
        boolean hasError = tokenResponse.contains("\"error\":\"") &&
                          !tokenResponse.contains("\"error\":\"\"") &&
                          !tokenResponse.contains("\"error\": \"\"");
        return hasAccessToken && !hasError;
    }
}
