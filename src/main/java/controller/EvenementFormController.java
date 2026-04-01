package controller;

import dao.EvenementDAO;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Evenement;

public class EvenementFormController {

    @FXML private Label lblTitle;
    @FXML private Label lblSuggestion;
    @FXML private TextField tfNom;
    @FXML private TextField tfDate;
    @FXML private TextField tfLieu;
    @FXML private TextField tfDescription;
    @FXML private Button btnSave;

    private final EvenementDAO dao = new EvenementDAO();
    private Evenement evenementToEdit;
    private Runnable onSaved;

    @FXML
    public void initialize() {
        addClickEffect(btnSave);
        updateSuggestion();
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    public void setEvenementToEdit(Evenement e) {
        evenementToEdit = e;
        lblTitle.setText("Modifier Événement");
        btnSave.setText("Enregistrer");

        tfNom.setText(e.getNomEvenement());
        tfDate.setText(e.getDateEvenement());
        tfLieu.setText(e.getLieu());
        tfDescription.setText(e.getDescription());
        updateSuggestion();
    }

    @FXML
    private void updateSuggestion() {
        String lieu = tfLieu.getText() == null ? "" : tfLieu.getText().toLowerCase();

        String suggestion;
        if (lieu.contains("marsa")) {
            suggestion = "Nettoyage plage recommandé ici : forte fréquentation touristique.";
        } else if (lieu.contains("hammamet")) {
            suggestion = "Nettoyage plage recommandé ici : accumulation de déchets saisonniers.";
        } else if (lieu.contains("sousse")) {
            suggestion = "Sensibilisation + nettoyage recommandé ici.";
        } else if (lieu.contains("bizerte")) {
            suggestion = "Observation biodiversité + nettoyage recommandé ici.";
        } else {
            suggestion = "Nettoyage plage recommandé ici selon l’affluence et l’état de la zone.";
        }

        if (lblSuggestion != null) {
            lblSuggestion.setText("Suggestion intelligente : " + suggestion);
        }
    }

    @FXML
    private void save() {
        try {
            String nom = tfNom.getText().trim();
            String date = tfDate.getText().trim();
            String lieu = tfLieu.getText().trim();
            String description = tfDescription.getText().trim();

            if (nom.isEmpty() || date.isEmpty() || lieu.isEmpty()) {
                showWarning("Veuillez remplir les champs obligatoires.");
                return;
            }

            if (evenementToEdit == null) {
                Evenement e = new Evenement();
                e.setNomEvenement(nom);
                e.setDateEvenement(date);
                e.setLieu(lieu);
                e.setDescription(description);
                dao.insert(e);
            } else {
                evenementToEdit.setNomEvenement(nom);
                evenementToEdit.setDateEvenement(date);
                evenementToEdit.setLieu(lieu);
                evenementToEdit.setDescription(description);
                dao.update(evenementToEdit);
            }

            if (onSaved != null) {
                onSaved.run();
            }

            closeWindow();

        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Impossible d'enregistrer l'événement.");
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