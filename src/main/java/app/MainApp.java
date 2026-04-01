package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // print where the classpath root is coming from:
        URL root = MainApp.class.getProtectionDomain().getCodeSource().getLocation();
        System.out.println("CLASSPATH ROOT = " + root);

        // check if the file exists on disk where the classpath points:
        File file = new File(new File(root.toURI()), "view/dashboard.fxml");
        System.out.println("CHECK DISK FILE = " + file.getAbsolutePath());
        System.out.println("EXISTS? " + file.exists());

        // now load normally
        URL url = MainApp.class.getResource("/view/dashboard.fxml");
        System.out.println("FXML URL = " + url);

        if (url == null) {
            throw new RuntimeException("NOT FOUND: /view/dashboard.fxml");
        }

        FXMLLoader loader = new FXMLLoader(url);
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("EcoMarine");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}