package controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import tn.edu.esprit.entities.ActiviteEcologique;
import tn.edu.esprit.services.ServiceActivite;

public class AjouterActivite {

    @FXML
    private TextField tfNom;
    @FXML
    private TextField tfDescription;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField tfCapacite;

    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
        
        datePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return formatter.format(date);
                }
                return "";
            }
            
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, formatter);
                }
                return null;
            }
        });
        
        tfCapacite.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfCapacite.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    @FXML
    private void ajouterActivite() {
        try {
            // Validation
            if (tfNom.getText() == null || tfNom.getText().trim().isEmpty()) {
                showAlert("Erreur", "Le nom de l'activité est requis");
                return;
            }

            if (datePicker.getValue() == null) {
                showAlert("Erreur", "La date est requise");
                return;
            }

            if (tfCapacite.getText() == null || tfCapacite.getText().trim().isEmpty()) {
                showAlert("Erreur", "La capacité est requise");
                return;
            }

            // Récupération des données
            String nom = tfNom.getText();
            String description = tfDescription.getText();
            LocalDate date = datePicker.getValue();
            int capacite = Integer.parseInt(tfCapacite.getText().trim());

            String dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE);

            ActiviteEcologique activite = new ActiviteEcologique(nom, description, dateString, capacite);

            ServiceActivite service = new ServiceActivite();
            service.ajouter(activite);

            showAlert("Succès", "Activité ajoutée avec succès !");

            // Fermer la fenêtre - les stats seront rafraîchies automatiquement
            Stage stage = (Stage) tfNom.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert("Erreur", "La capacité doit être un nombre valide");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}