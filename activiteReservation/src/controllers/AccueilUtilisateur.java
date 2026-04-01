package controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import tn.edu.esprit.entities.User;

import java.io.IOException;

public class AccueilUtilisateur {

    @FXML
    private Label lblNomUtilisateur;

    @FXML
    private Label lblDateActuelle;

    @FXML
    private Button btnAjouterReservation;

    @FXML
    private Button btnCalendrier;

    private User utilisateurConnecte;

    // 🔹 Initialisation
    @FXML
    public void initialize() {
        afficherDateActuelle();
    }

    // 🔹 Définir l'utilisateur connecté
    public void setUtilisateurConnecte(User user) {
        this.utilisateurConnecte = user;
        if (user != null && lblNomUtilisateur != null) {
            lblNomUtilisateur.setText("Bienvenue, " + user.getPrenom() + " " + user.getNom() + " ! 🌊");
        }
    }

    // 🔹 Afficher la date actuelle
    private void afficherDateActuelle() {
        LocalDate aujourdhui = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy");
        String dateFormatee = aujourdhui.format(formatter);
        dateFormatee = dateFormatee.substring(0, 1).toUpperCase() + dateFormatee.substring(1);
        lblDateActuelle.setText("📅 " + dateFormatee);
    }

    // 🔹 Ouvrir Ajouter réservation
    @FXML
    private void ouvrirAjouterReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReservation.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une réservation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir AjouterReservation.fxml : " + e.getMessage());
        }
    }

    // 🔹 Ouvrir Calendrier
    @FXML
    private void ouvrirCalendrier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Calendrier.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Calendrier des réservations");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir Calendrier.fxml : " + e.getMessage());
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