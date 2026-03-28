module activiteReservation {
    requires java.sql;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires com.google.gson;
    requires java.base;
    requires okhttp3;
    requires java.desktop;
    requires jakarta.mail;
    requires jakarta.activation;
    exports main;
    opens main to javafx.fxml;
    opens controllers to javafx.fxml;
    opens tn.edu.esprit.entities to javafx.base;
    requires com.calendarfx.view;
    requires org.controlsfx.controls;
}