package com.example.testshopgui;

import java.util.HashMap;
import java.util.Map;

/**
 * Shopee OAuth Callback Handler
 * Simulates an HTTP endpoint that receives the OAuth callback
 *
 * In production, this would be a servlet/controller like:
 * - Spring Boot: @GetMapping("/shopee/callback")
 * - Servlet: @WebServlet("/shopee/callback")
 *
 * URL: https://smartpickapi-sit.axonstech.com/shopee/callback
 */
public class ShopeeCallbackHandler {

    /**
     * Handle GET request from Shopee OAuth redirect
     * This simulates: GET /shopee/callback?code=XXX&shop_id=123
     *
     * In a real web framework, this would be:
     * public String handleCallback(@RequestParam String code, @RequestParam Long shop_id)
     */
    public static String handleCallback(Map<String, String> params) {
        try {
            // Extract parameters from request
            String code = params.get("code");
            String shopIdStr = params.get("shop_id");

            // Validate parameters
            if (code == null || code.isEmpty()) {
                return error("Missing authorization code");
            }

            if (shopIdStr == null || shopIdStr.isEmpty()) {
                return error("Missing shop_id");
            }

            long shopId = Long.parseLong(shopIdStr);

            System.out.println("=== SHOPEE OAUTH CALLBACK RECEIVED ===");
            System.out.println("Callback URL: https://smartpickapi-sit.axonstech.com/shopee/callback");
            System.out.println("Parameters received:");
            System.out.println("  - code: " + code);
            System.out.println("  - shop_id: " + shopId);
            System.out.println();

            // Exchange authorization code for access token
            System.out.println("--- Exchanging code for access token ---");
            String tokenResponse = ShopeeAuth.getAccessToken(shopId, code);

            // Check if successful
            if (!ShopeeAuth.isTokenResponseSuccess(tokenResponse)) {
                System.err.println("❌ Failed to get access token");
                return error("Token exchange failed: " + tokenResponse);
            }

            // Parse tokens (simplified - use proper JSON parser in production)
            String accessToken = extractJsonValue(tokenResponse, "access_token");
            String refreshToken = extractJsonValue(tokenResponse, "refresh_token");
            String expiresInStr = extractJsonValue(tokenResponse, "expire_in");

            if (accessToken == null || refreshToken == null) {
                return error("Failed to parse tokens from response");
            }

            long expiresIn = expiresInStr != null ? Long.parseLong(expiresInStr) : 14400; // default 4 hours

            // Save tokens to storage (database in production)
            ShopeeTokenStorage.saveTokens(shopId, accessToken, refreshToken, expiresIn);

            System.out.println("=".repeat(80));
            System.out.println("✓ AUTHORIZATION SUCCESSFUL!");
            System.out.println("=".repeat(80));
            System.out.println("Shop " + shopId + " is now authorized.");
            System.out.println("Tokens have been saved and are ready to use.");
            System.out.println("=".repeat(80));
            System.out.println();

            // In production, redirect to frontend success page
            return redirectToFrontend("https://smartpick-sit.axonstech.com/auth-success?shop_id=" + shopId);

        } catch (Exception e) {
            System.err.println("Error in callback handler: " + e.getMessage());
            e.printStackTrace();
            return error("Internal error: " + e.getMessage());
        }
    }

    /**
     * Simulate parsing URL query parameters
     * In production, this is handled by your web framework
     */
    public static Map<String, String> parseQueryString(String url) {
        Map<String, String> params = new HashMap<>();

        try {
            // Extract query string
            String queryString = url.contains("?") ? url.split("\\?")[1] : url;

            // Parse parameters
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing query string: " + e.getMessage());
        }

        return params;
    }

    /**
     * Simple JSON value extractor
     */
    private static String extractJsonValue(String json, String key) {
        try {
            String searchKey = "\"" + key + "\":\"";
            int startIndex = json.indexOf(searchKey);
            if (startIndex == -1) {
                searchKey = "\"" + key + "\":";
                startIndex = json.indexOf(searchKey);
                if (startIndex == -1) return null;
                startIndex += searchKey.length();
                int endIndex = json.indexOf(",", startIndex);
                if (endIndex == -1) endIndex = json.indexOf("}", startIndex);
                return json.substring(startIndex, endIndex).trim();
            }
            startIndex += searchKey.length();
            int endIndex = json.indexOf("\"", startIndex);
            return json.substring(startIndex, endIndex);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Return error response
     */
    private static String error(String message) {
        return "ERROR: " + message;
    }

    /**
     * Return redirect response
     */
    private static String redirectToFrontend(String url) {
        return "REDIRECT: " + url;
    }
}
