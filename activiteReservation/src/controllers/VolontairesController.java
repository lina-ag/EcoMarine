package controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.edu.esprit.entities.ActionNettoyage;
import tn.edu.esprit.entities.Volontaire;
import tn.edu.esprit.services.ServiceActionNettoyage;
import tn.edu.esprit.services.ServiceVolontaire;

public class VolontairesController {

    @FXML private ComboBox<ActionNettoyage> cbActions;

    @FXML private TableView<Volontaire> tableVol;
    @FXML private TableColumn<Volontaire, Number> colIdVol;
    @FXML private TableColumn<Volontaire, String> colNom;
    @FXML private TableColumn<Volontaire, String> colContact;

    @FXML private TextField txtNom;
    @FXML private TextField txtContact;

    private final ServiceActionNettoyage serviceAction = new ServiceActionNettoyage();
    private final ServiceVolontaire serviceVolontaire = new ServiceVolontaire();
    private final ObservableList<Volontaire> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cbActions.setItems(FXCollections.observableArrayList(serviceAction.findAll()));

        colIdVol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getIdVolontaire()));
        colNom.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNom()));
        colContact.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getContact()));

        tableVol.setItems(data);

        tableVol.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selectedVolontaire) -> {
            if (selectedVolontaire != null) {
                txtNom.setText(selectedVolontaire.getNom());
                txtContact.setText(selectedVolontaire.getContact());
            }
        });
    }

    @FXML
    private void loadVolontaires() {
        ActionNettoyage action = cbActions.getValue();
        if (action == null) return;

        data.setAll(serviceVolontaire.findByAction(action.getIdAction()));
    }

    @FXML
    private void addVol() {
        ActionNettoyage action = cbActions.getValue();
        if (action == null) {
            info("Choisis une action.");
            return;
        }

        String nom = txtNom.getText().trim();
        String contact = txtContact.getText().trim();

        if (nom.isEmpty()) {
            info("Nom obligatoire.");
            return;
        }

        serviceVolontaire.insert(nom, contact, action.getIdAction());
        loadVolontaires();
        clearVol();
    }

    @FXML
    private void updateVol() {
        Volontaire volontaire = tableVol.getSelectionModel().getSelectedItem();
        if (volontaire == null) {
            info("Choisis un volontaire.");
            return;
        }

        String nom = txtNom.getText().trim();
        String contact = txtContact.getText().trim();

        if (nom.isEmpty()) {
            info("Nom obligatoire.");
            return;
        }

        serviceVolontaire.update(volontaire.getIdVolontaire(), nom, contact);
        loadVolontaires();
    }

    @FXML
    private void deleteVol() {
        Volontaire volontaire = tableVol.getSelectionModel().getSelectedItem();
        if (volontaire == null) {
            info("Choisis un volontaire.");
            return;
        }

        serviceVolontaire.delete(volontaire.getIdVolontaire());
        loadVolontaires();
        clearVol();
    }

    @FXML
    private void clearVol() {
        txtNom.clear();
        txtContact.clear();
        tableVol.getSelectionModel().clearSelection();
    }

    private void info(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}