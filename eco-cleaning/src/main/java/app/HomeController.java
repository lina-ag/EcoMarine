package app;

import javafx.fxml.FXML;

public class HomeController {
    @FXML
    private void openMain() {
        MainApp.setRoot("/main.fxml", "Eco - Nettoyage & Volontaires");
    }
}