package com.example.testshopgui;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Lazada Token Storage
 * Manages storage and retrieval of Lazada access tokens and refresh tokens
 */
public class LazadaTokenStorage {
    private static final String STORAGE_FILE = "lazada_tokens.properties";
    private static final String STORAGE_DIR = System.getProperty("user.home") + File.separator + ".testshopgui";

    /**
     * Token data structure
     */
    public static class TokenData {
        public String accessToken;
        public String refreshToken;
        public long expiresIn;  // seconds until token expires
        public long refreshExpiresIn;  // seconds until refresh token expires
        public long createdAt;  // timestamp when token was created
        public String sellerId;

        public TokenData(String accessToken, String refreshToken, long expiresIn,
                        long refreshExpiresIn, String sellerId) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.expiresIn = expiresIn;
            this.refreshExpiresIn = refreshExpiresIn;
            this.createdAt = System.currentTimeMillis();
            this.sellerId = sellerId;
        }

        /**
         * Check if access token is expired or about to expire
         */
        public boolean isAccessTokenExpired() {
            long currentTime = System.currentTimeMillis();
            long elapsedSeconds = (currentTime - createdAt) / 1000;
            // Consider expired if within buffer time
            return elapsedSeconds >= (expiresIn - LazadaConfig.TOKEN_EXPIRY_BUFFER_SECONDS);
        }

        /**
         * Check if refresh token is expired
         */
        public boolean isRefreshTokenExpired() {
            long currentTime = System.currentTimeMillis();
            long elapsedSeconds = (currentTime - createdAt) / 1000;
            return elapsedSeconds >= refreshExpiresIn;
        }
    }

    /**
     * Save token data to file
     */
    public static void saveToken(TokenData tokenData) throws IOException {
        // Create directory if not exists
        File dir = new File(STORAGE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Create properties
        Properties props = new Properties();
        props.setProperty("access_token", tokenData.accessToken);
        props.setProperty("refresh_token", tokenData.refreshToken);
        props.setProperty("expires_in", String.valueOf(tokenData.expiresIn));
        props.setProperty("refresh_expires_in", String.valueOf(tokenData.refreshExpiresIn));
        props.setProperty("created_at", String.valueOf(tokenData.createdAt));
        props.setProperty("seller_id", tokenData.sellerId);

        // Save to file
        File file = new File(STORAGE_DIR, STORAGE_FILE);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            props.store(fos, "Lazada Token Storage");
        }

        System.out.println("Token saved to: " + file.getAbsolutePath());
    }

    /**
     * Load token data from file
     */
    public static TokenData loadToken() throws IOException {
        File file = new File(STORAGE_DIR, STORAGE_FILE);

        if (!file.exists()) {
            return null;
        }

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
        }

        TokenData tokenData = new TokenData(
                props.getProperty("access_token"),
                props.getProperty("refresh_token"),
                Long.parseLong(props.getProperty("expires_in", "0")),
                Long.parseLong(props.getProperty("refresh_expires_in", "0")),
                props.getProperty("seller_id", "")
        );
        tokenData.createdAt = Long.parseLong(props.getProperty("created_at", "0"));

        System.out.println("Token loaded from: " + file.getAbsolutePath());
        return tokenData;
    }

    /**
     * Delete stored token
     */
    public static void deleteToken() {
        File file = new File(STORAGE_DIR, STORAGE_FILE);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("Token deleted: " + file.getAbsolutePath());
            } else {
                System.err.println("Failed to delete token: " + file.getAbsolutePath());
            }
        }
    }

    /**
     * Check if token exists
     */
    public static boolean hasToken() {
        File file = new File(STORAGE_DIR, STORAGE_FILE);
        return file.exists();
    }
}
