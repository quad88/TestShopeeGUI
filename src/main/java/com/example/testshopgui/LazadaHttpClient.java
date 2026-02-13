package com.example.testshopgui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Lazada HTTP Client
 * Handles all HTTP communication with Lazada API
 */
public class LazadaHttpClient {

    /**
     * Make HTTP GET request to Lazada API
     * @param urlString Full URL with parameters and signature
     * @return Response body as string
     */
    public static String get(String urlString) throws Exception {
        // Extract endpoint name from URL
        String endpoint = extractEndpoint(urlString);

        // Log the request
        ApiRequestLogger.logRequest("GET", urlString, null, "Lazada", endpoint);

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);

        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream(),
                        StandardCharsets.UTF_8
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
     * Make HTTP POST request to Lazada API
     * @param urlString Full URL with parameters and signature
     * @param jsonBody JSON body (optional, can be null)
     * @return Response body as string
     */
    public static String post(String urlString, String jsonBody) throws Exception {
        // Extract endpoint name from URL
        String endpoint = extractEndpoint(urlString);

        // Log the request
        ApiRequestLogger.logRequest("POST", urlString, jsonBody, "Lazada", endpoint);

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        // Write body if provided
        if (jsonBody != null && !jsonBody.isEmpty()) {
            conn.connect();
            OutputStream os = conn.getOutputStream();
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            os.close();
        }

        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream(),
                        StandardCharsets.UTF_8
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
            if (urlString.contains("/orders/get")) {
                return "Get Orders";
            } else if (urlString.contains("/order/items/get")) {
                return "Get Order Items";
            } else if (urlString.contains("/auth/token/create")) {
                return "Create Access Token";
            } else if (urlString.contains("/auth/token/refresh")) {
                return "Refresh Access Token";
            } else {
                // Extract last part of path
                String path = urlString.split("\\?")[0];
                String[] parts = path.split("/");
                return parts[parts.length - 1];
            }
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
