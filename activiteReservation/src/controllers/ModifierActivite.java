package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.edu.esprit.entities.ActiviteEcologique;
import tn.edu.esprit.services.ServiceActivite;

public class ModifierActivite {

    @FXML
    private TextField idField;

    @FXML
    private TextField nomField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField dateField;

    @FXML
    private TextField capaciteField;

    private ServiceActivite service = new ServiceActivite();
    private ActiviteEcologique activiteActuelle;

    public void setActivite(ActiviteEcologique a) {
        this.activiteActuelle = a;

        idField.setText(String.valueOf(a.getIdActivite()));
        nomField.setText(a.getNom());
        descriptionField.setText(a.getDescription());
        dateField.setText(a.getDate());
        capaciteField.setText(String.valueOf(a.getCapacite()));
    }

    @FXML
    void modifierActivite() {

        if (activiteActuelle != null) {

            activiteActuelle.setNom(nomField.getText());
            activiteActuelle.setDescription(descriptionField.getText());
            activiteActuelle.setDate(dateField.getText());
            activiteActuelle.setCapacite(Integer.parseInt(capaciteField.getText()));

            service.modifier(activiteActuelle);

            Stage stage = (Stage) idField.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    void annuler() {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }
}