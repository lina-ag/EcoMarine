package controller;

import dao.BiodiversiteDAO;
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
import model.Biodiversite;

public class BiodiversiteListController {

    @FXML private TableView<Biodiversite> tableBiodiversite;
    @FXML private TableColumn<Biodiversite, Integer> colId;
    @FXML private TableColumn<Biodiversite, String> colEspece;
    @FXML private TableColumn<Biodiversite, String> colZone;
    @FXML private TableColumn<Biodiversite, Integer> colNombre;
    @FXML private TableColumn<Biodiversite, String> colDate;
    @FXML private TableColumn<Biodiversite, Void> colModifier;
    @FXML private TableColumn<Biodiversite, Void> colSupprimer;
    @FXML private TextField tfFiltre;

    private final BiodiversiteDAO dao = new BiodiversiteDAO();
    private final ObservableList<Biodiversite> data = FXCollections.observableArrayList();
    private FilteredList<Biodiversite> filteredData;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idBiodiversite"));
        colEspece.setCellValueFactory(new PropertyValueFactory<>("espece"));
        colZone.setCellValueFactory(new PropertyValueFactory<>("zone"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateObservation"));

        setupModifierButton();
        setupSupprimerButton();

        loadData();

        filteredData = new FilteredList<>(data, p -> true);
        tableBiodiversite.setItems(filteredData);

        applyFadeIn(tableBiodiversite);
    }

    @FXML
    private void openAddForm() {
        openForm(null);
    }

    @FXML
    private void refresh() {
        loadData();
        filterBiodiversite();
    }

    @FXML
    private void filterBiodiversite() {
        String keyword = tfFiltre.getText();

        if (keyword == null || keyword.trim().isEmpty()) {
            filteredData.setPredicate(b -> true);
            return;
        }

        String lower = keyword.toLowerCase().trim();

        filteredData.setPredicate(b ->
                contains(b.getEspece(), lower) ||
                        contains(b.getZone(), lower) ||
                        String.valueOf(b.getNombre()).contains(lower) ||
                        contains(b.getDateObservation(), lower)
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
                    Biodiversite b = getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setHeaderText(null);
                    confirm.setContentText("Supprimer cette observation ?");

                    if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                        dao.delete(b.getIdBiodiversite());
                        loadData();
                        filterBiodiversite();
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

    private void openForm(Biodiversite b) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/biodiversite_form.fxml"));

            if (loader.getLocation() == null) {
                throw new RuntimeException("FXML introuvable : /view/biodiversite_form.fxml");
            }

            Parent root = loader.load();

            BiodiversiteFormController controller = loader.getController();
            controller.setOnSaved(() -> {
                loadData();
                filterBiodiversite();
            });

            if (b != null) {
                controller.setBiodiversiteToEdit(b);
            }

            Stage stage = new Stage();
            stage.setTitle(b == null ? "Ajouter Observation" : "Modifier Observation");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Impossible d'ouvrir le formulaire de biodiversité.");
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