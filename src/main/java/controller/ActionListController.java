package controller;

import dao.ActionNettoyageDAO;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
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
import javafx.util.Duration;
import model.ActionNettoyage;

public class ActionListController {

    @FXML private TableView<ActionNettoyage> tableActions;
    @FXML private TableColumn<ActionNettoyage, Integer> colId;
    @FXML private TableColumn<ActionNettoyage, String> colDate;
    @FXML private TableColumn<ActionNettoyage, String> colLieu;
    @FXML private TableColumn<ActionNettoyage, Void> colModifier;
    @FXML private TableColumn<ActionNettoyage, Void> colSupprimer;

    private final ActionNettoyageDAO dao = new ActionNettoyageDAO();
    private final ObservableList<ActionNettoyage> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idAction"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateAction"));
        colLieu.setCellValueFactory(new PropertyValueFactory<>("lieu"));

        setupModifierButton();
        setupSupprimerButton();

        tableActions.setItems(data);
        loadData();
        applyFadeIn(tableActions);
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
                addClickEffect(btn);
                btn.setOnAction(e -> openForm(getTableView().getItems().get(getIndex())));
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
                addClickEffect(btn);
                btn.setOnAction(e -> {
                    ActionNettoyage a = getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setHeaderText(null);
                    confirm.setContentText("Supprimer cette action ?");

                    if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                        dao.delete(a.getIdAction());
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

    private void openForm(ActionNettoyage a) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/action_form.fxml"));

            if (loader.getLocation() == null) {
                throw new RuntimeException("FXML introuvable : /view/action_form.fxml");
            }

            Parent root = loader.load();

            ActionFormController controller = loader.getController();
            controller.setOnSaved(this::loadData);

            if (a != null) {
                controller.setActionToEdit(a);
            }

            Stage stage = new Stage();
            stage.setTitle(a == null ? "Ajouter Action" : "Modifier Action");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Impossible d'ouvrir le formulaire d'action.");
        }
    }

    private void showError(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    private void applyFadeIn(TableView<?> table) {
        FadeTransition ft = new FadeTransition(Duration.millis(400), table);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void addClickEffect(Button button) {
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