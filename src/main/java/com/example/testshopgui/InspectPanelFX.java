package com.example.testshopgui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * Inspect Panel (JavaFX)
 * Displays detailed information about API calls including tokens, signatures, timestamps, and cURL commands
 */
public class InspectPanelFX {
    private VBox panel;
    private TextArea inspectArea;
    private ComboBox<String> apiEndpointCombo;
    private TextField shopIdField;
    private String currentCurlCommand = "";
    private TabPane mainTabPane;
    private Tab manualTab;
    private Tab liveTab;
    private TextArea liveRequestArea;
    private Button liveCopyCurlButton;

    public InspectPanelFX() {
        createPanel();
        // Register this panel with the logger
        ApiRequestLogger.setInspectPanel(this);
    }

    private void createPanel() {
        panel = new VBox(15);
        panel.setPadding(new Insets(20));

        // Title
        Label title = new Label("🔍 API Inspector");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Create tab pane
        mainTabPane = new TabPane();

        // Tab 1: Manual Inspector (existing functionality)
        manualTab = new Tab("Manual Inspector");
        manualTab.setClosable(false);
        manualTab.setContent(createManualInspectorTab());

        // Tab 2: Live API Requests
        liveTab = new Tab("Live API Requests");
        liveTab.setClosable(false);
        liveTab.setContent(createLiveRequestsTab());

        mainTabPane.getTabs().addAll(manualTab, liveTab);
        VBox.setVgrow(mainTabPane, Priority.ALWAYS);

        panel.getChildren().addAll(title, mainTabPane);
    }

    private VBox createManualInspectorTab() {
        VBox tabContent = new VBox(15);
        tabContent.setPadding(new Insets(10));

        // Control panel
        HBox controlPanel = new HBox(10);
        controlPanel.setPadding(new Insets(10, 0, 10, 0));

        Label endpointLabel = new Label("API Endpoint:");
        apiEndpointCombo = new ComboBox<>();
        apiEndpointCombo.getItems().addAll(
            "Generate Auth URL",
            "Get Access Token",
            "Refresh Access Token",
            "Get Order List"
        );
        apiEndpointCombo.setValue("Generate Auth URL");
        apiEndpointCombo.setPrefWidth(200);

        Label shopIdLabel = new Label("Shop ID:");
        shopIdField = new TextField(String.valueOf(ShopeeConfig.SHOP_ID));
        shopIdField.setPrefWidth(150);

        Button generateButton = new Button("Generate Inspect");
        generateButton.setOnAction(e -> generateInspectData());
        generateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        Button copyAllButton = new Button("Copy All");
        copyAllButton.setOnAction(e -> copyToClipboard(inspectArea.getText()));

        Button copyCurlButton = new Button("Copy cURL");
        copyCurlButton.setOnAction(e -> copyToClipboard(currentCurlCommand));

        controlPanel.getChildren().addAll(
            endpointLabel, apiEndpointCombo,
            shopIdLabel, shopIdField,
            generateButton, copyAllButton, copyCurlButton
        );

        // Inspect area
        inspectArea = new TextArea();
        inspectArea.setEditable(false);
        inspectArea.setWrapText(true);
        inspectArea.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 11px;");
        VBox.setVgrow(inspectArea, Priority.ALWAYS);

        // Info label
        Label infoLabel = new Label("💡 Generate and inspect API requests manually");
        infoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        tabContent.getChildren().addAll(controlPanel, inspectArea, infoLabel);
        return tabContent;
    }

    private VBox createLiveRequestsTab() {
        VBox tabContent = new VBox(15);
        tabContent.setPadding(new Insets(10));

        // Control panel for live requests
        HBox controlPanel = new HBox(10);
        controlPanel.setPadding(new Insets(10, 0, 10, 0));

        Label statusLabel = new Label("📡 Live API Request Monitor");
        statusLabel.setStyle("-fx-font-weight: bold;");

        Button refreshButton = new Button("🔄 Refresh");
        refreshButton.setOnAction(e -> refreshLiveRequest());

        Button copyAllButton = new Button("Copy All");
        copyAllButton.setOnAction(e -> copyToClipboard(liveRequestArea.getText()));

        liveCopyCurlButton = new Button("Copy cURL");
        liveCopyCurlButton.setOnAction(e -> {
            ApiRequestLogger.ApiRequest latest = ApiRequestLogger.getLatestRequest();
            if (latest != null) {
                copyToClipboard(latest.generateCurl());
            }
        });

        Button clearButton = new Button("Clear History");
        clearButton.setOnAction(e -> {
            ApiRequestLogger.clear();
            liveRequestArea.setText("History cleared. Waiting for API requests...\n");
        });

        controlPanel.getChildren().addAll(statusLabel, refreshButton, copyAllButton, liveCopyCurlButton, clearButton);

        // Live request area
        liveRequestArea = new TextArea();
        liveRequestArea.setEditable(false);
        liveRequestArea.setWrapText(true);
        liveRequestArea.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 11px;");
        liveRequestArea.setText("Waiting for API requests...\n\nMake an API call (e.g., fetch orders from Shopee or Lazada)\nand the request details will appear here automatically.");
        VBox.setVgrow(liveRequestArea, Priority.ALWAYS);

        // Info label
        Label infoLabel = new Label("💡 Real-time API requests are logged here automatically with cURL commands");
        infoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        tabContent.getChildren().addAll(controlPanel, liveRequestArea, infoLabel);
        return tabContent;
    }

    /**
     * Update the live requests tab with the latest API request
     * Called by ApiRequestLogger
     */
    public void updateWithApiRequest(ApiRequestLogger.ApiRequest request) {
        if (request != null && liveRequestArea != null) {
            liveRequestArea.setText(request.getFormattedLog());
            liveRequestArea.setScrollTop(0);
        }
    }

    private void refreshLiveRequest() {
        ApiRequestLogger.ApiRequest latest = ApiRequestLogger.getLatestRequest();
        if (latest != null) {
            updateWithApiRequest(latest);
        } else {
            liveRequestArea.setText("No API requests logged yet.\n\nMake an API call to see it here.");
        }
    }

    private void generateInspectData() {
        try {
            long shopId = Long.parseLong(shopIdField.getText().trim());
            String selected = apiEndpointCombo.getValue();

            StringBuilder sb = new StringBuilder();
            currentCurlCommand = "";

            switch (selected) {
                case "Generate Auth URL":
                    generateAuthUrlInspect(sb);
                    break;
                case "Get Access Token":
                    generateAccessTokenInspect(sb, shopId);
                    break;
                case "Refresh Access Token":
                    generateRefreshTokenInspect(sb, shopId);
                    break;
                case "Get Order List":
                    generateOrderListInspect(sb, shopId);
                    break;
            }

            inspectArea.setText(sb.toString());
            inspectArea.setScrollTop(0);

        } catch (Exception e) {
            inspectArea.setText("Error: " + e.getMessage() + "\n\n" + getStackTrace(e));
        }
    }

    private void generateAuthUrlInspect(StringBuilder sb) throws Exception {
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("  SHOPEE API INSPECTOR - Generate Auth URL\n");
        sb.append("═══════════════════════════════════════════════════════════\n\n");

        long timestamp = ShopeeSignature.getCurrentTimestamp();
        String path = ShopeeConfig.AUTH_PARTNER_PATH;
        String sign = ShopeeSignature.generatePartnerSignature(path, timestamp);
        String redirectUrl = RuntimeConfig.getBackendCallbackUrl();

        sb.append("📋 CONFIGURATION\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append("Partner ID:      ").append(ShopeeConfig.PARTNER_ID).append("\n");
        sb.append("Partner Key:     ").append(ShopeeConfig.PARTNER_KEY).append("\n");
        sb.append("Host:            ").append(ShopeeConfig.HOST).append("\n");
        sb.append("Endpoint Path:   ").append(path).append("\n");
        sb.append("Redirect URL:    ").append(redirectUrl).append("\n\n");

        sb.append("⏰ TIMESTAMP\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append("Unix Timestamp:  ").append(timestamp).append("\n");
        sb.append("Human Readable:  ").append(new java.util.Date(timestamp * 1000)).append("\n\n");

        sb.append("🔐 SIGNATURE CALCULATION\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append("Base String:     ").append(ShopeeConfig.PARTNER_ID).append(path).append(timestamp).append("\n");
        sb.append("Algorithm:       HMAC-SHA256\n");
        sb.append("Secret Key:      ").append(ShopeeConfig.PARTNER_KEY).append("\n");
        sb.append("Signature:       ").append(sign).append("\n\n");

        String authUrl = ShopeeConfig.HOST + path +
                "?partner_id=" + ShopeeConfig.PARTNER_ID +
                "&timestamp=" + timestamp +
                "&sign=" + sign +
                "&redirect=" + redirectUrl;

        sb.append("🔗 GENERATED AUTH URL\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append(authUrl).append("\n\n");

        sb.append("📝 URL PARAMETERS\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append("partner_id:      ").append(RuntimeConfig.getPartnerId()).append("\n");
        sb.append("timestamp:       ").append(timestamp).append("\n");
        sb.append("sign:            ").append(sign).append("\n");
        sb.append("redirect:        ").append(redirectUrl).append("\n\n");

        sb.append("🌐 OPEN IN BROWSER\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append("Copy the URL above and open it in a browser to authorize.\n");
        sb.append("After authorization, you'll receive a 'code' parameter.\n\n");

        currentCurlCommand = "# This is a browser redirect URL, not a cURL command\n# Open this URL in a browser:\n" + authUrl;
    }

    private void generateAccessTokenInspect(StringBuilder sb, long shopId) throws Exception {
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("  SHOPEE API INSPECTOR - Get Access Token\n");
        sb.append("═══════════════════════════════════════════════════════════\n\n");

        long timestamp = ShopeeSignature.getCurrentTimestamp();
        String path = ShopeeConfig.AUTH_TOKEN_PATH;
        String sign = ShopeeSignature.generatePartnerSignature(path, timestamp);
        String code = "SAMPLE_AUTH_CODE_HERE";

        sb.append("📋 CONFIGURATION\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append("Partner ID:      ").append(RuntimeConfig.getPartnerId()).append("\n");
        sb.append("Partner Key:     ").append(RuntimeConfig.getPartnerKey()).append("\n");
        sb.append("Host:            ").append(RuntimeConfig.getApiHost()).append("\n");
        sb.append("Endpoint Path:   ").append(path).append("\n");
        sb.append("Shop ID:         ").append(shopId).append("\n");
        sb.append("Auth Code:       ").append(code).append(" (REPLACE WITH ACTUAL CODE)\n\n");

        sb.append("⏰ TIMESTAMP\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append("Unix Timestamp:  ").append(timestamp).append("\n");
        sb.append("Human Readable:  ").append(new java.util.Date(timestamp * 1000)).append("\n\n");

        sb.append("🔐 SIGNATURE CALCULATION\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append("Base String:     ").append(RuntimeConfig.getPartnerId()).append(path).append(timestamp).append("\n");
        sb.append("Algorithm:       HMAC-SHA256\n");
        sb.append("Secret Key:      ").append(RuntimeConfig.getPartnerKey()).append("\n");
        sb.append("Signature:       ").append(sign).append("\n\n");

        String url = RuntimeConfig.getApiHost() + path +
                "?partner_id=" + RuntimeConfig.getPartnerId() +
                "&timestamp=" + timestamp +
                "&sign=" + sign;

        String jsonBody = String.format("{\"code\":\"%s\",\"shop_id\":%d,\"partner_id\":%d}",
                code, shopId, RuntimeConfig.getPartnerId());

        sb.append("🌐 API REQUEST\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append("Method:          POST\n");
        sb.append("URL:             ").append(url).append("\n");
        sb.append("Content-Type:    application/json\n\n");

        sb.append("📤 REQUEST BODY (JSON)\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append(jsonBody).append("\n\n");

        sb.append("📝 URL PARAMETERS\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append("partner_id:      ").append(RuntimeConfig.getPartnerId()).append("\n");
        sb.append("timestamp:       ").append(timestamp).append("\n");
        sb.append("sign:            ").append(sign).append("\n\n");

        sb.append("💾 STORED TOKENS (from storage)\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        try {
            ShopeeTokenStorage.TokenData tokenData = ShopeeTokenStorage.getTokenData(shopId);
            if (tokenData != null) {
                sb.append("Access Token:    ").append(tokenData.getAccessToken()).append("\n");
                sb.append("Refresh Token:   ").append(tokenData.getRefreshToken()).append("\n");
                sb.append("Expires At:      ").append(tokenData.getExpiresAt())
                    .append(" (").append(new java.util.Date(tokenData.getExpiresAt() * 1000)).append(")\n");
            } else {
                sb.append("No tokens found for shop ").append(shopId).append("\n");
            }
        } catch (Exception e) {
            sb.append("Error reading tokens: ").append(e.getMessage()).append("\n");
        }
        sb.append("\n");

        currentCurlCommand = generateCurlCommand("POST", url, jsonBody);
        sb.append("📋 cURL COMMAND (for Postman)\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append(currentCurlCommand).append("\n");
    }

    private void generateRefreshTokenInspect(StringBuilder sb, long shopId) throws Exception {
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("  SHOPEE API INSPECTOR - Refresh Access Token\n");
        sb.append("═══════════════════════════════════════════════════════════\n\n");

        long timestamp = ShopeeSignature.getCurrentTimestamp();
        String path = ShopeeConfig.AUTH_REFRESH_PATH;
        String sign = ShopeeSignature.generatePartnerSignature(path, timestamp);

        String refreshToken = "SAMPLE_REFRESH_TOKEN_HERE";
        try {
            ShopeeTokenStorage.TokenData tokenData = ShopeeTokenStorage.getTokenData(shopId);
            if (tokenData != null && tokenData.getRefreshToken() != null) {
                refreshToken = tokenData.getRefreshToken();
            }
        } catch (Exception e) {
            // Use placeholder
        }

        sb.append("📋 CONFIGURATION\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append("Partner ID:      ").append(ShopeeConfig.PARTNER_ID).append("\n");
        sb.append("Partner Key:     ").append(ShopeeConfig.PARTNER_KEY).append("\n");
        sb.append("Host:            ").append(ShopeeConfig.HOST).append("\n");
        sb.append("Endpoint Path:   ").append(path).append("\n");
        sb.append("Shop ID:         ").append(shopId).append("\n");
        sb.append("Refresh Token:   ").append(refreshToken).append("\n\n");

        sb.append("⏰ TIMESTAMP\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append("Unix Timestamp:  ").append(timestamp).append("\n");
        sb.append("Human Readable:  ").append(new java.util.Date(timestamp * 1000)).append("\n\n");

        sb.append("🔐 SIGNATURE CALCULATION\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append("Base String:     ").append(ShopeeConfig.PARTNER_ID).append(path).append(timestamp).append("\n");
        sb.append("Algorithm:       HMAC-SHA256\n");
        sb.append("Secret Key:      ").append(ShopeeConfig.PARTNER_KEY).append("\n");
        sb.append("Signature:       ").append(sign).append("\n\n");

        String url = ShopeeConfig.HOST + path +
                "?partner_id=" + ShopeeConfig.PARTNER_ID +
                "&timestamp=" + timestamp +
                "&sign=" + sign;

        String jsonBody = String.format("{\"refresh_token\":\"%s\",\"shop_id\":%d,\"partner_id\":%d}",
                refreshToken, shopId, ShopeeConfig.PARTNER_ID);

        sb.append("🌐 API REQUEST\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append("Method:          POST\n");
        sb.append("URL:             ").append(url).append("\n");
        sb.append("Content-Type:    application/json\n\n");

        sb.append("📤 REQUEST BODY (JSON)\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append(jsonBody).append("\n\n");

        sb.append("📝 URL PARAMETERS\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append("partner_id:      ").append(RuntimeConfig.getPartnerId()).append("\n");
        sb.append("timestamp:       ").append(timestamp).append("\n");
        sb.append("sign:            ").append(sign).append("\n\n");

        sb.append("💾 STORED TOKENS (from storage)\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        try {
            ShopeeTokenStorage.TokenData tokenData = ShopeeTokenStorage.getTokenData(shopId);
            if (tokenData != null) {
                sb.append("Access Token:    ").append(tokenData.getAccessToken()).append("\n");
                sb.append("Refresh Token:   ").append(tokenData.getRefreshToken()).append("\n");
                sb.append("Expires At:      ").append(tokenData.getExpiresAt())
                    .append(" (").append(new java.util.Date(tokenData.getExpiresAt() * 1000)).append(")\n");
            } else {
                sb.append("No tokens found for shop ").append(shopId).append("\n");
            }
        } catch (Exception e) {
            sb.append("Error reading tokens: ").append(e.getMessage()).append("\n");
        }
        sb.append("\n");

        currentCurlCommand = generateCurlCommand("POST", url, jsonBody);
        sb.append("📋 cURL COMMAND (for Postman)\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append(currentCurlCommand).append("\n");
    }

    private void generateOrderListInspect(StringBuilder sb, long shopId) throws Exception {
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("  SHOPEE API INSPECTOR - Get Order List\n");
        sb.append("═══════════════════════════════════════════════════════════\n\n");

        long timestamp = ShopeeSignature.getCurrentTimestamp();
        String path = ShopeeConfig.ORDER_LIST_PATH;

        String accessToken = "SAMPLE_ACCESS_TOKEN_HERE";
        try {
            accessToken = ShopeeTokenStorage.getAccessToken(shopId);
        } catch (Exception e) {
            // Use placeholder
        }

        String sign = ShopeeSignature.generateShopSignature(path, timestamp, accessToken, shopId);

        sb.append("📋 CONFIGURATION\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append("Partner ID:      ").append(RuntimeConfig.getPartnerId()).append("\n");
        sb.append("Partner Key:     ").append(RuntimeConfig.getPartnerKey()).append("\n");
        sb.append("Host:            ").append(RuntimeConfig.getApiHost()).append("\n");
        sb.append("Endpoint Path:   ").append(path).append("\n");
        sb.append("Shop ID:         ").append(shopId).append("\n");
        sb.append("Access Token:    ").append(accessToken).append("\n\n");

        sb.append("⏰ TIMESTAMP\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append("Unix Timestamp:  ").append(timestamp).append("\n");
        sb.append("Human Readable:  ").append(new java.util.Date(timestamp * 1000)).append("\n\n");

        sb.append("🔐 SIGNATURE CALCULATION (Shop-Level)\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append("Base String:     ").append(RuntimeConfig.getPartnerId()).append(path).append(timestamp)
            .append(accessToken).append(shopId).append("\n");
        sb.append("Algorithm:       HMAC-SHA256\n");
        sb.append("Secret Key:      ").append(RuntimeConfig.getPartnerKey()).append("\n");
        sb.append("Signature:       ").append(sign).append("\n\n");

        long currentTime = System.currentTimeMillis() / 1000L;
        long timeFrom = currentTime - (15 * 24 * 60 * 60);
        long timeTo = currentTime;

        String url = RuntimeConfig.getApiHost() + path +
                "?partner_id=" + RuntimeConfig.getPartnerId() +
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
                "&logistics_channel_id=71001";

        sb.append("🌐 API REQUEST\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append("Method:          GET\n");
        sb.append("URL:             ").append(url).append("\n\n");

        sb.append("📝 URL PARAMETERS\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append("partner_id:                      ").append(RuntimeConfig.getPartnerId()).append("\n");
        sb.append("sign:                            ").append(sign).append("\n");
        sb.append("timestamp:                       ").append(timestamp).append("\n");
        sb.append("shop_id:                         ").append(shopId).append("\n");
        sb.append("access_token:                    ").append(accessToken).append("\n");
        sb.append("cursor:                          (empty - first page)\n");
        sb.append("page_size:                       20\n");
        sb.append("time_range_field:                create_time\n");
        sb.append("time_from:                       ").append(timeFrom).append(" (").append(new java.util.Date(timeFrom * 1000)).append(")\n");
        sb.append("time_to:                         ").append(timeTo).append(" (").append(new java.util.Date(timeTo * 1000)).append(")\n");
        sb.append("order_status:                    READY_TO_SHIP\n");
        sb.append("response_optional_fields:        order_status\n");
        sb.append("request_order_status_pending:    true\n");
        sb.append("logistics_channel_id:            71001\n\n");

        sb.append("💾 STORED TOKENS (from storage)\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        try {
            ShopeeTokenStorage.TokenData tokenData = ShopeeTokenStorage.getTokenData(shopId);
            if (tokenData != null) {
                sb.append("Access Token:    ").append(tokenData.getAccessToken()).append("\n");
                sb.append("Refresh Token:   ").append(tokenData.getRefreshToken()).append("\n");
                sb.append("Expires At:      ").append(tokenData.getExpiresAt())
                    .append(" (").append(new java.util.Date(tokenData.getExpiresAt() * 1000)).append(")\n");
            } else {
                sb.append("No tokens found for shop ").append(shopId).append("\n");
            }
        } catch (Exception e) {
            sb.append("Error reading tokens: ").append(e.getMessage()).append("\n");
        }
        sb.append("\n");

        currentCurlCommand = generateCurlCommand("GET", url, null);
        sb.append("📋 cURL COMMAND (for Postman)\n");
        sb.append("─────────────────────────────────────────────────────────\n");
        sb.append(currentCurlCommand).append("\n");
    }

    private String generateCurlCommand(String method, String url, String jsonBody) {
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

    private void copyToClipboard(String text) {
        try {
            ClipboardContent content = new ClipboardContent();
            content.putString(text);
            Clipboard.getSystemClipboard().setContent(content);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Copied to clipboard!");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to copy: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getClass().getName()).append(": ").append(e.getMessage()).append("\n");
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("  at ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    public VBox getPanel() {
        return panel;
    }
}
