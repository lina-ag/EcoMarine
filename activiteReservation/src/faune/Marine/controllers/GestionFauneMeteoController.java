package faune.Marine.controllers;

import faune.Marine.entities.Observation;
import faune.Marine.entities.FauneMarine;
import faune.Marine.services.ServiceFauneMarine;
import faune.Marine.services.ServiceObservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class GestionFauneMeteoController implements Initializable {
    
    @FXML
    private Label lblTotalAnimaux;
    
    @FXML
    private Label lblTotalObservations;
    
    private ServiceFauneMarine serviceFaune;
    private ServiceObservation serviceObservation;
    private ObservableList<FauneMarine> animauxList;
    private ObservableList<Observation> observationsList;
    
    private MainController mainController;
    
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("✅ GestionFauneMeteoController initialisé");
        
        try {
            serviceFaune = new ServiceFauneMarine();
            serviceObservation = new ServiceObservation();
            
            animauxList = FXCollections.observableArrayList();
            observationsList = FXCollections.observableArrayList();
            
            refreshData();
            
        } catch (Exception e) {
            System.err.println("❌ Erreur initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void refreshData() {
        loadData();
        updateStatistics();
    }
    
    private void loadData() {
        try {
            animauxList.clear();
            animauxList.addAll(serviceFaune.getAll());
            
            observationsList.clear();
            observationsList.addAll(serviceObservation.getAll());
            
            System.out.println("📊 Données chargées: " + animauxList.size() + " animaux, " + 
                             observationsList.size() + " observations");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement données: " + e.getMessage());
        }
    }
    
    private void updateStatistics() {
        lblTotalAnimaux.setText(String.valueOf(animauxList.size()));
        lblTotalObservations.setText(String.valueOf(observationsList.size()));
    }
    
    @FXML
    private void handleTableauBord() {
        System.out.println("Tableau de Bord - Rafraîchissement");
        refreshData();
    }
    
    @FXML
    private void handleFauneMarine() {
        System.out.println("Navigation vers Faune Marine");
        if (mainController != null) {
            mainController.navigateToFauneMarine();
        }
    }
    
    @FXML
    private void handleObservations() {
        System.out.println("Navigation vers Observations");
        if (mainController != null) {
            mainController.navigateToObservations();
        }
    }
    
    @FXML
    private void handlePredictionEchouages() {
        System.out.println("Navigation vers Prédiction des Échouages");
        if (mainController != null) {
            mainController.navigateToPredictionEchouages();
        }
    }
    
    @FXML
    private void handleMissionsDrone() {
        System.out.println("Navigation vers Missions Drone");
        if (mainController != null) {
            mainController.navigateToMissionsDrone();
        }
    }
}