package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainFx extends Application {
 
    @Override
    public void start(Stage primaryStage) {
        try {
        	  System.setProperty("sun.net.http.allowRestrictedHeaders", "true"); 

            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionUsers.fxml"));

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Home.fxml"));

            Scene scene = new Scene(loader.load());

            primaryStage.setTitle("Gestion des Activités");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}