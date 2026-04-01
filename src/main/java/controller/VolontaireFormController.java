package controller;

import dao.VolontaireDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.ActionNettoyage;
import model.Volontaire;

public class VolontaireFormController {

    @FXML private Label lblTitle;
    @FXML private TextField tfNom;
    @FXML private TextField tfContact;
    @FXML private ComboBox<ActionNettoyage> cbAction;
    @FXML private Button btnSave;

    private final VolontaireDAO dao = new VolontaireDAO();
    private Volontaire volontaireToEdit;
    private Runnable onSaved;

    @FXML
    public void initialize() {
        cbAction.getItems().setAll(dao.getActions());

        cbAction.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ActionNettoyage item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLieu());
            }
        });

        cbAction.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ActionNettoyage item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLieu());
            }
        });
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    public void setVolontaireToEdit(Volontaire v) {
        volontaireToEdit = v;
        lblTitle.setText("Modifier Volontaire");
        btnSave.setText("Enregistrer");
        tfNom.setText(v.getNom());
        tfContact.setText(v.getContact());

        for (ActionNettoyage a : cbAction.getItems()) {
            if (a.getIdAction() == v.getIdAction()) {
                cbAction.setValue(a);
                break;
            }
        }
    }

    @FXML
    private void save() {
        try {
            String nom = tfNom.getText().trim();
            String contact = tfContact.getText().trim();
            ActionNettoyage action = cbAction.getValue();

            if (nom.isEmpty() || contact.isEmpty() || action == null) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setHeaderText(null);
                a.setContentText("Veuillez remplir tous les champs.");
                a.showAndWait();
                return;
            }

            if (volontaireToEdit == null) {
                Volontaire v = new Volontaire();
                v.setNom(nom);
                v.setContact(contact);
                v.setIdAction(action.getIdAction());
                dao.insert(v);
            } else {
                volontaireToEdit.setNom(nom);
                volontaireToEdit.setContact(contact);
                volontaireToEdit.setIdAction(action.getIdAction());
                dao.update(volontaireToEdit);
            }

            if (onSaved != null) {
                onSaved.run();
            }

            ((Stage) btnSave.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText(null);
            a.setContentText("Impossible d'enregistrer le volontaire.");
            a.showAndWait();
        }
    }

    @FXML
    private void annuler() {
        ((Stage) btnSave.getScene().getWindow()).close();
    }
}