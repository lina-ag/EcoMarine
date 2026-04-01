package controller;

import dao.BiodiversiteDAO;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Biodiversite;

public class BiodiversiteFormController {

    @FXML private Label lblTitle;
    @FXML private TextField tfEspece;
    @FXML private TextField tfZone;
    @FXML private TextField tfNombre;
    @FXML private TextField tfDate;
    @FXML private Button btnSave;

    private final BiodiversiteDAO dao = new BiodiversiteDAO();
    private Biodiversite biodiversiteToEdit;
    private Runnable onSaved;

    @FXML
    public void initialize() {
        addClickEffect(btnSave);
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    public void setBiodiversiteToEdit(Biodiversite b) {
        biodiversiteToEdit = b;
        lblTitle.setText("Modifier Observation");
        btnSave.setText("Enregistrer");

        tfEspece.setText(b.getEspece());
        tfZone.setText(b.getZone());
        tfNombre.setText(String.valueOf(b.getNombre()));
        tfDate.setText(b.getDateObservation());
    }

    @FXML
    private void save() {
        try {
            String espece = tfEspece.getText().trim();
            String zone = tfZone.getText().trim();
            String nombreText = tfNombre.getText().trim();
            String date = tfDate.getText().trim();

            if (espece.isEmpty() || zone.isEmpty() || date.isEmpty() || nombreText.isEmpty()) {
                showWarning("Veuillez remplir tous les champs.");
                return;
            }

            int nombre;
            try {
                nombre = Integer.parseInt(nombreText);
            } catch (NumberFormatException e) {
                showWarning("Le nombre doit être un entier valide.");
                return;
            }

            if (biodiversiteToEdit == null) {
                Biodiversite b = new Biodiversite();
                b.setEspece(espece);
                b.setZone(zone);
                b.setNombre(nombre);
                b.setDateObservation(date);
                dao.insert(b);
            } else {
                biodiversiteToEdit.setEspece(espece);
                biodiversiteToEdit.setZone(zone);
                biodiversiteToEdit.setNombre(nombre);
                biodiversiteToEdit.setDateObservation(date);
                dao.update(biodiversiteToEdit);
            }

            if (onSaved != null) {
                onSaved.run();
            }

            closeWindow();

        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Impossible d'enregistrer l'observation.");
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