package faune.Marine.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class TopNavigationBarController {
    
    @FXML
    private Button btnDashboard;
    
    @FXML
    private Button btnFauneMarine;
    
    @FXML
    private Button btnObservations;
    
    @FXML
    private Button btnQuitter;
    
    private MainController mainController;
    
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    @FXML
    private void handleDashboard() {
        if (mainController != null) {
            mainController.navigateToDashboard();
        }
    }
    
    @FXML
    private void handleFauneMarine() {
        if (mainController != null) {
            mainController.navigateToFauneMarine();
        }
    }
    
    @FXML
    private void handleObservations() {
        if (mainController != null) {
            mainController.navigateToObservations();
        }
    }
    
    @FXML
    private void handleQuitter() {
        // Fermer directement l'application
        Stage stage = (Stage) btnQuitter.getScene().getWindow();
        stage.close();
    }
}