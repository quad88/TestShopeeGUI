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

        // Create tabs
        Tab shopTab = new Tab("📋 Shop Manager", shopManagerPanel.getPanel());
        Tab authTab = new Tab("🔐 Authorization", authPanel.getPanel());
        Tab orderTab = new Tab("📦 Orders", orderPanel.getPanel());
        Tab settingsTab = new Tab("⚙️ Settings", createSettingsPanel());

        tabPane.getTabs().addAll(shopTab, authTab, orderTab, settingsTab);

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

        Label apiStatus = new Label("API: " + ShopeeConfig.HOST);
        apiStatus.setStyle("-fx-font-size: 10px;");

        Label partnerInfo = new Label("Partner ID: " + ShopeeConfig.PARTNER_ID);
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

        Label title = new Label("API Configuration");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Partner ID
        grid.add(new Label("Partner ID:"), 0, 0);
        TextField partnerIdField = new TextField(String.valueOf(ShopeeConfig.PARTNER_ID));
        partnerIdField.setEditable(false);
        partnerIdField.setPrefWidth(300);
        grid.add(partnerIdField, 1, 0);

        // Partner Key
        grid.add(new Label("Partner Key:"), 0, 1);
        PasswordField partnerKeyField = new PasswordField();
        partnerKeyField.setText(ShopeeConfig.PARTNER_KEY);
        partnerKeyField.setEditable(false);
        partnerKeyField.setPrefWidth(300);
        grid.add(partnerKeyField, 1, 1);

        // Host
        grid.add(new Label("API Host:"), 0, 2);
        TextField hostField = new TextField(ShopeeConfig.HOST);
        hostField.setEditable(false);
        hostField.setPrefWidth(300);
        grid.add(hostField, 1, 2);

        // Default Shop ID
        grid.add(new Label("Default Shop ID:"), 0, 3);
        TextField shopIdField = new TextField(String.valueOf(ShopeeConfig.SHOP_ID));
        shopIdField.setPrefWidth(300);
        grid.add(shopIdField, 1, 3);

        // Token storage info
        Label storageInfo = new Label("💾 Token Storage: In-Memory (for testing)");
        storageInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        // Info box
        TextArea infoBox = new TextArea(
            "ℹ️ Configuration Information:\n\n" +
            "• This application uses your existing ShopeeConfig.java settings\n" +
            "• Tokens are stored in memory during this session\n" +
            "• For production, implement database token storage\n" +
            "• Partner credentials are from your Shopee Partner Portal\n" +
            "• Currently using SANDBOX environment for testing"
        );
        infoBox.setEditable(false);
        infoBox.setPrefHeight(150);
        infoBox.setWrapText(true);
        infoBox.setStyle("-fx-font-size: 11px;");

        panel.getChildren().addAll(title, grid, storageInfo, new Separator(), infoBox);

        return panel;
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
