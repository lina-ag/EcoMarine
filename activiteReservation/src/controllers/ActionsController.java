package controllers;


import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.edu.esprit.entities.ActionNettoyage;
import tn.edu.esprit.entities.Volontaire;
import tn.edu.esprit.services.ServiceActionNettoyage;
import tn.edu.esprit.services.ServiceVolontaire;

public class ActionsController {

    @FXML private TableView<ActionNettoyage> tableActions;
    @FXML private TableColumn<ActionNettoyage, Number> colId;
    @FXML private TableColumn<ActionNettoyage, String> colDate;
    @FXML private TableColumn<ActionNettoyage, String> colLieu;
    @FXML private TextField txtDate;
    @FXML private TextField txtLieu;
    @FXML private TableColumn<ActionNettoyage, Void> colParticipants;

    private final ServiceVolontaire volDAO = new ServiceVolontaire();
    private final ServiceActionNettoyage dao = new ServiceActionNettoyage();
    private final ObservableList<ActionNettoyage> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getIdAction()));
        colDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDateAction()));
        colLieu.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLieu()));
        tableActions.setItems(data);

        tableActions.getSelectionModel().selectedItemProperty().addListener((obs, o, a) -> {
            if (a != null) {
                txtDate.setText(a.getDateAction());
                txtLieu.setText(a.getLieu());
            }
        });

        refresh();
        colParticipants.setCellFactory(tc -> new TableCell<>() {
            private final Button btn = new Button("Voir");

            {
                btn.setOnAction(e -> {
                    ActionNettoyage action = getTableView().getItems().get(getIndex());
                    showParticipants(action);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    private void refresh() {
        data.setAll(dao.findAll());
    }

    @FXML
    private void addAction() {
        String d;
        try { d = normalizeDate(txtDate.getText()); }
        catch (Exception e) { info(e.getMessage()); return; }

        String l = txtLieu.getText().trim();
        if (l.isEmpty()) { info("Lieu obligatoire."); return; }

        dao.insert(d, l);
        refresh();
        clearAction();
    }

    @FXML
    private void updateAction() {
        ActionNettoyage a = tableActions.getSelectionModel().getSelectedItem();
        if (a == null) { info("Choisis une action."); return; }

        String d;
        try { d = normalizeDate(txtDate.getText()); }
        catch (Exception e) { info(e.getMessage()); return; }

        String l = txtLieu.getText().trim();
        if (l.isEmpty()) { info("Lieu obligatoire."); return; }

        dao.update(a.getIdAction(), d, l);
        refresh();
    }

    @FXML
    private void deleteAction() {
        ActionNettoyage a = tableActions.getSelectionModel().getSelectedItem();
        if (a == null) { info("Choisis une action."); return; }

        dao.delete(a.getIdAction());
        refresh();
        clearAction();
    }

    @FXML
    private void clearAction() {
        txtDate.clear();
        txtLieu.clear();
        tableActions.getSelectionModel().clearSelection();
    }

    private String normalizeDate(String input) {
        input = input.trim();
        if (input.matches("\\d{4}-\\d{2}-\\d{2}")) return input;
        if (input.matches("\\d{2}/\\d{2}/\\d{4}")) {
            String[] p = input.split("/");
            return p[2] + "-" + p[1] + "-" + p[0];
        }
        throw new IllegalArgumentException("Date invalide. Utilise DD/MM/YYYY ou YYYY-MM-DD");
    }
    private void showParticipants(ActionNettoyage action) {
        var list = volDAO.findByAction(action.getIdAction());

        TableView<Volontaire> t = new TableView<>();
        TableColumn<Volontaire, Number> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getIdVolontaire()));

        TableColumn<Volontaire, String> cNom = new TableColumn<>("Nom");
        cNom.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNom()));

        TableColumn<Volontaire, String> cContact = new TableColumn<>("Contact");
        cContact.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getContact()));

        t.getColumns().addAll(cId, cNom, cContact);
        t.setItems(FXCollections.observableArrayList(list));

        Label title = new Label("Participants de l'action: " + action.toString());
        VBox root = new VBox(10, title, t);
        root.setStyle("-fx-padding: 12;");

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Participants");
        dialog.setScene(new Scene(root, 600, 400));
        dialog.showAndWait();
    }

    private void info(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

}
