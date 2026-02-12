package com.example.testshopgui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.Map;

public class ShopeeSwingGui extends JFrame {
    private JTable shopTable;
    private DefaultTableModel tableModel;
    private JTextArea logArea;

    public ShopeeSwingGui() {
        setTitle("Shopee Multi-Shop Testing Tool");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Shopee API Multi-Shop Manager"));
        add(topPanel, BorderLayout.NORTH);

        // Center - tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Shops", createShopPanel());
        tabs.addTab("Authorization", createAuthPanel());
        tabs.addTab("Orders", createOrderPanel());
        tabs.addTab("Inspect", new InspectPanel());
        add(tabs, BorderLayout.CENTER);

        // Bottom - log
        logArea = new JTextArea(5, 50);
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        add(logScroll, BorderLayout.SOUTH);

        log("Ready - API: " + ShopeeConfig.HOST);
    }

    private JPanel createShopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
        String[] columns = {"Shop ID", "Status", "Token", "Expires"};
        tableModel = new DefaultTableModel(columns, 0);
        shopTable = new JTable(tableModel);
        panel.add(new JScrollPane(shopTable), BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        JButton addBtn = new JButton("Add Test Shops");
        refreshBtn.addActionListener(e -> refreshShops());
        addBtn.addActionListener(e -> addTestShops());
        btnPanel.add(refreshBtn);
        btnPanel.add(addBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAuthPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Shop ID
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Shop ID:"), gbc);
        JTextField shopIdField = new JTextField(String.valueOf(ShopeeConfig.SHOP_ID), 20);
        gbc.gridx = 1;
        panel.add(shopIdField, gbc);

        // Generate URL button
        JButton genBtn = new JButton("Generate Auth URL");
        gbc.gridx = 2;
        panel.add(genBtn, gbc);

        // Auth URL
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Auth URL:"), gbc);
        JTextArea authUrlArea = new JTextArea(3, 40);
        authUrlArea.setLineWrap(true);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(new JScrollPane(authUrlArea), gbc);

        genBtn.addActionListener(e -> {
            try {
                String url = ShopeeAuth.generateAuthUrlWithBackendCallback();
                authUrlArea.setText(url);
                log("Auth URL generated");
            } catch (Exception ex) {
                log("Error: " + ex.getMessage());
            }
        });

        return panel;
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea orderArea = new JTextArea(15, 50);
        orderArea.setEditable(false);
        panel.add(new JScrollPane(orderArea), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField shopField = new JTextField(String.valueOf(ShopeeConfig.SHOP_ID), 15);
        JButton fetchBtn = new JButton("Fetch Orders");
        fetchBtn.addActionListener(e -> {
            try {
                long shopId = Long.parseLong(shopField.getText());
                log("Fetching orders for shop " + shopId);
                String orders = ShopeeOrderAPI.getOrderListWithStoredToken(shopId);
                orderArea.setText(orders);
                log("Orders fetched successfully");
            } catch (Exception ex) {
                log("Error: " + ex.getMessage());
            }
        });

        btnPanel.add(new JLabel("Shop ID:"));
        btnPanel.add(shopField);
        btnPanel.add(fetchBtn);
        panel.add(btnPanel, BorderLayout.NORTH);

        return panel;
    }

    private void refreshShops() {
        tableModel.setRowCount(0);
        try {
            Map<Long, ShopeeTokenStorage.ShopInfo> shops = ShopeeTokenStorage.getAllShopsInfo();
            for (ShopeeTokenStorage.ShopInfo info : shops.values()) {
                tableModel.addRow(new Object[]{
                    info.getShopId(),
                    info.getStatus(),
                    info.getToken().substring(0, Math.min(20, info.getToken().length())) + "...",
                    info.getExpiresAt()
                });
            }
            log("Loaded " + shops.size() + " shops");
        } catch (Exception e) {
            log("Error: " + e.getMessage());
        }
    }

    private void addTestShops() {
        ShopeeTokenStorage.saveTokens(226457519L, "test_token_1", "refresh_1", 0);
        ShopeeTokenStorage.saveTokens(123456789L, "test_token_2", "refresh_2", 0);
        ShopeeTokenStorage.saveTokens(987654321L, "test_token_3", "refresh_3", 0);
        log("Added 3 test shops");
        refreshShops();
    }

    private void log(String msg) {
        logArea.append("[" + new Date() + "] " + msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ShopeeSwingGui gui = new ShopeeSwingGui();
            gui.setVisible(true);
        });
    }
}
