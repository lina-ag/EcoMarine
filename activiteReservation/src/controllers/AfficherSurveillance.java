package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.edu.esprit.entities.SurveillanceZone;
import tn.edu.esprit.services.ServiceSurv;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AfficherSurveillance {

    @FXML
    private TableView<SurveillanceZone> tableSurv;

    @FXML
    private TableColumn<SurveillanceZone, Integer> colId;

    @FXML
    private TableColumn<SurveillanceZone, String> colDate;

    @FXML
    private TableColumn<SurveillanceZone, String> colObservation;

    @FXML
    private TableColumn<SurveillanceZone, Integer> colIdZone;

    @FXML
    private TableColumn<SurveillanceZone, Void> colModifier;

    @FXML
    private TableColumn<SurveillanceZone, Void> colSupprimer;

    @FXML
    private TextField txtRecherche;
    
    @FXML
    private ComboBox<String> comboFiltrePeriode;
    
    @FXML
    private ComboBox<String> comboFiltreTri;
    
    @FXML
    private Button btnAujourdhui;
    
    @FXML
    private Button btnCeMois;
    
    @FXML
    private Button btnCetteAnnee;
    
    @FXML
    private Button btnResetFiltres;
    
    @FXML
    private Label lblResultCount;

    private ServiceSurv service = new ServiceSurv();
    private ObservableList<SurveillanceZone> survList;
    private FilteredList<SurveillanceZone> filteredData;
    private String periodeFiltreActuelle = "";

    @FXML
    public void initialize() {
        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("idSurveillance"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateSurveillance"));
        colObservation.setCellValueFactory(new PropertyValueFactory<>("observation"));
        colIdZone.setCellValueFactory(new PropertyValueFactory<>("idZone"));

        addDeleteButton();
        addModifyButton();
        
        // Chargement des données
        survList = FXCollections.observableArrayList(service.getAll(null));
        
        // Configuration des filtres
        setupFilters();
        setupTriComboBox();
        
        // Mise à jour du compteur
        updateResultCount();
    }

    private void setupFilters() {
        filteredData = new FilteredList<>(survList, b -> true);
        
        // Filtre par recherche
        txtRecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });
        
        // Configuration des boutons de filtre par période
        btnAujourdhui.setOnAction(e -> filtrerParPeriode("aujourdhui"));
        btnCeMois.setOnAction(e -> filtrerParPeriode("cemois"));
        btnCetteAnnee.setOnAction(e -> filtrerParPeriode("cetteannee"));
        btnResetFiltres.setOnAction(e -> resetFilters());
        
        // Configuration du combo de tri
        comboFiltreTri.setOnAction(e -> applySort());
        
        // Configuration du combo de filtre par période (optionnel)
        comboFiltrePeriode.getItems().addAll("Toutes", "Aujourd'hui", "Ce mois", "Cette année");
        comboFiltrePeriode.setValue("Toutes");
        comboFiltrePeriode.setOnAction(e -> {
            String selected = comboFiltrePeriode.getValue();
            if ("Aujourd'hui".equals(selected)) {
                filtrerParPeriode("aujourdhui");
            } else if ("Ce mois".equals(selected)) {
                filtrerParPeriode("cemois");
            } else if ("Cette année".equals(selected)) {
                filtrerParPeriode("cetteannee");
            } else {
                resetFilters();
            }
        });
        
        SortedList<SurveillanceZone> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableSurv.comparatorProperty());
        tableSurv.setItems(sortedData);
    }
    
    private void filtrerParPeriode(String periode) {
        periodeFiltreActuelle = periode;
        applyFilters();
    }
    
    private void applyFilters() {
        filteredData.setPredicate(surv -> {
            // Filtre par recherche
            String searchText = txtRecherche.getText();
            if (searchText != null && !searchText.isEmpty()) {
                try {
                    int idRecherche = Integer.parseInt(searchText);
                    if (surv.getIdZone() != idRecherche) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            
            // Filtre par période
            if (!periodeFiltreActuelle.isEmpty()) {
                try {
                    String dateStr = surv.getDateSurveillance();
                    LocalDate date = LocalDate.parse(dateStr);
                    LocalDate aujourdhui = LocalDate.now();
                    
                    switch (periodeFiltreActuelle) {
                        case "aujourdhui":
                            if (!date.equals(aujourdhui)) return false;
                            break;
                        case "cemois":
                            if (date.getMonth() != aujourdhui.getMonth() || 
                                date.getYear() != aujourdhui.getYear()) return false;
                            break;
                        case "cetteannee":
                            if (date.getYear() != aujourdhui.getYear()) return false;
                            break;
                    }
                } catch (DateTimeParseException e) {
                    return false;
                }
            }
            
            return true;
        });
        
        updateResultCount();
        applySort();
    }
    
    private void resetFilters() {
        periodeFiltreActuelle = "";
        txtRecherche.clear();
        comboFiltrePeriode.setValue("Toutes");
        applyFilters();
    }
    
    private void setupTriComboBox() {
        comboFiltreTri.getItems().addAll(
            "Date (Plus récente)",
            "Date (Plus ancienne)",
            "ID Zone (Croissant)",
            "ID Zone (Décroissant)",
            "Observation (A-Z)",
            "Observation (Z-A)"
        );
        comboFiltreTri.setValue("Date (Plus récente)");
    }
    
    private void applySort() {
        String tri = comboFiltreTri.getValue();
        SortedList<SurveillanceZone> sortedData = new SortedList<>(filteredData);
        
        sortedData.comparatorProperty().set((s1, s2) -> {
            if (tri == null) return 0;
            
            switch (tri) {
                case "Date (Plus récente)":
                    return s2.getDateSurveillance().compareTo(s1.getDateSurveillance());
                case "Date (Plus ancienne)":
                    return s1.getDateSurveillance().compareTo(s2.getDateSurveillance());
                case "ID Zone (Croissant)":
                    return Integer.compare(s1.getIdZone(), s2.getIdZone());
                case "ID Zone (Décroissant)":
                    return Integer.compare(s2.getIdZone(), s1.getIdZone());
                case "Observation (A-Z)":
                    return s1.getObservation().compareTo(s2.getObservation());
                case "Observation (Z-A)":
                    return s2.getObservation().compareTo(s1.getObservation());
                default:
                    return 0;
            }
        });
        
        tableSurv.setItems(sortedData);
    }
    
    private void updateResultCount() {
        int count = filteredData.size();
        lblResultCount.setText(count + " surveillance(s) trouvée(s)");
    }

    private void refreshTable() {
        survList.clear();
        survList.addAll(service.getAll(null));
        applyFilters();
    }

    private void addDeleteButton() {
        Callback<TableColumn<SurveillanceZone, Void>, TableCell<SurveillanceZone, Void>> cellFactory =
                param -> new TableCell<>() {
                    private final Button btn = new Button("Supprimer");
                    {
                        btn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 5 10; -fx-cursor: hand;");
                        btn.setOnAction(event -> {
                            SurveillanceZone data = getTableView().getItems().get(getIndex());
                            service.supprimer(data.getIdSurveillance());
                            refreshTable();
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

    private void addModifyButton() {
        Callback<TableColumn<SurveillanceZone, Void>, TableCell<SurveillanceZone, Void>> cellFactory =
                param -> new TableCell<>() {
                    private final Button btn = new Button("Modifier");
                    {
                        btn.setStyle("-fx-background-color: #ffc107; -fx-text-fill: #1e3c72; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 5 10; -fx-cursor: hand;");
                        btn.setOnAction(event -> {
                            try {
                                SurveillanceZone data = getTableView().getItems().get(getIndex());
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierSurveillance.fxml"));
                                Parent root = loader.load();
                                ModifierSurveillance controller = loader.getController();
                                controller.setSurveillance(data);
                                Stage stage = new Stage();
                                stage.setScene(new Scene(root));
                                stage.setTitle("Modifier Surveillance");
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
}