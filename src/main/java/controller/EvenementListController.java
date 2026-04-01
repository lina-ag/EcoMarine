package controller;

import dao.EvenementDAO;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Evenement;

public class EvenementListController {

    @FXML private TableView<Evenement> tableEvenements;
    @FXML private TableColumn<Evenement, Integer> colId;
    @FXML private TableColumn<Evenement, String> colNom;
    @FXML private TableColumn<Evenement, String> colDate;
    @FXML private TableColumn<Evenement, String> colLieu;
    @FXML private TableColumn<Evenement, String> colDescription;
    @FXML private TableColumn<Evenement, Void> colModifier;
    @FXML private TableColumn<Evenement, Void> colSupprimer;
    @FXML private TextField tfRecherche;

    private final EvenementDAO dao = new EvenementDAO();
    private final ObservableList<Evenement> data = FXCollections.observableArrayList();
    private FilteredList<Evenement> filteredData;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idEvenement"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomEvenement"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateEvenement"));
        colLieu.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        setupModifierButton();
        setupSupprimerButton();

        loadData();

        filteredData = new FilteredList<>(data, p -> true);
        tableEvenements.setItems(filteredData);

        applyFadeIn(tableEvenements);
    }

    @FXML
    private void openAddForm() {
        openForm(null);
    }

    @FXML
    private void refresh() {
        loadData();
        searchEvenements();
    }

    @FXML
    private void searchEvenements() {
        String keyword = tfRecherche.getText();

        if (keyword == null || keyword.trim().isEmpty()) {
            filteredData.setPredicate(e -> true);
            return;
        }

        String lower = keyword.toLowerCase().trim();

        filteredData.setPredicate(e ->
                contains(e.getNomEvenement(), lower) ||
                        contains(e.getDateEvenement(), lower) ||
                        contains(e.getLieu(), lower) ||
                        contains(e.getDescription(), lower)
        );
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
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
                    Evenement ev = getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setHeaderText(null);
                    confirm.setContentText("Supprimer cet événement ?");

                    if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                        dao.delete(ev.getIdEvenement());
                        loadData();
                        searchEvenements();
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

    private void openForm(Evenement e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/evenement_form.fxml"));

            if (loader.getLocation() == null) {
                throw new RuntimeException("FXML introuvable : /view/evenement_form.fxml");
            }

            Parent root = loader.load();

            EvenementFormController controller = loader.getController();
            controller.setOnSaved(() -> {
                loadData();
                searchEvenements();
            });

            if (e != null) {
                controller.setEvenementToEdit(e);
            }

            Stage stage = new Stage();
            stage.setTitle(e == null ? "Ajouter Événement" : "Modifier Événement");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Impossible d'ouvrir le formulaire d'événement.");
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