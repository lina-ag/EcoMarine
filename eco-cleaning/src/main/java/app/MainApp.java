package app;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        DB.init();
        setRoot("/home.fxml", "Eco - Accueil");
        stage.show();
    }

    public static void setRoot(String fxml, String title) {
        try {
            Parent root = FXMLLoader.load(MainApp.class.getResource(fxml));
            Scene scene = new Scene(root, 900, 560);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
    public static class Controller {
        @FXML
        private void goHome() {
            MainApp.setRoot("/home.fxml", "Eco - Accueil");
        }

        // Mets ici le reste de ton code (addAction, updateAction, loadVolontaires, etc.)
    }
}