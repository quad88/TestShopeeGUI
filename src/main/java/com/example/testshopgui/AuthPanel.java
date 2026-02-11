package com.example.testshopgui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Authorization Panel
 * Handles OAuth authorization flow
 */
public class AuthPanel {
    private VBox panel;
    private TextArea authUrlArea;
    private TextField shopIdField;
    private TextField authCodeField;
    private TextArea resultArea;

    public AuthPanel() {
        createPanel();
    }

    private void createPanel() {
        panel = new VBox(15);
        panel.setPadding(new Insets(20));

        // Title
        Label title = new Label("🔐 Shop Authorization");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Instructions
        TextArea instructions = new TextArea(
            "Authorization Steps:\n\n" +
            "1. Enter Shop ID (or use default from config)\n" +
            "2. Click 'Generate Auth URL' to create authorization link\n" +
            "3. Copy the URL and open it in a browser\n" +
            "4. Login to Shopee and authorize your app\n" +
            "5. After redirect, copy the 'code' parameter from URL\n" +
            "6. Paste the code and click 'Complete Authorization'\n" +
            "7. Tokens will be automatically saved"
        );
        instructions.setEditable(false);
        instructions.setPrefHeight(120);
        instructions.setWrapText(true);
        instructions.setStyle("-fx-font-size: 11px; -fx-background-color: #f9f9f9;");

        // Form
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        // Shop ID
        Label shopIdLabel = new Label("Shop ID:");
        shopIdField = new TextField(String.valueOf(ShopeeConfig.SHOP_ID));
        shopIdField.setPrefWidth(200);

        Button generateBtn = new Button("🔗 Generate Auth URL");
        generateBtn.setOnAction(e -> generateAuthUrl());

        form.add(shopIdLabel, 0, 0);
        form.add(shopIdField, 1, 0);
        form.add(generateBtn, 2, 0);

        // Auth URL display
        Label urlLabel = new Label("Authorization URL:");
        authUrlArea = new TextArea();
        authUrlArea.setPrefHeight(80);
        authUrlArea.setWrapText(true);
        authUrlArea.setEditable(false);

        Button copyUrlBtn = new Button("📋 Copy URL");
        copyUrlBtn.setOnAction(e -> copyAuthUrl());

        Button openBrowserBtn = new Button("🌐 Open in Browser");
        openBrowserBtn.setOnAction(e -> openInBrowser());

        HBox urlButtons = new HBox(10, copyUrlBtn, openBrowserBtn);

        form.add(urlLabel, 0, 1);
        form.add(authUrlArea, 1, 1, 2, 1);
        form.add(urlButtons, 1, 2, 2, 1);

        // Separator
        Separator separator = new Separator();

        // Authorization Code
        Label codeLabel = new Label("Authorization Code:");
        authCodeField = new TextField();
        authCodeField.setPromptText("Paste code from redirect URL...");
        authCodeField.setPrefWidth(400);

        Button completeBtn = new Button("✅ Complete Authorization");
        completeBtn.setOnAction(e -> completeAuthorization());
        completeBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        HBox codeBox = new HBox(10, codeLabel, authCodeField, completeBtn);
        codeBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Result area
        Label resultLabel = new Label("Result:");
        resultArea = new TextArea();
        resultArea.setPrefHeight(150);
        resultArea.setEditable(false);
        resultArea.setWrapText(true);

        panel.getChildren().addAll(
            title,
            instructions,
            new Separator(),
            form,
            separator,
            codeBox,
            resultLabel,
            resultArea
        );
    }

    public VBox getPanel() {
        return panel;
    }

    private void generateAuthUrl() {
        try {
            String url = ShopeeAuth.generateAuthUrlWithBackendCallback();
            authUrlArea.setText(url);
            resultArea.setText("✓ Authorization URL generated successfully!\n\n" +
                "Next steps:\n" +
                "1. Copy the URL above\n" +
                "2. Open it in a browser\n" +
                "3. Login and authorize the app\n" +
                "4. Copy the 'code' from redirect URL\n" +
                "5. Paste it below and complete authorization");

            System.out.println("Auth URL generated: " + url);
        } catch (Exception ex) {
            showError("Error generating auth URL", ex);
        }
    }

    private void copyAuthUrl() {
        String url = authUrlArea.getText();
        if (!url.isEmpty()) {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(url);
            clipboard.setContent(content);

            resultArea.setText("✓ URL copied to clipboard!");
        }
    }

    private void openInBrowser() {
        String url = authUrlArea.getText();
        if (!url.isEmpty()) {
            try {
                if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                    resultArea.setText("✓ Browser opened. Please complete authorization and copy the code.");
                } else {
                    resultArea.setText("⚠ Desktop not supported. Please copy and paste the URL manually.");
                }
            } catch (Exception ex) {
                showError("Error opening browser", ex);
            }
        }
    }

    private void completeAuthorization() {
        String code = authCodeField.getText().trim();
        String shopIdStr = shopIdField.getText().trim();

        if (code.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Missing Authorization Code");
            alert.setContentText("Please paste the authorization code from the redirect URL");
            alert.showAndWait();
            return;
        }

        try {
            long shopId = Long.parseLong(shopIdStr);
            resultArea.setText("⏳ Exchanging code for tokens...\n");

            // Get access token
            String response = ShopeeAuth.getAccessToken(shopId, code);

            // Check if successful
            if (ShopeeAuth.isTokenResponseSuccess(response)) {
                // Parse and save tokens
                String accessToken = extractJsonValue(response, "access_token");
                String refreshToken = extractJsonValue(response, "refresh_token");
                String expiresInStr = extractJsonValue(response, "expire_in");
                long expiresIn = expiresInStr != null ? Long.parseLong(expiresInStr) : 14400;

                ShopeeTokenStorage.saveTokens(shopId, accessToken, refreshToken, expiresIn);

                resultArea.setText("✅ Authorization completed successfully!\n\n" +
                    "Shop ID: " + shopId + "\n" +
                    "Access Token: " + (accessToken != null ? accessToken.substring(0, Math.min(20, accessToken.length())) + "..." : "N/A") + "\n" +
                    "Expires in: " + expiresIn + " seconds\n\n" +
                    "Tokens have been saved. You can now fetch orders in the Orders tab.");

                // Clear the code field
                authCodeField.clear();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Authorization Complete!");
                alert.setContentText("Shop " + shopId + " has been authorized successfully.");
                alert.showAndWait();

            } else {
                resultArea.setText("❌ Authorization failed!\n\nResponse:\n" + response);
            }

        } catch (NumberFormatException ex) {
            showError("Invalid Shop ID", new Exception("Please enter a valid numeric Shop ID"));
        } catch (Exception ex) {
            showError("Authorization Error", ex);
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
        resultArea.setText("❌ " + title + "\n\n" + ex.getMessage());

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(ex.getMessage());
        alert.showAndWait();
    }
}
