package controller;

import dao.ZonePlageDAO;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.ZonePlage;

public class ZoneFormController {

    @FXML private Label lblTitle;
    @FXML private TextField tfNom;
    @FXML private TextField tfLocalisation;
    @FXML private ComboBox<String> cbStatut;
    @FXML private Button btnSave;

    private final ZonePlageDAO dao = new ZonePlageDAO();
    private ZonePlage zoneToEdit = null;
    private Runnable onSaved;

    @FXML
    public void initialize() {
        cbStatut.setItems(FXCollections.observableArrayList("Propre", "Moyen", "Polluée"));
        cbStatut.getSelectionModel().selectFirst();
        addClickEffect(btnSave);
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    public void setZoneToEdit(ZonePlage zone) {
        this.zoneToEdit = zone;

        lblTitle.setText("Modifier Zone");
        btnSave.setText("Enregistrer");

        tfNom.setText(zone.getNomZone());
        tfLocalisation.setText(zone.getLocalisation());
        cbStatut.setValue(zone.getStatut());
    }

    @FXML
    private void save() {
        String nom = tfNom.getText() == null ? "" : tfNom.getText().trim();
        String localisation = tfLocalisation.getText() == null ? "" : tfLocalisation.getText().trim();
        String statut = cbStatut.getValue();

        if (nom.isEmpty() || localisation.isEmpty() || statut == null || statut.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs obligatoires", "Veuillez remplir tous les champs.");
            return;
        }

        try {
            if (zoneToEdit == null) {
                ZonePlage z = new ZonePlage(0, nom, localisation, statut);
                dao.insert(z);
            } else {
                zoneToEdit.setNomZone(nom);
                zoneToEdit.setLocalisation(localisation);
                zoneToEdit.setStatut(statut);
                dao.update(zoneToEdit);
            }

            if (onSaved != null) {
                onSaved.run();
            }

            close();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'enregistrer la zone.");
        }
    }

    @FXML
    private void annuler() {
        close();
    }

    private void close() {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void addClickEffect(Button button) {
        if (button == null) return;

        button.setOnMousePressed(e -> animateScale(button, 0.96, 0.96, 100));
        button.setOnMouseReleased(e -> animateScale(button, 1.0, 1.0, 100));
    }

    private void animateScale(Button button, double x, double y, int millis) {
        ScaleTransition st = new ScaleTransition(Duration.millis(millis), button);
        st.setToX(x);
        st.setToY(y);
        st.play();
    }
}