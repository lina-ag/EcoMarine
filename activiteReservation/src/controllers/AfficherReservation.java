package controllers;

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
import tn.edu.esprit.entities.Reservation;
import tn.edu.esprit.services.ServiceReservation;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class AfficherReservation {

    @FXML
    private TableView<Reservation> tableReservation;

    @FXML
    private TableColumn<Reservation, Integer> colId;
    @FXML
    private TableColumn<Reservation, String> colNom;
    @FXML
    private TableColumn<Reservation, String> colEmail;
    @FXML
    private TableColumn<Reservation, Integer> colNombrePersonnes;
    @FXML
    private TableColumn<Reservation, String> colDateReservation;
    @FXML
    private TableColumn<Reservation, Integer> colIdActivite;
    @FXML
    private TableColumn<Reservation, Void> colModifier;
    @FXML
    private TableColumn<Reservation, Void> colSupprimer;
    
    // Champs pour la recherche
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button microphoneButton;  // AJOUTÉ : bouton microphone
    @FXML
    private Label resultCountLabel;

    private ServiceReservation service = new ServiceReservation();
    private ObservableList<Reservation> reservationList;
    private FilteredList<Reservation> filteredData;
    private VoiceSearchController voiceSearch;  // AJOUTÉ : contrôleur vocal

    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReservation.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Réservation");
            stage.showAndWait();

            refreshTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void initialize() throws SQLException {

        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colNombrePersonnes.setCellValueFactory(new PropertyValueFactory<>("nombre_personnes"));
        colDateReservation.setCellValueFactory(new PropertyValueFactory<>("date_reservation"));
        colIdActivite.setCellValueFactory(new PropertyValueFactory<>("idActivite"));
        
        addButtonToTableModifier();
        addButtonToTableSupprimer();

        reservationList = FXCollections.observableArrayList(service.getAll());
        
        // Initialiser le FilteredList
        filteredData = new FilteredList<>(reservationList, p -> true);
        
        // Lier le TableView au FilteredList via un SortedList
        SortedList<Reservation> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableReservation.comparatorProperty());
        
        tableReservation.setItems(sortedData);
        
        // Ajouter le listener pour la recherche en temps réel
        setupSearchListener();
        
        // Mettre à jour le compteur
        updateResultCount();
        
        // ========== INITIALISATION DE LA RECHERCHE VOCALE ==========
        voiceSearch = new VoiceSearchController();
        voiceSearch.setSearchField(searchField);
        voiceSearch.setVoiceButton(microphoneButton);
        voiceSearch.initialize();
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
            filteredData.setPredicate(reservation -> {
                if (searchText == null || searchText.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = searchText.toLowerCase();

                // Recherche sur plusieurs champs
                return (reservation.getNom() != null && reservation.getNom().toLowerCase().contains(lowerCaseFilter))
                        || (reservation.getEmail() != null && reservation.getEmail().toLowerCase().contains(lowerCaseFilter))
                        || (reservation.getDate_reservation() != null && reservation.getDate_reservation().contains(lowerCaseFilter))
                        || String.valueOf(reservation.getIdActivite()).contains(lowerCaseFilter)
                        || String.valueOf(reservation.getNombre_personnes()).contains(lowerCaseFilter);
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
            resultCountLabel.setText(count + " réservation(s) trouvée(s)");
        }
    }

    // ========== FILTRES RAPIDES ==========
    @FXML
    private void filterToday() {
        if (filteredData != null) {
            String today = LocalDate.now().toString();
            filteredData.setPredicate(reservation -> 
                reservation.getDate_reservation() != null && reservation.getDate_reservation().equals(today)
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
            
            filteredData.setPredicate(reservation -> {
                if (reservation.getDate_reservation() == null) return false;
                LocalDate date = LocalDate.parse(reservation.getDate_reservation());
                return !date.isBefore(weekStart) && !date.isAfter(weekEnd);
            });
            updateResultCount();
        }
    }

    @FXML
    private void filterThisMonth() {
        if (filteredData != null) {
            LocalDate now = LocalDate.now();
            filteredData.setPredicate(reservation -> {
                if (reservation.getDate_reservation() == null) return false;
                LocalDate date = LocalDate.parse(reservation.getDate_reservation());
                return date.getMonth() == now.getMonth() && date.getYear() == now.getYear();
            });
            updateResultCount();
        }
    }

    @FXML
    private void filterTwoPlus() {
        if (filteredData != null) {
            filteredData.setPredicate(reservation -> reservation.getNombre_personnes() >= 2);
            updateResultCount();
        }
    }
    // ========== FIN FILTRES RAPIDES ==========

    // ========== RECHERCHE VOCALE ==========
    @FXML
    private void rechercheVocale() {
        if (voiceSearch != null) {
            voiceSearch.startVoiceRecognition();
        }
    }
    // ========== FIN RECHERCHE VOCALE ==========

    private void addButtonToTableModifier() {
        Callback<TableColumn<Reservation, Void>, TableCell<Reservation, Void>> cellFactory =
                param -> new TableCell<>() {

                    private final Button btn = new Button("Modifier");

                    {
                        btn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 5 15; -fx-font-weight: bold; -fx-cursor: hand;");

                        btn.setOnMouseEntered(e -> 
                            btn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 15; -fx-cursor: hand; -fx-padding: 5 15; -fx-font-weight: bold;")
                        );
                        btn.setOnMouseExited(e -> 
                            btn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 15; -fx-cursor: hand; -fx-padding: 5 15; -fx-font-weight: bold;")
                        );
                        
                        btn.setOnAction(event -> {

                            Reservation data = getTableView().getItems().get(getIndex());

                            try {
                                FXMLLoader loader = new FXMLLoader(
                                        getClass().getResource("/ModifierReservation.fxml")
                                );

                                Parent root = loader.load();

                                ModifierReservation controller = loader.getController();
                                controller.setReservation(data);

                                Stage stage = new Stage();
                                stage.setScene(new Scene(root));
                                stage.setTitle("Modifier Réservation");
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
        Callback<TableColumn<Reservation, Void>, TableCell<Reservation, Void>> cellFactory =
                param -> new TableCell<>() {

                    private final Button btn = new Button("Supprimer");

                    {
                        btn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 5 15; -fx-font-weight: bold; -fx-cursor: hand;");
                        
                        btn.setOnMouseEntered(e -> 
                            btn.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-background-radius: 15; -fx-cursor: hand; -fx-padding: 5 15; -fx-font-weight: bold;")
                        );
                        btn.setOnMouseExited(e -> 
                            btn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 15; -fx-cursor: hand; -fx-padding: 5 15; -fx-font-weight: bold;")
                        );
                        
                        btn.setOnAction(event -> {

                            Reservation data = getTableView().getItems().get(getIndex());

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation");
                            alert.setHeaderText("Supprimer la réservation");
                            alert.setContentText("Voulez-vous vraiment supprimer la réservation de \"" + data.getNom() + "\" ?");

                            alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    service.supprimer(data.getId());
                                    refreshTable();
                                    GestionActiviteReservation.refreshStats();
                                    System.out.println("Réservation supprimée: " + data.getId());
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
        reservationList.clear();
        reservationList.addAll(service.getAll());
        updateResultCount();
    }
}