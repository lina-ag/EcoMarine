package controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.edu.esprit.entities.User;

public class AccueilController {

    @FXML
    private Label lblDateActuelle;
    
    @FXML
    private Label lblMessageBienvenue;
    
    @FXML
    private Label lblNomUtilisateur;
    
    @FXML
    private Label lblRole;
    
    @FXML
    private HBox cardsContainer;
    
    private User utilisateurConnecte;

    @FXML
    public void initialize() {
        System.out.println("✅ Page d'accueil chargée avec les 3 modules");
        
        // Afficher la date actuelle
        afficherDateActuelle();
        
        // Afficher un message de bienvenue personnalisé
        afficherMessageBienvenue();
        
        // Animer les cartes
        animerCartes();
        
        // Ajouter des animations d'entrée
        animerEntree();
    }
    
    // 🔥 Méthode pour définir l'utilisateur connecté
    public void setUtilisateurConnecte(User user) {
        this.utilisateurConnecte = user;
        
        if (user != null) {
            // Afficher le nom de l'utilisateur
            if (lblNomUtilisateur != null) {
                lblNomUtilisateur.setText(user.getPrenom() + " " + user.getNom());
            }
            
            // Afficher le rôle avec une icône appropriée
            if (lblRole != null) {
                String role = user.getRole().toLowerCase();
                if (role.contains("chercheur")) {
                    lblRole.setText("👨‍🔬 Chercheur");
                    lblRole.setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold;");
                } else if (role.contains("admin")) {
                    lblRole.setText("👑 Administrateur");
                    lblRole.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
                } else {
                    lblRole.setText("👤 Utilisateur");
                    lblRole.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                }
            }
            
            // Personnaliser le message de bienvenue
            String message = getMessagePersonnalise(user);
            lblMessageBienvenue.setText(message);
            
            System.out.println("✅ Utilisateur connecté dans Accueil: " + user.getEmail() + " (" + user.getRole() + ")");
        } else {
            if (lblNomUtilisateur != null) {
                lblNomUtilisateur.setText("Invité");
            }
            lblMessageBienvenue.setText("Bienvenue sur EcoMarine ! 🌊");
        }
    }
    
    // 🔥 Message personnalisé selon l'heure et le rôle
    private String getMessagePersonnalise(User user) {
        LocalTime now = LocalTime.now();
        String heureMessage;
        
        if (now.isBefore(LocalTime.NOON)) {
            heureMessage = "Bonjour";
        } else if (now.isBefore(LocalTime.of(18, 0))) {
            heureMessage = "Bon après-midi";
        } else {
            heureMessage = "Bonsoir";
        }
        
        String prenom = user.getPrenom();
        String role = user.getRole().toLowerCase();
        
        if (role.contains("chercheur")) {
            return heureMessage + ", " + prenom + " ! 🔬 Prêt pour vos recherches marines ?";
        } else if (role.contains("admin")) {
            return heureMessage + ", " + prenom + " ! 👑 Gestionnaire de l'écosystème EcoMarine";
        } else {
            return heureMessage + ", " + prenom + " ! 🌊 Bienvenue sur EcoMarine";
        }
    }
    
    private void afficherDateActuelle() {
        LocalDate aujourdhui = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy");
        String dateFormatee = aujourdhui.format(formatter);
        dateFormatee = dateFormatee.substring(0, 1).toUpperCase() + dateFormatee.substring(1);
        lblDateActuelle.setText("📅 " + dateFormatee);
    }
    
    private void afficherMessageBienvenue() {
        if (utilisateurConnecte != null) {
            String message = getMessagePersonnalise(utilisateurConnecte);
            lblMessageBienvenue.setText(message);
        } else {
            lblMessageBienvenue.setText("Bienvenue sur EcoMarine ! 🌊");
        }
    }
    
    private void animerCartes() {
        if (cardsContainer == null) return;
        
        for (int i = 0; i < cardsContainer.getChildren().size(); i++) {
            VBox carte = (VBox) cardsContainer.getChildren().get(i);
            
            // Animation de survol
            carte.setOnMouseEntered(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), carte);
                scale.setToX(1.05);
                scale.setToY(1.05);
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
        if (lblDateActuelle != null) {
            FadeTransition fade = new FadeTransition(Duration.millis(800), lblDateActuelle);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }
        
        if (lblMessageBienvenue != null) {
            ScaleTransition scale = new ScaleTransition(Duration.millis(500), lblMessageBienvenue);
            scale.setFromX(0.8);
            scale.setFromY(0.8);
            scale.setToX(1);
            scale.setToY(1);
            scale.play();
        }
    }

    // ========================
    // MÉTHODES DE NAVIGATION
    // ========================
    
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
        ouvrirFenetre("/AfficherActivites.fxml", "Liste des activités");
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
        // Vérifier si l'utilisateur est admin
        if (utilisateurConnecte != null && utilisateurConnecte.getRole().toLowerCase().contains("admin")) {
            ouvrirFenetre("/gestionUsers.fxml", "Gestion des utilisateurs");
        } else {
            showAlert("Accès refusé", "Vous n'avez pas les droits pour accéder à cette section.");
        }
    }
    
    @FXML
    private void ouvrirProfil() {
        ouvrirFenetreAvecDonnees("/Profil.fxml", "Mon profil", utilisateurConnecte);
    }
    
    @FXML
    private void ouvrirMesReservations() {
        ouvrirFenetreAvecDonnees("/MesReservations.fxml", "Mes réservations", utilisateurConnecte);
    }
    
    @FXML
    private void deconnexion() {
        try {
            // Fermer la fenêtre actuelle
            Stage stage = (Stage) lblMessageBienvenue.getScene().getWindow();
            stage.close();
            
            // Ouvrir la fenêtre de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignIn.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("Connexion - EcoMarine");
            loginStage.setScene(new Scene(root));
            loginStage.show();
            
            System.out.println("✅ Déconnexion réussie");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la déconnexion");
        }
    }

    // ========================
    // MÉTHODES UTILITAIRES
    // ========================
    
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
            System.err.println("❌ Erreur lors de l'ouverture de : " + fxml);
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre: " + titre);
        }
    }
    
    private void ouvrirFenetreAvecDonnees(String fxml, String titre, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            
            // Transmettre l'utilisateur au contrôleur si nécessaire
            if (fxml.contains("Profil") || fxml.contains("MesReservations")) {
                try {
                    Object controller = loader.getController();
                    controller.getClass().getMethod("setUtilisateurConnecte", User.class).invoke(controller, user);
                } catch (Exception e) {
                    System.out.println("Le contrôleur n'a pas de méthode setUtilisateurConnecte");
                }
            }
            
            Stage stage = new Stage();
            stage.setTitle(titre);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de l'ouverture de : " + fxml);
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre: " + titre);
        }
    }
    
    private void showAlert(String titre, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // ========================
    // GETTERS
    // ========================
    
    public User getUtilisateurConnecte() {
        return utilisateurConnecte;
    }
}