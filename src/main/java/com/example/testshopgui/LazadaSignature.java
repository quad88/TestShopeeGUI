package com.example.testshopgui;

import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Lazada Signature Generator
 * Handles signature generation for Lazada API requests
 * Lazada uses HMAC-SHA256 for request signing
 */
public class LazadaSignature {

    /**
     * Generate signature for Lazada API request
     * @param path API path (e.g., "/auth/token/create")
     * @param params All request parameters (excluding sign)
     * @param appSecret The app secret key
     * @return Generated signature
     */
    public static String generateSignature(String path, Map<String, String> params, String appSecret) throws Exception {
        // Sort parameters by key
        TreeMap<String, String> sortedParams = new TreeMap<>(params);

        // Build concatenated string
        StringBuilder sb = new StringBuilder();
        sb.append(path);

        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            sb.append(entry.getKey());
            sb.append(entry.getValue());
        }

        String stringToSign = sb.toString();

        // Generate HMAC-SHA256 signature
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(secretKey);
        byte[] hmacBytes = hmacSHA256.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));

        // Convert to uppercase hex string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hmacBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString().toUpperCase();
    }

    /**
     * Get current timestamp in milliseconds
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * Build URL with signature
     * @param baseUrl The base API URL
     * @param path The API path
     * @param params Request parameters
     * @param appSecret The app secret
     * @return Full URL with signature
     */
    public static String buildSignedUrl(String baseUrl, String path, Map<String, String> params, String appSecret) throws Exception {
        // Generate signature
        String signature = generateSignature(path, params, appSecret);

        // Build URL
        StringBuilder url = new StringBuilder(baseUrl);
        url.append(path);
        url.append("?");

        // Add all parameters
        for (Map.Entry<String, String> entry : params.entrySet()) {
            url.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        // Add signature
        url.append("sign=").append(signature);

        return url.toString();
    }
}
