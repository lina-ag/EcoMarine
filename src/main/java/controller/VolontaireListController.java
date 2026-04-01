package controller;

import dao.VolontaireDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Volontaire;

public class VolontaireListController {

    @FXML private TableView<Volontaire> tableVolontaires;
    @FXML private TableColumn<Volontaire, Integer> colId;
    @FXML private TableColumn<Volontaire, String> colNom;
    @FXML private TableColumn<Volontaire, String> colContact;
    @FXML private TableColumn<Volontaire, String> colAction;
    @FXML private TableColumn<Volontaire, Void> colModifier;
    @FXML private TableColumn<Volontaire, Void> colSupprimer;

    private final VolontaireDAO dao = new VolontaireDAO();
    private final ObservableList<Volontaire> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idVolontaire"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("actionNom"));

        setupModifierButton();
        setupSupprimerButton();

        tableVolontaires.setItems(data);
        loadData();
    }

    @FXML
    private void openAddForm() {
        openForm(null);
    }

    @FXML
    private void refresh() {
        loadData();
    }

    private void loadData() {
        data.setAll(dao.findAll());
    }

    private void setupModifierButton() {
        colModifier.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Modifier");

            {
                btn.getStyleClass().add("btn-secondary");
                btn.setOnAction(e -> {
                    Volontaire v = getTableView().getItems().get(getIndex());
                    openForm(v);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void setupSupprimerButton() {
        colSupprimer.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Supprimer");

            {
                btn.setStyle("-fx-background-color:#ef4444; -fx-text-fill:white; -fx-font-weight:bold; -fx-background-radius:10;");
                btn.setOnAction(e -> {
                    Volontaire v = getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setHeaderText(null);
                    confirm.setContentText("Supprimer ce volontaire ?");

                    if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                        dao.delete(v.getIdVolontaire());
                        loadData();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void openForm(Volontaire v) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/volontaire_form.fxml"));

            if (loader.getLocation() == null) {
                throw new RuntimeException("FXML introuvable : /view/volontaire_form.fxml");
            }

            Parent root = loader.load();

            VolontaireFormController controller = loader.getController();
            controller.setOnSaved(this::loadData);

            if (v != null) {
                controller.setVolontaireToEdit(v);
            }

            Stage stage = new Stage();
            stage.setTitle(v == null ? "Ajouter Volontaire" : "Modifier Volontaire");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}