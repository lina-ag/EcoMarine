package controller;

import dao.ZonePlageDAO;
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
import model.ZonePlage;

public class ZoneListController {

    @FXML private TableView<ZonePlage> tableZones;
    @FXML private TableColumn<ZonePlage, Integer> colId;
    @FXML private TableColumn<ZonePlage, String> colNom;
    @FXML private TableColumn<ZonePlage, String> colLocalisation;
    @FXML private TableColumn<ZonePlage, String> colStatut;
    @FXML private TableColumn<ZonePlage, Void> colModifier;
    @FXML private TableColumn<ZonePlage, Void> colSupprimer;

    private final ZonePlageDAO dao = new ZonePlageDAO();
    private final ObservableList<ZonePlage> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idZone"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomZone"));
        colLocalisation.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        setupModifierButton();
        setupSupprimerButton();

        tableZones.setItems(data);
        loadData();
        applyFadeIn(tableZones);
    }

    @FXML
    private void refresh() {
        loadData();
    }

    @FXML
    private void openAddForm() {
        openForm(null);
    }

    @FXML
    private void openMap() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/zone_map.fxml"));

            if (loader.getLocation() == null) {
                throw new RuntimeException("FXML introuvable : /view/zone_map.fxml");
            }

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Carte des zones");
            stage.setScene(new Scene(root, 900, 600));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la carte des zones.");
        }
    }

    private void loadData() {
        try {
            data.setAll(dao.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les zones.");
        }
    }

    private void setupModifierButton() {
        colModifier.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Modifier");

            {
                btn.getStyleClass().add("btn-secondary");
                addClickEffect(btn);
                btn.setOnAction(e -> {
                    ZonePlage zone = getTableView().getItems().get(getIndex());
                    openForm(zone);
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
                    ZonePlage zone = getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirmation");
                    confirm.setHeaderText(null);
                    confirm.setContentText("Supprimer la zone : " + zone.getNomZone() + " ?");

                    if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                        try {
                            dao.delete(zone.getIdZone());
                            loadData();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showAlert(Alert.AlertType.ERROR, "Erreur", "Suppression impossible.");
                        }
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

    private void openForm(ZonePlage zoneToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/zone_form.fxml"));

            if (loader.getLocation() == null) {
                throw new RuntimeException("FXML introuvable : /view/zone_form.fxml");
            }

            Parent root = loader.load();

            ZoneFormController controller = loader.getController();
            controller.setOnSaved(this::loadData);

            if (zoneToEdit != null) {
                controller.setZoneToEdit(zoneToEdit);
            }

            Stage stage = new Stage();
            stage.setTitle(zoneToEdit == null ? "Ajouter Zone" : "Modifier Zone");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
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