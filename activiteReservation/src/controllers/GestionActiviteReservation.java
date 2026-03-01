package controllers;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Modality;

import tn.edu.esprit.entities.ActiviteEcologique;
import tn.edu.esprit.services.ServiceActivite;
import tn.edu.esprit.services.ServiceReservation;

public class GestionActiviteReservation {

    @FXML
    private Label activitesCount;
    @FXML
    private Label reservationsCount;
    
    // Instance statique pour pouvoir y accéder depuis d'autres controllers
    private static GestionActiviteReservation instance;

    @FXML
    public void initialize() {
        instance = this;
        loadStatistics();
    }

    public void loadStatistics() {
        try {
            // Compter les activités
            ServiceActivite serviceActivite = new ServiceActivite();
            List<ActiviteEcologique> activites = serviceActivite.getAll();
            int totalActivites = activites.size();
            
            // Compter les réservations
            ServiceReservation serviceReservation = new ServiceReservation();
            int totalReservations = serviceReservation.getAll().size();
            
            // Mettre à jour les labels
            activitesCount.setText(String.valueOf(totalActivites));
            reservationsCount.setText(String.valueOf(totalReservations));
            
            System.out.println("Statistiques mises à jour: " + totalActivites + " activités, " + totalReservations + " réservations");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des statistiques: " + e.getMessage());
            activitesCount.setText("0");
            reservationsCount.setText("0");
        }
    }
    
    // Méthode statique pour rafraîchir les stats depuis n'importe où
    public static void refreshStats() {
        if (instance != null) {
            instance.loadStatistics();
        }
    }

    @FXML
    private void ouvrirAjouterActivite() {
        ouvrirFenetreAvecRafraichissement("/AjouterActivite.fxml", "Ajouter Activité");
    }

    @FXML
    private void ouvrirAfficherActivites() {
        ouvrirFenetreAvecRafraichissement("/AfficherActivite.fxml", "Liste des Activités");
    }
  
    @FXML
    private void ouvrirAfficherReservation() {
        ouvrirFenetreAvecRafraichissement("/AfficherReservation.fxml", "Liste des Réservations");
    }
    
    @FXML
    private void ouvrirAjouterReservation() {
        ouvrirFenetreAvecRafraichissement("/AjouterReservation.fxml", "Ajouter Réservation");
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
   
    // Nouvelle méthode qui rafraîchit les stats quand la fenêtre se ferme
    private void ouvrirFenetreAvecRafraichissement(String chemin, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(chemin));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle(titre);
            stage.setScene(new Scene(root));
            
            // Rafraîchir les statistiques quand la fenêtre se ferme
            stage.setOnHiding(event -> refreshStats());
            
            // Optionnel: rendre la fenêtre modale (bloque la fenêtre principale)
            stage.initModality(Modality.APPLICATION_MODAL);
            
            stage.showAndWait(); // Utilisez showAndWait() au lieu de show() pour les fenêtres modales
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}