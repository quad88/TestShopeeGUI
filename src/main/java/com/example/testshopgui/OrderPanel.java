package com.example.testshopgui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Order Panel
 * Fetches and displays orders from Shopee API
 */
public class OrderPanel {
    private VBox panel;
    private TextField shopIdField;
    private TextArea orderArea;
    private Label statusLabel;

    public OrderPanel() {
        createPanel();
    }

    private void createPanel() {
        panel = new VBox(15);
        panel.setPadding(new Insets(20));

        // Title
        Label title = new Label("📦 Order Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Instructions
        Label instructions = new Label(
            "💡 Fetch orders from authorized shops. " +
            "Make sure the shop is authorized in the Authorization tab first."
        );
        instructions.setWrapText(true);
        instructions.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        // Form
        HBox formBox = new HBox(10);
        formBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label shopIdLabel = new Label("Shop ID:");
        shopIdField = new TextField(String.valueOf(RuntimeConfig.getDefaultShopId()));
        shopIdField.setPrefWidth(150);

        Button fetchBtn = new Button("📥 Fetch Orders");
        fetchBtn.setOnAction(e -> fetchOrders());
        fetchBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        Button refreshTokenBtn = new Button("🔄 Refresh Token");
        refreshTokenBtn.setOnAction(e -> refreshToken());

        Button clearBtn = new Button("🗑️ Clear");
        clearBtn.setOnAction(e -> orderArea.clear());

        formBox.getChildren().addAll(shopIdLabel, shopIdField, fetchBtn, refreshTokenBtn, clearBtn);

        // Status
        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        // Order display area
        orderArea = new TextArea();
        orderArea.setEditable(false);
        orderArea.setWrapText(true);
        orderArea.setPrefHeight(400);
        orderArea.setPromptText("Orders will appear here...");

        VBox.setVgrow(orderArea, Priority.ALWAYS);

        panel.getChildren().addAll(
            title,
            instructions,
            new Separator(),
            formBox,
            statusLabel,
            orderArea
        );
    }

    public VBox getPanel() {
        return panel;
    }

    private void fetchOrders() {
        try {
            long shopId = Long.parseLong(shopIdField.getText().trim());

            if (!ShopeeTokenStorage.hasTokens(shopId)) {
                showError("Shop Not Authorized",
                    new Exception("Shop " + shopId + " is not authorized yet. " +
                    "Please authorize it in the Authorization tab first."));
                return;
            }

            statusLabel.setText("⏳ Fetching orders for shop " + shopId + "...");
            orderArea.setText("Loading orders...\n");

            // Fetch orders in background to avoid blocking UI
            new Thread(() -> {
                try {
                    String orders = ShopeeOrderAPI.getOrderListWithStoredToken(shopId);

                    // Update UI on JavaFX thread
                    javafx.application.Platform.runLater(() -> {
                        orderArea.setText(formatOrderResponse(orders));
                        statusLabel.setText("✓ Orders fetched successfully at " + new java.util.Date());
                    });

                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> {
                        showError("Error Fetching Orders", ex);
                        statusLabel.setText("❌ Error fetching orders");
                    });
                }
            }).start();

        } catch (NumberFormatException ex) {
            showError("Invalid Shop ID", new Exception("Please enter a valid numeric Shop ID"));
        }
    }

    private void refreshToken() {
        try {
            long shopId = Long.parseLong(shopIdField.getText().trim());

            ShopeeTokenStorage.TokenData tokenData = ShopeeTokenStorage.getTokenData(shopId);
            if (tokenData == null) {
                showError("No Tokens Found",
                    new Exception("No tokens found for shop " + shopId));
                return;
            }

            statusLabel.setText("⏳ Refreshing token for shop " + shopId + "...");
            orderArea.setText("Refreshing access token...\n");

            new Thread(() -> {
                try {
                    String response = ShopeeAuth.refreshAccessToken(shopId, tokenData.getRefreshToken());

                    // Parse and save new tokens
                    String newAccessToken = extractJsonValue(response, "access_token");
                    String newRefreshToken = extractJsonValue(response, "refresh_token");
                    String expiresInStr = extractJsonValue(response, "expire_in");

                    if (newAccessToken != null && newRefreshToken != null && expiresInStr != null) {
                        long expiresIn = Long.parseLong(expiresInStr);
                        ShopeeTokenStorage.saveTokens(shopId, newAccessToken, newRefreshToken, expiresIn);

                        javafx.application.Platform.runLater(() -> {
                            orderArea.setText("✅ Token refreshed successfully!\n\n" +
                                "New Access Token: " + newAccessToken.substring(0, Math.min(20, newAccessToken.length())) + "...\n" +
                                "Expires in: " + expiresIn + " seconds");
                            statusLabel.setText("✓ Token refreshed at " + new java.util.Date());
                        });
                    } else {
                        throw new Exception("Failed to parse token response");
                    }

                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> {
                        showError("Error Refreshing Token", ex);
                        statusLabel.setText("❌ Error refreshing token");
                    });
                }
            }).start();

        } catch (NumberFormatException ex) {
            showError("Invalid Shop ID", new Exception("Please enter a valid numeric Shop ID"));
        }
    }

    private String formatOrderResponse(String json) {
        // Basic formatting for better readability
        try {
            // Add line breaks after commas and braces for readability
            String formatted = json
                .replace("{", "{\n  ")
                .replace("}", "\n}")
                .replace(",", ",\n  ")
                .replace("[", "[\n    ")
                .replace("]", "\n  ]");

            return "Order Response:\n" +
                   "=".repeat(80) + "\n" +
                   formatted + "\n" +
                   "=".repeat(80);
        } catch (Exception e) {
            return json;
        }
    }

    private String extractJsonValue(String json, String key) {
        try {
            String searchKey = "\"" + key + "\":\"";
            int startIndex = json.indexOf(searchKey);
            if (startIndex == -1) {
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

    private void showError(String title, Exception ex) {
        orderArea.setText("❌ " + title + "\n\n" + ex.getMessage());

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(ex.getMessage());
        alert.showAndWait();
    }
}
