module com.example.screenpet {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.sun.jna.platform;


    opens com.example.screenpet to javafx.fxml;
    exports com.example.screenpet;
}