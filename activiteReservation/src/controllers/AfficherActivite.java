package controllers;

import tn.edu.esprit.api.CalendarService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.edu.esprit.entities.ActiviteEcologique;
import tn.edu.esprit.services.ServiceActivite;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;




public class AfficherActivite {
    
   CalendarService calendarService = new CalendarService();
    String events = calendarService.getEvents();
    
    @FXML
    private TableView<ActiviteEcologique> tableActivite;

    @FXML
    private TableColumn<ActiviteEcologique, Integer> colId;

    @FXML
    private TableColumn<ActiviteEcologique, String> colNom;

    @FXML
    private TableColumn<ActiviteEcologique, String> colDescription;

    @FXML
    private TableColumn<ActiviteEcologique, String> colDate;

    @FXML
    private TableColumn<ActiviteEcologique, String> colCapacite;

    @FXML
    private TableColumn<ActiviteEcologique, Void> colModifier;

    @FXML
    private TableColumn<ActiviteEcologique, Void> colSupprimer;
    
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private Button clearButton;
    @FXML
    private Label resultCountLabel;

    private ServiceActivite service = new ServiceActivite();
    private ObservableList<ActiviteEcologique> activiteList;
    private FilteredList<ActiviteEcologique> filteredData;

    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterActivite.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Activité");
            stage.showAndWait();

            refreshTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void initialize() throws SQLException {

        colId.setCellValueFactory(new PropertyValueFactory<>("idActivite"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        
        addButtonToTableModifier();
        addButtonToTableSupprimer();

        activiteList = FXCollections.observableArrayList(service.getAll());
        filteredData = new FilteredList<>(activiteList, p -> true);
        
        SortedList<ActiviteEcologique> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableActivite.comparatorProperty());
        
        tableActivite.setItems(sortedData);
        
        setupSearchListener();
        updateResultCount();
    }

    private void setupSearchListener() {
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterData(newValue);
            });
        }
    }

    private void filterData(String searchText) {
        if (filteredData != null) {
            filteredData.setPredicate(activite -> {
                if (searchText == null || searchText.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = searchText.toLowerCase();

                return activite.getNom().toLowerCase().contains(lowerCaseFilter)
                        || (activite.getDescription() != null && activite.getDescription().toLowerCase().contains(lowerCaseFilter))
                        || activite.getDate().contains(lowerCaseFilter)
                        || String.valueOf(activite.getCapacite()).contains(lowerCaseFilter);
            });
            updateResultCount();
        }
    }

    @FXML
    private void handleSearch() {
        if (searchField != null && filteredData != null) {
            filterData(searchField.getText());
        }
    }

    @FXML
    private void clearSearch() {
        if (searchField != null) {
            searchField.clear();
        }
        if (filteredData != null) {
            filteredData.setPredicate(null);
        }
        updateResultCount();
    }
    
    private void updateResultCount() {
        if (resultCountLabel != null && filteredData != null) {
            int count = filteredData.size();
            resultCountLabel.setText(count + " activité(s) trouvée(s)");
        }
    }

    // ========== FILTRES RAPIDES ==========
    @FXML
    private void filterToday() {
        if (filteredData != null) {
            filteredData.setPredicate(activite -> 
                activite.getDate().equals(LocalDate.now().toString())
            );
            updateResultCount();
        }
    }

    @FXML
    private void filterThisWeek() {
        if (filteredData != null) {
            LocalDate now = LocalDate.now();
            LocalDate weekStart = now.minusDays(now.getDayOfWeek().getValue() - 1);
            LocalDate weekEnd = weekStart.plusDays(6);
            
            filteredData.setPredicate(activite -> {
                LocalDate date = LocalDate.parse(activite.getDate());
                return !date.isBefore(weekStart) && !date.isAfter(weekEnd);
            });
            updateResultCount();
        }
    }

    @FXML
    private void filterThisMonth() {
        if (filteredData != null) {
            LocalDate now = LocalDate.now();
            filteredData.setPredicate(activite -> {
                LocalDate date = LocalDate.parse(activite.getDate());
                return date.getMonth() == now.getMonth() && date.getYear() == now.getYear();
            });
            updateResultCount();
        }
    }

    @FXML
    private void filterCapacity() {
        if (filteredData != null) {
            filteredData.setPredicate(activite -> activite.getCapacite() > 10);
            updateResultCount();
        }
    }
    // ========== FIN FILTRES RAPIDES ==========

    private void addButtonToTableModifier() {
        Callback<TableColumn<ActiviteEcologique, Void>, TableCell<ActiviteEcologique, Void>> cellFactory =
                param -> new TableCell<>() {

                    private final Button btn = new Button("Modifier");

                    {
                        // Style bleu pour le bouton Modifier
                    	// Dans addButtonToTableModifier()
                    	btn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 5 15; -fx-font-weight: bold; -fx-cursor: hand;");

                    	
                        btn.setOnAction(event -> {

                            ActiviteEcologique data = getTableView().getItems().get(getIndex());

                            try {
                                FXMLLoader loader = new FXMLLoader(
                                        getClass().getResource("/ModifierActivite.fxml")
                                );

                                Parent root = loader.load();

                                ModifierActivite controller = loader.getController();
                                controller.setActivite(data);

                                Stage stage = new Stage();
                                stage.setScene(new Scene(root));
                                stage.setTitle("Modifier Activité");
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
        Callback<TableColumn<ActiviteEcologique, Void>, TableCell<ActiviteEcologique, Void>> cellFactory =
                param -> new TableCell<>() {

                    private final Button btn = new Button("Supprimer");

                    {
                    	// Dans addButtonToTableSupprimer()
                    	btn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 5 15; -fx-font-weight: bold; -fx-cursor: hand;" );
                        
                        // Effet au survol
                        btn.setOnMouseEntered(e -> 
                            btn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 6 18; -fx-font-weight: 700; -fx-font-size: 12px; -fx-cursor: hand; -fx-scale-x: 1.1; -fx-scale-y: 1.1;")
                        );
                        btn.setOnMouseExited(e -> 
                            btn.setStyle("-fx-background-color: #f87171; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 6 18; -fx-font-weight: 700; -fx-font-size: 12px; -fx-cursor: hand;")
                        );
                        
                        btn.setOnAction(event -> {

                            ActiviteEcologique data = getTableView().getItems().get(getIndex());

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation");
                            alert.setHeaderText("Supprimer l'activité");
                            alert.setContentText("Voulez-vous vraiment supprimer l'activité \"" + data.getNom() + "\" ?");

                            alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    service.supprimer(data.getIdActivite());
                                    refreshTable();
                                    GestionActiviteReservation.refreshStats();
                                    System.out.println("Activité supprimée: " + data.getIdActivite());
                                }
                            });
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
        activiteList.clear();
        activiteList.addAll(service.getAll());
        updateResultCount();
    }
}