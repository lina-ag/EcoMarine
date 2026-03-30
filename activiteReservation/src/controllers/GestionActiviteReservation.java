package controllers;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import tn.edu.esprit.entities.ActiviteEcologique;
import tn.edu.esprit.services.ServiceActivite;
import tn.edu.esprit.services.ServiceReservation;

public class GestionActiviteReservation {

    @FXML
    private Label activitesCount;
    @FXML
    private Label reservationsCount;
    
    // ✅ NOUVEAU : le panneau central
    @FXML
    private StackPane mainContent;

    private static GestionActiviteReservation instance;

    @FXML
    public void initialize() {
        instance = this;
        loadStatistics();
        // ✅ Charger le tableau de bord au démarrage
        chargerVueCentrale("/TableauBord.fxml");
    }

    // ✅ NOUVELLE méthode : charge n'importe quel FXML dans le center
    private void chargerVueCentrale(String chemin) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(chemin));
            Parent vue = loader.load();
            mainContent.getChildren().setAll(vue);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue : " + chemin);
        }
    }

    public void loadStatistics() {
        try {
            ServiceActivite serviceActivite = new ServiceActivite();
            List<ActiviteEcologique> activites = serviceActivite.getAll();
            int totalActivites = activites.size();

            ServiceReservation serviceReservation = new ServiceReservation();
            int totalReservations = serviceReservation.getAll().size();

            activitesCount.setText(String.valueOf(totalActivites));
            reservationsCount.setText(String.valueOf(totalReservations));

        } catch (Exception e) {
            System.err.println("Erreur stats: " + e.getMessage());
            activitesCount.setText("0");
            reservationsCount.setText("0");
        }
    }

    public static void refreshStats() {
        if (instance != null) {
            instance.loadStatistics();
        }
    }

    // ✅ Tous les boutons sidebar chargent dans le center maintenant
    @FXML
    private void ouvrirAjouterActivite() {
        chargerVueCentrale("/AjouterActivite.fxml");
    }

    @FXML
    private void ouvrirAfficherActivites() {
        chargerVueCentrale("/AfficherActivite.fxml");
    }

    @FXML
    private void ouvrirAfficherReservation() {
        chargerVueCentrale("/AfficherReservation.fxml");
    }

    @FXML
    private void ouvrirAjouterReservation() {
        chargerVueCentrale("/AjouterReservation.fxml");
    }

    @FXML
    private void ouvrirTableauBord() {
        chargerVueCentrale("/TableauBord.fxml");
    }

    // ✅ Calendrier garde sa propre fenêtre (CalendarFX fonctionne mieux en Stage séparé)
    @FXML
    private void ouvrirCalendrier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Calendrier.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ✅ Chatbot garde aussi sa propre fenêtre
    @FXML
    private void ouvrirChatbot() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Chatbotview.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("🤖 Assistant EcoMarine");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir l'assistant : " + e.getMessage());
        }
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}