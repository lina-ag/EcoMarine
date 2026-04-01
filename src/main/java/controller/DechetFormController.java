package controller;

import dao.DechetDAO;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Dechet;
import model.ZonePlage;

public class DechetFormController {

    @FXML private Label lblTitle;
    @FXML private TextField tfType;
    @FXML private TextField tfQuantite;
    @FXML private ComboBox<ZonePlage> cbZone;
    @FXML private Button btnSave;

    private final DechetDAO dao = new DechetDAO();
    private Dechet dechetToEdit;
    private Runnable onSaved;

    @FXML
    public void initialize() {
        cbZone.getItems().setAll(dao.getZones());

        cbZone.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ZonePlage item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNomZone());
            }
        });

        cbZone.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ZonePlage item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNomZone());
            }
        });

        addClickEffect(btnSave);
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    public void setDechetToEdit(Dechet d) {
        this.dechetToEdit = d;
        lblTitle.setText("Modifier Déchet");
        btnSave.setText("Enregistrer");

        tfType.setText(d.getTypeDechet());
        tfQuantite.setText(String.valueOf(d.getQuantite()));

        for (ZonePlage z : cbZone.getItems()) {
            if (z.getIdZone() == d.getIdZone()) {
                cbZone.setValue(z);
                break;
            }
        }
    }

    @FXML
    private void save() {
        try {
            String type = tfType.getText().trim();
            String quantiteText = tfQuantite.getText().trim();
            ZonePlage zone = cbZone.getValue();

            if (type.isEmpty() || quantiteText.isEmpty() || zone == null) {
                showWarning("Veuillez remplir tous les champs.");
                return;
            }

            double quantite;
            try {
                quantite = Double.parseDouble(quantiteText);
            } catch (NumberFormatException e) {
                showWarning("La quantité doit être un nombre valide.");
                return;
            }

            if (dechetToEdit == null) {
                dao.insert(new Dechet(0, type, quantite, zone.getIdZone(), zone.getNomZone()));
            } else {
                dechetToEdit.setTypeDechet(type);
                dechetToEdit.setQuantite(quantite);
                dechetToEdit.setIdZone(zone.getIdZone());
                dechetToEdit.setNomZone(zone.getNomZone());
                dao.update(dechetToEdit);
            }

            if (onSaved != null) {
                onSaved.run();
            }

            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Impossible d'enregistrer le déchet.");
        }
    }

    @FXML
    private void annuler() {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) btnSave.getScene().getWindow()).close();
    }

    private void showWarning(String message) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    private void showError(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(message);
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