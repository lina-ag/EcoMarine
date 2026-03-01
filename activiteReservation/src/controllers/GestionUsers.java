package controllers;

import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.stage.Stage;
import tn.edu.esprit.entities.User;
import tn.edu.esprit.services.ServiceUser;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GestionUsers implements Initializable {
    
    // Labels pour les statistiques des rôles
    @FXML
    private Label adminsLabel;        // Pour le nombre d'admins
    
    @FXML
    private Label chercheursLabel;     // Pour le nombre de chercheurs
    
    @FXML
    private Label utilisateursLabel;   // Pour le nombre d'utilisateurs simples
    
    private User utilisateurConnecte;
    private ServiceUser serviceUser = new ServiceUser();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Charger les statistiques au démarrage
        chargerStatistiques();
    }
    
    // Méthode pour charger les statistiques depuis la base de données
    private void chargerStatistiques() {
        try {
            // Récupérer tous les utilisateurs
            List<User> tousLesUtilisateurs = serviceUser.getAll();
            
            int admins = 0;
            int chercheurs = 0;
            int utilisateurs = 0;
            
            // Compter par rôle
            for (User user : tousLesUtilisateurs) {
                String role = user.getRole() != null ? user.getRole().toLowerCase().trim() : "";
                
                if (role.contains("admin") || role.equals("administrateur")) {
                    admins++;
                } else if (role.contains("chercheur")) {
                    chercheurs++;
                } else {
                    // Tout ce qui n'est ni admin ni chercheur = utilisateur simple
                    utilisateurs++;
                }
            }
            
            // Mettre à jour les labels
            adminsLabel.setText(String.valueOf(admins));
            chercheursLabel.setText(String.valueOf(chercheurs));
            utilisateursLabel.setText(String.valueOf(utilisateurs));
            
            System.out.println("📊 Statistiques des rôles :");
            System.out.println("   Admins: " + admins);
            System.out.println("   Chercheurs: " + chercheurs);
            System.out.println("   Utilisateurs: " + utilisateurs);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des statistiques");
            e.printStackTrace();
            
            // Valeurs par défaut en cas d'erreur
            adminsLabel.setText("0");
            chercheursLabel.setText("0");
            utilisateursLabel.setText("0");
        }
    }
    
    // Méthode pour recevoir l'utilisateur admin connecté
    public void setUtilisateurConnecte(User user) {
        this.utilisateurConnecte = user;
        System.out.println("✅ Admin connecté: " + user.getEmail());
    }

    @FXML
    private void ouvrirAjouterUser() {
        ouvrirFenetre("/AjouterUser.fxml", "Ajouter un utilisateur");
    }

    @FXML
    private void ouvrirAfficherUsers() {
        ouvrirFenetre("/AfficherUsers.fxml", "Liste des utilisateurs");
    }

    private void ouvrirFenetre(String cheminFXML, String titre) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(cheminFXML));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titre);
            stage.show();
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'ouverture de la fenêtre : " + cheminFXML);
            e.printStackTrace();
        }
    }
    
    @FXML
    private void deconnexion(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/SignIn.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Méthode pour rafraîchir les statistiques (appelée après ajout/suppression)
    public void rafraichirStatistiques() {
        chargerStatistiques();
    }
}