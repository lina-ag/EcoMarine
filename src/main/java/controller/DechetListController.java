package controller;

import dao.DechetDAO;
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
import model.Dechet;

public class DechetListController {

    @FXML private TableView<Dechet> tableDechets;
    @FXML private TableColumn<Dechet, Integer> colId;
    @FXML private TableColumn<Dechet, String> colType;
    @FXML private TableColumn<Dechet, Double> colQuantite;
    @FXML private TableColumn<Dechet, String> colZone;
    @FXML private TableColumn<Dechet, Void> colModifier;
    @FXML private TableColumn<Dechet, Void> colSupprimer;

    private final DechetDAO dao = new DechetDAO();
    private final ObservableList<Dechet> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idDechet"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeDechet"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colZone.setCellValueFactory(new PropertyValueFactory<>("nomZone"));

        setupModifierButton();
        setupSupprimerButton();

        tableDechets.setItems(data);
        loadData();
        applyFadeIn(tableDechets);
    }

    @FXML
    private void refresh() {
        loadData();
    }

    @FXML
    private void openAddForm() {
        openForm(null);
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
                btn.setOnAction(e -> {
                    Dechet d = getTableView().getItems().get(getIndex());
                    openForm(d);
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
                addClickEffect(btn);
                btn.setOnAction(e -> {
                    Dechet d = getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setHeaderText(null);
                    confirm.setContentText("Supprimer ce déchet ?");

                    if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                        dao.delete(d.getIdDechet());
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

    private void openForm(Dechet d) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dechet_form.fxml"));

            if (loader.getLocation() == null) {
                throw new RuntimeException("FXML introuvable : /view/dechet_form.fxml");
            }

            Parent root = loader.load();

            DechetFormController controller = loader.getController();
            controller.setOnSaved(this::loadData);

            if (d != null) {
                controller.setDechetToEdit(d);
            }

            Stage stage = new Stage();
            stage.setTitle(d == null ? "Ajouter Déchet" : "Modifier Déchet");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Impossible d'ouvrir le formulaire de déchet.");
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