package controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AcceuilController {

    @FXML
    public void initialize() {
        System.out.println("Page d'accueil chargée avec les 3 modules");
    }

    @FXML
    private void ouvrirAccueil() {
        // Déjà sur l'accueil, peut-être recharger
        System.out.println("Déjà sur la page d'accueil");
    }

    // ===== MODULE 1 : ACTIVITÉS & RÉSERVATIONS =====
    
    @FXML
    private void ouvrirAjouterActivite() {
        ouvrirFenetre("/GestionActiviteReservation.fxml", "Ajouter une activité");
    }

    @FXML
    private void ouvrirAfficherActivites() {
        ouvrirFenetre("/GestionActiviteReservation.fxml", "Liste des activités");
    }

    @FXML
    private void ouvrirCalendrier() {
        ouvrirFenetre("/Calendrier.fxml", "Calendrier des activités");
    }

    @FXML
    private void ouvrirModuleActivitesReservations() {
        // Ouvre la fenêtre principale de gestion des activités et réservations
        ouvrirFenetre("/GestionActiviteReservation.fxml", "Module Activités & Réservations");
    }
    // ===== MODULE 2 : GESTION ZONES =====
    
    @FXML
    private void ouvrirGestionZones() {
        ouvrirFenetre("/gestionzonespr.fxml", "Gestion des zones protégées");
    }

    // ===== MODULE 3 : GESTION UTILISATEURS =====
    
    @FXML
    private void ouvrirGestionUser() {
        ouvrirFenetre("/gestionUsers.fxml", "Gestion des utilisateurs");
    }

    // ===== MÉTHODE UTILITAIRE =====
    
    private void ouvrirFenetre(String fxml, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(titre);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'ouverture de : " + fxml);
        }
    }
}