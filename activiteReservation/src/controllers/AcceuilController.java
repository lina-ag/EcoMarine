
package controllers;

import java.io.IOException;

import javafx.scene.control.Alert;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AcceuilController {

    @FXML
    private Label lblDateActuelle;
    
    @FXML
    private Label lblMessageBienvenue;
    
    @FXML
    private HBox cardsContainer;

    @FXML
    public void initialize() {
        System.out.println("Page d'accueil chargée avec les 3 modules");
        
        // Afficher la date actuelle
        afficherDateActuelle();
        
        // Afficher un message de bienvenue personnalisé
        afficherMessageBienvenue();
        
        // Animer les cartes
        animerCartes();
        
        // Ajouter des animations d'entrée
        animerEntree();
    }
    
    private void afficherDateActuelle() {
        LocalDate aujourdhui = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy");
        String dateFormatee = aujourdhui.format(formatter);
        dateFormatee = dateFormatee.substring(0, 1).toUpperCase() + dateFormatee.substring(1);
        lblDateActuelle.setText("📅 " + dateFormatee);
    }
    
    private void afficherMessageBienvenue() {
        int heure = LocalDate.now().getDayOfMonth();
        String message = "Bienvenue sur EcoMarine ! 🌊";
        lblMessageBienvenue.setText(message);
    }
    
    private void animerCartes() {
        for (int i = 0; i < cardsContainer.getChildren().size(); i++) {
            VBox carte = (VBox) cardsContainer.getChildren().get(i);
            
            // Animation de survol
            carte.setOnMouseEntered(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), carte);
                scale.setToX(1.03);
                scale.setToY(1.03);
                scale.play();
            });
            
            carte.setOnMouseExited(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), carte);
                scale.setToX(1);
                scale.setToY(1);
                scale.play();
            });
            
            // Animation d'apparition décalée
            PauseTransition pause = new PauseTransition(Duration.millis(i * 100));
            pause.setOnFinished(e -> {
                FadeTransition fade = new FadeTransition(Duration.millis(500), carte);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.play();
                
                TranslateTransition translate = new TranslateTransition(Duration.millis(500), carte);
                translate.setFromY(30);
                translate.setToY(0);
                translate.play();
            });
            pause.play();
        }
    }
    
    private void animerEntree() {
        FadeTransition fade = new FadeTransition(Duration.millis(800), lblDateActuelle);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    @FXML
    private void ouvrirAccueil() {
        System.out.println("Déjà sur la page d'accueil");
    }

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
        ouvrirFenetre("/GestionActiviteReservation.fxml", "Module Activités & Réservations");
    }
    
    @FXML
    private void ouvrirGestionZones() {
        ouvrirFenetre("/gestionzonespr.fxml", "Gestion des zones protégées");
    }

    @FXML
    private void ouvrirGestionUser() {
        ouvrirFenetre("/gestionUsers.fxml", "Gestion des utilisateurs");
    }
    
    

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

