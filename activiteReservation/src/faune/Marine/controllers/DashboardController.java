package faune.Marine.controllers;

import faune.Marine.entities.Observation;
import faune.Marine.entities.FauneMarine;
import faune.Marine.services.ServiceFauneMarine;
import faune.Marine.services.ServiceObservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardController implements Initializable {
    
    // ==================== COMPOSANTS FXML ====================
    
    @FXML
    private Label lblTotalAnimaux;
    
    @FXML
    private Label lblTotalObservations;
    
    @FXML
    private Label lblEspecesUniques;
    
    @FXML
    private Label lblDerniereObservation;
    
    @FXML
    private PieChart pieChartEtats;
    
    @FXML
    private BarChart<String, Number> barChartObservations;
    
    @FXML
    private LineChart<String, Number> lineChartTemperatures;
    
    @FXML
    private DatePicker dpDateDebut;
    
    @FXML
    private DatePicker dpDateFin;
    
    // ==================== SERVICES ====================
    
    private ServiceFauneMarine serviceFaune;
    private ServiceObservation serviceObservation;
    
    // ==================== DONNÉES ====================
    
    private ObservableList<FauneMarine> animauxList;
    private ObservableList<Observation> observationsList;
    
    // ==================== INITIALISATION ====================
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("✅ Initialisation du DashboardController...");
        
        try {
            serviceFaune = new ServiceFauneMarine();
            serviceObservation = new ServiceObservation();
            
            animauxList = FXCollections.observableArrayList();
            observationsList = FXCollections.observableArrayList();
            
            loadData();
            setupCharts();
            updateStatistics();
            
            System.out.println("✅ DashboardController initialisé avec succès");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur d'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ==================== CHARGEMENT DES DONNÉES ====================
    
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
    
    // ==================== CONFIGURATION DES GRAPHIQUES ====================
    
    private void setupCharts() {
        setupPieChart();
        setupBarChart();
        setupLineChart();
    }
    
    private void setupPieChart() {
        try {
            Map<String, Long> etatCount = animauxList.stream()
                .filter(a -> a.getEtat() != null && !a.getEtat().isEmpty())
                .collect(Collectors.groupingBy(
                    FauneMarine::getEtat,
                    Collectors.counting()
                ));
            
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            
            if (etatCount.isEmpty()) {
                pieChartData.add(new PieChart.Data("Aucune donnée", 1));
            } else {
                etatCount.forEach((etat, count) -> {
                    pieChartData.add(new PieChart.Data(etat + " (" + count + ")", count));
                });
            }
            
            pieChartEtats.setData(pieChartData);
            pieChartEtats.setTitle("État de santé des espèces");
            pieChartEtats.setClockwise(true);
            pieChartEtats.setLabelsVisible(true);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur configuration PieChart: " + e.getMessage());
        }
    }
    
    private void setupBarChart() {
        try {
            Map<String, Long> observationsParMois = observationsList.stream()
                .filter(o -> o.getDateObservation() != null)
                .collect(Collectors.groupingBy(
                    o -> o.getDateObservation().format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH)),
                    Collectors.counting()
                ));
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Observations");
            
            if (observationsParMois.isEmpty()) {
                series.getData().add(new XYChart.Data<>("Aucune donnée", 0));
            } else {
                observationsParMois.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                    });
            }
            
            barChartObservations.getData().clear();
            barChartObservations.getData().add(series);
            barChartObservations.setTitle("Observations par mois");
            barChartObservations.setAnimated(false);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur configuration BarChart: " + e.getMessage());
        }
    }
    
    private void setupLineChart() {
        try {
            List<Observation> sortedObs = observationsList.stream()
                .filter(o -> o.getDateObservation() != null)
                .sorted(Comparator.comparing(Observation::getDateObservation))
                .collect(Collectors.toList());
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Température");
            
            if (sortedObs.isEmpty()) {
                series.getData().add(new XYChart.Data<>("Aucune donnée", 0));
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                
                for (Observation o : sortedObs) {
                    series.getData().add(new XYChart.Data<>(
                        o.getDateObservation().format(formatter),
                        o.getTemperature()
                    ));
                }
            }
            
            lineChartTemperatures.getData().clear();
            lineChartTemperatures.getData().add(series);
            lineChartTemperatures.setTitle("Évolution des températures");
            lineChartTemperatures.setAnimated(false);
            lineChartTemperatures.setCreateSymbols(true);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur configuration LineChart: " + e.getMessage());
        }
    }
    
    // ==================== STATISTIQUES ====================
    
    private void updateStatistics() {
        updateTotalAnimaux();
        updateTotalObservations();
        updateEspecesUniques();
        updateDerniereObservation();
    }
    
    private void updateTotalAnimaux() {
        lblTotalAnimaux.setText(String.valueOf(animauxList.size()));
    }
    
    private void updateTotalObservations() {
        lblTotalObservations.setText(String.valueOf(observationsList.size()));
    }
    
    private void updateEspecesUniques() {
        long especesUniques = animauxList.stream()
            .map(FauneMarine::getEspece)
            .filter(Objects::nonNull)
            .distinct()
            .count();
        lblEspecesUniques.setText(String.valueOf(especesUniques));
    }
    
    private void updateDerniereObservation() {
        if (observationsList.isEmpty()) {
            lblDerniereObservation.setText("Aucune");
            return;
        }
        
        Optional<LocalDate> derniereDate = observationsList.stream()
            .map(Observation::getDateObservation)
            .filter(Objects::nonNull)
            .max(LocalDate::compareTo);
        
        if (derniereDate.isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            lblDerniereObservation.setText(derniereDate.get().format(formatter));
        } else {
            lblDerniereObservation.setText("N/A");
        }
    }
    
    // ==================== GESTION DES ÉVÉNEMENTS ====================
    
    @FXML
    private void handleFiltrer() {
        if (dpDateDebut.getValue() == null || dpDateFin.getValue() == null) {
            showAlert("Attention", "Veuillez sélectionner une période valide");
            return;
        }
        
        LocalDate debut = dpDateDebut.getValue();
        LocalDate fin = dpDateFin.getValue();
        
        if (debut.isAfter(fin)) {
            showAlert("Erreur", "La date de début doit être antérieure à la date de fin");
            return;
        }
        
        List<Observation> filteredObs = observationsList.stream()
            .filter(o -> o.getDateObservation() != null)
            .filter(o -> !o.getDateObservation().isBefore(debut) && !o.getDateObservation().isAfter(fin))
            .collect(Collectors.toList());
        
        updateChartsWithFilteredData(filteredObs);
        
        showAlert("Filtre appliqué", "Période du " + debut + " au " + fin + "\n" +
                  filteredObs.size() + " observation(s) trouvée(s)");
    }
    
    @FXML
    private void handleReinitialiserFiltre() {
        dpDateDebut.setValue(null);
        dpDateFin.setValue(null);
        loadData();
        setupCharts();
        updateStatistics();
        System.out.println("✅ Filtre réinitialisé");
    }
    
    private void updateChartsWithFilteredData(List<Observation> filteredObs) {
        lblTotalObservations.setText(String.valueOf(filteredObs.size()));
        
        // Mettre à jour le LineChart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Température");
        
        List<Observation> sortedFiltered = filteredObs.stream()
            .sorted(Comparator.comparing(Observation::getDateObservation))
            .collect(Collectors.toList());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Observation o : sortedFiltered) {
            series.getData().add(new XYChart.Data<>(
                o.getDateObservation().format(formatter),
                o.getTemperature()
            ));
        }
        
        lineChartTemperatures.getData().clear();
        lineChartTemperatures.getData().add(series);
        
        // Mettre à jour le BarChart
        Map<String, Long> obsParMois = filteredObs.stream()
            .collect(Collectors.groupingBy(
                o -> o.getDateObservation().format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH)),
                Collectors.counting()
            ));
        
        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        barSeries.setName("Observations");
        
        obsParMois.forEach((mois, count) -> {
            barSeries.getData().add(new XYChart.Data<>(mois, count));
        });
        
        barChartObservations.getData().clear();
        barChartObservations.getData().add(barSeries);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // ==================== MÉTHODES DE RAFRAÎCHISSEMENT ====================
    
    public void refreshData() {
        loadData();
        setupCharts();
        updateStatistics();
    }
}