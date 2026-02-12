package com.example.testshopgui;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;

/**
 * Shopee Signature Generation Utilities
 * Handles HMAC-SHA256 signature generation for API requests
 */
public class ShopeeSignature {

    /**
     * Generate HMAC-SHA256 signature for partner-level API
     * Format: partner_id + path + timestamp
     */
    public static String generatePartnerSignature(String path, long timestamp) throws Exception {
        String baseString = String.format("%s%s%s", RuntimeConfig.getPartnerId(), path, timestamp);
        return hmacSha256(baseString, RuntimeConfig.getPartnerKey());
    }

    /**
     * Generate HMAC-SHA256 signature for shop-level API
     * Format: partner_id + path + timestamp + access_token + shop_id
     */
    public static String generateShopSignature(String path, long timestamp, String accessToken, long shopId) throws Exception {
        String baseString = String.format("%s%s%s%s%s",
            RuntimeConfig.getPartnerId(), path, timestamp, accessToken, shopId);
        return hmacSha256(baseString, RuntimeConfig.getPartnerKey());
    }

    /**
     * Calculate HMAC-SHA256 hash
     */
    private static String hmacSha256(String data, String key) throws Exception {
        byte[] keyBytes = key.getBytes("UTF-8");
        byte[] dataBytes = data.getBytes("UTF-8");

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        mac.init(secretKey);

        byte[] hash = mac.doFinal(dataBytes);
        return String.format("%064x", new BigInteger(1, hash));
    }

    /**
     * Get current Unix timestamp in seconds
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis() / 1000L;
    }
}
