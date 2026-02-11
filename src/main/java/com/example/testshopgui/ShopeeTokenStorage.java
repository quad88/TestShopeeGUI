package com.example.testshopgui;

import java.util.HashMap;
import java.util.Map;

/**
 * Shopee Token Storage
 * Simulates a database for storing access/refresh tokens
 * In production, replace this with actual database operations
 */
public class ShopeeTokenStorage {

    // In-memory storage (replace with database in production)
    private static final Map<Long, TokenData> tokenStore = new HashMap<>();

    /**
     * Token data model
     */
    public static class TokenData {
        private String accessToken;
        private String refreshToken;
        private long expiresAt;
        private long shopId;

        public TokenData(long shopId, String accessToken, String refreshToken, long expiresIn) {
            this.shopId = shopId;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.expiresAt = System.currentTimeMillis() / 1000L + expiresIn;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public long getExpiresAt() {
            return expiresAt;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() / 1000L >= expiresAt;
        }

        public long getShopId() {
            return shopId;
        }
    }

    /**
     * Save tokens to storage
     */
    public static void saveTokens(long shopId, String accessToken, String refreshToken, long expiresIn) {
        TokenData tokenData = new TokenData(shopId, accessToken, refreshToken, expiresIn);
        tokenStore.put(shopId, tokenData);

        System.out.println("✓ Tokens saved for shop_id: " + shopId);
        System.out.println("  Access Token: " + accessToken);
        System.out.println("  Refresh Token: " + refreshToken);
        System.out.println("  Expires in: " + expiresIn + " seconds\n");
    }

    /**
     * Get stored token data for a shop
     */
    public static TokenData getTokenData(long shopId) {
        return tokenStore.get(shopId);
    }

    /**
     * Get valid access token (auto-refresh if expired)
     */
    public static String getAccessToken(long shopId) throws Exception {
        TokenData tokenData = tokenStore.get(shopId);

        if (tokenData == null) {
            throw new Exception("No tokens found for shop_id: " + shopId +
                ". Please complete authorization first.");
        }

        // If token is expired, refresh it
        if (tokenData.isExpired()) {
            System.out.println("⚠ Access token expired. Refreshing...");
            String response = ShopeeAuth.refreshAccessToken(shopId, tokenData.getRefreshToken());

            // Parse and save new tokens (simplified - in production use JSON parser)
            // This is a simple extraction, you should use proper JSON parsing
            String newAccessToken = extractJsonValue(response, "access_token");
            String newRefreshToken = extractJsonValue(response, "refresh_token");
            String expiresInStr = extractJsonValue(response, "expire_in");

            if (newAccessToken != null && newRefreshToken != null && expiresInStr != null) {
                long expiresIn = Long.parseLong(expiresInStr);
                saveTokens(shopId, newAccessToken, newRefreshToken, expiresIn);
                return newAccessToken;
            } else {
                throw new Exception("Failed to refresh token");
            }
        }

        return tokenData.getAccessToken();
    }

    /**
     * Simple JSON value extractor (replace with proper JSON parser in production)
     */
    private static String extractJsonValue(String json, String key) {
        try {
            String searchKey = "\"" + key + "\":\"";
            int startIndex = json.indexOf(searchKey);
            if (startIndex == -1) {
                // Try without quotes (for numbers)
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
     * Check if tokens exist for a shop
     */
    public static boolean hasTokens(long shopId) {
        return tokenStore.containsKey(shopId);
    }

    /**
     * Clear all tokens (for testing)
     */
    public static void clearAll() {
        tokenStore.clear();
        System.out.println("✓ All tokens cleared");
    }

    /**
     * Get all registered shop IDs (for GUI display)
     */
    public static java.util.List<Long> getAllShopIds() {
        return new java.util.ArrayList<>(tokenStore.keySet());
    }

    /**
     * Get all shops with their info (for GUI table display)
     */
    public static Map<Long, ShopInfo> getAllShopsInfo() {
        Map<Long, ShopInfo> result = new HashMap<>();
        for (Map.Entry<Long, TokenData> entry : tokenStore.entrySet()) {
            TokenData data = entry.getValue();
            String status = data.isExpired() ? "⚠️ Expired" : "✅ Active";
            result.put(entry.getKey(), new ShopInfo(
                entry.getKey(),
                status,
                data.getAccessToken(),
                new java.util.Date(data.expiresAt * 1000L)
            ));
        }
        return result;
    }

    /**
     * Remove a shop from storage (for GUI management)
     */
    public static void removeShop(long shopId) {
        tokenStore.remove(shopId);
        System.out.println("✓ Shop " + shopId + " removed from storage");
    }

    /**
     * Shop info class for GUI display
     */
    public static class ShopInfo {
        private long shopId;
        private String status;
        private String token;
        private java.util.Date expiresAt;

        public ShopInfo(long shopId, String status, String token, java.util.Date expiresAt) {
            this.shopId = shopId;
            this.status = status;
            this.token = token;
            this.expiresAt = expiresAt;
        }

        public long getShopId() { return shopId; }
        public String getStatus() { return status; }
        public String getToken() { return token; }
        public java.util.Date getExpiresAt() { return expiresAt; }
    }
}
