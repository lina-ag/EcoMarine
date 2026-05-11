package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

public class DechetModuleController {

    @FXML private StackPane contentPane;

    @FXML
    public void initialize() {
        openDashboard();
    }

    @FXML
    private void openDashboard() {
        load("/DechetDashboard.fxml");
    }

    @FXML
    private void openNew() {
        load("/DechetNew.fxml");
    }

    @FXML
    private void openStats() {
        load("/DechetStats.fxml");
    }

    @FXML
    private void openDocuments() {
        load("/DechetDocuments.fxml");
    }

    private void load(String fxml) {
        try {
            URL resource = getClass().getResource(fxml);
            if (resource == null) {
                showError("FXML introuvable: " + fxml);
                return;
            }

            Parent view = FXMLLoader.load(resource);
            contentPane.getChildren().setAll(view);
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
}
