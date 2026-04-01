package controller;

import dao.ActionNettoyageDAO;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.ActionNettoyage;

public class ActionFormController {

    @FXML private Label lblTitle;
    @FXML private TextField tfDate;
    @FXML private TextField tfLieu;
    @FXML private Button btnSave;

    private final ActionNettoyageDAO dao = new ActionNettoyageDAO();
    private ActionNettoyage actionToEdit;
    private Runnable onSaved;

    @FXML
    public void initialize() {
        addClickEffect(btnSave);
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    public void setActionToEdit(ActionNettoyage a) {
        actionToEdit = a;
        lblTitle.setText("Modifier Action");
        btnSave.setText("Enregistrer");
        tfDate.setText(a.getDateAction());
        tfLieu.setText(a.getLieu());
    }

    @FXML
    private void save() {
        try {
            String date = tfDate.getText().trim();
            String lieu = tfLieu.getText().trim();

            if (date.isEmpty() || lieu.isEmpty()) {
                showWarning("Veuillez remplir tous les champs.");
                return;
            }

            if (actionToEdit == null) {
                dao.insert(new ActionNettoyage(0, date, lieu));
            } else {
                actionToEdit.setDateAction(date);
                actionToEdit.setLieu(lieu);
                dao.update(actionToEdit);
            }

            if (onSaved != null) {
                onSaved.run();
            }

            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Impossible d'enregistrer l'action.");
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