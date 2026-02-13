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
            "Make sure you have completed authorization and have a valid access token."
        );
        instructions.setWrapText(true);
        instructions.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        // Form
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);

        Label tokenLabel = new Label("Access Token:");
        accessTokenField = new TextField();
        accessTokenField.setPromptText("Enter your access token or load from storage");
        accessTokenField.setPrefWidth(400);

        Button loadTokenBtn = new Button("📁 Load Token");
        loadTokenBtn.setOnAction(e -> loadToken());

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
            formGrid,
            buttonBox,
            statusLabel,
            orderArea
        );
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
                    statusLabel.setText("✅ Token loaded from storage");

                    if (tokenData.isAccessTokenExpired()) {
                        statusLabel.setText("⚠️ Token loaded but expired - use refresh token");
                    }
                } else {
                    statusLabel.setText("❌ Failed to load token data");
                }
            } else {
                statusLabel.setText("❌ No saved token found. Please authorize first.");
            }
        } catch (Exception ex) {
            showError("Failed to load token", ex);
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
