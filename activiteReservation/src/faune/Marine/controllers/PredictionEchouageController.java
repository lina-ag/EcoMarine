package faune.Marine.controllers;

import faune.Marine.entities.PredictionEchouage;
import faune.Marine.services.ServicePredictionEchouage;
import faune.Marine.services.ServicePredictionAvance;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PredictionEchouageController implements Initializable {
    
    // ==================== COMPOSANTS FXML ====================
    
    @FXML
    private TableView<PredictionEchouage> tableView;
    
    @FXML
    private TableColumn<PredictionEchouage, Integer> colId;
    
    @FXML
    private TableColumn<PredictionEchouage, LocalDate> colDate;
    
    @FXML
    private TableColumn<PredictionEchouage, String> colZone;
    
    @FXML
    private TableColumn<PredictionEchouage, Integer> colRisque;
    
    @FXML
    private TableColumn<PredictionEchouage, String> colEspece;
    
    @FXML
    private TableColumn<PredictionEchouage, Double> colTemperature;
    
    @FXML
    private TableColumn<PredictionEchouage, String> colRecommandations;
    
    @FXML
    private DatePicker dpDatePrediction;
    
    @FXML
    private ComboBox<String> cmbZone;
    
    @FXML
    private Slider sliderRisque;
    
    @FXML
    private Label lblNiveauRisque;
    
    @FXML
    private ComboBox<String> cmbEspece;
    
    @FXML
    private TextField txtTemperatureEau;
    
    @FXML
    private ComboBox<String> cmbConditionsMeteo;
    
    @FXML
    private TextArea txtRecommandations;
    
    @FXML
    private Button btnAjouter;
    
    @FXML
    private Button btnModifier;
    
    @FXML
    private Button btnSupprimer;
    
    @FXML
    private Button btnEffacer;
    
    @FXML
    private Button btnAlertesRouges;
    
    @FXML
    private Button btnPredictionAuto;
    
    @FXML
    private Button btnExporterRapport;
    
    @FXML
    private Button btnToutesPredictions;
    
    @FXML
    private Label lblStatistiques;
    
    // ==================== SERVICES ET DONNÉES ====================
    
    private ServicePredictionEchouage service;
    private ServicePredictionAvance serviceAvance;
    private ObservableList<PredictionEchouage> predictionList;
    private PredictionEchouage selectedPrediction;
    
    // ==================== INITIALISATION ====================
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("✅ PredictionEchouageController initialisé");
        
        try {
            service = new ServicePredictionEchouage();
            serviceAvance = new ServicePredictionAvance();
            predictionList = FXCollections.observableArrayList();
            
            // Configuration des colonnes
            setupTableColumns();
            
            // Configuration des ComboBox
            setupComboBoxes();
            
            // Configuration du slider
            setupSlider();
            
            // Chargement des données
            refreshTable();
            updateStatistiques();
            
            // Listener pour la sélection
            setupSelectionListener();
            
        } catch (Exception e) {
            System.err.println("❌ Erreur initialisation: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'initialisation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Configuration des colonnes du tableau
     */
    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idPrediction"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("datePrediction"));
        colZone.setCellValueFactory(new PropertyValueFactory<>("zone"));
        colRisque.setCellValueFactory(new PropertyValueFactory<>("niveauRisque"));
        colEspece.setCellValueFactory(new PropertyValueFactory<>("especeConcernee"));
        colTemperature.setCellValueFactory(new PropertyValueFactory<>("temperatureEau"));
        colRecommandations.setCellValueFactory(new PropertyValueFactory<>("recommandations"));
        
        // Formatage de la colonne risque avec couleur
        colRisque.setCellFactory(column -> new TableCell<PredictionEchouage, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item + "/10");
                    if (item >= 8) {
                        setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-font-weight: bold;");
                    } else if (item >= 5) {
                        setStyle("-fx-background-color: #feca57; -fx-text-fill: black;");
                    } else {
                        setStyle("-fx-background-color: #54a0ff; -fx-text-fill: white;");
                    }
                }
            }
        });
        
        // Formatage de la colonne température
        colTemperature.setCellFactory(column -> new TableCell<PredictionEchouage, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f °C", item));
                }
            }
        });
    }
    
    /**
     * Configuration des ComboBox
     */
    private void setupComboBoxes() {
        cmbZone.getItems().addAll("Nord", "Sud", "Est", "Ouest", "Centre", "Lagons", "Récifs", "Haute mer");
        cmbEspece.getItems().addAll("Dauphin", "Baleine", "Phoque", "Tortue marine", "Requin", "Morse", "Otarie");
        cmbConditionsMeteo.getItems().addAll("☀️ Ensoleillé", "⛅ Nuageux", "🌧️ Pluvieux", "🌊 Fortes vagues", "🌪️ Tempête");
    }
    
    /**
     * Configuration du slider
     */
    private void setupSlider() {
        sliderRisque.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblNiveauRisque.setText(String.valueOf(newVal.intValue()));
        });
    }
    
    /**
     * Configuration du listener de sélection
     */
    private void setupSelectionListener() {
        tableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedPrediction = newSelection;
                    displayPredictionDetails(newSelection);
                    btnModifier.setDisable(false);
                    btnSupprimer.setDisable(false);
                }
            }
        );
    }
    
    // ==================== GESTION DES ÉVÉNEMENTS ====================
    
    @FXML
    private void handleAjouter() {
        if (validateInputs()) {
            try {
                PredictionEchouage p = new PredictionEchouage(
                    dpDatePrediction.getValue(),
                    cmbZone.getValue(),
                    (int) sliderRisque.getValue(),
                    cmbEspece.getValue(),
                    Double.parseDouble(txtTemperatureEau.getText()),
                    cmbConditionsMeteo.getValue(),
                    txtRecommandations.getText()
                );
                
                service.ajouter(p);
                refreshTable();
                updateStatistiques();
                clearForm();
                showAlert("Succès", "✅ Prédiction ajoutée avec succès!", Alert.AlertType.INFORMATION);
                
            } catch (NumberFormatException e) {
                showAlert("Erreur", "❌ La température doit être un nombre valide", Alert.AlertType.ERROR);
            } catch (Exception e) {
                showAlert("Erreur", "❌ Erreur lors de l'ajout: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleModifier() {
        if (selectedPrediction != null && validateInputs()) {
            try {
                selectedPrediction.setDatePrediction(dpDatePrediction.getValue());
                selectedPrediction.setZone(cmbZone.getValue());
                selectedPrediction.setNiveauRisque((int) sliderRisque.getValue());
                selectedPrediction.setEspeceConcernee(cmbEspece.getValue());
                selectedPrediction.setTemperatureEau(Double.parseDouble(txtTemperatureEau.getText()));
                selectedPrediction.setConditionsMeteo(cmbConditionsMeteo.getValue());
                selectedPrediction.setRecommandations(txtRecommandations.getText());
                
                service.modifier(selectedPrediction);
                refreshTable();
                updateStatistiques();
                clearForm();
                showAlert("Succès", "✅ Prédiction modifiée avec succès!", Alert.AlertType.INFORMATION);
                
            } catch (NumberFormatException e) {
                showAlert("Erreur", "❌ La température doit être un nombre valide", Alert.AlertType.ERROR);
            } catch (Exception e) {
                showAlert("Erreur", "❌ Erreur lors de la modification: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleSupprimer() {
        if (selectedPrediction != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Supprimer la prédiction");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer cette prédiction?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    service.supprimer(selectedPrediction.getIdPrediction());
                    refreshTable();
                    updateStatistiques();
                    clearForm();
                    showAlert("Succès", "✅ Prédiction supprimée avec succès!", Alert.AlertType.INFORMATION);
                } catch (Exception e) {
                    showAlert("Erreur", "❌ Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Attention", "Veuillez sélectionner une prédiction à supprimer", Alert.AlertType.WARNING);
        }
    }
    
    @FXML
    private void handleEffacer() {
        clearForm();
    }
    
    @FXML
    private void handlePredictionAuto() {
        if (cmbZone.getValue() == null || cmbEspece.getValue() == null) {
            showAlert("Attention", "Veuillez sélectionner une zone et une espèce", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            // Générer la prédiction automatique
            PredictionEchouage prediction = serviceAvance.genererPrediction(
                cmbZone.getValue(), 
                cmbEspece.getValue()
            );
            
            // Remplir le formulaire
            dpDatePrediction.setValue(prediction.getDatePrediction());
            sliderRisque.setValue(prediction.getNiveauRisque());
            txtTemperatureEau.setText(String.valueOf(prediction.getTemperatureEau()));
            cmbConditionsMeteo.setValue(prediction.getConditionsMeteo());
            txtRecommandations.setText(prediction.getRecommandations());
            
            showAlert("Prédiction IA", 
                      "🤖 Prédiction générée par IA\n\n" +
                      "Niveau de risque: " + prediction.getNiveauRisque() + "/10\n" +
                      "Température eau: " + String.format("%.1f", prediction.getTemperatureEau()) + "°C\n" +
                      "Conditions: " + prediction.getConditionsMeteo(),
                      Alert.AlertType.INFORMATION);
            
        } catch (Exception e) {
            showAlert("Erreur", "❌ Erreur lors de la génération: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleAlertesRouges() {
        // Afficher uniquement les prédictions à risque élevé (>= 7)
        ObservableList<PredictionEchouage> alertes = FXCollections.observableArrayList();
        for (PredictionEchouage p : predictionList) {
            if (p.getNiveauRisque() >= 7) {
                alertes.add(p);
            }
        }
        tableView.setItems(alertes);
        
        if (alertes.isEmpty()) {
            showAlert("Information", "✅ Aucune alerte rouge pour le moment", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Alertes Rouges", 
                      "⚠️ " + alertes.size() + " prédiction(s) à risque élevé\n" +
                      "Consultez les recommandations détaillées dans le tableau.", 
                      Alert.AlertType.WARNING);
        }
    }
    
    @FXML
    private void handleToutesPredictions() {
        tableView.setItems(predictionList);
        showAlert("Information", "Affichage de toutes les prédictions", Alert.AlertType.INFORMATION);
    }
    
    @FXML
    private void handleExporterRapport() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter le rapport");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichier CSV", "*.csv")
            );
            
            String fileName = "predictions_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";
            fileChooser.setInitialFileName(fileName);
            
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                    // En-tête CSV
                    writer.println("ID,Date,Zone,Risque,Espèce,Température Eau,Recommandations");
                    
                    // Données
                    for (PredictionEchouage p : predictionList) {
                        writer.println(
                            p.getIdPrediction() + "," +
                            p.getDatePrediction() + "," +
                            p.getZone() + "," +
                            p.getNiveauRisque() + "/10," +
                            p.getEspeceConcernee() + "," +
                            String.format("%.1f", p.getTemperatureEau()) + "°C," +
                            "\"" + p.getRecommandations().replace("\n", " ").replace("\"", "\"\"") + "\""
                        );
                    }
                }
                showAlert("Succès", "✅ Rapport exporté avec succès vers:\n" + file.getName(), Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            showAlert("Erreur", "❌ Erreur lors de l'export: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    // ==================== MÉTHODES UTILITAIRES ====================
    
    private void refreshTable() {
        try {
            predictionList.clear();
            List<PredictionEchouage> list = service.getAll();
            predictionList.addAll(list);
            tableView.setItems(predictionList);
            System.out.println("📊 " + list.size() + " prédictions chargées");
        } catch (Exception e) {
            System.err.println("❌ Erreur refreshTable: " + e.getMessage());
        }
    }
    
    private void updateStatistiques() {
        try {
            List<PredictionEchouage> toutes = service.getAll();
            long alertesRouges = toutes.stream().filter(p -> p.getNiveauRisque() >= 7).count();
            double risqueMoyen = toutes.stream().mapToInt(PredictionEchouage::getNiveauRisque).average().orElse(0);
            
            lblStatistiques.setText(
                "📊 Total: " + toutes.size() + 
                " | Alertes: " + alertesRouges +
                " | Risque moyen: " + String.format("%.1f", risqueMoyen) + "/10"
            );
        } catch (Exception e) {
            lblStatistiques.setText("📊 Total: 0 | Alertes: 0 | Risque moyen: 0/10");
        }
    }
    
    private void displayPredictionDetails(PredictionEchouage p) {
        dpDatePrediction.setValue(p.getDatePrediction());
        cmbZone.setValue(p.getZone());
        sliderRisque.setValue(p.getNiveauRisque());
        lblNiveauRisque.setText(String.valueOf(p.getNiveauRisque()));
        cmbEspece.setValue(p.getEspeceConcernee());
        txtTemperatureEau.setText(String.valueOf(p.getTemperatureEau()));
        cmbConditionsMeteo.setValue(p.getConditionsMeteo());
        txtRecommandations.setText(p.getRecommandations());
    }
    
    private void clearForm() {
        dpDatePrediction.setValue(LocalDate.now());
        cmbZone.setValue(null);
        sliderRisque.setValue(5);
        lblNiveauRisque.setText("5");
        cmbEspece.setValue(null);
        txtTemperatureEau.clear();
        cmbConditionsMeteo.setValue(null);
        txtRecommandations.clear();
        selectedPrediction = null;
        tableView.getSelectionModel().clearSelection();
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
    }
    
    private boolean validateInputs() {
        if (dpDatePrediction.getValue() == null) {
            showAlert("Erreur de validation", "❌ La date de prédiction est obligatoire", Alert.AlertType.ERROR);
            dpDatePrediction.requestFocus();
            return false;
        }
        
        if (cmbZone.getValue() == null) {
            showAlert("Erreur de validation", "❌ La zone est obligatoire", Alert.AlertType.ERROR);
            cmbZone.requestFocus();
            return false;
        }
        
        if (cmbEspece.getValue() == null) {
            showAlert("Erreur de validation", "❌ L'espèce concernée est obligatoire", Alert.AlertType.ERROR);
            cmbEspece.requestFocus();
            return false;
        }
        
        if (txtTemperatureEau.getText() == null || txtTemperatureEau.getText().trim().isEmpty()) {
            showAlert("Erreur de validation", "❌ La température de l'eau est obligatoire", Alert.AlertType.ERROR);
            txtTemperatureEau.requestFocus();
            return false;
        }
        
        try {
            double temp = Double.parseDouble(txtTemperatureEau.getText());
            if (temp < -5 || temp > 40) {
                showAlert("Erreur de validation", "❌ La température doit être comprise entre -5°C et 40°C", Alert.AlertType.ERROR);
                txtTemperatureEau.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur de validation", "❌ La température doit être un nombre valide", Alert.AlertType.ERROR);
            txtTemperatureEau.requestFocus();
            return false;
        }
        
        if (cmbConditionsMeteo.getValue() == null) {
            showAlert("Erreur de validation", "❌ Les conditions météo sont obligatoires", Alert.AlertType.ERROR);
            cmbConditionsMeteo.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}