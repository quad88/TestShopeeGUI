package com.example.testshopgui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Lazada Authorization Panel
 * Handles OAuth authorization flow for Lazada
 */
public class LazadaAuthPanel {
    private VBox panel;
    private TextArea authUrlArea;
    private TextField authCodeField;
    private TextArea resultArea;

    public LazadaAuthPanel() {
        createPanel();
    }

    private void createPanel() {
        panel = new VBox(15);
        panel.setPadding(new Insets(20));

        // Title
        Label title = new Label("🔐 Lazada Shop Authorization");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Instructions
        TextArea instructions = new TextArea(
            "Lazada Authorization Steps:\n\n" +
            "1. Click 'Generate Auth URL' to create authorization link\n" +
            "2. Copy the URL and open it in a browser\n" +
            "3. Login to your Lazada Seller Center account\n" +
            "4. Authorize the application\n" +
            "5. After redirect, copy the 'code' parameter from the URL\n" +
            "6. Paste the code below and click 'Complete Authorization'\n" +
            "7. Tokens will be automatically saved\n\n" +
            "⚠️ Authorization code is valid for 10 minutes only!"
        );
        instructions.setEditable(false);
        instructions.setPrefHeight(150);
        instructions.setWrapText(true);
        instructions.setStyle("-fx-font-size: 11px; -fx-background-color: #f9f9f9;");

        // Generate Auth URL section
        Button generateBtn = new Button("🔗 Generate Auth URL");
        generateBtn.setOnAction(e -> generateAuthUrl());
        generateBtn.setStyle("-fx-background-color: #FF6600; -fx-text-fill: white;");

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

        // Separator
        Separator separator = new Separator();

        // Authorization Code section
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
        VBox.setVgrow(resultArea, Priority.ALWAYS);

        panel.getChildren().addAll(
            title,
            instructions,
            new Separator(),
            generateBtn,
            urlLabel,
            authUrlArea,
            urlButtons,
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
            resultArea.setText("Generating authorization URL...\n");
            String authUrl = LazadaAuth.generateAuthUrl();
            authUrlArea.setText(authUrl);
            resultArea.appendText("✅ Authorization URL generated successfully!\n");
            resultArea.appendText("Copy the URL and open it in your browser.\n");
        } catch (Exception ex) {
            showError("Failed to generate auth URL", ex);
        }
    }

    private void copyAuthUrl() {
        String url = authUrlArea.getText();
        if (url != null && !url.isEmpty()) {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(url);
            clipboard.setContent(content);
            resultArea.appendText("📋 URL copied to clipboard!\n");
        }
    }

    private void openInBrowser() {
        String url = authUrlArea.getText();
        if (url != null && !url.isEmpty()) {
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                resultArea.appendText("🌐 Opening URL in browser...\n");
            } catch (Exception ex) {
                showError("Failed to open browser", ex);
            }
        }
    }

    private void completeAuthorization() {
        String code = authCodeField.getText().trim();

        if (code.isEmpty()) {
            resultArea.setText("❌ Error: Please enter the authorization code\n");
            return;
        }

        resultArea.setText("⏳ Exchanging authorization code for access token...\n");

        // Run in background thread
        new Thread(() -> {
            try {
                String response = LazadaAuth.createAccessToken(code);

                Platform.runLater(() -> {
                    resultArea.appendText("\n✅ Authorization successful!\n");
                    resultArea.appendText("Response:\n" + response + "\n\n");
                    resultArea.appendText("💡 Note: Parse this JSON to extract tokens and save them using LazadaTokenStorage\n");
                    authCodeField.clear();
                });
            } catch (Exception ex) {
                Platform.runLater(() -> showError("Authorization failed", ex));
            }
        }).start();
    }

    private void showError(String message, Exception ex) {
        resultArea.appendText("\n❌ Error: " + message + "\n");
        resultArea.appendText(ex.getMessage() + "\n");
        ex.printStackTrace();
    }
}
