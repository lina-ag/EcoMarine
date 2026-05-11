package controllers;

import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.edu.esprit.entities.User;
import tn.edu.esprit.services.ServiceUser;

import java.time.LocalDate;

public class ModifierUser {

    @FXML
    private TextField idField;

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

    private ServiceUser service = new ServiceUser();

    private User utilisateurActuel;

    // ----------------------------------------------------------
    //     Réception de l'utilisateur sélectionné
    // ----------------------------------------------------------
    public void setUser(User u) {
        this.utilisateurActuel = u;

        idField.setText(String.valueOf(u.getIdUtilisateur()));
        nomField.setText(u.getNom());
        prenomField.setText(u.getPrenom());
        emailField.setText(u.getEmail());
        motDePasseField.setText(u.getMotDePasse());
        telephoneField.setText(u.getTelephone());
        roleCombo.setValue(u.getRole());
        dateNaissancePicker.setValue(u.getDateNaissance());
    }

    // ----------------------------------------------------------
    //                 VALIDATION DES CHAMPS
    // ----------------------------------------------------------
    private boolean validateInputs() {

        if (nomField.getText().isEmpty() ||
            prenomField.getText().isEmpty() ||
            emailField.getText().isEmpty() ||
            motDePasseField.getText().isEmpty() ||
            telephoneField.getText().isEmpty() ||
            roleCombo.getValue() == null ||
            dateNaissancePicker.getValue() == null) {

            showAlert("Champs vides", "Tous les champs doivent être remplis !");
            return false;
        }

        // Email valide
        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert("Email invalide", "Veuillez saisir un email valide.");
            return false;
        }

        // Téléphone tunisien : commence par 2,5 ou 9 (8 chiffres)
        if (!telephoneField.getText().matches("^(2|5|9)[0-9]{7}$")) {
            showAlert("Téléphone invalide",
                    "Le numéro doit contenir 8 chiffres et commencer par 2, 5 ou 9.");
            return false;
        }

        // Mot de passe >= 6 caractères
        if (motDePasseField.getText().length() < 6) {
            showAlert("Mot de passe trop court",
                    "Le mot de passe doit contenir au moins 6 caractères.");
            return false;
        }

        // Date de naissance non future
        if (dateNaissancePicker.getValue().isAfter(LocalDate.now())) {
            showAlert("Date invalide", "La date de naissance ne peut pas être dans le futur.");
            return false;
        }

        return true;
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ----------------------------------------------------------
    //                    Bouton Modifier
    // ----------------------------------------------------------
    @FXML
    void modifierUser() {

        if (utilisateurActuel != null) {

            // Empêcher la modification si données invalides
            if (!validateInputs()) return;

            utilisateurActuel.setNom(nomField.getText());
            utilisateurActuel.setPrenom(prenomField.getText());
            utilisateurActuel.setEmail(emailField.getText());
            utilisateurActuel.setMotDePasse(motDePasseField.getText());
            utilisateurActuel.setTelephone(telephoneField.getText());
            utilisateurActuel.setRole(roleCombo.getValue());
            utilisateurActuel.setDateNaissance(dateNaissancePicker.getValue());

            service.modifier(utilisateurActuel);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Modification réussie");
            alert.setHeaderText(null);
            alert.setContentText("Utilisateur modifié avec succès !");
            alert.showAndWait();

            Stage stage = (Stage) idField.getScene().getWindow();
            stage.close();
        }
    }

    // ----------------------------------------------------------
    //                    Bouton Annuler
    // ----------------------------------------------------------
    @FXML
    void annuler() {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }

    // ----------------------------------------------------------
    //                 Initialisation des rôles
    // ----------------------------------------------------------
    @FXML
    public void initialize() {
        roleCombo.getItems().addAll(
                "Admin",
                "Responsable",
                "Utilisateur"
        );
    }
}