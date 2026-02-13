package com.example.testshopgui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Shopee HTTP Client
 * Handles all HTTP communication with Shopee API
 */
public class ShopeeHttpClient {

    /**
     * Make HTTP GET request to Shopee API
     */
    public static String get(String urlString) throws Exception {
        // Extract endpoint name from URL
        String endpoint = extractEndpoint(urlString);

        // Log the request
        ApiRequestLogger.logRequest("GET", urlString, null, "Shopee", endpoint);

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");

        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode + "\n");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        responseCode == 200 ? conn.getInputStream() : conn.getErrorStream()
                )
        );

        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String responseBody = response.toString();

        // Log the response
        ApiRequestLogger.logResponse(responseCode, responseBody);

        return responseBody;
    }

    /**
     * Make HTTP POST request with JSON body to Shopee API
     */
    public static String post(String urlString, String jsonBody) throws Exception {
        // Extract endpoint name from URL
        String endpoint = extractEndpoint(urlString);

        // Log the request
        ApiRequestLogger.logRequest("POST", urlString, jsonBody, "Shopee", endpoint);

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(10000);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        // Write JSON body
        conn.connect();
        java.io.PrintWriter out = new java.io.PrintWriter(conn.getOutputStream());
        out.print(jsonBody);
        out.flush();
        out.close();

        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode + "\n");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        responseCode == 200 ? conn.getInputStream() : conn.getErrorStream()
                )
        );

        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String responseBody = response.toString();

        // Log the response
        ApiRequestLogger.logResponse(responseCode, responseBody);

        return responseBody;
    }

    /**
     * Extract endpoint name from URL for logging
     */
    private static String extractEndpoint(String urlString) {
        try {
            if (urlString.contains("/order/get_order_list")) {
                return "Get Order List";
            } else if (urlString.contains("/auth/token/get")) {
                return "Get Access Token";
            } else if (urlString.contains("/auth/access_token/get")) {
                return "Refresh Access Token";
            } else if (urlString.contains("/auth/partner/get")) {
                return "Generate Auth URL";
            } else {
                // Extract last part of path
                String[] parts = urlString.split("/");
                return parts[parts.length - 1].split("\\?")[0];
            }
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
