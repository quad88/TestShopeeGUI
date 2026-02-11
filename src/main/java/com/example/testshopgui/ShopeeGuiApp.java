package com.example.testshopgui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Shopee API Testing GUI Application
 *
 * This GUI allows you to test multiple Shopee shops with a visual interface.
 * No need to use console commands - everything is point-and-click!
 *
 * Features:
 * - Manage multiple shops
 * - Authorize shops with OAuth
 * - Fetch and display orders
 * - Monitor token status
 *
 * To run:
 * javac --module-path %PATH_TO_FX% --add-modules javafx.controls ShopeeGuiApp.java
 * java --module-path %PATH_TO_FX% --add-modules javafx.controls ShopeeGuiApp
 */
public class ShopeeGuiApp extends Application {

    private TabPane tabPane;
    private ShopManagerPanel shopManagerPanel;
    private AuthPanel authPanel;
    private OrderPanel orderPanel;
    private InspectPanelFX inspectPanel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Shopee API Testing Tool - Multi-Shop Manager");

        // Create tab pane
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Create panels
        shopManagerPanel = new ShopManagerPanel();
        authPanel = new AuthPanel();
        orderPanel = new OrderPanel();
        inspectPanel = new InspectPanelFX();

        // Create tabs
        Tab shopTab = new Tab("📋 Shop Manager", shopManagerPanel.getPanel());
        Tab authTab = new Tab("🔐 Authorization", authPanel.getPanel());
        Tab orderTab = new Tab("📦 Orders", orderPanel.getPanel());
        Tab inspectTab = new Tab("🔍 Inspect", inspectPanel.getPanel());
        Tab settingsTab = new Tab("⚙️ Settings", createSettingsPanel());

        tabPane.getTabs().addAll(shopTab, authTab, orderTab, inspectTab, settingsTab);

        // Create main layout
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Header
        Label header = new Label("🛒 Shopee API Testing Tool");
        header.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label subtitle = new Label("Test multiple shops with visual interface");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        VBox headerBox = new VBox(5, header, subtitle);
        headerBox.setPadding(new Insets(0, 0, 10, 0));

        // Status bar
        HBox statusBar = createStatusBar();

        root.getChildren().addAll(headerBox, tabPane, statusBar);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // Create scene
        Scene scene = new Scene(root, 900, 650);

        // Add CSS styling (inline for simplicity)
        scene.getRoot().setStyle("-fx-font-family: 'Segoe UI', Arial, sans-serif;");

        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize
        loadInitialData();
    }

    /**
     * Create status bar
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox(20);
        statusBar.setPadding(new Insets(5));
        statusBar.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-width: 1 0 0 0;");

        Label apiStatus = new Label("API: " + RuntimeConfig.getApiHost());
        apiStatus.setStyle("-fx-font-size: 10px;");

        Label partnerInfo = new Label("Partner ID: " + RuntimeConfig.getPartnerId());
        partnerInfo.setStyle("-fx-font-size: 10px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label timestamp = new Label("Ready");
        timestamp.setStyle("-fx-font-size: 10px;");

        statusBar.getChildren().addAll(apiStatus, partnerInfo, spacer, timestamp);

        return statusBar;
    }

    /**
     * Create settings panel
     */
    private VBox createSettingsPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));

        Label title = new Label("⚙️ API Configuration");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Instructions
        Label instructions = new Label("💡 Edit the fields below and click 'Save Changes' to update the configuration.");
        instructions.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        instructions.setWrapText(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(10, 0, 10, 0));

        // Partner ID
        grid.add(new Label("Partner ID:"), 0, 0);
        TextField partnerIdField = new TextField(String.valueOf(RuntimeConfig.getPartnerId()));
        partnerIdField.setPrefWidth(400);
        partnerIdField.setPromptText("Enter your Partner ID");
        grid.add(partnerIdField, 1, 0);

        // Partner Key
        grid.add(new Label("Partner Key:"), 0, 1);
        TextField partnerKeyField = new TextField(RuntimeConfig.getPartnerKey());
        partnerKeyField.setPrefWidth(400);
        partnerKeyField.setPromptText("Enter your Partner Key");
        grid.add(partnerKeyField, 1, 1);

        // API Host
        grid.add(new Label("API Host:"), 0, 2);
        TextField hostField = new TextField(RuntimeConfig.getApiHost());
        hostField.setPrefWidth(400);
        hostField.setPromptText("Enter API Host URL");
        grid.add(hostField, 1, 2);

        // Default Shop ID
        grid.add(new Label("Default Shop ID:"), 0, 3);
        TextField shopIdField = new TextField(String.valueOf(RuntimeConfig.getDefaultShopId()));
        shopIdField.setPrefWidth(400);
        shopIdField.setPromptText("Enter default Shop ID");
        grid.add(shopIdField, 1, 3);

        // Backend Callback URL
        grid.add(new Label("Backend Callback URL:"), 0, 4);
        TextField backendCallbackField = new TextField(RuntimeConfig.getBackendCallbackUrl());
        backendCallbackField.setPrefWidth(400);
        backendCallbackField.setPromptText("Enter backend callback URL (OAuth redirect)");
        grid.add(backendCallbackField, 1, 4);

        // Current values display
        Label currentValuesLabel = new Label("📋 Current Active Configuration:");
        currentValuesLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextArea currentValues = new TextArea();
        currentValues.setEditable(false);
        currentValues.setPrefHeight(100);
        currentValues.setWrapText(true);
        currentValues.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 11px;");
        updateCurrentValuesDisplay(currentValues);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button saveButton = new Button("💾 Save Changes");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        saveButton.setOnAction(e -> {
            try {
                long partnerId = Long.parseLong(partnerIdField.getText().trim());
                String partnerKey = partnerKeyField.getText().trim();
                String host = hostField.getText().trim();
                long shopId = Long.parseLong(shopIdField.getText().trim());
                String backendCallback = backendCallbackField.getText().trim();

                RuntimeConfig.setPartnerId(partnerId);
                RuntimeConfig.setPartnerKey(partnerKey);
                RuntimeConfig.setApiHost(host);
                RuntimeConfig.setDefaultShopId(shopId);
                RuntimeConfig.setBackendCallbackUrl(backendCallback);

                updateCurrentValuesDisplay(currentValues);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Configuration Updated");
                alert.setContentText("Configuration has been updated successfully!\nOther tabs will now use these values.");
                alert.showAndWait();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Input");
                alert.setContentText("Partner ID and Shop ID must be valid numbers.");
                alert.showAndWait();
            }
        });

        Button resetButton = new Button("🔄 Reset to Defaults");
        resetButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        resetButton.setOnAction(e -> {
            RuntimeConfig.resetToDefaults();
            partnerIdField.setText(String.valueOf(RuntimeConfig.getPartnerId()));
            partnerKeyField.setText(RuntimeConfig.getPartnerKey());
            hostField.setText(RuntimeConfig.getApiHost());
            shopIdField.setText(String.valueOf(RuntimeConfig.getDefaultShopId()));
            backendCallbackField.setText(RuntimeConfig.getBackendCallbackUrl());
            updateCurrentValuesDisplay(currentValues);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reset");
            alert.setHeaderText("Configuration Reset");
            alert.setContentText("Configuration has been reset to default values from ShopeeConfig.java");
            alert.showAndWait();
        });

        buttonBox.getChildren().addAll(saveButton, resetButton);

        // Info box
        Label infoLabel = new Label("ℹ️ Information:");
        infoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextArea infoBox = new TextArea(
            "• Changes apply immediately to all tabs (Authorization, Orders, Inspect)\n" +
            "• Configuration is stored in memory only (resets on app restart)\n" +
            "• For production, implement persistent storage (database/file)\n" +
            "• Partner credentials are from your Shopee Partner Portal\n" +
            "• Currently using SANDBOX environment for testing\n" +
            "• Token storage is in-memory for this session"
        );
        infoBox.setEditable(false);
        infoBox.setPrefHeight(120);
        infoBox.setWrapText(true);
        infoBox.setStyle("-fx-font-size: 11px; -fx-background-color: #f9f9f9;");

        panel.getChildren().addAll(
            title,
            instructions,
            new Separator(),
            grid,
            buttonBox,
            new Separator(),
            currentValuesLabel,
            currentValues,
            new Separator(),
            infoLabel,
            infoBox
        );

        return panel;
    }

    private void updateCurrentValuesDisplay(TextArea textArea) {
        textArea.setText(String.format(
            "Partner ID:           %d\n" +
            "Partner Key:          %s\n" +
            "API Host:             %s\n" +
            "Default Shop ID:      %d\n" +
            "Backend Callback URL: %s",
            RuntimeConfig.getPartnerId(),
            RuntimeConfig.getPartnerKey(),
            RuntimeConfig.getApiHost(),
            RuntimeConfig.getDefaultShopId(),
            RuntimeConfig.getBackendCallbackUrl()
        ));
    }

    /**
     * Load initial data
     */
    private void loadInitialData() {
        shopManagerPanel.refreshShopList();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
