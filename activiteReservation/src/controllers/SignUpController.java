package controllers;

import java.io.IOException;

import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import tn.edu.esprit.entities.User;
import tn.edu.esprit.services.ServiceUser;
import javafx.scene.Node;

public class SignUpController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField telephoneField;
    @FXML private DatePicker dateNaissancePicker;
    @FXML private ComboBox<String> roleCombo;

    private ServiceUser service = new ServiceUser();
    @FXML
    public void initialize() {

        roleCombo.getItems().addAll(
                "chercheur",
                "Utilisateur"
        );
        }
    @FXML
    void signUp() {
    	// Champs vides
        if (nomField.getText().isEmpty() ||
            prenomField.getText().isEmpty() ||
            emailField.getText().isEmpty() ||
            passwordField.getText().isEmpty() ||
            telephoneField.getText().isEmpty() ||
            dateNaissancePicker.getValue() == null||
            roleCombo.getValue() == null) { 

            showAlert1("Champs vides", "Veuillez remplir tous les champs.");
            return;
        }
        // Email valide
        String email = emailField.getText();
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert1("Email invalide", "Veuillez saisir un email valide.");
            return;
        }
        // Mot de passe ≥ 8caractères
        if (passwordField.getText().length() < 8) {
            showAlert1("Mot de passe court", "Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }

        // Téléphone tunisien : commence par 2,5,9 et contient 8 chiffres
        String tel = telephoneField.getText();
        if (!tel.matches("^(2|5|9)[0-9]{7}$")) {
            showAlert1("Téléphone invalide", "Numéro tunisien valide requis (8 chiffres : commence par 2,5 ou 9).");
            return;
        }
        // Date de naissance
        LocalDate dn = dateNaissancePicker.getValue();
        if (dn.isAfter(LocalDate.now())) {
            showAlert1("Date invalide", "La date de naissance ne peut pas être dans le futur.");
            return;
        }
        User u = new User();
        u.setNom(nomField.getText());
        u.setPrenom(prenomField.getText());
        u.setEmail(emailField.getText());
        u.setMotDePasse(passwordField.getText());
        u.setTelephone(telephoneField.getText());
        u.setRole("Utilisateur,chercheur"); // obligatoire
        u.setDateNaissance(dateNaissancePicker.getValue()); // jamais null maintenant

        service.ajouter(u);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Compte créé avec succès !");
        }
        private void showAlert(Alert.AlertType type, String title, String message) {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
        private void showAlert1(String titre, String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(titre);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
        @FXML
        private void openSignIn(MouseEvent event) throws IOException {
            Parent root = FXMLLoader.load(getClass().getResource("/SignIn.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        }
    }