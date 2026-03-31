package controllers;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import tn.edu.esprit.services.ServiceActivite;
import tn.edu.esprit.services.ServiceReservation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class TableauBordController {

    // KPI
    @FXML private Label lblTotalActivites;
    @FXML private Label lblTotalReservations;
    @FXML private Label lblTotalParticipants;
    @FXML private Label lblTauxRemplissage;

    // Progress bars KPI
    @FXML private Region progressActivites;
    @FXML private Region progressReservations;
    @FXML private Region progressParticipants;
    @FXML private Region progressTaux;

    // Graphiques
    @FXML private VBox chartReservations;
    @FXML private VBox chartActivites;

    // Donut
    @FXML private Canvas donutCanvas;
    @FXML private Label lblDonutPct;
    @FXML private Label lblLegendPart;
    @FXML private Label lblLegendDispo;
    @FXML private Label lblStatutOccupation;

    // Header
    @FXML private Label lblDateJour;
    @FXML private Label lblTotalMois;

    // Infos rapides
    @FXML private VBox boxInfosRapides;

    private final ServiceReservation serviceRes = new ServiceReservation();
    private final ServiceActivite    serviceAct = new ServiceActivite();

    @FXML
    public void initialize() {
        afficherDate();
        chargerTout();
    }

    @FXML
    private void rafraichir() {
        chartReservations.getChildren().clear();
        chartActivites.getChildren().clear();
        boxInfosRapides.getChildren().clear();
        chargerTout();
    }

    private void afficherDate() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH);
        String date = LocalDate.now().format(fmt);
        // Capitalize first letter
        date = date.substring(0, 1).toUpperCase() + date.substring(1);
        lblDateJour.setText("Aujourd'hui · " + date);
    }

    private void chargerTout() {
        int totalAct   = serviceAct.getTotalActivites();
        int totalRes   = serviceRes.getTotalReservations();
        int totalPart  = serviceRes.getTotalParticipants();
        int totalCap   = serviceAct.getTotalCapacite();

        chargerKPIs(totalAct, totalRes, totalPart, totalCap);
        chargerGraphiqueReservations();
        chargerGraphiqueActivites();
        chargerDonut(totalPart, totalCap);
        chargerInfosRapides(totalAct, totalRes, totalPart, totalCap);
    }

    private void chargerKPIs(int totalAct, int totalRes, int totalPart, int totalCap) {
        lblTotalActivites.setText(String.valueOf(totalAct));
        lblTotalReservations.setText(String.valueOf(totalRes));
        lblTotalParticipants.setText(String.valueOf(totalPart));

        double taux = (totalCap > 0) ? (double) totalPart / totalCap * 100 : 0;
        lblTauxRemplissage.setText(String.format("%.0f%%", taux));

        // Progress bars animées (ratio par rapport à des valeurs cibles)
        animerProgress(progressActivites,  Math.min(1.0, totalAct / 20.0));
        animerProgress(progressReservations, Math.min(1.0, totalRes / 30.0));
        animerProgress(progressParticipants, Math.min(1.0, totalPart / 100.0));
        animerProgress(progressTaux, Math.min(1.0, taux / 100.0));
    }

    private void animerProgress(Region bar, double ratio) {
        // Bind width after layout
        bar.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                javafx.application.Platform.runLater(() -> {
                    double parentWidth = bar.getParent() instanceof StackPane
                        ? ((StackPane) bar.getParent()).getWidth() : 300;
                    if (parentWidth <= 0) parentWidth = 300;
                    final double targetWidth = parentWidth * ratio;
                    javafx.animation.Timeline tl = new javafx.animation.Timeline(
                        new javafx.animation.KeyFrame(javafx.util.Duration.ZERO,
                            new javafx.animation.KeyValue(bar.prefWidthProperty(), 0)),
                        new javafx.animation.KeyFrame(javafx.util.Duration.millis(900),
                            new javafx.animation.KeyValue(bar.prefWidthProperty(), targetWidth,
                                javafx.animation.Interpolator.EASE_OUT))
                    );
                    tl.play();
                });
            }
        });

        // Fallback si scène déjà chargée
        if (bar.getScene() != null) {
            javafx.application.Platform.runLater(() -> {
                double parentWidth = bar.getParent() instanceof StackPane
                    ? ((StackPane) bar.getParent()).getWidth() : 300;
                if (parentWidth <= 0) parentWidth = 300;
                final double targetWidth = parentWidth * ratio;
                javafx.animation.Timeline tl = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(javafx.util.Duration.ZERO,
                        new javafx.animation.KeyValue(bar.prefWidthProperty(), 0)),
                    new javafx.animation.KeyFrame(javafx.util.Duration.millis(900),
                        new javafx.animation.KeyValue(bar.prefWidthProperty(), targetWidth,
                            javafx.animation.Interpolator.EASE_OUT))
                );
                tl.play();
            });
        }
    }

    private void chargerGraphiqueReservations() {
        chartReservations.getChildren().clear();
        List<String[]> data = serviceRes.getReservationsParMois();

        if (data.isEmpty()) {
            chartReservations.getChildren().add(videLabel());
            lblTotalMois.setText("—");
            return;
        }

        int totalGlobal = data.stream().mapToInt(d -> Integer.parseInt(d[1])).sum();
        lblTotalMois.setText(totalGlobal + " au total");

        int max = data.stream().mapToInt(d -> Integer.parseInt(d[1])).max().orElse(1);

        for (String[] entry : data) {
            String mois  = entry[0];
            int    total = Integer.parseInt(entry[1]);
            double ratio = (double) total / max;
            int    pct   = (int) Math.round(ratio * 100);

            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);

            // Mois label
            Label lblMois = new Label(mois.substring(0, Math.min(3, mois.length())).toUpperCase());
            lblMois.setStyle("-fx-font-size:11px;-fx-font-weight:700;"
                + "-fx-text-fill:#64748b;-fx-min-width:32px;");

            // Barre
            StackPane barContainer = new StackPane();
            barContainer.setPrefHeight(28);
            HBox.setHgrow(barContainer, Priority.ALWAYS);

            Region bgBar = new Region();
            bgBar.setPrefHeight(14);
            bgBar.setMaxWidth(Double.MAX_VALUE);
            bgBar.setStyle("-fx-background-color:#e2e8f0;-fx-background-radius:7;");

            Region fillBar = new Region();
            fillBar.setPrefHeight(14);
            fillBar.setPrefWidth(0);
            fillBar.setStyle("-fx-background-color:linear-gradient(to right,#1e3c72,#2a9df4);"
                + "-fx-background-radius:7;");
            StackPane.setAlignment(fillBar, Pos.CENTER_LEFT);

            // Pourcentage dans la barre
            Label lblPct = new Label(pct + "%");
            lblPct.setStyle("-fx-font-size:10px;-fx-font-weight:700;-fx-text-fill:white;"
                + "-fx-padding:0 0 0 8;");
            StackPane.setAlignment(lblPct, Pos.CENTER_LEFT);

            barContainer.getChildren().addAll(bgBar, fillBar, lblPct);

            // Animation
            final double targetW = 380 * ratio;
            javafx.animation.Timeline tl = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.ZERO,
                    new javafx.animation.KeyValue(fillBar.prefWidthProperty(), 0)),
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(900),
                    new javafx.animation.KeyValue(fillBar.prefWidthProperty(), targetW,
                        javafx.animation.Interpolator.EASE_OUT))
            );
            tl.play();

            // Valeur
            Label lblVal = new Label(String.valueOf(total));
            lblVal.setStyle("-fx-font-size:13px;-fx-font-weight:800;"
                + "-fx-text-fill:#0a4d7a;-fx-min-width:28px;-fx-alignment:CENTER_RIGHT;");

            row.getChildren().addAll(lblMois, barContainer, lblVal);
            chartReservations.getChildren().add(row);
        }
    }

    private void chargerGraphiqueActivites() {
        chartActivites.getChildren().clear();
        List<String[]> data = serviceRes.getTopActivites();

        if (data.isEmpty()) {
            chartActivites.getChildren().add(videLabel());
            return;
        }

        int max = data.stream().mapToInt(d -> Integer.parseInt(d[1])).max().orElse(1);

        String[] couleurs = { "#10b981", "#3b82f6", "#f59e0b", "#8b5cf6", "#ef4444" };
        String[] medailles = { "🥇", "🥈", "🥉", "4️⃣", "5️⃣" };

        for (int i = 0; i < data.size(); i++) {
            String activite = data.get(i)[0];
            int    nb       = Integer.parseInt(data.get(i)[1]);
            double ratio    = (double) nb / max;
            String couleur  = couleurs[i % couleurs.length];
            String medaille = i < medailles.length ? medailles[i] : (i + 1) + ".";

            VBox item = new VBox(5);

            HBox topRow = new HBox(8);
            topRow.setAlignment(Pos.CENTER_LEFT);

            Label lblMed = new Label(medaille);
            lblMed.setStyle("-fx-font-size:14px;-fx-min-width:28px;");

            String nomCourt = activite.length() > 24 ? activite.substring(0, 21) + "…" : activite;
            Label lblNom = new Label(nomCourt);
            lblNom.setStyle("-fx-font-size:13px;-fx-font-weight:700;-fx-text-fill:#0f172a;");
            HBox.setHgrow(lblNom, Priority.ALWAYS);

            Label lblNb = new Label(nb + " résa");
            lblNb.setStyle("-fx-font-size:12px;-fx-font-weight:800;"
                + "-fx-text-fill:" + couleur + ";"
                + "-fx-background-color:" + couleur + "22;"
                + "-fx-background-radius:12;-fx-padding:2 8;");

            topRow.getChildren().addAll(lblMed, lblNom, lblNb);

            // Barre
            StackPane barContainer = new StackPane();
            barContainer.setPrefHeight(10);

            Region bgBar = new Region();
            bgBar.setPrefHeight(8);
            bgBar.setMaxWidth(Double.MAX_VALUE);
            bgBar.setStyle("-fx-background-color:#f1f5f9;-fx-background-radius:4;");

            Region fillBar = new Region();
            fillBar.setPrefHeight(8);
            fillBar.setPrefWidth(0);
            fillBar.setStyle("-fx-background-color:" + couleur + ";-fx-background-radius:4;");
            StackPane.setAlignment(fillBar, Pos.CENTER_LEFT);

            barContainer.getChildren().addAll(bgBar, fillBar);

            final double targetW = 300 * ratio;
            final long delay = 200L + i * 120L;
            javafx.animation.Timeline tl = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.ZERO,
                    new javafx.animation.KeyValue(fillBar.prefWidthProperty(), 0)),
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(delay),
                    new javafx.animation.KeyValue(fillBar.prefWidthProperty(), 0)),
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(delay + 700),
                    new javafx.animation.KeyValue(fillBar.prefWidthProperty(), targetW,
                        javafx.animation.Interpolator.EASE_OUT))
            );
            tl.play();

            item.getChildren().addAll(topRow, barContainer);
            chartActivites.getChildren().add(item);
        }
    }

    private void chargerDonut(int participants, int capacite) {
        GraphicsContext gc = donutCanvas.getGraphicsContext2D();
        double w = donutCanvas.getWidth();
        double h = donutCanvas.getHeight();
        double cx = w / 2, cy = h / 2;
        double outerR = 70, innerR = 48;

        gc.clearRect(0, 0, w, h);

        double ratio = (capacite > 0) ? Math.min(1.0, (double) participants / capacite) : 0;
        double angleFill = 360 * ratio;

        // Arc de fond (gris)
        gc.setLineWidth(outerR - innerR);
        gc.setStroke(Color.web("#e2e8f0"));
        gc.strokeArc(cx - (outerR + innerR) / 2.0, cy - (outerR + innerR) / 2.0,
            outerR + innerR, outerR + innerR, 90, -360, javafx.scene.shape.ArcType.OPEN);

        // Arc rempli (gradient bleu→vert)
        if (angleFill > 0) {
            gc.setStroke(Color.web(ratio > 0.8 ? "#ef4444" : ratio > 0.5 ? "#f59e0b" : "#2a9df4"));
            gc.strokeArc(cx - (outerR + innerR) / 2.0, cy - (outerR + innerR) / 2.0,
                outerR + innerR, outerR + innerR, 90, -angleFill, javafx.scene.shape.ArcType.OPEN);
        }

        // Pourcentage au centre
        int pct = (int) Math.round(ratio * 100);
        lblDonutPct.setText(pct + "%");

        // Légende
        int dispo = Math.max(0, capacite - participants);
        lblLegendPart.setText(participants + " / " + capacite + " places");
        lblLegendDispo.setText(dispo + " restantes");

        // Statut
        if (ratio >= 0.9) {
            lblStatutOccupation.setText("⚠️ Quasi complet");
            lblStatutOccupation.setStyle("-fx-text-fill:#ef4444;-fx-font-size:12px;-fx-font-weight:700;");
        } else if (ratio >= 0.5) {
            lblStatutOccupation.setText("📊 Bien rempli");
            lblStatutOccupation.setStyle("-fx-text-fill:#f59e0b;-fx-font-size:12px;-fx-font-weight:700;");
        } else {
            lblStatutOccupation.setText("✅ Places disponibles");
            lblStatutOccupation.setStyle("-fx-text-fill:#10b981;-fx-font-size:12px;-fx-font-weight:700;");
        }
    }

    private void chargerInfosRapides(int totalAct, int totalRes, int totalPart, int totalCap) {
        boxInfosRapides.getChildren().clear();

        double moyResPsAct = (totalAct > 0) ? (double) totalRes / totalAct : 0;
        double tauxOcc     = (totalCap > 0) ? (double) totalPart / totalCap * 100 : 0;
        int    disponibles = Math.max(0, totalCap - totalPart);

        ajouterInfoRapide("📌", "Moy. réservations / activité",
            String.format("%.1f résa", moyResPsAct), "#0a4d7a");
        ajouterInfoRapide("🎯", "Taux d'occupation",
            String.format("%.1f%%", tauxOcc), tauxOcc > 80 ? "#ef4444" : tauxOcc > 50 ? "#f59e0b" : "#10b981");
        ajouterInfoRapide("💺", "Places encore disponibles",
            disponibles + " places", "#3b82f6");
        ajouterInfoRapide("🏝️", "Activités EcoMarine Kuriat",
            totalAct + " proposées", "#8b5cf6");
    }

    private void ajouterInfoRapide(String icon, String libelle, String valeur, String couleur) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color:#f8fafc;-fx-background-radius:12;"
            + "-fx-padding:10 14;-fx-border-color:#e2e8f0;-fx-border-width:1;-fx-border-radius:12;");

        Label lblIcon = new Label(icon);
        lblIcon.setStyle("-fx-font-size:20px;-fx-min-width:28px;");

        VBox vb = new VBox(2);
        HBox.setHgrow(vb, Priority.ALWAYS);
        Label lbl = new Label(libelle);
        lbl.setStyle("-fx-font-size:11px;-fx-text-fill:#64748b;-fx-font-weight:600;");
        Label val = new Label(valeur);
        val.setStyle("-fx-font-size:15px;-fx-font-weight:800;-fx-text-fill:" + couleur + ";");
        vb.getChildren().addAll(lbl, val);

        row.getChildren().addAll(lblIcon, vb);

        // Hover effect via mouse events
        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color:#e0f2fe;-fx-background-radius:12;"
            + "-fx-padding:10 14;-fx-border-color:" + couleur + ";-fx-border-width:1.5;-fx-border-radius:12;"
            + "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.08),6,0,0,2);"));
        row.setOnMouseExited(e -> row.setStyle("-fx-background-color:#f8fafc;-fx-background-radius:12;"
            + "-fx-padding:10 14;-fx-border-color:#e2e8f0;-fx-border-width:1;-fx-border-radius:12;"));

        boxInfosRapides.getChildren().add(row);
    }

    private Label videLabel() {
        Label l = new Label("Aucune donnée disponible");
        l.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:13px;-fx-font-style:italic;");
        return l;
    }
}