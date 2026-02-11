package com.example.testshopgui;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shopee Backend-Only Service
 *
 * This service allows PURE BACKEND interaction with Shopee API.
 * No frontend needed - everything is handled by backend endpoints.
 *
 * Use Cases:
 * 1. Admin panel authentication
 * 2. Background job authentication
 * 3. Server-to-server integration
 * 4. Automated order processing
 */
public class ShopeeBackendOnlyService {

    // Store pending authorization requests (in production, use Redis or database)
    private static final Map<String, AuthorizationRequest> pendingAuthorizations = new ConcurrentHashMap<>();

    /**
     * Authorization request tracking
     */
    static class AuthorizationRequest {
        String requestId;
        long shopId;
        String authUrl;
        long createdAt;
        String status; // "pending", "completed", "expired"

        public AuthorizationRequest(String requestId, long shopId, String authUrl) {
            this.requestId = requestId;
            this.shopId = shopId;
            this.authUrl = authUrl;
            this.createdAt = System.currentTimeMillis();
            this.status = "pending";
        }

        public boolean isExpired() {
            return (System.currentTimeMillis() - createdAt) > 600000; // 10 minutes
        }
    }

    /**
     * STEP 1: Initiate Authorization (Backend Only)
     *
     * This method generates an authorization URL that your backend will use.
     * You can:
     * - Return it to an admin panel
     * - Send it via email to shop owner
     * - Store it for manual processing
     * - Use it in automated flows
     *
     * @param shopId The shop ID to authorize (optional, can be obtained from callback)
     * @return Authorization request with URL and tracking ID
     */
    public static AuthorizationRequest initiateAuthorization(Long shopId) throws Exception {
        String requestId = generateRequestId();
        String authUrl = ShopeeAuth.generateAuthUrlWithBackendCallback();

        AuthorizationRequest request = new AuthorizationRequest(requestId,
            shopId != null ? shopId : 0, authUrl);
        pendingAuthorizations.put(requestId, request);

        System.out.println("=== AUTHORIZATION INITIATED (BACKEND) ===");
        System.out.println("Request ID: " + requestId);
        System.out.println("Authorization URL: " + authUrl);
        System.out.println("\nBackend Callback URL: " + ShopeeConfig.BACKEND_CALLBACK_URL);
        System.out.println("\nOptions to complete authorization:");
        System.out.println("1. Send URL to shop owner via email/notification");
        System.out.println("2. Display in admin panel for staff to complete");
        System.out.println("3. Use automated browser (Selenium) for testing");
        System.out.println("=".repeat(80));

        return request;
    }

    /**
     * STEP 2: Handle OAuth Callback (Automatic)
     *
     * This method is called when Shopee redirects to your backend callback URL.
     * It runs automatically - no manual intervention needed.
     *
     * @param code Authorization code from Shopee
     * @param shopId Shop ID from Shopee
     * @return Success status
     */
    public static Map<String, Object> handleAuthorizationCallback(String code, long shopId) throws Exception {
        System.out.println("\n=== OAUTH CALLBACK RECEIVED (AUTOMATIC) ===");
        System.out.println("Code: " + code);
        System.out.println("Shop ID: " + shopId);

        // Exchange code for tokens
        String tokenResponse = ShopeeAuth.getAccessToken(shopId, code);

        // Validate response
        if (!ShopeeAuth.isTokenResponseSuccess(tokenResponse)) {
            System.err.println("❌ Token exchange failed");
            return Map.of("success", false, "error", "Token exchange failed");
        }

        // Parse tokens
        String accessToken = extractJsonValue(tokenResponse, "access_token");
        String refreshToken = extractJsonValue(tokenResponse, "refresh_token");
        String expiresInStr = extractJsonValue(tokenResponse, "expire_in");
        long expiresIn = expiresInStr != null ? Long.parseLong(expiresInStr) : 14400;

        // Save to storage (database in production)
        ShopeeTokenStorage.saveTokens(shopId, accessToken, refreshToken, expiresIn);

        // Mark any pending authorization as completed
        for (AuthorizationRequest req : pendingAuthorizations.values()) {
            if (req.shopId == shopId || req.shopId == 0) {
                req.status = "completed";
            }
        }

        System.out.println("✓ Authorization completed successfully!");
        System.out.println("✓ Tokens saved for shop_id: " + shopId);
        System.out.println("=".repeat(80));

        return Map.of(
            "success", true,
            "shop_id", shopId,
            "access_token", accessToken,
            "expires_in", expiresIn
        );
    }

    /**
     * STEP 3: Fetch Orders (Fully Automated)
     *
     * This method fetches orders using stored tokens.
     * No manual intervention - everything is automatic.
     *
     * @param shopId Shop ID
     * @return Order list response
     */
    public static String fetchOrders(long shopId) throws Exception {
        System.out.println("\n=== FETCHING ORDERS (AUTOMATIC) ===");
        System.out.println("Shop ID: " + shopId);

        // Check if authorized
        if (!ShopeeTokenStorage.hasTokens(shopId)) {
            throw new Exception("Shop not authorized. Please complete authorization first.");
        }

        // Fetch orders (auto-refreshes token if needed)
        String orders = ShopeeOrderAPI.getOrderListWithStoredToken(shopId);

        System.out.println("✓ Orders fetched successfully!");
        System.out.println("=".repeat(80));

        return orders;
    }

    /**
     * Check authorization status
     */
    public static Map<String, Object> checkAuthorizationStatus(String requestId) {
        AuthorizationRequest request = pendingAuthorizations.get(requestId);

        if (request == null) {
            return Map.of("status", "not_found");
        }

        if (request.isExpired() && !request.status.equals("completed")) {
            request.status = "expired";
        }

        return Map.of(
            "request_id", request.requestId,
            "status", request.status,
            "shop_id", request.shopId,
            "auth_url", request.authUrl,
            "created_at", request.createdAt
        );
    }

    /**
     * Get all shops that are currently authorized
     */
    public static Map<Long, Map<String, Object>> getAuthorizedShops() {
        Map<Long, Map<String, Object>> result = new HashMap<>();

        // This would query your database in production
        // For now, we'll check the token storage
        System.out.println("\n=== AUTHORIZED SHOPS ===");

        // You would implement this based on your storage mechanism
        System.out.println("Check ShopeeTokenStorage for authorized shops");

        return result;
    }

    /**
     * Revoke authorization (remove tokens)
     */
    public static boolean revokeAuthorization(long shopId) {
        // In production, delete from database
        System.out.println("\n=== REVOKING AUTHORIZATION ===");
        System.out.println("Shop ID: " + shopId);
        System.out.println("Removing stored tokens...");

        // You would implement token deletion here
        return true;
    }

    // Helper methods

    private static String generateRequestId() {
        return "auth_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }

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
}
