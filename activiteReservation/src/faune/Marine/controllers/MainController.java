package faune.Marine.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    
    @FXML
    private BorderPane mainBorderPane;
    
    @FXML
    private Button btnDashboard;
    
    @FXML
    private Button btnFauneMarine;
    
    @FXML
    private Button btnObservations;
    
    @FXML
    private Button btnQuitter;
    
    private GestionFauneMeteoController dashboardController;
    private TopNavigationBarController topBarController;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✅ MainController initialisé");
        showDashboard();
    }
    
    @FXML
    private void handleDashboard() {
        showDashboard();
    }
    
    @FXML
    private void handleFauneMarine() {
        showFauneMarine();
    }
    
    @FXML
    private void handleObservations() {
        showObservations();
    }
    
    @FXML
    private void handleQuitter() {
        Stage stage = (Stage) btnQuitter.getScene().getWindow();
        stage.close();
    }
    
    private void showDashboard() {
        System.out.println("Navigation vers Dashboard");
        loadView("/faune/Marine/ressource/GestionFauneMeteo.fxml", false);
        setActiveButton(btnDashboard);
    }
    
    private void showFauneMarine() {
        System.out.println("Navigation vers Faune Marine");
        loadView("/faune/Marine/ressource/FauneMarine.fxml", true);
        setActiveButton(btnFauneMarine);
    }
    
    private void showObservations() {
        System.out.println("Navigation vers Observations");
        loadView("/faune/Marine/ressource/Observation.fxml", true);
        setActiveButton(btnObservations);
    }
    
    // Méthodes pour les innovations
    public void navigateToPredictionEchouages() {
        System.out.println("Navigation vers Prédiction des Échouages");
        loadView("/faune/Marine/ressource/PredictionEchouage.fxml", true);
        setActiveButton(null);
    }
    
    public void navigateToMissionsDrone() {
        System.out.println("Navigation vers Missions Drone");
        loadView("/faune/Marine/ressource/MissionDrone.fxml", true);
        setActiveButton(null);
    }
    
    private void loadView(String fxmlPath, boolean showTopBar) {
        try {
            System.out.println("Chargement de: " + fxmlPath);
            
            URL resourceUrl = getClass().getResource(fxmlPath);
            if (resourceUrl == null) {
                System.err.println("❌ Fichier non trouvé: " + fxmlPath);
                showAlert("Erreur", "Fichier non trouvé: " + fxmlPath, Alert.AlertType.ERROR);
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent view = loader.load();
            
            // Si on charge le dashboard, garder une référence à son contrôleur
            if (fxmlPath.contains("GestionFauneMeteo")) {
                dashboardController = loader.getController();
                dashboardController.setMainController(this);
            }
            
            // Afficher ou cacher la barre supérieure
            if (showTopBar) {
                showTopNavigationBar();
            } else {
                hideTopNavigationBar();
            }
            
            mainBorderPane.setCenter(view);
            
            
            System.out.println("✅ Vue chargée avec succès: " + fxmlPath);
            
        } catch (IOException e) {
            System.err.println("❌ Erreur chargement: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void showTopNavigationBar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/faune/Marine/ressource/TopNavigationBar.fxml"));
            Parent topBar = loader.load();
            topBarController = loader.getController();
            topBarController.setMainController(this);
            mainBorderPane.setTop(topBar);
            System.out.println("✅ Barre supérieure affichée");
        } catch (IOException e) {
            System.err.println("❌ Erreur chargement barre supérieure: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void hideTopNavigationBar() {
        mainBorderPane.setTop(null);
        System.out.println("✅ Barre supérieure cachée");
    }
    
    private void setActiveButton(Button activeButton) {
        if (btnDashboard != null) btnDashboard.getStyleClass().remove("active-button");
        if (btnFauneMarine != null) btnFauneMarine.getStyleClass().remove("active-button");
        if (btnObservations != null) btnObservations.getStyleClass().remove("active-button");
        
        if (activeButton != null) {
            activeButton.getStyleClass().add("active-button");
        }
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Méthodes de navigation publiques
    public void navigateToFauneMarine() { showFauneMarine(); }
    public void navigateToObservations() { showObservations(); }
    public void navigateToDashboard() { showDashboard(); }
}