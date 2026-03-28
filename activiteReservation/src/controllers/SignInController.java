package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.edu.esprit.entities.User;
import tn.edu.esprit.services.ServiceUser;

public class SignInController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private ServiceUser service = new ServiceUser();
    
    // Stocker l'utilisateur connecté
    private static User utilisateurConnecte;

    @FXML
    void login() {

        String email = emailField.getText();
        String mdp = passwordField.getText();

        if (email.isEmpty() || mdp.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs !");
            return;
        }

        // 🔥 ADMIN PAR DÉFAUT (hardcoded)
        if (email.equals("admin@gmail.com") && mdp.equals("admin")) {
            User admin = new User();
            admin.setNom("Admin");
            admin.setPrenom("System");
            admin.setEmail("admin@gmail.com");
            admin.setRole("admin");

            utilisateurConnecte = admin;

            ouvrirGestionUsers();
            return;
        }

        // 🔽 login normal
        User u = service.login(email, mdp);

        if (u == null) {
            showAlert("Connexion échouée", "Email ou mot de passe incorrect !");
            return;
        }

        utilisateurConnecte = u;

        if (estAdmin(u)) {
            ouvrirGestionUsers();
        } else {
            ouvrirGestionActiviteReservation();
        }
    }
    
    private void ouvrirGestionUsers() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionUsers.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Optionnel : passer l'utilisateur admin au contrôleur
            GestionUsers controller = loader.getController();
            controller.setUtilisateurConnecte(utilisateurConnecte);
            
            stage.setScene(scene);
            stage.setTitle("Gestion des utilisateurs - Admin");
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir GestionUsers : " + e.getMessage());
        }
    }
    
    private void ouvrirGestionActiviteReservation() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/acceuil.fxml")));
            stage.setScene(scene);
            stage.setTitle("Accueil");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir l'accueil : " + e.getMessage());
        }
    }

    @FXML
    void openSignUp() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/SignUp.fxml")));
            stage.setScene(scene);
            stage.setTitle("Inscription");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔍 Vérification du rôle admin
    private boolean estAdmin(User user) {
        if (user == null || user.getRole() == null) return false;
        
        String role = user.getRole().toLowerCase().trim();
        return role.equals("admin") || 
               role.equals("administrateur") || 
               role.contains("admin");
    } 
    
    // Getter static pour accéder à l'utilisateur connecté
    public static User getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    private void showAlert(String titre, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titre);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }
}