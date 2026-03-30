module activiteReservation {
    requires java.sql;
<<<<<<< HEAD
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires com.google.gson;
    requires java.base;
    requires okhttp3;
    requires java.desktop;
=======
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.controls;
	requires com.google.gson;
	requires java.base;
	requires okhttp3;
	requires java.desktop;
	exports main;
	exports tn.edu.esprit.entities;
	exports tn.edu.esprit.services;
    opens main to javafx.fxml;
    opens controllers to javafx.fxml;
    opens tn.edu.esprit.entities to javafx.base;
>>>>>>> branch 'master' of https://github.com/lina-ag/EcoMarine.git
    requires com.calendarfx.view;
    requires org.controlsfx.controls;
<<<<<<< HEAD
    requires javafx.web;
    requires com.sothawo.mapjfx;
    requires com.google.protobuf;
    requires kotlin.stdlib;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires itextpdf;
	requires jakarta.mail;
	requires java.net.http;
	requires jdk.jsobject;
	requires vosk;

    exports main;
    opens main to javafx.fxml;
    opens controllers to javafx.fxml;
    opens tn.edu.esprit.entities to javafx.base, com.google.gson;
}
=======
	requires javafx.web;
	requires com.sothawo.mapjfx;
	requires com.google.protobuf;
	requires kotlin.stdlib;

	requires opencv;

	
}



>>>>>>> branch 'master' of https://github.com/lina-ag/EcoMarine.git
