module com.comp2042 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;
    requires java.logging;

    opens com.comp2042.controller to javafx.fxml;
    opens com.comp2042.main to javafx.fxml;

    exports com.comp2042.main;
}