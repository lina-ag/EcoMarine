package faune.Marine.controllers;

import faune.Marine.entities.MissionDrone;
import faune.Marine.entities.DetectionDrone;
import faune.Marine.services.ServiceMissionDrone;
import faune.Marine.services.ServiceDetectionDrone;
import faune.Marine.services.OllamaService;
import faune.Marine.services.OllamaDetectionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.scene.image.ImageView;
import javafx.application.Platform;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;  // ← IMPORT AJOUTÉ
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MissionDroneController implements Initializable {
    
    // ==================== COMPOSANTS FXML MISSIONS ====================
    
    @FXML
    private TableView<MissionDrone> tableView;
    
    @FXML
    private TableColumn<MissionDrone, Integer> colId;
    
    @FXML
    private TableColumn<MissionDrone, LocalDate> colDate;
    
    @FXML
    private TableColumn<MissionDrone, String> colZone;
    
    @FXML
    private TableColumn<MissionDrone, Double> colDistance;
    
    @FXML
    private TableColumn<MissionDrone, Integer> colAltitude;
    
    @FXML
    private TableColumn<MissionDrone, String> colConditions;
    
    // Formulaire mission
    @FXML
    private DatePicker dpDateMission;
    
    @FXML
    private TextField txtHeureDebut;
    
    @FXML
    private TextField txtHeureFin;
    
    @FXML
    private ComboBox<String> cmbZoneSurvolee;
    
    @FXML
    private TextField txtDistance;
    
    @FXML
    private TextField txtAltitude;
    
    @FXML
    private ComboBox<String> cmbConditionsVol;
    
    @FXML
    private TextArea txtObservations;
    
    @FXML
    private Button btnAjouter;
    
    @FXML
    private Button btnModifier;
    
    @FXML
    private Button btnSupprimer;
    
    @FXML
    private Button btnEffacer;
    
    // ==================== COMPOSANTS POUR L'IA ====================
    
    @FXML
    private TabPane tabPane;
    
    @FXML
    private Tab tabMissions;
    
    @FXML
    private Tab tabDetections;
    
    @FXML
    private Tab tabChat;
    
    @FXML
    private TableView<DetectionDrone> tableDetections;
    
    @FXML
    private TableColumn<DetectionDrone, Integer> colDetId;
    
    @FXML
    private TableColumn<DetectionDrone, String> colDetEspece;
    
    @FXML
    private TableColumn<DetectionDrone, Integer> colDetNombre;
    
    @FXML
    private TableColumn<DetectionDrone, String> colDetComportement;
    
    @FXML
    private TableColumn<DetectionDrone, String> colDetConfiance;
    
    @FXML
    private TableColumn<DetectionDrone, String> colDetTimestamp;
    
    @FXML
    private Button btnAnalyserAvecOllama;
    
    @FXML
    private Button btnExporterRapport;
    
    @FXML
    private Button btnVoirCarte;
    
    @FXML
    private Button btnDiscuter;
    
    @FXML
    private Label lblStatistiquesIA;
    
    @FXML
    private ImageView imagePreview;
    
    // ==================== SERVICES ====================
    
    private ServiceMissionDrone service;
    private ServiceDetectionDrone serviceDetection;
    private OllamaService ollamaService;
    private OllamaDetectionService ollamaDetectionService;
    private ObservableList<MissionDrone> missionList;
    private ObservableList<DetectionDrone> detectionList;
    private MissionDrone selectedMission;
    private String derniereAnalyse = "";
    private String dernierNomImage = "";
    private IAImageChatController chatController;
    
    // Modèle DeepSeek pour le chat
    private final String MODELE_DEEPSEEK = "deepseek-r1:7b-qwen-distill-q4_K_M";
    
    // ==================== INITIALISATION ====================
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("✅ MissionDroneController initialisé");
        System.out.println("🚀 Modèle DeepSeek: " + MODELE_DEEPSEEK);
        
        try {
            service = new ServiceMissionDrone();
            serviceDetection = new ServiceDetectionDrone();
            ollamaService = new OllamaService();
            ollamaDetectionService = new OllamaDetectionService();
            
            missionList = FXCollections.observableArrayList();
            detectionList = FXCollections.observableArrayList();
            
            setupMissionColumns();
            setupDetectionColumns();
            setupComboBoxes();
            
            refreshTable();
            verifierConnexionOllama();
            setupSelectionListener();
            
            btnAnalyserAvecOllama.setDisable(true);
            btnExporterRapport.setDisable(true);
            btnVoirCarte.setDisable(true);
            btnDiscuter.setDisable(true);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur initialisation: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur d'initialisation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void verifierConnexionOllama() {
        try {
            if (ollamaService.testConnexion()) {
                System.out.println("✅ Ollama est accessible");
            } else {
                System.err.println("❌ Ollama n'est pas accessible");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur connexion Ollama: " + e.getMessage());
        }
    }
    
    private void setupMissionColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idMission"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateMission"));
        colZone.setCellValueFactory(new PropertyValueFactory<>("zoneSurvolee"));
        colDistance.setCellValueFactory(new PropertyValueFactory<>("distanceParcourue"));
        colAltitude.setCellValueFactory(new PropertyValueFactory<>("altitudeVol"));
        colConditions.setCellValueFactory(new PropertyValueFactory<>("conditionsVol"));
        
        colDistance.setCellFactory(column -> new TableCell<MissionDrone, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f km", item));
                }
            }
        });
        
        colAltitude.setCellFactory(column -> new TableCell<MissionDrone, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item + " m");
                }
            }
        });
    }
    
    private void setupDetectionColumns() {
        colDetId.setCellValueFactory(new PropertyValueFactory<>("idDetection"));
        colDetEspece.setCellValueFactory(new PropertyValueFactory<>("espece"));
        colDetNombre.setCellValueFactory(new PropertyValueFactory<>("nombreIndividus"));
        colDetComportement.setCellValueFactory(new PropertyValueFactory<>("comportement"));
        colDetConfiance.setCellValueFactory(new PropertyValueFactory<>("confianceIA"));
        colDetTimestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        
        colDetConfiance.setCellFactory(column -> new TableCell<DetectionDrone, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Certaine") || item.equals("Haute")) {
                        setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
                    } else if (item.equals("Moyenne")) {
                        setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                    } else {
                        setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                    }
                }
            }
        });
        
        colDetTimestamp.setCellFactory(column -> new TableCell<DetectionDrone, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    try {
                        String[] parts = item.split("T");
                        if (parts.length > 1) {
                            setText(parts[0] + " " + parts[1].substring(0, 8));
                        } else {
                            setText(item);
                        }
                    } catch (Exception e) {
                        setText(item);
                    }
                }
            }
        });
    }
    
    private void setupComboBoxes() {
        cmbZoneSurvolee.getItems().addAll("Nord - Large", "Nord - Côtier", "Sud - Récif", "Sud - Lagons",
                                         "Est - Canyon", "Est - Plateau", "Ouest - Falaise", "Ouest - Baie",
                                         "Centre - Bassin", "Centre - Îlots", "Haute mer - Zone A", "Haute mer - Zone B");
        cmbConditionsVol.getItems().addAll("Excellent", "Bon", "Moyen", "Difficile", "Annulé");
    }
    
    private void setupSelectionListener() {
        tableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedMission = newSelection;
                    displayMissionDetails(newSelection);
                    btnModifier.setDisable(false);
                    btnSupprimer.setDisable(false);
                    btnAnalyserAvecOllama.setDisable(false);
                    loadDetectionsForMission(newSelection.getIdMission());
                }
            }
        );
    }
    
    // ==================== GESTION DES MISSIONS ====================
    
    @FXML
    private void handleAjouter() {
        if (validateInputs()) {
            try {
                MissionDrone m = new MissionDrone(
                    dpDateMission.getValue(),
                    LocalTime.parse(txtHeureDebut.getText()),
                    LocalTime.parse(txtHeureFin.getText()),
                    cmbZoneSurvolee.getValue(),
                    Double.parseDouble(txtDistance.getText()),
                    Integer.parseInt(txtAltitude.getText()),
                    cmbConditionsVol.getValue(),
                    txtObservations.getText()
                );
                
                service.ajouter(m);
                refreshTable();
                clearForm();
                showAlert("Succès", "✅ Mission drone ajoutée !", Alert.AlertType.INFORMATION);
                
            } catch (NumberFormatException e) {
                showAlert("Erreur", "❌ La distance et l'altitude doivent être des nombres valides", Alert.AlertType.ERROR);
            } catch (DateTimeParseException e) {
                showAlert("Erreur", "❌ Format d'heure invalide (utilisez HH:MM)", Alert.AlertType.ERROR);
            } catch (Exception e) {
                showAlert("Erreur", "❌ " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleModifier() {
        if (selectedMission != null && validateInputs()) {
            try {
                selectedMission.setDateMission(dpDateMission.getValue());
                selectedMission.setHeureDebut(LocalTime.parse(txtHeureDebut.getText()));
                selectedMission.setHeureFin(LocalTime.parse(txtHeureFin.getText()));
                selectedMission.setZoneSurvolee(cmbZoneSurvolee.getValue());
                selectedMission.setDistanceParcourue(Double.parseDouble(txtDistance.getText()));
                selectedMission.setAltitudeVol(Integer.parseInt(txtAltitude.getText()));
                selectedMission.setConditionsVol(cmbConditionsVol.getValue());
                selectedMission.setObservations(txtObservations.getText());
                
                service.modifier(selectedMission);
                refreshTable();
                clearForm();
                showAlert("Succès", "✅ Mission modifiée !", Alert.AlertType.INFORMATION);
                
            } catch (NumberFormatException e) {
                showAlert("Erreur", "❌ La distance et l'altitude doivent être des nombres valides", Alert.AlertType.ERROR);
            } catch (DateTimeParseException e) {
                showAlert("Erreur", "❌ Format d'heure invalide (utilisez HH:MM)", Alert.AlertType.ERROR);
            } catch (Exception e) {
                showAlert("Erreur", "❌ " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Attention", "Veuillez sélectionner une mission à modifier", Alert.AlertType.WARNING);
        }
    }
    
    @FXML
    private void handleSupprimer() {
        if (selectedMission != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Supprimer la mission");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer cette mission?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                service.supprimer(selectedMission.getIdMission());
                refreshTable();
                clearForm();
                detectionList.clear();
                tableDetections.setItems(detectionList);
                showAlert("Succès", "✅ Mission supprimée !", Alert.AlertType.INFORMATION);
            }
        } else {
            showAlert("Attention", "Veuillez sélectionner une mission à supprimer", Alert.AlertType.WARNING);
        }
    }
    
    @FXML
    private void handleEffacer() {
        clearForm();
    }
    
    // ==================== FONCTIONNALITÉS IA ====================
    
    @FXML
    private void handleAnalyserAvecOllama() {
        if (selectedMission == null) {
            showAlert("Attention", "Veuillez sélectionner une mission d'abord", Alert.AlertType.WARNING);
            return;
        }
        
        if (!ollamaService.testConnexion()) {
            showAlert("Erreur", "❌ Ollama n'est pas accessible", Alert.AlertType.ERROR);
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner les images");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png")
        );
        
        List<File> images = fileChooser.showOpenMultipleDialog(null);
        
        if (images != null && !images.isEmpty()) {
            Alert progressAlert = new Alert(Alert.AlertType.INFORMATION);
            progressAlert.setTitle("Analyse en cours");
            progressAlert.setHeaderText("🤖 Analyse...");
            progressAlert.setContentText("Traitement de " + images.size() + " images");
            progressAlert.show();
            
            int successCount = 0;
            int errorCount = 0;
            StringBuilder toutesAnalyses = new StringBuilder();
            
            try {
                for (File image : images) {
                    try {
                        OllamaDetectionService.DetectionResult result = 
                            ollamaDetectionService.analyserImage(image, selectedMission.getIdMission(), "bakllava");
                        
                        if (result.hasDetection()) {
                            serviceDetection.ajouter(result.getDetection());
                            successCount++;
                            
                            toutesAnalyses.append("📸 ").append(image.getName()).append("\n");
                            toutesAnalyses.append(result.getRapport()).append("\n\n");
                            
                            derniereAnalyse = result.getRapport();
                            dernierNomImage = image.getName();
                        }
                        
                        showDetailedReport("Analyse - " + image.getName(), result.getRapport());
                        
                    } catch (Exception e) {
                        errorCount++;
                        showAlert("Erreur", "Erreur sur " + image.getName(), Alert.AlertType.ERROR);
                    }
                }
                
                progressAlert.close();
                
                loadDetectionsForMission(selectedMission.getIdMission());
                tabPane.getSelectionModel().select(tabDetections);
                
                if (images.size() > 1) {
                    derniereAnalyse = toutesAnalyses.toString();
                    dernierNomImage = "Analyse multiple (" + images.size() + " images)";
                }
                
                if (successCount > 0) {
                    btnDiscuter.setDisable(false);
                    System.out.println("✅ Analyses réussies");
                }
                
                showAlert("Analyse terminée", 
                    "✅ Terminé !\n" +
                    "Images: " + images.size() + "\n" +
                    "Détections: " + successCount, 
                    Alert.AlertType.INFORMATION);
                
            } catch (Exception e) {
                progressAlert.close();
                showAlert("Erreur", "❌ Erreur: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleDiscuter() {
        if (derniereAnalyse.isEmpty()) {
            showAlert("Information", "Analysez d'abord une image", Alert.AlertType.INFORMATION);
            return;
        }
        
        System.out.println("💬 Passage à DeepSeek...");
        
        try {
            // Charger le FXML du chat
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/faune/Marine/ressource/IAImageChat.fxml"));
            Node chatNode = loader.load();
            chatController = loader.getController();
            
            // Remplacer le contenu de l'onglet chat
            tabChat.setContent(chatNode);
            
            // Passer à l'onglet chat
            tabPane.getSelectionModel().select(tabChat);
            
            // Initialiser le chat avec l'analyse
            chatController.initChat(derniereAnalyse, dernierNomImage);
            System.out.println("✅ Chat DeepSeek initialisé avec l'analyse");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Erreur: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleExporterRapport() {
        if (detectionList.isEmpty()) {
            showAlert("Attention", "Aucune détection à exporter", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter le rapport des détections");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichier CSV", "*.csv")
            );
            
            // Utiliser LocalDateTime pour inclure l'heure
            String fileName = "detections_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + 
                ".csv";
            fileChooser.setInitialFileName(fileName);
            
            // Dossier par défaut : Bureau
            String userHome = System.getProperty("user.home");
            File desktopPath = new File(userHome + "/Desktop");
            if (desktopPath.exists()) {
                fileChooser.setInitialDirectory(desktopPath);
            }
            
            File file = fileChooser.showSaveDialog(null);
            
            if (file != null) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                    // En-tête CSV
                    writer.println("ID Mission,Espèce,Nombre,Latitude,Longitude,Comportement,Confiance IA,Timestamp");
                    
                    // Données
                    for (DetectionDrone d : detectionList) {
                        writer.println(
                            d.getIdMission() + "," +
                            escapeCsv(d.getEspece()) + "," +
                            d.getNombreIndividus() + "," +
                            String.format("%.4f", d.getLatitude()) + "," +
                            String.format("%.4f", d.getLongitude()) + "," +
                            escapeCsv(d.getComportement()) + "," +
                            d.getConfianceIA() + "," +
                            d.getTimestamp()
                        );
                    }
                    
                    showAlert("Succès", "✅ Rapport exporté avec succès vers :\n" + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur export: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "❌ Erreur lors de l'export : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    // Méthode utilitaire pour échapper les virgules dans le CSV
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    @FXML
    private void handleVoirCarte() {
        if (detectionList.isEmpty()) {
            showAlert("Attention", "Aucune détection", Alert.AlertType.WARNING);
            return;
        }
        
        StringBuilder carte = new StringBuilder();
        carte.append("🗺️ **CARTE DES DÉTECTIONS**\n\n");
        
        for (DetectionDrone d : detectionList) {
            carte.append(String.format("📍 %s (%d) - %.4f, %.4f\n",
                d.getEspece(), d.getNombreIndividus(), d.getLatitude(), d.getLongitude()));
        }
        
        showDetailedReport("Carte", carte.toString());
    }
    
    // ==================== MÉTHODES UTILITAIRES ====================
    
    private void loadDetectionsForMission(int idMission) {
        try {
            detectionList.clear();
            List<DetectionDrone> detections = serviceDetection.getDetectionsByMission(idMission);
            detectionList.addAll(detections);
            tableDetections.setItems(detectionList);
            
            if (!detections.isEmpty()) {
                lblStatistiquesIA.setText("📊 " + detections.size() + " détections");
                btnExporterRapport.setDisable(false);
                btnVoirCarte.setDisable(false);
            } else {
                lblStatistiquesIA.setText("📊 Aucune détection");
                btnExporterRapport.setDisable(true);
                btnVoirCarte.setDisable(true);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }
    
    private void refreshTable() {
        try {
            missionList.clear();
            missionList.addAll(service.getAll());
            tableView.setItems(missionList);
        } catch (Exception e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }
    
    private void displayMissionDetails(MissionDrone m) {
        dpDateMission.setValue(m.getDateMission());
        txtHeureDebut.setText(m.getHeureDebut().toString());
        txtHeureFin.setText(m.getHeureFin().toString());
        cmbZoneSurvolee.setValue(m.getZoneSurvolee());
        txtDistance.setText(String.valueOf(m.getDistanceParcourue()));
        txtAltitude.setText(String.valueOf(m.getAltitudeVol()));
        cmbConditionsVol.setValue(m.getConditionsVol());
        txtObservations.setText(m.getObservations());
    }
    
    private void clearForm() {
        dpDateMission.setValue(LocalDate.now());
        txtHeureDebut.setText("09:00");
        txtHeureFin.setText("10:30");
        cmbZoneSurvolee.setValue(null);
        txtDistance.clear();
        txtAltitude.clear();
        cmbConditionsVol.setValue(null);
        txtObservations.clear();
        selectedMission = null;
        tableView.getSelectionModel().clearSelection();
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
        btnAnalyserAvecOllama.setDisable(true);
        btnExporterRapport.setDisable(true);
        btnVoirCarte.setDisable(true);
        btnDiscuter.setDisable(true);
        detectionList.clear();
        tableDetections.setItems(detectionList);
        lblStatistiquesIA.setText("");
        derniereAnalyse = "";
        dernierNomImage = "";
    }
    
    private boolean validateInputs() {
        if (dpDateMission.getValue() == null) {
            showAlert("Erreur", "La date est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        if (txtHeureDebut.getText().isEmpty()) {
            showAlert("Erreur", "L'heure de début est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        if (txtHeureFin.getText().isEmpty()) {
            showAlert("Erreur", "L'heure de fin est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        if (cmbZoneSurvolee.getValue() == null) {
            showAlert("Erreur", "La zone est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        if (txtDistance.getText().isEmpty()) {
            showAlert("Erreur", "La distance est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        if (txtAltitude.getText().isEmpty()) {
            showAlert("Erreur", "L'altitude est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        if (cmbConditionsVol.getValue() == null) {
            showAlert("Erreur", "Les conditions sont obligatoires", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    
    private void showDetailedReport(String title, String content) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setResizable(true);
        
        ButtonType closeButton = new ButtonType("Fermer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        
        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(20);
        textArea.setPrefColumnCount(60);
        
        dialog.getDialogPane().setContent(textArea);
        dialog.showAndWait();
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}