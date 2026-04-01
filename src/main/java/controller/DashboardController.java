package controller;

import app.routes.SceneManager;
import dao.BiodiversiteDAO;
import dao.DechetDAO;
import dao.EvenementDAO;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.awt.Desktop;
import java.net.URI;
import java.util.Map;

public class DashboardController {

    @FXML private BorderPane rootPane;
    @FXML private VBox dashboardContent;
    @FXML private ToggleButton toggleDarkMode;

    @FXML private Label lblTotalDechets;
    @FXML private Label lblTotalEvenements;
    @FXML private Label lblTotalBiodiversite;

    @FXML private PieChart pieDechets;
    @FXML private BarChart<String, Number> barEvenements;
    @FXML private LineChart<String, Number> lineBiodiversite;

    private SceneManager sceneManager;
    private Node dashboardHome;

    private final DechetDAO dechetDAO = new DechetDAO();
    private final EvenementDAO evenementDAO = new EvenementDAO();
    private final BiodiversiteDAO biodiversiteDAO = new BiodiversiteDAO();

    @FXML
    public void initialize() {
        sceneManager = new SceneManager(rootPane);
        dashboardHome = dashboardContent;

        loadDashboardStats();
        loadCharts();

        if (dashboardContent != null) {
            applyFadeIn(dashboardContent);
            addEffectsToButtons(dashboardContent);
            addHoverEffectToCards(dashboardContent);
        }
    }

    @FXML
    private void showDashboard() {
        rootPane.setCenter(dashboardHome);

        if (dashboardHome != null) {
            applyFadeIn(dashboardHome);
            addEffectsToButtons(dashboardHome);
            addHoverEffectToCards(dashboardHome);
        }
    }

    @FXML
    private void openZones() {
        sceneManager.setCenter("zone_list.fxml");
    }

    @FXML
    private void openDechets() {
        sceneManager.setCenter("dechet_list.fxml");
    }

    @FXML
    private void openActions() {
        sceneManager.setCenter("action_list.fxml");
    }

    @FXML
    private void openVolontaires() {
        sceneManager.setCenter("volontaire_list.fxml");
    }

    @FXML
    private void openEvenements() {
        sceneManager.setCenter("evenement_list.fxml");
    }

    @FXML
    private void openBiodiversite() {
        sceneManager.setCenter("biodiversite_list.fxml");
    }

    @FXML
    private void toggleDarkMode() {
        if (rootPane == null || rootPane.getScene() == null) return;

        Node root = rootPane.getScene().getRoot();

        if (toggleDarkMode.isSelected()) {
            if (!root.getStyleClass().contains("dark-mode")) {
                root.getStyleClass().add("dark-mode");
            }
            toggleDarkMode.setText("☀️ Light mode");
        } else {
            root.getStyleClass().remove("dark-mode");
            toggleDarkMode.setText("🌙 Dark mode");
        }
    }

    @FXML
    private void openQrLink() {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/your-project-link"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDashboardStats() {
        if (lblTotalDechets != null) {
            lblTotalDechets.setText(String.valueOf(dechetDAO.countAll()));
        }
        if (lblTotalEvenements != null) {
            lblTotalEvenements.setText(String.valueOf(evenementDAO.countAll()));
        }
        if (lblTotalBiodiversite != null) {
            lblTotalBiodiversite.setText(String.valueOf(biodiversiteDAO.countAll()));
        }
    }

    private void loadCharts() {
        loadPieDechets();
        loadBarEvenements();
        loadLineBiodiversite();
    }

    private void loadPieDechets() {
        if (pieDechets == null) return;

        pieDechets.setData(FXCollections.observableArrayList());
        for (Map.Entry<String, Double> entry : dechetDAO.getDechetDistribution().entrySet()) {
            pieDechets.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        pieDechets.setLegendVisible(true);
        pieDechets.setLabelsVisible(true);
    }

    private void loadBarEvenements() {
        if (barEvenements == null) return;

        barEvenements.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Événements");

        for (Map.Entry<String, Integer> entry : evenementDAO.getEventsPerMonth().entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barEvenements.getData().add(series);
    }

    private void loadLineBiodiversite() {
        if (lineBiodiversite == null) return;

        lineBiodiversite.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Observations");

        for (Map.Entry<String, Integer> entry : biodiversiteDAO.getEvolutionData().entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        lineBiodiversite.getData().add(series);
    }

    private void applyFadeIn(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(500), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void addEffectsToButtons(Node parent) {
        for (Node node : parent.lookupAll(".button")) {
            node.setOnMousePressed(e -> animateScale(node, 0.96, 0.96, 100));
            node.setOnMouseReleased(e -> animateScale(node, 1.0, 1.0, 100));
        }
    }

    private void addHoverEffectToCards(Node parent) {
        for (Node node : parent.lookupAll(".hover-card")) {
            node.setOnMouseEntered(e -> animateScale(node, 1.02, 1.02, 150));
            node.setOnMouseExited(e -> animateScale(node, 1.0, 1.0, 150));
        }
    }

    private void animateScale(Node node, double x, double y, int millis) {
        ScaleTransition st = new ScaleTransition(Duration.millis(millis), node);
        st.setToX(x);
        st.setToY(y);
        st.play();
    }
}