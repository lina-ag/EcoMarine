package controllers;

import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.edu.esprit.entities.User;
import javafx.scene.Node;
import javafx.event.ActionEvent;

public class HomeController {

    // 🔥 Déclarer le champ utilisateurConnecte
    private User utilisateurConnecte;

    // 🔹 Bouton : Sign In
    @FXML
    private void openSignIn(ActionEvent event) {
        ouvrirFenetreEtFermer(event, "/SignIn.fxml", "Sign In");
    }

    // 🔹 Bouton : Sign Up
    @FXML
    private void openSignUp(ActionEvent event) {
        ouvrirFenetreEtFermer(event, "/SignUp.fxml", "Sign Up");
    }

    // 🌟 Méthode pour ouvrir une nouvelle fenêtre et fermer l'ancienne
    private void ouvrirFenetreEtFermer(ActionEvent event, String cheminFXML, String titre) {
        try {
            // Ouvrir nouvelle fenêtre
            Parent root = FXMLLoader.load(getClass().getResource(cheminFXML));
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle(titre);
            newStage.show();

            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'ouverture de la fenêtre : " + cheminFXML);
            e.printStackTrace();
        }
    }
    
    // 🔥 Setter pour utilisateurConnecte
    public void setUtilisateurConnecte(User user) {
        this.utilisateurConnecte = user;
        System.out.println("Utilisateur connecté: " + (user != null ? user.getEmail() : "null"));
        
        // Optionnel: Mettre à jour l'interface avec le nom de l'utilisateur
        // Si vous avez un Label dans votre FXML, vous pouvez l'afficher
        // labelNomUtilisateur.setText(user.getPrenom() + " " + user.getNom());
    }
    
    // 🔥 Getter pour utilisateurConnecte
    public User getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

}