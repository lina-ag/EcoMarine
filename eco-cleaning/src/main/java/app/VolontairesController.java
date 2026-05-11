package app;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class VolontairesController {

    @FXML private ComboBox<ActionNettoyage> cbActions;

    @FXML private TableView<Volontaire> tableVol;
    @FXML private TableColumn<Volontaire, Number> colIdVol;
    @FXML private TableColumn<Volontaire, String> colNom;
    @FXML private TableColumn<Volontaire, String> colContact;

    @FXML private TextField txtNom;
    @FXML private TextField txtContact;

    private final ActionDAO actionDAO = new ActionDAO();
    private final VolontaireDAO volDAO = new VolontaireDAO();
    private final ObservableList<Volontaire> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cbActions.setItems(FXCollections.observableArrayList(actionDAO.findAll()));

        colIdVol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getIdVolontaire()));
        colNom.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNom()));
        colContact.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getContact()));

        tableVol.setItems(data);

        tableVol.getSelectionModel().selectedItemProperty().addListener((obs, o, v) -> {
            if (v != null) {
                txtNom.setText(v.getNom());
                txtContact.setText(v.getContact());
            }
        });
    }

    @FXML
    private void loadVolontaires() {
        ActionNettoyage a = cbActions.getValue();
        if (a == null) return;
        data.setAll(volDAO.findByAction(a.getIdAction()));
        clearVol();
    }

    @FXML
    private void addVol() {
        ActionNettoyage a = cbActions.getValue();
        if (a == null) { info("Choisis une action."); return; }

        String nom = txtNom.getText().trim();
        String contact = txtContact.getText().trim();
        if (nom.isEmpty()) { info("Nom obligatoire."); return; }

        volDAO.insert(nom, contact, a.getIdAction());
        loadVolontaires();
    }

    @FXML
    private void updateVol() {
        Volontaire v = tableVol.getSelectionModel().getSelectedItem();
        if (v == null) { info("Choisis un volontaire."); return; }

        String nom = txtNom.getText().trim();
        String contact = txtContact.getText().trim();
        if (nom.isEmpty()) { info("Nom obligatoire."); return; }

        volDAO.update(v.getIdVolontaire(), nom, contact);
        loadVolontaires();
    }

    @FXML
    private void deleteVol() {
        Volontaire v = tableVol.getSelectionModel().getSelectedItem();
        if (v == null) { info("Choisis un volontaire."); return; }
        volDAO.delete(v.getIdVolontaire());
        loadVolontaires();
    }

    @FXML
    private void clearVol() {
        txtNom.clear();
        txtContact.clear();
        tableVol.getSelectionModel().clearSelection();
    }

    private void info(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}