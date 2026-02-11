package com.example.testshopgui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.Map;

/**
 * Shop Manager Panel
 * Displays and manages all authorized shops
 */
public class ShopManagerPanel {
    private VBox panel;
    private TableView<ShopData> shopTable;
    private ObservableList<ShopData> shopData;

    public ShopManagerPanel() {
        createPanel();
    }

    private void createPanel() {
        panel = new VBox(15);
        panel.setPadding(new Insets(20));

        // Title
        Label title = new Label("📋 Registered Shops");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Create table
        shopTable = new TableView<>();
        shopData = FXCollections.observableArrayList();
        shopTable.setItems(shopData);

        // Define columns
        TableColumn<ShopData, Long> shopIdCol = new TableColumn<>("Shop ID");
        shopIdCol.setCellValueFactory(new PropertyValueFactory<>("shopId"));
        shopIdCol.setPrefWidth(120);

        TableColumn<ShopData, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);

        TableColumn<ShopData, String> tokenCol = new TableColumn<>("Access Token");
        tokenCol.setCellValueFactory(new PropertyValueFactory<>("tokenPreview"));
        tokenCol.setPrefWidth(200);

        TableColumn<ShopData, String> expiresCol = new TableColumn<>("Expires At");
        expiresCol.setCellValueFactory(new PropertyValueFactory<>("expiresAt"));
        expiresCol.setPrefWidth(180);

        shopTable.getColumns().addAll(shopIdCol, statusCol, tokenCol, expiresCol);
        shopTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Buttons
        HBox buttonBox = new HBox(10);
        Button refreshBtn = new Button("🔄 Refresh");
        Button addTestBtn = new Button("➕ Add Test Shops");
        Button removeBtn = new Button("❌ Remove Selected");
        Button clearBtn = new Button("🗑️ Clear All");

        refreshBtn.setOnAction(e -> refreshShopList());
        addTestBtn.setOnAction(e -> addTestShops());
        removeBtn.setOnAction(e -> removeSelectedShop());
        clearBtn.setOnAction(e -> clearAllShops());

        buttonBox.getChildren().addAll(refreshBtn, addTestBtn, removeBtn, clearBtn);

        // Info label
        Label infoLabel = new Label("💡 Tip: Authorize shops in the Authorization tab to add them here");
        infoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        panel.getChildren().addAll(title, shopTable, buttonBox, infoLabel);
        VBox.setVgrow(shopTable, Priority.ALWAYS);
    }

    public VBox getPanel() {
        return panel;
    }

    public void refreshShopList() {
        shopData.clear();
        Map<Long, ShopeeTokenStorage.ShopInfo> shops = ShopeeTokenStorage.getAllShopsInfo();

        for (ShopeeTokenStorage.ShopInfo info : shops.values()) {
            String tokenPreview = info.getToken().length() > 20
                ? info.getToken().substring(0, 20) + "..."
                : info.getToken();

            shopData.add(new ShopData(
                info.getShopId(),
                info.getStatus(),
                tokenPreview,
                info.getExpiresAt().toString()
            ));
        }

        System.out.println("✓ Loaded " + shops.size() + " shops");
    }

    private void addTestShops() {
        ShopeeTokenStorage.saveTokens(226457519L, "test_token_shop_1", "refresh_1", 14400);
        ShopeeTokenStorage.saveTokens(123456789L, "test_token_shop_2", "refresh_2", 14400);
        ShopeeTokenStorage.saveTokens(987654321L, "test_token_shop_3", "refresh_3", 14400);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Added 3 test shops successfully!");
        alert.showAndWait();

        refreshShopList();
    }

    private void removeSelectedShop() {
        ShopData selected = shopTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            ShopeeTokenStorage.removeShop(selected.getShopId());
            refreshShopList();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Shop " + selected.getShopId() + " removed successfully!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please select a shop to remove");
            alert.showAndWait();
        }
    }

    private void clearAllShops() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm");
        confirm.setHeaderText("Clear all shops?");
        confirm.setContentText("This will remove all shop tokens from storage. Continue?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                ShopeeTokenStorage.clearAll();
                refreshShopList();
            }
        });
    }

    // Data class for TableView
    public static class ShopData {
        private Long shopId;
        private String status;
        private String tokenPreview;
        private String expiresAt;

        public ShopData(Long shopId, String status, String tokenPreview, String expiresAt) {
            this.shopId = shopId;
            this.status = status;
            this.tokenPreview = tokenPreview;
            this.expiresAt = expiresAt;
        }

        public Long getShopId() { return shopId; }
        public String getStatus() { return status; }
        public String getTokenPreview() { return tokenPreview; }
        public String getExpiresAt() { return expiresAt; }
    }
}
