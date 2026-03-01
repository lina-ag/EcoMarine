package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.edu.esprit.entities.ZoneProtegee;
import tn.edu.esprit.services.ServiceZoneP;

public class ModifierZone {

    @FXML
    private TextField idField;

    @FXML
    private TextField nomField;

    @FXML
    private TextField categorieField;

    @FXML
    private ComboBox<String> statutCombo;

    private ServiceZoneP service = new ServiceZoneP();

    private ZoneProtegee zoneActuelle;

    // 🔹 Méthode pour recevoir la zone depuis la TableView
    public void setZone(ZoneProtegee z) {
        this.zoneActuelle = z;

        idField.setText(String.valueOf(z.getIdZone()));
        nomField.setText(z.getNomZone());
        categorieField.setText(z.getCategorieZone());
        statutCombo.setValue(z.getStatut());
    }

    // 🔹 Bouton Enregistrer
    @FXML
    void modifierZone() {

        if (zoneActuelle != null) {

            zoneActuelle.setNomZone(nomField.getText());
            zoneActuelle.setCategorieZone(categorieField.getText());
            zoneActuelle.setStatut(statutCombo.getValue());

            service.modifier(zoneActuelle);

            // fermer la fenêtre après modification
            Stage stage = (Stage) idField.getScene().getWindow();
            stage.close();
        }
    }

    // 🔹 Bouton Annuler
    @FXML
    void annuler() {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }

    // 🔹 Initialisation du ComboBox
    @FXML
    public void initialize() {
        statutCombo.getItems().addAll(
                "Active",
                "Inactive",
                "Menacee",
                "Sous surveillance"
        );
    }
}