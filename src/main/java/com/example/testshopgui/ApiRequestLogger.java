package com.example.testshopgui;

import javafx.application.Platform;
import java.util.ArrayList;
import java.util.List;

/**
 * API Request Logger
 * Captures and logs all API requests with CURL generation capability
 */
public class ApiRequestLogger {

    public static class ApiRequest {
        public String method;
        public String url;
        public String jsonBody;
        public long timestamp;
        public String platform; // "Shopee" or "Lazada"
        public String endpoint;
        public int responseCode;
        public String responseBody;

        public ApiRequest(String method, String url, String jsonBody, String platform, String endpoint) {
            this.method = method;
            this.url = url;
            this.jsonBody = jsonBody;
            this.platform = platform;
            this.endpoint = endpoint;
            this.timestamp = System.currentTimeMillis();
        }

        public String generateCurl() {
            StringBuilder curl = new StringBuilder();
            curl.append("curl -X ").append(method).append(" \\\n");
            curl.append("  \"").append(url).append("\"");

            if (jsonBody != null && !jsonBody.isEmpty()) {
                curl.append(" \\\n");
                curl.append("  -H \"Content-Type: application/json\" \\\n");
                curl.append("  -d '").append(jsonBody).append("'");
            }

            return curl.toString();
        }

        public String getFormattedLog() {
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════════════════════\n");
            sb.append("  ").append(platform).append(" API REQUEST - ").append(endpoint).append("\n");
            sb.append("═══════════════════════════════════════════════════════════\n\n");

            sb.append("⏰ TIMESTAMP\n");
            sb.append("─────────────────────────────────────────────────────────\n");
            sb.append("Time:            ").append(new java.util.Date(timestamp)).append("\n");
            sb.append("Unix:            ").append(timestamp / 1000).append("\n\n");

            sb.append("🌐 HTTP REQUEST\n");
            sb.append("─────────────────────────────────────────────────────────\n");
            sb.append("Method:          ").append(method).append("\n");
            sb.append("URL:             ").append(url).append("\n\n");

            if (jsonBody != null && !jsonBody.isEmpty()) {
                sb.append("📤 REQUEST BODY (JSON)\n");
                sb.append("─────────────────────────────────────────────────────────\n");
                sb.append(jsonBody).append("\n\n");
            }

            if (responseCode > 0) {
                sb.append("📥 RESPONSE\n");
                sb.append("─────────────────────────────────────────────────────────\n");
                sb.append("Status Code:     ").append(responseCode).append("\n");
                if (responseBody != null) {
                    sb.append("Response Body:\n").append(responseBody).append("\n\n");
                }
            }

            sb.append("📋 cURL COMMAND (for Postman)\n");
            sb.append("─────────────────────────────────────────────────────────\n");
            sb.append(generateCurl()).append("\n");

            return sb.toString();
        }
    }

    private static final List<ApiRequest> requests = new ArrayList<>();
    private static ApiRequest latestRequest = null;
    private static InspectPanelFX inspectPanel = null;

    public static void setInspectPanel(InspectPanelFX panel) {
        inspectPanel = panel;
    }

    public static void logRequest(String method, String url, String jsonBody, String platform, String endpoint) {
        ApiRequest request = new ApiRequest(method, url, jsonBody, platform, endpoint);
        requests.add(request);
        latestRequest = request;

        // Print to console
        System.out.println("\n" + request.getFormattedLog());

        // Update inspect panel if available
        if (inspectPanel != null) {
            Platform.runLater(() -> inspectPanel.updateWithApiRequest(request));
        }
    }

    public static void logResponse(int responseCode, String responseBody) {
        if (latestRequest != null) {
            latestRequest.responseCode = responseCode;
            latestRequest.responseBody = responseBody;

            // Update inspect panel with response
            if (inspectPanel != null) {
                Platform.runLater(() -> inspectPanel.updateWithApiRequest(latestRequest));
            }
        }
    }

    public static List<ApiRequest> getAllRequests() {
        return new ArrayList<>(requests);
    }

    public static ApiRequest getLatestRequest() {
        return latestRequest;
    }

    public static void clear() {
        requests.clear();
        latestRequest = null;
    }
}
