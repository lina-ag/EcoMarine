package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.edu.esprit.entities.ZoneProtegee;
import tn.edu.esprit.services.ServiceZoneP;

import java.sql.SQLException;

public class AfficherZones {

    @FXML private TableView<ZoneProtegee> tableZones;
    @FXML private TableColumn<ZoneProtegee, Integer> colId;
    @FXML private TableColumn<ZoneProtegee, String> colNom;
    @FXML private TableColumn<ZoneProtegee, String> colCategorie;
    @FXML private TableColumn<ZoneProtegee, String> colStatut;
    @FXML private TableColumn<ZoneProtegee, Void> colModifier;
    @FXML private TableColumn<ZoneProtegee, Void> colSupprimer;

    @FXML private TextField txtRecherche;
    @FXML private ComboBox<String> comboTri;

    private ServiceZoneP zs = new ServiceZoneP();
    private ObservableList<ZoneProtegee> zonesList;
    private FilteredList<ZoneProtegee> filteredData;

    @FXML
    public void initialize() throws SQLException {

        colId.setCellValueFactory(new PropertyValueFactory<>("idZone"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomZone"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorieZone"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        addButtonToTableModifier();
        addButtonToTableSupprimer();

        zonesList = FXCollections.observableArrayList(zs.getAll(null));

        filteredData = new FilteredList<>(zonesList, b -> true);

        // recherche
        txtRecherche.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(zone -> {

                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }

                return zone.getNomZone().toLowerCase().contains(newVal.toLowerCase());
            });
        });

        SortedList<ZoneProtegee> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableZones.comparatorProperty());
        tableZones.setItems(sortedData);

        comboTri.setItems(FXCollections.observableArrayList(
                "Nom",
                "Catégorie",
                "Statut"
        ));
    }

    // ================= FILTRES =================

    @FXML
    private void filtrerActive() {
        filteredData.setPredicate(zone ->
                zone.getStatut().equalsIgnoreCase("active"));
    }

    @FXML
    private void filtrerInactive() {
        filteredData.setPredicate(zone ->
                zone.getStatut().equalsIgnoreCase("inactive"));
    }

    @FXML
    private void filtrerSurveillance() {
        filteredData.setPredicate(zone ->
                zone.getStatut().equalsIgnoreCase("sous surveillance"));
    }

    @FXML
    private void filtrerMenacee() {
        filteredData.setPredicate(zone ->
                zone.getStatut().equalsIgnoreCase("menacee")
                || zone.getStatut().equalsIgnoreCase("menacée"));
    }

    @FXML
    private void resetFiltres() {
        filteredData.setPredicate(zone -> true);
    }

    // ================= TRI =================

    @FXML
    private void trier() {

        String choix = comboTri.getValue();

        if (choix == null) return;

        switch (choix) {

            case "Nom":
                tableZones.getSortOrder().setAll(colNom);
                colNom.setSortType(TableColumn.SortType.ASCENDING);
                break;

            case "Catégorie":
                tableZones.getSortOrder().setAll(colCategorie);
                colCategorie.setSortType(TableColumn.SortType.ASCENDING);
                break;

            case "Statut":
                tableZones.getSortOrder().setAll(colStatut);
                colStatut.setSortType(TableColumn.SortType.ASCENDING);
                break;
        }
    }

    // ================= BOUTONS =================

    private void addButtonToTableModifier() {

        Callback<TableColumn<ZoneProtegee, Void>, TableCell<ZoneProtegee, Void>> cellFactory =
                param -> new TableCell<>() {

                    private final Button btn = new Button("Modifier");

                    {
                        btn.setOnAction(event -> {
                            ZoneProtegee data = getTableView().getItems().get(getIndex());

                            try {
                                FXMLLoader loader = new FXMLLoader(
                                        getClass().getResource("/ModifierZone.fxml"));

                                Parent root = loader.load();

                                ModifierZone controller = loader.getController();
                                controller.setZone(data);

                                Stage stage = new Stage();
                                stage.setScene(new Scene(root));
                                stage.showAndWait();

                                refreshTable();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btn);
                    }
                };

        colModifier.setCellFactory(cellFactory);
    }

    private void addButtonToTableSupprimer() {

        Callback<TableColumn<ZoneProtegee, Void>, TableCell<ZoneProtegee, Void>> cellFactory =
                param -> new TableCell<>() {

                    private final Button btn = new Button("Supprimer");

                    {
                        btn.setOnAction(event -> {
                            ZoneProtegee data = getTableView().getItems().get(getIndex());

                            zs.supprimer(data.getIdZone());
                            zonesList.remove(data);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btn);
                    }
                };

        colSupprimer.setCellFactory(cellFactory);
    }

    private void refreshTable() {
        zonesList.clear();
        zonesList.addAll(zs.getAll(null));
    }
    @FXML
    private void retourner(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionZonesPr.fxml"));
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setTitle("Gestion des Zones Protégées");
            newStage.setScene(new Scene(root));
            newStage.setMaximized(true);

            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}