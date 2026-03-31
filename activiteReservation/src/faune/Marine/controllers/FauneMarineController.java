package faune.Marine.controllers;

import faune.Marine.entities.FauneMarine;
import faune.Marine.services.ServiceFauneMarine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class FauneMarineController implements Initializable {
    
    @FXML
    private TableView<FauneMarine> tableView;
    
    @FXML
    private TableColumn<FauneMarine, Integer> colId;
    
    @FXML
    private TableColumn<FauneMarine, String> colEspece;
    
    @FXML
    private TableColumn<FauneMarine, String> colEtat;
    
    @FXML
    private TableColumn<FauneMarine, String> colDescription;
    
    @FXML
    private TextField txtEspece;
    
    @FXML
    private ComboBox<String> cmbEtat;
    
    @FXML
    private TextArea txtDescription;
    
    @FXML
    private Button btnAjouter;
    
    @FXML
    private Button btnModifier;
    
    @FXML
    private Button btnSupprimer;
    
    @FXML
    private Button btnEffacer;
    
    private ServiceFauneMarine service;
    private ObservableList<FauneMarine> fauneList;
    private FauneMarine selectedFaune;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("✅ FauneMarineController initialisé");
        
        try {
            service = new ServiceFauneMarine();
            fauneList = FXCollections.observableArrayList();
            
            // Configuration des colonnes
            colId.setCellValueFactory(new PropertyValueFactory<>("idAnimal"));
            colEspece.setCellValueFactory(new PropertyValueFactory<>("espece"));
            colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));
            colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
            
            // Configuration de la ComboBox
            cmbEtat.getItems().addAll("Excellent", "Bon", "Moyen", "Mauvais", "Critique");
            
            // Chargement des données
            refreshTable();
            
            // Listener pour la sélection
            tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedFaune = newSelection;
                        displayFauneDetails(newSelection);
                        btnModifier.setDisable(false);
                        btnSupprimer.setDisable(false);
                    }
                }
            );
            
        } catch (Exception e) {
            System.err.println("❌ Erreur initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void refreshTable() {
        try {
            fauneList.clear();
            fauneList.addAll(service.getAll());
            tableView.setItems(fauneList);
            System.out.println("📊 " + fauneList.size() + " animaux chargés");
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAjouter() {
        if (validateInputs()) {
            try {
                FauneMarine f = new FauneMarine(
                    txtEspece.getText(),
                    cmbEtat.getValue(),
                    txtDescription.getText()
                );
                
                service.ajouter(f);
                refreshTable();
                clearForm();
                showAlert("Succès", "✅ Faune marine ajoutée!", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "❌ " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleModifier() {
        if (selectedFaune != null && validateInputs()) {
            try {
                selectedFaune.setEspece(txtEspece.getText());
                selectedFaune.setEtat(cmbEtat.getValue());
                selectedFaune.setDescription(txtDescription.getText());
                
                service.modifier(selectedFaune);
                refreshTable();
                clearForm();
                showAlert("Succès", "✅ Faune marine modifiée!", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "❌ " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleSupprimer() {
        if (selectedFaune != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Supprimer?");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    service.supprimer(selectedFaune.getIdAnimal());
                    refreshTable();
                    clearForm();
                    showAlert("Succès", "✅ Faune marine supprimée!", Alert.AlertType.INFORMATION);
                } catch (Exception e) {
                    showAlert("Erreur", "❌ " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        }
    }
    
    @FXML
    private void handleEffacer() {
        clearForm();
    }
    
    private void displayFauneDetails(FauneMarine f) {
        txtEspece.setText(f.getEspece());
        cmbEtat.setValue(f.getEtat());
        txtDescription.setText(f.getDescription());
    }
    
    private void clearForm() {
        txtEspece.clear();
        cmbEtat.setValue(null);
        txtDescription.clear();
        selectedFaune = null;
        tableView.getSelectionModel().clearSelection();
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
    }
    
    private boolean validateInputs() {
        if (txtEspece.getText() == null || txtEspece.getText().trim().isEmpty()) {
            showAlert("Erreur", "L'espèce est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        if (cmbEtat.getValue() == null) {
            showAlert("Erreur", "L'état est obligatoire", Alert.AlertType.ERROR);
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