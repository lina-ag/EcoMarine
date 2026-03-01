package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.edu.esprit.entities.SurveillanceZone;
import tn.edu.esprit.services.ServiceSurv;

public class ModifierSurveillance {

    @FXML
    private TextField idSurvField;

    @FXML
    private TextField dateField;

    @FXML
    private TextField observationField;

    @FXML
    private TextField idZoneField;

    private ServiceSurv service = new ServiceSurv();

    private SurveillanceZone surveillanceActuelle;

    // 🔹 Méthode pour recevoir la surveillance depuis la TableView
    public void setSurveillance(SurveillanceZone s) {
        this.surveillanceActuelle = s;

        idSurvField.setText(String.valueOf(s.getIdSurveillance()));
        dateField.setText(s.getDateSurveillance());
        observationField.setText(s.getObservation());
        idZoneField.setText(String.valueOf(s.getIdZone()));
    }

    // 🔹 Bouton Enregistrer
    @FXML
    void modifierSurveillance() {

        if (surveillanceActuelle != null) {

            surveillanceActuelle.setDateSurveillance(dateField.getText());
            surveillanceActuelle.setObservation(observationField.getText());
            surveillanceActuelle.setIdZone(Integer.parseInt(idZoneField.getText()));

            service.modifier(surveillanceActuelle);

            // fermer la fenêtre après modification
            Stage stage = (Stage) idSurvField.getScene().getWindow();
            stage.close();
        }
    }

    // 🔹 Bouton Annuler
    @FXML
    void annuler() {
        Stage stage = (Stage) idSurvField.getScene().getWindow();
        stage.close();
    }
}