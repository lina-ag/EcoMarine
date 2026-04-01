package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import tn.edu.esprit.entities.ActiviteEcologique;
import tn.edu.esprit.services.ServiceActivite;
import tn.edu.esprit.services.ServiceReservation;

public class GestionActiviteReservation {

    @FXML
    private Label activitesCount;
    @FXML
    private Label reservationsCount;

    @FXML
    private StackPane mainContent;

    @FXML
    private Button btnBack;

    private static GestionActiviteReservation instance;

    @FXML
    public void initialize() {
        instance = this;
        loadStatistics();
        chargerVueCentrale("/TableauBord.fxml");
    }

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

    // ============================================
    // 🐋 FAUNE MARINE - YOUR NEW METHOD
    // ============================================
    @FXML
    private void ouvrirFauneMarine(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/faune/Marine/ressource/Main.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("🐋 Gestion Faune Marine & Météo");
            stage.setScene(new Scene(root, 1200, 700));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger Faune Marine:\n" + e.getMessage());
        }
    }
    @FXML
    private void handleBack() {
        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}