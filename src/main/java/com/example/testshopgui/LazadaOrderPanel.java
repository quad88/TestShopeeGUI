package com.example.testshopgui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Lazada Order Panel
 * Fetches and displays orders from Lazada API
 */
public class LazadaOrderPanel {
    private VBox panel;
    private TextField accessTokenField;
    private TextArea orderArea;
    private Label statusLabel;

    public LazadaOrderPanel() {
        createPanel();
    }

    private void createPanel() {
        panel = new VBox(15);
        panel.setPadding(new Insets(20));

        // Title
        Label title = new Label("📦 Lazada Order Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Instructions
        Label instructions = new Label(
            "💡 Fetch orders from Lazada. " +
            "Token will be auto-loaded if you've completed authorization."
        );
        instructions.setWrapText(true);
        instructions.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        // Form
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);

        Label tokenLabel = new Label("Access Token:");
        accessTokenField = new TextField();
        accessTokenField.setPromptText("Token will auto-load from storage");
        accessTokenField.setPrefWidth(400);

        Button loadTokenBtn = new Button("📁 Reload Token");
        loadTokenBtn.setOnAction(e -> loadToken());
        loadTokenBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        formGrid.add(tokenLabel, 0, 0);
        formGrid.add(accessTokenField, 1, 0);
        formGrid.add(loadTokenBtn, 2, 0);

        // Action buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Button fetchBtn = new Button("📥 Fetch Orders");
        fetchBtn.setOnAction(e -> fetchOrders());
        fetchBtn.setStyle("-fx-background-color: #FF6600; -fx-text-fill: white;");

        Button clearBtn = new Button("🗑️ Clear");
        clearBtn.setOnAction(e -> orderArea.clear());

        buttonBox.getChildren().addAll(fetchBtn, clearBtn);

        // Status
        statusLabel = new Label("Initializing...");
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
            formGrid,
            buttonBox,
            statusLabel,
            orderArea
        );

        // Auto-load token on startup
        loadToken();
    }

    public VBox getPanel() {
        return panel;
    }

    private void loadToken() {
        try {
            if (LazadaTokenStorage.hasToken()) {
                LazadaTokenStorage.TokenData tokenData = LazadaTokenStorage.loadToken();
                if (tokenData != null) {
                    accessTokenField.setText(tokenData.accessToken);

                    // Display token info
                    orderArea.clear();
                    orderArea.appendText("✅ Token loaded successfully!\n\n");
                    orderArea.appendText("🔑 Access Token: " + tokenData.accessToken.substring(0, Math.min(40, tokenData.accessToken.length())) + "...\n");
                    orderArea.appendText("🔄 Refresh Token: " + tokenData.refreshToken.substring(0, Math.min(40, tokenData.refreshToken.length())) + "...\n");
                    orderArea.appendText("⏱️ Expires in: " + (tokenData.expiresIn / 86400) + " days\n");
                    orderArea.appendText("📅 Created: " + new java.util.Date(tokenData.createdAt) + "\n");

                    if (!tokenData.sellerId.isEmpty()) {
                        orderArea.appendText("🏪 Seller ID: " + tokenData.sellerId + "\n");
                    }

                    if (tokenData.isAccessTokenExpired()) {
                        statusLabel.setText("⚠️ Token expired - needs refresh");
                        statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #ff6600;");
                        orderArea.appendText("\n⚠️ WARNING: Access token has expired!\n");
                        orderArea.appendText("💡 Use refresh token to get a new access token\n");
                    } else {
                        long remainingSeconds = tokenData.expiresIn - ((System.currentTimeMillis() - tokenData.createdAt) / 1000);
                        long remainingDays = remainingSeconds / 86400;
                        long remainingHours = (remainingSeconds % 86400) / 3600;
                        statusLabel.setText("✅ Token valid - " + remainingDays + "d " + remainingHours + "h remaining");
                        statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #4CAF50;");
                        orderArea.appendText("\n✅ Token is valid and ready to use!\n");
                        orderArea.appendText("💡 Click 'Fetch Orders' to retrieve your Lazada orders\n");
                    }
                } else {
                    statusLabel.setText("❌ Failed to load token data");
                    statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #f44336;");
                    orderArea.setText("❌ Failed to load token data\n");
                }
            } else {
                statusLabel.setText("❌ No saved token - Please authorize first");
                statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #f44336;");
                orderArea.setText("❌ No saved token found\n\n");
                orderArea.appendText("💡 Go to 'Lazada Auth' tab to authorize your shop first\n");
            }
        } catch (Exception ex) {
            showError("Failed to load token", ex);
            statusLabel.setText("❌ Error loading token");
            statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #f44336;");
        }
    }

    private void fetchOrders() {
        String accessToken = accessTokenField.getText().trim();

        if (accessToken.isEmpty()) {
            orderArea.setText("❌ Error: Please enter an access token or load from storage\n");
            return;
        }

        statusLabel.setText("⏳ Fetching orders from Lazada...");
        orderArea.setText("Loading orders...\n");

        // Run in background thread
        new Thread(() -> {
            try {
                String ordersJson = LazadaOrderAPI.getOrders(accessToken);

                Platform.runLater(() -> {
                    orderArea.setText("✅ Orders fetched successfully!\n\n");
                    orderArea.appendText(ordersJson);
                    statusLabel.setText("✅ Orders fetched at " + new java.util.Date());
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    showError("Failed to fetch orders", ex);
                    statusLabel.setText("❌ Failed to fetch orders");
                });
            }
        }).start();
    }

    private void showError(String message, Exception ex) {
        orderArea.appendText("\n❌ Error: " + message + "\n");
        orderArea.appendText(ex.getMessage() + "\n");
        ex.printStackTrace();
    }
}
