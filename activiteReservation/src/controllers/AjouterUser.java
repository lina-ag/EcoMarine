package controllers;

import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.edu.esprit.entities.User;
import tn.edu.esprit.services.ServiceUser;

import java.time.LocalDate;

public class AjouterUser {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField motDePasseField;

    @FXML
    private TextField telephoneField;

    @FXML
    private ComboBox<String> roleCombo;

    @FXML
    private DatePicker dateNaissancePicker;

    private ServiceUser serviceUser = new ServiceUser();

    @FXML
    public void initialize() {

        roleCombo.getItems().addAll(
                "Admin",
                "chercheur",
                "Utilisateur"
        );
    }

    // -----------------------------
    // VALIDATION DES CHAMPS
    // -----------------------------
    private boolean validateInputs() {

        // Champs vides
        if (nomField.getText().isEmpty() ||
            prenomField.getText().isEmpty() ||
            emailField.getText().isEmpty() ||
            motDePasseField.getText().isEmpty() ||
            telephoneField.getText().isEmpty() ||
            dateNaissancePicker.getValue() == null ||
            roleCombo.getValue() == null) {

            showAlert("Champs vides", "Veuillez remplir tous les champs.");
            return false;
        }

        // Email valide
        String email = emailField.getText();
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert("Email invalide", "Veuillez saisir un email valide.");
            return false;
        }

        // Téléphone tunisien : commence par 2,5,9 et contient 8 chiffres
        String tel = telephoneField.getText();
        if (!tel.matches("^(2|5|9)[0-9]{7}$")) {
            showAlert("Téléphone invalide", "Numéro tunisien valide requis (8 chiffres : commence par 2,5 ou 9).");
            return false;
        }

        // Mot de passe ≥ 8caractères
        if (motDePasseField.getText().length() < 6) {
            showAlert("Mot de passe court", "Le mot de passe doit contenir au moins 6 caractères.");
            return false;
        }

        // Date de naissance
        LocalDate dn = dateNaissancePicker.getValue();
        if (dn.isAfter(LocalDate.now())) {
            showAlert("Date invalide", "La date de naissance ne peut pas être dans le futur.");
            return false;
        }

        return true;
    }

    // -----------------------------
    // FENÊTRE ALERT
    // -----------------------------
    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // -----------------------------
    // BOUTON AJOUTER UTILISATEUR
    // -----------------------------
    @FXML
    void ajouterUser() {

        // Vérification complète
        if (!validateInputs()) {
            return;
        }

        // Création objet
        User u = new User(
                0,
                nomField.getText(),
                prenomField.getText(),
                emailField.getText(),
                motDePasseField.getText(),
                telephoneField.getText(),
                roleCombo.getValue(),
                dateNaissancePicker.getValue()
        );

        // Ajout DB
        serviceUser.ajouter(u);

        // Confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Utilisateur ajouté avec succès !");
        alert.showAndWait();

        // Fermer la fenêtre
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    @FXML
    void annuler() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}