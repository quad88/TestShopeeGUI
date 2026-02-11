package com.example.testshopgui;

/**
 * Shopee Order API Service
 * Handles order-related API calls
 */
public class ShopeeOrderAPI {

    /**
     * Get order list from Shopee API
     * @param shopId The shop ID
     * @param accessToken The access token for authentication
     * @return JSON response containing order list
     */
    public static String getOrderList(long shopId, String accessToken) throws Exception {
        long timestamp = ShopeeSignature.getCurrentTimestamp();

        // Generate signature dynamically
        String sign = ShopeeSignature.generateShopSignature(
            ShopeeConfig.ORDER_LIST_PATH, timestamp, accessToken, shopId);

        // Build URL with all parameters
        String url = buildOrderListUrl(shopId, accessToken, timestamp, sign);

        // Log request details
        System.out.println("Request Details:");
        System.out.println("  Timestamp: " + timestamp);
        System.out.println("  Signature: " + sign);
        System.out.println("  (Signature is generated dynamically)\n");

        // Make API call
        String response = ShopeeHttpClient.get(url);

        // Display response
        System.out.println("Order List Response:");
        System.out.println(response);

        return response;
    }

    /**
     * Get order list using stored access token (auto-refresh if needed)
     * This is the recommended method for backend applications
     * @param shopId The shop ID
     * @return JSON response containing order list
     */
    public static String getOrderListWithStoredToken(long shopId) throws Exception {
        System.out.println("--- Fetching orders for shop_id: " + shopId + " ---");

        // Get valid access token from storage (auto-refreshes if expired)
        String accessToken = ShopeeTokenStorage.getAccessToken(shopId);

        System.out.println("Using access token: " + accessToken);
        System.out.println();

        // Call API with token
        return getOrderList(shopId, accessToken);
    }

    /**
     * Build complete URL for order list API with all parameters
     */
    private static String buildOrderListUrl(long shopId, String accessToken, long timestamp, String sign) {
        // Time range parameters - Last 15 days from now (Shopee API max: 15 days)
        long currentTime = System.currentTimeMillis() / 1000L;
        long timeFrom = currentTime - (15 * 24 * 60 * 60); // 15 days ago
        long timeTo = currentTime;

        return ShopeeConfig.HOST + ShopeeConfig.ORDER_LIST_PATH +
                "?partner_id=" + ShopeeConfig.PARTNER_ID +
                "&sign=" + sign +
                "&timestamp=" + timestamp +
                "&shop_id=" + shopId +
                "&access_token=" + accessToken +
                "&cursor=" +
                "&page_size=20" +
                "&time_range_field=create_time" +
                "&time_from=" + timeFrom +
                "&time_to=" + timeTo +
                "&order_status=READY_TO_SHIP" +
                "&response_optional_fields=order_status" +
                "&request_order_status_pending=true" +
                "&logistics_channel_id=91007";
    }
}
