package faune.Marine;  // ← DOIT ÊTRE EXACTEMENT CECI

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/faune/Marine/ressource/Main.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 1100, 600);
            
            String css = getClass().getResource("/faune/Marine/ressource/style.css").toExternalForm();
            scene.getStylesheets().add(css);
            
            primaryStage.setTitle("Gestion de la Faune Marine");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}