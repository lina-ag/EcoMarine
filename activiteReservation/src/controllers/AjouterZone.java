package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.edu.esprit.entities.ZoneProtegee;
import tn.edu.esprit.services.ServiceZoneP;

public class AjouterZone {

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfCategorie;

    @FXML
    private ComboBox<String> cbStatut;

    private ServiceZoneP service = new ServiceZoneP();
    
    

    @FXML
    public void initialize() {

        cbStatut.getItems().addAll(
                "Active",
                "En restauration",
                "Menacée"
        );
    }

    @FXML
    private void ajouterZone() {

        String nom = tfNom.getText().trim();
        String categorie = tfCategorie.getText().trim();
        String statut = cbStatut.getValue();

        boolean isValid = true;

        
        tfNom.setStyle("");
        tfCategorie.setStyle("");
        cbStatut.setStyle("");

        //  Vérification Nom
        if (nom.isEmpty()) {
            tfNom.setStyle("-fx-border-color: red;");
            isValid = false;
        } else if (nom.length() < 3) {
            showAlert("Le nom doit contenir au moins 3 caractères.");
            tfNom.setStyle("-fx-border-color: red;");
            return;
        } else if (!nom.matches("[a-zA-ZÀ-ÿ\\s]+")) {
            showAlert("Le nom ne doit pas contenir de chiffres.");
            tfNom.setStyle("-fx-border-color: red;");
            return;
        }

        //  Vérification Catégorie
        if (categorie.isEmpty()) {
            tfCategorie.setStyle("-fx-border-color: red;");
            isValid = false;
        } else if (categorie.length() < 3) {
            showAlert("La catégorie doit contenir au moins 3 caractères.");
            tfCategorie.setStyle("-fx-border-color: red;");
            return;
        }

        // 🔴 Vérification Statut
        if (statut == null) {
            cbStatut.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (!isValid) {
            showAlert("Veuillez corriger les champs en rouge !");
            return;
        }
        
     //  Vérification Catégorie
        if (categorie.isEmpty()) {
            tfCategorie.setStyle("-fx-border-color: red;");
            isValid = false;
        } else if (categorie.length() < 3) {
            showAlert("La catégorie doit contenir au moins 3 caractères.");
            tfCategorie.setStyle("-fx-border-color: red;");
            return;
        } else if (!categorie.matches("[a-zA-ZÀ-ÿ\\s]+")) {
            showAlert("La catégorie ne doit contenir que des lettres.");
            tfCategorie.setStyle("-fx-border-color: red;");
            return;
        }

        
        ZoneProtegee z = new ZoneProtegee(nom, categorie, statut);

       
        service.ajouter(z);

        showSuccess("Zone protégée ajoutée avec succès ! 🌱");
        viderChamps();

        
        tfNom.clear();
        tfCategorie.clear();
        cbStatut.setValue(null);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void viderChamps() {
        tfNom.clear();
        tfCategorie.clear();
        cbStatut.getSelectionModel().clearSelection();

        // enlever les bordures rouges
        tfNom.setStyle("");
        tfCategorie.setStyle("");
        cbStatut.setStyle("");
    }
    
    @FXML
    private void retournerGestionZones() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionZonesPr.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gestion des zones protégées");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);

            // fermer la fenêtre actuelle
            Stage currentStage = (Stage) tfNom.getScene().getWindow();
            currentStage.close();

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}