package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import tn.edu.esprit.entities.SurveillanceZone;
import tn.edu.esprit.services.ServiceSurv;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AjouterSurveillance {

    @FXML
    private TextField tfDate;

    @FXML
    private TextField tfObservation;

    @FXML
    private TextField tfIdZone;

    private ServiceSurv service = new ServiceSurv();

    @FXML
    public void initialize() {

        // Empêcher les lettres dans ID Zone
        tfIdZone.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfIdZone.setText(oldValue);
            }
        });
    }

    @FXML
    private void ajouterSurveillance() {

        String dateText = tfDate.getText().trim();
        String observation = tfObservation.getText().trim();
        String idZoneText = tfIdZone.getText().trim();

        boolean isValid = true;

        // Reset styles
        tfDate.setStyle("");
        tfObservation.setStyle("");
        tfIdZone.setStyle("");

        //  Vérification date
        if (dateText.isEmpty()) {
            tfDate.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            try {
                LocalDate.parse(dateText); // format YYYY-MM-DD
            } catch (DateTimeParseException e) {
                showAlert("Format date invalide ! Utilisez : YYYY-MM-DD");
                tfDate.setStyle("-fx-border-color: red;");
                return;
            }
        }

        //  Vérification observation
        if (observation.isEmpty()) {
            tfObservation.setStyle("-fx-border-color: red;");
            isValid = false;
        } else if (observation.length() < 5) {
            showAlert("L'observation doit contenir au moins 5 caractères.");
            tfObservation.setStyle("-fx-border-color: red;");
            return;
        }

        //  Vérification ID Zone
        if (idZoneText.isEmpty()) {
            tfIdZone.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (!isValid) {
            showAlert("Veuillez corriger les champs en rouge !");
            return;
        }

        int idZone = Integer.parseInt(idZoneText);

        SurveillanceZone s = new SurveillanceZone(dateText, observation, idZone);

        boolean ajoutReussi = service.ajouter(s);

        if (!ajoutReussi) {
            showAlertError("La zone avec cet ID n'existe pas !");
            return;
        }

        showSuccess("Surveillance ajoutée avec succès ! 🔍🌱");

        tfDate.clear();
        tfObservation.clear();
        tfIdZone.clear();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlertError(String message) { 
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}