package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class DechetDocumentsController {

    @FXML
    private void openSortingGuide() {
        showInfo("Guide tri littoral",
                "Contenu pret : consignes de tri, risques biodiversite et protocole de collecte terrain.");
    }

    @FXML
    private void exportInterventionReport() {
        showInfo("Rapport intervention",
                "Export simule : rapport avant/apres, quantite collectee, zone cible et equipe mobilisee.");
    }

    @FXML
    private void openAwarenessKit() {
        showInfo("Kit sensibilisation",
                "Messages prets pour panneaux, reseaux sociaux, annonces terrain et ateliers citoyens.");
    }

    @FXML
    private void prepareAlert() {
        showInfo("Alerte terrain",
                "Alerte preparee : zone, photo, type de dechet, niveau de priorite et contact intervention.");
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
