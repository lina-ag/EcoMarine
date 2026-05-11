package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ModuleTestApp extends Application {

    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        primaryStage.setTitle("EcoMarine - Test modules");
        showMenu();
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void showMenu() {
        Label kicker = new Label("TEST DIRECT DES MODULES");
        kicker.setStyle("-fx-text-fill: #67e8f9; -fx-font-weight: 900; -fx-font-size: 13px;");

        Label title = new Label("EcoMarine JavaFX");
        title.setStyle("-fx-text-fill: white; -fx-font-weight: 900; -fx-font-size: 42px;");

        Label subtitle = new Label("Choisis uniquement les parties a tester : dechets, nettoyage, biodiversite et evenement.");
        subtitle.setWrapText(true);
        subtitle.setStyle("-fx-text-fill: #c8e6f4; -fx-font-size: 16px;");

        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(18);

        grid.add(moduleButton("Dechets", "Dashboard, nouveau signalement, statistiques et documents", "/DechetModule.fxml"), 0, 0);
        grid.add(moduleButton("Nettoyage", "Missions, volontaires et suivi terrain", "/actions.fxml"), 1, 0);
        grid.add(moduleButton("Evenement", "Agenda, recherche et filtres rapides", "/AfficherActivite.fxml"), 0, 1);
        grid.add(moduleButton("Biodiversite", "Observatoire faune marine", "/faune/Marine/ressource/FauneMarine.fxml"), 1, 1);

        VBox panel = new VBox(20, kicker, title, subtitle, grid);
        panel.setMaxWidth(920);
        panel.setStyle("""
                -fx-background-color: rgba(255, 255, 255, 0.10);
                -fx-background-radius: 28;
                -fx-border-color: rgba(103, 232, 249, 0.28);
                -fx-border-radius: 28;
                -fx-padding: 34;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 30, 0.25, 0, 12);
                """);

        VBox root = new VBox(panel);
        root.setStyle("-fx-background-color: radial-gradient(center 20% 15%, radius 70%, #0c4f75 0%, #081f49 48%, #061735 100%); -fx-padding: 60;");
        root.setFillWidth(true);

        stage.setScene(new Scene(root, 1200, 760));
    }

    private VBox moduleButton(String title, String text, String fxml) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 23px; -fx-font-weight: 900;");

        Label textLabel = new Label(text);
        textLabel.setWrapText(true);
        textLabel.setStyle("-fx-text-fill: #bfe9f7; -fx-font-size: 14px;");

        Button open = new Button("Ouvrir");
        open.setMaxWidth(Double.MAX_VALUE);
        open.setStyle("-fx-background-color: linear-gradient(to right, #17c2e8, #7941f4); -fx-text-fill: white; -fx-font-weight: 900; -fx-background-radius: 16; -fx-padding: 11 18;");
        open.setOnAction(event -> loadModule(fxml));

        VBox card = new VBox(12, titleLabel, textLabel, open);
        card.setPrefWidth(410);
        card.setMinHeight(185);
        card.setStyle("-fx-background-color: rgba(7, 23, 54, 0.64); -fx-background-radius: 22; -fx-border-color: rgba(127, 220, 255, 0.22); -fx-border-radius: 22; -fx-padding: 22;");
        return card;
    }

    private void loadModule(String fxml) {
        try {
            URL resource = getClass().getResource(fxml);
            if (resource == null) {
                showError("FXML introuvable: " + fxml);
                return;
            }

            Parent module = FXMLLoader.load(resource);
            Button back = new Button("Retour aux modules");
            back.setOnAction(event -> showMenu());
            back.setStyle("-fx-background-color: rgba(255,255,255,0.14); -fx-text-fill: white; -fx-font-weight: 900; -fx-background-radius: 14; -fx-padding: 10 16;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox topBar = new HBox(back, spacer);
            topBar.setStyle("-fx-background-color: #061735; -fx-padding: 12 18;");

            VBox root = new VBox(topBar, module);
            VBox.setVgrow(module, Priority.ALWAYS);
            stage.setScene(new Scene(root, 1320, 820));
        } catch (IOException e) {
            e.printStackTrace();
            showError("Impossible d'ouvrir " + fxml + "\n" + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
