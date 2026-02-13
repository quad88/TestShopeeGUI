package com.example.testshopgui;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Lazada Authentication Service
 * Handles authorization URL generation, token creation, and token refresh
 */
public class LazadaAuth {

    /**
     * Generate authorization URL for seller to authorize the app
     * Seller needs to visit this URL to grant permissions
     * @param redirectUrl The callback URL after authorization
     * @return Authorization URL
     */
    public static String generateAuthUrl(String redirectUrl) throws Exception {
        String encodedRedirectUrl = URLEncoder.encode(redirectUrl, StandardCharsets.UTF_8);

        // Lazada authorization URL format
        // https://auth.lazada.com/oauth/authorize?response_type=code&force_auth=true&redirect_uri={redirect_uri}&client_id={app_key}
        String authUrl = "https://auth.lazada.com/oauth/authorize" +
                "?response_type=code" +
                "&force_auth=true" +
                "&redirect_uri=" + encodedRedirectUrl +
                "&client_id=" + LazadaConfig.APP_KEY;

        System.out.println("=== Generated Lazada Auth URL ===");
        System.out.println("App Key: " + LazadaConfig.APP_KEY);
        System.out.println("Redirect URL: " + redirectUrl);
        System.out.println("Full Auth URL: " + authUrl);
        System.out.println("==================================");

        return authUrl;
    }

    /**
     * Generate authorization URL with default redirect URL from config
     * @return Authorization URL
     */
    public static String generateAuthUrl() throws Exception {
        return generateAuthUrl(LazadaConfig.REDIRECT_URL);
    }

    /**
     * Create access token using authorization code
     * Exchange the authorization code for an access token
     * @param code The authorization code from callback
     * @return JSON response containing access_token, refresh_token, and expiry info
     */
    public static String createAccessToken(String code) throws Exception {
        long timestamp = LazadaSignature.getCurrentTimestamp();

        // Build parameters
        Map<String, String> params = new HashMap<>();
        params.put("app_key", LazadaConfig.APP_KEY);
        params.put("timestamp", String.valueOf(timestamp));
        params.put("sign_method", "sha256");
        params.put("code", code);

        // Build signed URL
        String url = LazadaSignature.buildSignedUrl(
                LazadaConfig.API_GATEWAY,
                LazadaConfig.TOKEN_CREATE_PATH,
                params,
                LazadaConfig.APP_SECRET
        );

        // Log request details
        System.out.println("=== Create Access Token Request ===");
        System.out.println("Endpoint: " + LazadaConfig.TOKEN_CREATE_PATH);
        System.out.println("App Key: " + LazadaConfig.APP_KEY);
        System.out.println("Authorization Code: " + code);
        System.out.println("Timestamp: " + timestamp);
        System.out.println("Full URL: " + url);
        System.out.println("====================================");

        // Make API call
        String response = LazadaHttpClient.post(url, null);

        // Display response
        System.out.println("Token Response:");
        System.out.println(response);
        System.out.println();

        return response;
    }

    /**
     * Refresh access token using refresh token
     * @param refreshToken The refresh token from previous token response
     * @return JSON response containing new access_token and refresh_token
     */
    public static String refreshAccessToken(String refreshToken) throws Exception {
        long timestamp = LazadaSignature.getCurrentTimestamp();

        // Build parameters
        Map<String, String> params = new HashMap<>();
        params.put("app_key", LazadaConfig.APP_KEY);
        params.put("timestamp", String.valueOf(timestamp));
        params.put("sign_method", "sha256");
        params.put("refresh_token", refreshToken);

        // Build signed URL
        String url = LazadaSignature.buildSignedUrl(
                LazadaConfig.API_GATEWAY,
                LazadaConfig.TOKEN_REFRESH_PATH,
                params,
                LazadaConfig.APP_SECRET
        );

        // Log request details
        System.out.println("=== Refresh Access Token Request ===");
        System.out.println("Endpoint: " + LazadaConfig.TOKEN_REFRESH_PATH);
        System.out.println("App Key: " + LazadaConfig.APP_KEY);
        System.out.println("Refresh Token: " + refreshToken);
        System.out.println("Timestamp: " + timestamp);
        System.out.println("Full URL: " + url);
        System.out.println("=====================================");

        // Make API call
        String response = LazadaHttpClient.post(url, null);

        // Display response
        System.out.println("Refresh Token Response:");
        System.out.println(response);
        System.out.println();

        return response;
    }
}
