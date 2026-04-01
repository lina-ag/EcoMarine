package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.edu.esprit.entities.ZoneProtegee;
import tn.edu.esprit.services.ServiceSurv;
import tn.edu.esprit.services.ServiceZoneP;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GestionZonesPr {

    // ── Labels sidebar ──
    @FXML private Label lblNbZones;
    @FXML private Label lblNbSurv;
    @FXML private Label lblNbMenacees;

    // ── KPI Cards ──
    @FXML private Label kpiTotalZones;
    @FXML private Label kpiTotalZonesSub;
    @FXML private Label kpiActives;
    @FXML private Label kpiActivesSub;
    @FXML private Label kpiMenacees;
    @FXML private Label kpimenacessSub;
    @FXML private Label lblScoreEco;
    @FXML private Label kpiEcoSub;

    // ── Titre ──
    @FXML private Label lblDateHeure;

    // ── Alerte ──
    @FXML private Label lblAlerteContenu;
    @FXML private VBox  panneauAlerte;

    // ── Dernière activité (gardé de l'original) ──
    @FXML private Label lblDerniereZone;
    @FXML private Label lblDerniereSurv;

    // ── Graphiques ──
    @FXML private PieChart pieChart;
    @FXML private BarChart<String, Number> barChart;

    // ── Zones menacées liste ──
    @FXML private VBox listeMenacees;

    // ── Barres de progression ──
    @FXML private ProgressBar barActives;
    @FXML private ProgressBar barRestauration;
    @FXML private ProgressBar barMenacees;
    @FXML private ProgressBar barInactives;
    @FXML private Label pctActives;
    @FXML private Label pctRestauration;
    @FXML private Label pctMenacees;
    @FXML private Label pctInactives;

    // ── Centre ──
    @FXML private StackPane centerPane;

    private ServiceZoneP serviceZone = new ServiceZoneP();
    private ServiceSurv  serviceSurv  = new ServiceSurv();

    // ════════════════════════════════════════
    //  INITIALIZE
    // ════════════════════════════════════════
    @FXML
    public void initialize() {
        demarrerHorloge();
        updateStats();
    }

    // Horloge en temps réel dans le titre
    private void demarrerHorloge() {
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            String now = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy  |  HH:mm:ss"));
            lblDateHeure.setText(now);
        }));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    // ════════════════════════════════════════
    //  UPDATE GLOBAL
    // ════════════════════════════════════════
    public void updateStats() {
        List<ZoneProtegee> zones = serviceZone.getAll(null);
        int nbZones = zones.size();
        int nbSurv  = serviceSurv.getAll(null).size();

        int active      = 0, restauration = 0, menacee = 0, inactive = 0;
        for (ZoneProtegee z : zones) {
            String s = z.getStatut().toLowerCase().trim();
            if      (s.contains("active") && !s.contains("in")) active++;
            else if (s.contains("restauration"))                 restauration++;
            else if (s.contains("menac"))                        menacee++;
            else if (s.contains("inactive"))                     inactive++;
        }

        int total = nbZones == 0 ? 1 : nbZones;
        int score = ((active + restauration) * 100) / total;

        // ── Sidebar stats ──
        lblNbZones.setText(String.valueOf(nbZones));
        lblNbSurv.setText(String.valueOf(nbSurv));
        lblNbMenacees.setText(String.valueOf(menacee));

        // ── KPI Total ──
        kpiTotalZones.setText(String.valueOf(nbZones));
        kpiTotalZonesSub.setText(nbSurv + " surveillance(s) enregistrée(s)");

        // ── KPI Actives ──
        kpiActives.setText(String.valueOf(active));
        int pctA = (active * 100) / total;
        kpiActivesSub.setText(pctA + "% du total");

        // ── KPI Menacées ──
        kpiMenacees.setText(String.valueOf(menacee));
        kpimenacessSub.setText(menacee > 0 ? "Intervention requise !" : "Aucun risque détecté");

        // ── KPI Score Eco ──
        lblScoreEco.setText(score + "%");
        kpiEcoSub.setText(score >= 70 ? "Excellent état" : score >= 40 ? "État moyen" : "État critique");
        if (score >= 70)
            lblScoreEco.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        else if (score >= 40)
            lblScoreEco.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: #f39c12;");
        else
            lblScoreEco.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        // ── Alerte menacées ──
        loadAlerte(zones, menacee);

        // ── Dernière activité (gardé de l'original) ──
        loadLastActivity();

        // ── Graphiques ──
        loadPieChart(active, restauration, menacee, inactive);
        loadBarChart(zones);

        // ── Liste zones menacées ──
        loadListeMenacees(zones);

        // ── Barres de progression ──
        loadProgressBars(active, restauration, menacee, inactive, total);
    }

    // ════════════════════════════════════════
    //  ALERTE
    // ════════════════════════════════════════
    private void loadAlerte(List<ZoneProtegee> zones, int nbMenacees) {
        if (nbMenacees == 0) {
            lblAlerteContenu.setText("Toutes les zones sont en bon état. Aucune intervention requise.");
            panneauAlerte.setStyle(
                "-fx-background-color: #f0fff4; -fx-background-radius: 18; -fx-padding: 18;" +
                "-fx-border-color: #b2f5c8; -fx-border-width: 1.5; -fx-border-radius: 18;");
        } else {
            String noms = zones.stream()
                .filter(z -> z.getStatut().toLowerCase().contains("menac"))
                .map(ZoneProtegee::getNomZone)
                .collect(Collectors.joining(", "));
            lblAlerteContenu.setText(nbMenacees + " zone(s) menacée(s) : " + noms);
            panneauAlerte.setStyle(
                "-fx-background-color: #fff5f5; -fx-background-radius: 18; -fx-padding: 18;" +
                "-fx-border-color: #ffcccc; -fx-border-width: 1.5; -fx-border-radius: 18;");
        }
    }

    // ════════════════════════════════════════
    //  DERNIERE ACTIVITE (gardé de l'original)
    // ════════════════════════════════════════
    public void loadLastActivity() {
        var zones = serviceZone.getAll(null);
        var surveillances = serviceSurv.getAll(null);

        if (!zones.isEmpty()) {
            var lastZone = zones.get(zones.size() - 1);
            lblDerniereZone.setText("Dernière zone : " + lastZone.getNomZone());
        } else {
            lblDerniereZone.setText("Aucune zone ajoutée");
        }

        if (!surveillances.isEmpty()) {
            var lastSurv = surveillances.get(surveillances.size() - 1);
            lblDerniereSurv.setText("Dernière surveillance : " + lastSurv.getDateSurveillance());
        } else {
            lblDerniereSurv.setText("Aucune surveillance");
        }
    }

    // ════════════════════════════════════════
    //  PIE CHART (gardé de l'original, amélioré)
    // ════════════════════════════════════════
    public void loadPieChart(int active, int restauration, int menacee, int inactive) {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
            new PieChart.Data("Menacée : "       + menacee,     menacee),
            new PieChart.Data("Active : "         + active,      active),
            new PieChart.Data("En restauration : " + restauration, restauration),
            new PieChart.Data("Inactive : "       + inactive,    inactive)
        );
        pieChart.setData(data);
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(true);
    }

    // ════════════════════════════════════════
    //  BAR CHART — zones par catégorie (NOUVEAU)
    // ════════════════════════════════════════
    public void loadBarChart(List<ZoneProtegee> zones) {
        barChart.getData().clear();
        barChart.setLegendVisible(false);

        Map<String, Long> parCategorie = zones.stream()
            .collect(Collectors.groupingBy(
                z -> z.getCategorieZone() == null ? "Autre" : z.getCategorieZone(),
                Collectors.counting()));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        parCategorie.forEach((cat, count) ->
            series.getData().add(new XYChart.Data<>(cat, count)));

        barChart.getData().add(series);
        barChart.setAnimated(true);
    }

    // ════════════════════════════════════════
    //  LISTE ZONES MENACEES (NOUVEAU)
    // ════════════════════════════════════════
    private void loadListeMenacees(List<ZoneProtegee> zones) {
        listeMenacees.getChildren().clear();

        List<ZoneProtegee> menacees = zones.stream()
            .filter(z -> z.getStatut().toLowerCase().contains("menac"))
            .collect(Collectors.toList());

        if (menacees.isEmpty()) {
            Label lbl = new Label("Aucune zone menacée");
            lbl.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 13px; -fx-font-weight: bold;");
            listeMenacees.getChildren().add(lbl);
            return;
        }

        for (ZoneProtegee z : menacees) {
            HBox ligne = new HBox(10);
            ligne.setStyle(
                "-fx-background-color: #fff0f0;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 10 14;");
            ligne.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            // Indicateur rouge
            Region dot = new Region();
            dot.setMinSize(10, 10);
            dot.setMaxSize(10, 10);
            dot.setStyle("-fx-background-color: #e74c3c; -fx-background-radius: 5;");

            VBox info = new VBox(2);
            Label nom = new Label(z.getNomZone());
            nom.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #333;");
            Label cat = new Label(z.getCategorieZone());
            cat.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
            info.getChildren().addAll(nom, cat);

            ligne.getChildren().addAll(dot, info);
            listeMenacees.getChildren().add(ligne);
        }
    }

    // ════════════════════════════════════════
    //  BARRES DE PROGRESSION (NOUVEAU)
    // ════════════════════════════════════════
    private void loadProgressBars(int active, int restauration,
                                   int menacee, int inactive, int total) {
        double pA  = (double) active       / total;
        double pR  = (double) restauration / total;
        double pM  = (double) menacee      / total;
        double pI  = (double) inactive     / total;

        barActives.setProgress(pA);
        barRestauration.setProgress(pR);
        barMenacees.setProgress(pM);
        barInactives.setProgress(pI);

        pctActives.setText(Math.round(pA * 100) + "%  (" + active + ")");
        pctRestauration.setText(Math.round(pR * 100) + "%  (" + restauration + ")");
        pctMenacees.setText(Math.round(pM * 100) + "%  (" + menacee + ")");
        pctInactives.setText(Math.round(pI * 100) + "%  (" + inactive + ")");
    }

    // ════════════════════════════════════════
    //  BOUTON ACTUALISER (NOUVEAU)
    // ════════════════════════════════════════
    @FXML
    private void actualiserDashboard() {
        updateStats();
    }

    // ════════════════════════════════════════
    //  NAVIGATION (gardé de l'original)
    // ════════════════════════════════════════
    @FXML private void ouvrirAjouter()         { ouvrirFenetre("/AjouterZone.fxml",         "Ajouter Zone"); }
    @FXML private void ouvrirAfficher()        { ouvrirFenetre("/AfficherZones.fxml",        "Liste des Zones"); }
    @FXML private void ouvrirSurveillance()    { ouvrirFenetre("/AfficherSurveillance.fxml", "Surveillances"); }
    @FXML private void ouvrirAjouterSurv()     { ouvrirFenetre("/AjouterSurveillance.fxml",  "Nouvelle Surveillance"); }
    @FXML private void ouvrirAccueil()         { ouvrirFenetre("/acceuil.fxml",              "Accueil"); }
    @FXML private void ouvrirRechercheVocale() { ouvrirFenetre("/RechercheVocale.fxml",      "Recherche Vocale"); }

    private void ouvrirFenetre(String chemin, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(chemin));
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setTitle(titre);
            newStage.setScene(new Scene(root));
            newStage.setFullScreen(true);
            Stage currentStage = (Stage) lblNbZones.getScene().getWindow();
            currentStage.close();
            
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ouvrirCarte() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/CarteZones.fxml"));
            centerPane.getChildren().setAll(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}
