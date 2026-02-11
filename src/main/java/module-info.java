module com.example.testshopgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.example.testshopgui to javafx.fxml;
    exports com.example.testshopgui;
}