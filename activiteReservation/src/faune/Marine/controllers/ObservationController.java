package faune.Marine.controllers;

import faune.Marine.entities.Observation;
import faune.Marine.entities.FauneMarine;
import faune.Marine.services.ServiceObservation;
import faune.Marine.services.ServiceFauneMarine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ObservationController implements Initializable {
    
    // ==================== COMPOSANTS FXML ====================
    
    @FXML
    private TableView<Observation> tableView;
    
    @FXML
    private TableColumn<Observation, Integer> colId;
    
    @FXML
    private TableColumn<Observation, LocalDate> colDate;
    
    @FXML
    private TableColumn<Observation, Double> colTemperature;
    
    @FXML
    private TableColumn<Observation, String> colMeteo;
    
    @FXML
    private TableColumn<Observation, String> colAnimal;
    
    @FXML
    private DatePicker dpDateObservation;
    
    @FXML
    private TextField txtTemperature;
    
    @FXML
    private ComboBox<String> cmbMeteo;
    
    @FXML
    private ComboBox<FauneMarine> cmbAnimal;
    
    @FXML
    private Button btnAjouter;
    
    @FXML
    private Button btnModifier;
    
    @FXML
    private Button btnSupprimer;
    
    @FXML
    private Button btnEffacer;
    
    @FXML
    private DatePicker dpDateDebut;
    
    @FXML
    private DatePicker dpDateFin;
    
    @FXML
    private Label lblTotalObservations;
    
    @FXML
    private Label lblTempMoyenne;
    
    @FXML
    private Label lblMeteoDominante;
    
    // ==================== SERVICES ET DONNÉES ====================
    
    private ServiceObservation service;
    private ServiceFauneMarine serviceFaune;
    private ObservableList<Observation> observationList;
    private ObservableList<FauneMarine> animalList;
    private Observation selectedObservation;
    
    // ==================== INITIALISATION ====================
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("✅ Initialisation du ObservationController...");
        
        try {
            // Initialiser les services
            service = new ServiceObservation();
            serviceFaune = new ServiceFauneMarine();
            
            // Initialiser les listes observables
            observationList = FXCollections.observableArrayList();
            animalList = FXCollections.observableArrayList();
            
            // Configurer les colonnes du tableau
            setupTableColumns();
            
            // Configurer les ComboBox
            setupComboBoxes();
            
            // Charger les données
            loadAnimals();
            refreshData();
            
            // Configurer les listeners
            setupListeners();
            
            System.out.println("✅ ObservationController initialisé avec succès");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Configuration des colonnes du tableau
     */
    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idObservation"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateObservation"));
        colTemperature.setCellValueFactory(new PropertyValueFactory<>("temperature"));
        colMeteo.setCellValueFactory(new PropertyValueFactory<>("meteo"));
        
        // Configuration spéciale pour la colonne animal (affichage de l'espèce)
        colAnimal.setCellValueFactory(cellData -> {
            if (cellData.getValue() != null && cellData.getValue().getAnimal() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getAnimal().getEspece()
                );
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        
        // Formatage de la colonne température
        colTemperature.setCellFactory(column -> new TableCell<Observation, Double>() {
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
        // Configurer les options météo
        cmbMeteo.getItems().addAll(
            "☀️ Ensoleillé",
            "⛅ Nuageux", 
            "🌧️ Pluvieux",
            "⛈️ Orageux",
            "🌫️ Brouillard",
            "🌬️ Venteux"
        );
        
        // Configurer l'affichage des animaux dans la ComboBox
        cmbAnimal.setCellFactory(param -> new ListCell<FauneMarine>() {
            @Override
            protected void updateItem(FauneMarine item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getEspece() + " (ID: " + item.getIdAnimal() + ")");
                }
            }
        });
        
        cmbAnimal.setButtonCell(new ListCell<FauneMarine>() {
            @Override
            protected void updateItem(FauneMarine item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getEspece());
                }
            }
        });
    }
    
    /**
     * Configuration des listeners
     */
    private void setupListeners() {
        // Listener pour la sélection dans le tableau
        tableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedObservation = newSelection;
                    displayObservationDetails(newSelection);
                    btnModifier.setDisable(false);
                    btnSupprimer.setDisable(false);
                }
            }
        );
        
        // Validation de la température (uniquement des nombres)
        txtTemperature.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                txtTemperature.setText(oldVal);
            }
        });
    }
    
    // ==================== CHARGEMENT DES DONNÉES ====================
    
    /**
     * Charge tous les animaux dans la ComboBox
     */
    private void loadAnimals() {
        try {
            animalList.clear();
            animalList.addAll(serviceFaune.getAll());
            cmbAnimal.setItems(animalList);
            
            if (animalList.isEmpty()) {
                System.out.println("⚠️ Aucun animal trouvé dans la base de données");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des animaux: " + e.getMessage());
            showAlert("Erreur", "Impossible de charger la liste des animaux: " + e.getMessage(), 
                     Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Charge toutes les observations
     */
    private void loadData() {
        try {
            observationList.clear();
            List<Observation> observations = service.getAll();
            observationList.addAll(observations);
            tableView.setItems(observationList);
            
            System.out.println("✅ " + observations.size() + " observation(s) chargée(s)");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des observations: " + e.getMessage());
            showAlert("Erreur", "Impossible de charger les observations: " + e.getMessage(), 
                     Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Rafraîchit toutes les données et met à jour les statistiques
     */
    private void refreshData() {
        loadData();
        updateStats();
    }
    
    // ==================== STATISTIQUES ====================
    
    /**
     * Met à jour les statistiques affichées
     */
    private void updateStats() {
        try {
            List<Observation> observations = service.getAll();
            
            // Mettre à jour le total
            lblTotalObservations.setText("Total: " + observations.size());
            
            if (!observations.isEmpty()) {
                // Calculer la température moyenne
                double moyenne = observations.stream()
                    .mapToDouble(Observation::getTemperature)
                    .average()
                    .orElse(0.0);
                lblTempMoyenne.setText(String.format("🌡️ Temp. moyenne: %.1f °C", moyenne));
                
                // Trouver la météo dominante
                Map<String, Long> meteoCount = observations.stream()
                    .filter(o -> o.getMeteo() != null && !o.getMeteo().isEmpty())
                    .collect(Collectors.groupingBy(Observation::getMeteo, Collectors.counting()));
                
                if (!meteoCount.isEmpty()) {
                    String meteoDominante = meteoCount.entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("N/A");
                    
                    // Extraire l'emoji si présent
                    String meteoSimple = meteoDominante.replaceAll("[☀️⛅🌧️⛈️🌫️🌬️]", "").trim();
                    lblMeteoDominante.setText("☁️ Météo dominante: " + meteoSimple);
                } else {
                    lblMeteoDominante.setText("☁️ Météo dominante: N/A");
                }
            } else {
                lblTempMoyenne.setText("🌡️ Temp. moyenne: -- °C");
                lblMeteoDominante.setText("☁️ Météo dominante: --");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du calcul des stats: " + e.getMessage());
        }
    }
    
    // ==================== GESTION DES ÉVÉNEMENTS ====================
    
    @FXML
    private void handleAjouter() {
        if (!validateInputs()) return;
        
        try {
            Observation o = new Observation(
                dpDateObservation.getValue(),
                Double.parseDouble(txtTemperature.getText()),
                cmbMeteo.getValue(),
                cmbAnimal.getValue()
            );
            
            service.ajouter(o);
            refreshData();
            clearForm();
            showAlert("Succès", "✅ Observation ajoutée avec succès!", Alert.AlertType.INFORMATION);
            
        } catch (NumberFormatException e) {
            showAlert("Erreur", "❌ La température doit être un nombre valide", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erreur", "❌ Erreur lors de l'ajout: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleModifier() {
        if (selectedObservation == null) {
            showAlert("Attention", "Veuillez sélectionner une observation à modifier", Alert.AlertType.WARNING);
            return;
        }
        
        if (!validateInputs()) return;
        
        try {
            selectedObservation.setDateObservation(dpDateObservation.getValue());
            selectedObservation.setTemperature(Double.parseDouble(txtTemperature.getText()));
            selectedObservation.setMeteo(cmbMeteo.getValue());
            selectedObservation.setAnimal(cmbAnimal.getValue());
            
            service.modifier(selectedObservation);
            refreshData();
            clearForm();
            showAlert("Succès", "✅ Observation modifiée avec succès!", Alert.AlertType.INFORMATION);
            
        } catch (NumberFormatException e) {
            showAlert("Erreur", "❌ La température doit être un nombre valide", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erreur", "❌ Erreur lors de la modification: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleSupprimer() {
        if (selectedObservation == null) {
            showAlert("Attention", "Veuillez sélectionner une observation à supprimer", Alert.AlertType.WARNING);
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'observation");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette observation du " + 
                            selectedObservation.getDateObservation() + " ?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                service.supprimer(selectedObservation.getIdObservation());
                refreshData();
                clearForm();
                showAlert("Succès", "✅ Observation supprimée avec succès!", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "❌ Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleEffacer() {
        clearForm();
    }
    
    @FXML
    private void handleFiltrer() {
        if (dpDateDebut.getValue() == null || dpDateFin.getValue() == null) {
            showAlert("Attention", "Veuillez sélectionner une date de début ET une date de fin", 
                     Alert.AlertType.WARNING);
            return;
        }
        
        LocalDate debut = dpDateDebut.getValue();
        LocalDate fin = dpDateFin.getValue();
        
        if (debut.isAfter(fin)) {
            showAlert("Erreur", "La date de début doit être antérieure à la date de fin", 
                     Alert.AlertType.ERROR);
            return;
        }
        
        // Filtrer les observations par période
        ObservableList<Observation> filteredList = FXCollections.observableArrayList();
        
        for (Observation o : observationList) {
            LocalDate date = o.getDateObservation();
            if ((date.isEqual(debut) || date.isAfter(debut)) && 
                (date.isEqual(fin) || date.isBefore(fin))) {
                filteredList.add(o);
            }
        }
        
        tableView.setItems(filteredList);
        lblTotalObservations.setText("Filtré: " + filteredList.size() + " / " + observationList.size());
        
        if (filteredList.isEmpty()) {
            showAlert("Information", "Aucune observation trouvée pour cette période", 
                     Alert.AlertType.INFORMATION);
        }
    }
    
    @FXML
    private void handleReinitialiserFiltre() {
        dpDateDebut.setValue(null);
        dpDateFin.setValue(null);
        tableView.setItems(observationList);
        updateStats();
    }
    
    // ==================== MÉTHODES UTILITAIRES ====================
    
    /**
     * Affiche les détails d'une observation dans le formulaire
     */
    private void displayObservationDetails(Observation o) {
        dpDateObservation.setValue(o.getDateObservation());
        txtTemperature.setText(String.valueOf(o.getTemperature()));
        cmbMeteo.setValue(o.getMeteo());
        cmbAnimal.setValue(o.getAnimal());
    }
    
    /**
     * Vide le formulaire
     */
    private void clearForm() {
        dpDateObservation.setValue(null);
        txtTemperature.clear();
        cmbMeteo.setValue(null);
        cmbAnimal.setValue(null);
        selectedObservation = null;
        tableView.getSelectionModel().clearSelection();
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
    }
    
    /**
     * Valide les entrées du formulaire
     */
    private boolean validateInputs() {
        if (dpDateObservation.getValue() == null) {
            showAlert("Erreur de validation", "❌ La date d'observation est obligatoire", 
                     Alert.AlertType.ERROR);
            dpDateObservation.requestFocus();
            return false;
        }
        
        if (txtTemperature.getText() == null || txtTemperature.getText().trim().isEmpty()) {
            showAlert("Erreur de validation", "❌ La température est obligatoire", 
                     Alert.AlertType.ERROR);
            txtTemperature.requestFocus();
            return false;
        }
        
        try {
            double temp = Double.parseDouble(txtTemperature.getText());
            if (temp < -50 || temp > 60) {
                showAlert("Erreur de validation", 
                         "❌ La température doit être comprise entre -50°C et 60°C", 
                         Alert.AlertType.ERROR);
                txtTemperature.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur de validation", "❌ La température doit être un nombre valide", 
                     Alert.AlertType.ERROR);
            txtTemperature.requestFocus();
            return false;
        }
        
        if (cmbMeteo.getValue() == null) {
            showAlert("Erreur de validation", "❌ La météo est obligatoire", 
                     Alert.AlertType.ERROR);
            cmbMeteo.requestFocus();
            return false;
        }
        
        if (cmbAnimal.getValue() == null) {
            showAlert("Erreur de validation", "❌ L'animal observé est obligatoire", 
                     Alert.AlertType.ERROR);
            cmbAnimal.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Affiche une alerte
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}