package com.example.testshopgui;

import java.util.HashMap;
import java.util.Map;

/**
 * Lazada Order API
 * Handles order-related API calls
 */
public class LazadaOrderAPI {

    /**
     * Get list of orders
     * @param accessToken The access token for authentication
     * @param createdAfter Filter orders created after this date (ISO 8601 format, optional)
     * @param createdBefore Filter orders created before this date (ISO 8601 format, optional)
     * @param status Order status filter (optional: pending, canceled, ready_to_ship, delivered, etc.)
     * @param offset Pagination offset (default: 0)
     * @param limit Number of orders to return (default: 20, max: 100)
     * @return JSON response containing order list
     */
    public static String getOrders(String accessToken, String createdAfter, String createdBefore,
                                   String status, int offset, int limit) throws Exception {
        long timestamp = LazadaSignature.getCurrentTimestamp();

        // Build parameters
        Map<String, String> params = new HashMap<>();
        params.put("app_key", LazadaConfig.APP_KEY);
        params.put("timestamp", String.valueOf(timestamp));
        params.put("sign_method", "sha256");
        params.put("access_token", accessToken);

        // Add optional filters
        if (createdAfter != null && !createdAfter.isEmpty()) {
            params.put("created_after", createdAfter);
        }
        if (createdBefore != null && !createdBefore.isEmpty()) {
            params.put("created_before", createdBefore);
        }
        if (status != null && !status.isEmpty()) {
            params.put("status", status);
        }
        params.put("offset", String.valueOf(offset));
        params.put("limit", String.valueOf(limit));

        // Build signed URL
        String url = LazadaSignature.buildSignedUrl(
                LazadaConfig.API_GATEWAY,
                LazadaConfig.ORDERS_GET_PATH,
                params,
                LazadaConfig.APP_SECRET
        );

        // Log request details
        System.out.println("=== Get Orders Request ===");
        System.out.println("Endpoint: " + LazadaConfig.ORDERS_GET_PATH);
        System.out.println("Timestamp: " + timestamp);
        System.out.println("Offset: " + offset + ", Limit: " + limit);
        if (status != null) System.out.println("Status Filter: " + status);
        System.out.println("==========================");

        // Make API call
        String response = LazadaHttpClient.get(url);

        // Display response
        System.out.println("Orders Response:");
        System.out.println(response);
        System.out.println();

        return response;
    }

    /**
     * Get list of orders with default pagination
     * @param accessToken The access token for authentication
     * @return JSON response containing order list
     */
    public static String getOrders(String accessToken) throws Exception {
        return getOrders(accessToken, null, null, null, 0, 20);
    }

    /**
     * Get order items for specific order IDs
     * @param accessToken The access token for authentication
     * @param orderIds Array of order IDs to retrieve items for
     * @return JSON response containing order items
     */
    public static String getOrderItems(String accessToken, long[] orderIds) throws Exception {
        long timestamp = LazadaSignature.getCurrentTimestamp();

        // Build order IDs string
        StringBuilder orderIdsStr = new StringBuilder();
        for (int i = 0; i < orderIds.length; i++) {
            orderIdsStr.append(orderIds[i]);
            if (i < orderIds.length - 1) {
                orderIdsStr.append(",");
            }
        }

        // Build parameters
        Map<String, String> params = new HashMap<>();
        params.put("app_key", LazadaConfig.APP_KEY);
        params.put("timestamp", String.valueOf(timestamp));
        params.put("sign_method", "sha256");
        params.put("access_token", accessToken);
        params.put("order_ids", orderIdsStr.toString());

        // Build signed URL
        String url = LazadaSignature.buildSignedUrl(
                LazadaConfig.API_GATEWAY,
                LazadaConfig.ORDER_ITEMS_GET_PATH,
                params,
                LazadaConfig.APP_SECRET
        );

        // Log request details
        System.out.println("=== Get Order Items Request ===");
        System.out.println("Endpoint: " + LazadaConfig.ORDER_ITEMS_GET_PATH);
        System.out.println("Order IDs: " + orderIdsStr);
        System.out.println("Timestamp: " + timestamp);
        System.out.println("================================");

        // Make API call
        String response = LazadaHttpClient.get(url);

        // Display response
        System.out.println("Order Items Response:");
        System.out.println(response);
        System.out.println();

        return response;
    }
}
