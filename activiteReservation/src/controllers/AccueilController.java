package controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.edu.esprit.entities.User;

public class AccueilController {

    // ---- Topbar ----
    @FXML private Label lblDateActuelle;
    @FXML private Label lblMessageBienvenue;
    @FXML private Label lblNomUtilisateur;
    @FXML private Label lblRole;
    @FXML private Label lblAvatar;

    // ---- KPI labels ----
    @FXML private Label lblTotalActivites;
    @FXML private Label lblTotalReservations;
    @FXML private Label lblTotalZones;
    @FXML private Label lblTotalTaches;

    // ---- KPI progress bars ----
    @FXML private StackPane barActivites;
    @FXML private StackPane barReservations;
    @FXML private StackPane barZones;
    @FXML private StackPane barTaches;

    // ---- Module badges ----
    @FXML private Label badgeActivites;
    @FXML private Label badgeTaches;
    @FXML private Label badgeZones;

    private User utilisateurConnecte;

    @FXML
    public void initialize() {
        System.out.println("✅ Page d'accueil chargée avec les 3 modules");
        afficherDateActuelle();
        afficherMessageBienvenue();
        // Valeurs de démo — à remplacer par de vraies requêtes BD si souhaité
        mettreAJourKPIs(24, 142, 6, 7);
        animerEntree();
    }

    // ================================================================
    // UTILISATEUR CONNECTÉ
    // ================================================================

    public void setUtilisateurConnecte(User user) {
        this.utilisateurConnecte = user;

        if (user != null) {
            if (lblNomUtilisateur != null)
                lblNomUtilisateur.setText(user.getPrenom() + " " + user.getNom());

            // Initiales dans l'avatar
            if (lblAvatar != null) {
                String initiales = "";
                if (user.getPrenom() != null && !user.getPrenom().isEmpty())
                    initiales += user.getPrenom().charAt(0);
                if (user.getNom() != null && !user.getNom().isEmpty())
                    initiales += user.getNom().charAt(0);
                lblAvatar.setText(initiales.toUpperCase());
            }

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

            lblMessageBienvenue.setText(getMessagePersonnalise(user));
            System.out.println("✅ Utilisateur connecté dans Accueil: " + user.getEmail() + " (" + user.getRole() + ")");
        } else {
            if (lblNomUtilisateur != null) lblNomUtilisateur.setText("Invité");
            lblMessageBienvenue.setText("Bienvenue sur EcoMarine ! 🌊");
        }
    }

    // ================================================================
    // KPI
    // ================================================================

    private void mettreAJourKPIs(int activites, int reservations, int zones, int taches) {
        setKPI(lblTotalActivites, badgeActivites, barActivites, activites, 50);
        setKPI(lblTotalReservations, null, barReservations, reservations, 200);
        setKPI(lblTotalZones, badgeZones, barZones, zones, 10);
        setKPI(lblTotalTaches, badgeTaches, barTaches, taches, 20);
    }

    private void setKPI(Label valLabel, Label badge, StackPane bar, int val, int max) {
        if (valLabel != null) valLabel.setText(String.valueOf(val));
        if (badge != null) badge.setText(String.valueOf(val));
        // Animer la barre de progression
        if (bar != null) {
            double pct = Math.min(1.0, (double) val / max);
            // La barre est dans un StackPane — on anime sa largeur via le prefWidth
            // Le parent StackPane (accueil-kpi-bar-bg) a une largeur variable donc on fixe
            // via un listener post-layout ou on utilise une valeur approximative
            PauseTransition pause = new PauseTransition(Duration.millis(300));
            pause.setOnFinished(e -> {
                double parentWidth = bar.getParent().getBoundsInLocal().getWidth();
                if (parentWidth <= 0) parentWidth = 200; // fallback
                bar.setPrefWidth(parentWidth * pct);
            });
            pause.play();
        }
    }

    // ================================================================
    // DATE & MESSAGES
    // ================================================================

    private void afficherDateActuelle() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH);
        String date = today.format(fmt);
        date = date.substring(0, 1).toUpperCase() + date.substring(1);
        if (lblDateActuelle != null) lblDateActuelle.setText("📅 " + date);
    }

    private void afficherMessageBienvenue() {
        if (utilisateurConnecte != null) {
            lblMessageBienvenue.setText(getMessagePersonnalise(utilisateurConnecte));
        } else {
            if (lblMessageBienvenue != null)
                lblMessageBienvenue.setText("Bienvenue sur EcoMarine ! 🌊");
        }
    }

    private String getMessagePersonnalise(User user) {
        LocalTime now = LocalTime.now();
        String heure = now.isBefore(LocalTime.NOON) ? "Bonjour"
                     : now.isBefore(LocalTime.of(18, 0)) ? "Bon après-midi"
                     : "Bonsoir";
        String prenom = user.getPrenom();
        String role = user.getRole().toLowerCase();
        if (role.contains("chercheur"))
            return heure + ", " + prenom + " ! 🔬 Prêt pour vos recherches marines ?";
        else if (role.contains("admin"))
            return heure + ", " + prenom + " ! 👑 Gestionnaire de l'écosystème EcoMarine";
        else
            return heure + ", " + prenom + " ! 🌊 Bienvenue sur EcoMarine";
    }

    // ================================================================
    // ANIMATIONS D'ENTRÉE
    // ================================================================

    private void animerEntree() {
        animerLabel(lblDateActuelle, 0);
        animerLabel(lblMessageBienvenue, 100);
        animerLabel(lblNomUtilisateur, 200);
    }

    private void animerLabel(javafx.scene.Node node, int delayMs) {
        if (node == null) return;
        node.setOpacity(0);
        PauseTransition pause = new PauseTransition(Duration.millis(delayMs));
        pause.setOnFinished(e -> {
            FadeTransition fade = new FadeTransition(Duration.millis(500), node);
            fade.setFromValue(0);
            fade.setToValue(1);
            TranslateTransition slide = new TranslateTransition(Duration.millis(500), node);
            slide.setFromY(15);
            slide.setToY(0);
            fade.play();
            slide.play();
        });
        pause.play();
    }

    // ================================================================
    // NAVIGATION
    // ================================================================

    @FXML private void ouvrirAccueil() { System.out.println("Déjà sur la page d'accueil"); }

    @FXML
    private void ouvrirModuleActivitesReservations() {
        ouvrirFenetre("/GestionActiviteReservation.fxml", "Module Activités & Réservations");
    }

    @FXML
    private void ouvrirGestionZones() {
        ouvrirFenetre("/gestionzonespr.fxml", "Gestion des zones protégées");
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
            Stage stage = (Stage) lblMessageBienvenue.getScene().getWindow();
            stage.close();
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

    @FXML private void ouvrirAjouterActivite()   { ouvrirFenetre("/GestionActiviteReservation.fxml", "Ajouter une activité"); }
    @FXML private void ouvrirAfficherActivites() { ouvrirFenetre("/AfficherActivites.fxml", "Liste des activités"); }
    @FXML private void ouvrirCalendrier()         { ouvrirFenetre("/Calendrier.fxml", "Calendrier des activités"); }

    @FXML
    private void ouvrirGestionUser() {
        if (utilisateurConnecte != null && utilisateurConnecte.getRole().toLowerCase().contains("admin")) {
            ouvrirFenetre("/gestionUsers.fxml", "Gestion des utilisateurs");
        } else {
            showAlert("Accès refusé", "Vous n'avez pas les droits pour accéder à cette section.");
        }
    }

    // ================================================================
    // UTILITAIRES
    // ================================================================

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
            if (fxml.contains("Profil") || fxml.contains("MesReservations")) {
                try {
                    Object controller = loader.getController();
                    controller.getClass().getMethod("setUtilisateurConnecte", User.class).invoke(controller, user);
                } catch (Exception ex) {
                    System.out.println("Le contrôleur n'a pas de méthode setUtilisateurConnecte");
                }
            }
            Stage stage = new Stage();
            stage.setTitle(titre);
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
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
    

    
    // ========================
    // GETTERS
    // ========================
    
    public User getUtilisateurConnecte() {
        return utilisateurConnecte;
    }


   

}