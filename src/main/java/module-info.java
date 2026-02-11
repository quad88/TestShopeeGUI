module com.example.testshopgui {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.testshopgui to javafx.fxml;
    exports com.example.testshopgui;
}