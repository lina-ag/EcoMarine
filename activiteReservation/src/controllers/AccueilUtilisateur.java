package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import tn.edu.esprit.entities.User;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.ParallelTransition;
import javafx.util.Duration;
import javafx.scene.shape.Circle;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccueilUtilisateur implements Initializable {

    // Météo
    @FXML private Label labelTemperature;
    @FXML private Label labelDescription;
    @FXML private Label labelHumidite;
    @FXML private Label labelVent;
    @FXML private Label labelRessenti;
    @FXML private Label labelMeteoIcone;
    @FXML private Label labelVille;
    @FXML private Label labelMeteoStatus;
    @FXML private Button btnRefreshMeteo;
    
    
    @FXML private Pane orb1;
    @FXML private Pane orb2;
    @FXML private Pane orb3;
    @FXML private Pane orb4;
    @FXML private Pane orb5;
    // Nav
    @FXML private Label labelNomUtilisateur;
    @FXML private Button btnDeconnexion;

    // Stats
    @FXML private Label labelNbReservations;

    // Config météo – Monastir / Kuriat Island
    private static final double LAT  = 35.7643;
    private static final double LON  = 10.8113;
    private static final String VILLE = "Monastir, TN";

    // Utilisateur connecté
    private User utilisateurConnecte;
    private String nomUtilisateur = "";

    // Scheduler rafraîchissement météo
    private ScheduledExecutorService scheduler;

    // ──────────────────────────────────────────────────────────────────
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	 animerOrbs();
        labelVille.setText(VILLE);
        labelNomUtilisateur.setText("👤 Bienvenue !");
        chargerMeteo();
        demarrerRafraichissementAuto();
    }

    // ──────────────────────────────────────────────────────────────────
    //  SETTER – appelé depuis SignInController après loader.getController()
    // ──────────────────────────────────────────────────────────────────
    public void setUtilisateurConnecte(User user) {
        this.utilisateurConnecte = user;
        if (user != null) {
            nomUtilisateur = user.getPrenom() + " " + user.getNom();
            if (labelNomUtilisateur != null) {
                labelNomUtilisateur.setText("👤 Bienvenue, " + nomUtilisateur);
            }
            chargerNombreReservations();
        }
    }

    // ══════════════════════════════════════════════════════════════════
    // ANIMATIONS DES ORBS (version pour Pane)
    // ══════════════════════════════════════════════════════════════════
    
    private void animerOrbs() {
        // Vérifier que les orbs existent
        if (orb1 == null || orb2 == null || orb3 == null || orb4 == null || orb5 == null) {
            System.out.println("Orbs non trouvés dans le FXML");
            return;
        }
        
        try {
            // ORB 1 - Pulsation lente
            ScaleTransition pulse1 = new ScaleTransition(Duration.seconds(4), orb1);
            pulse1.setFromX(1);
            pulse1.setFromY(1);
            pulse1.setToX(1.15);
            pulse1.setToY(1.15);
            pulse1.setAutoReverse(true);
            pulse1.setCycleCount(Animation.INDEFINITE);
            pulse1.play();
            
            FadeTransition fade1 = new FadeTransition(Duration.seconds(4), orb1);
            fade1.setFromValue(0.08);
            fade1.setToValue(0.2);
            fade1.setAutoReverse(true);
            fade1.setCycleCount(Animation.INDEFINITE);
            fade1.play();
            
            // ORB 2 - Pulsation rapide
            ScaleTransition pulse2 = new ScaleTransition(Duration.seconds(3), orb2);
            pulse2.setFromX(1);
            pulse2.setFromY(1);
            pulse2.setToX(1.2);
            pulse2.setToY(1.2);
            pulse2.setAutoReverse(true);
            pulse2.setCycleCount(Animation.INDEFINITE);
            pulse2.play();
            
            FadeTransition fade2 = new FadeTransition(Duration.seconds(3), orb2);
            fade2.setFromValue(0.06);
            fade2.setToValue(0.18);
            fade2.setAutoReverse(true);
            fade2.setCycleCount(Animation.INDEFINITE);
            fade2.play();
            
            // ORB 3 - Flottement (déplacement)
            TranslateTransition float3Y = new TranslateTransition(Duration.seconds(6), orb3);
            float3Y.setFromY(0);
            float3Y.setToY(-25);
            float3Y.setAutoReverse(true);
            float3Y.setCycleCount(Animation.INDEFINITE);
            float3Y.play();
            
            TranslateTransition float3X = new TranslateTransition(Duration.seconds(8), orb3);
            float3X.setFromX(0);
            float3X.setToX(15);
            float3X.setAutoReverse(true);
            float3X.setCycleCount(Animation.INDEFINITE);
            float3X.play();
            
            FadeTransition fade3 = new FadeTransition(Duration.seconds(6), orb3);
            fade3.setFromValue(0.05);
            fade3.setToValue(0.15);
            fade3.setAutoReverse(true);
            fade3.setCycleCount(Animation.INDEFINITE);
            fade3.play();
            
            // ORB 4 - Pulsation très lente
            ScaleTransition scale4 = new ScaleTransition(Duration.seconds(20), orb4);
            scale4.setFromX(1);
            scale4.setFromY(1);
            scale4.setToX(1.08);
            scale4.setToY(1.08);
            scale4.setAutoReverse(true);
            scale4.setCycleCount(Animation.INDEFINITE);
            scale4.play();
            
            FadeTransition fade4 = new FadeTransition(Duration.seconds(8), orb4);
            fade4.setFromValue(0.04);
            fade4.setToValue(0.12);
            fade4.setAutoReverse(true);
            fade4.setCycleCount(Animation.INDEFINITE);
            fade4.play();
            
            // ORB 5 - Pulsation + flottement
            ScaleTransition pulse5 = new ScaleTransition(Duration.seconds(5), orb5);
            pulse5.setFromX(1);
            pulse5.setFromY(1);
            pulse5.setToX(1.1);
            pulse5.setToY(1.1);
            pulse5.setAutoReverse(true);
            pulse5.setCycleCount(Animation.INDEFINITE);
            pulse5.play();
            
            TranslateTransition float5Y = new TranslateTransition(Duration.seconds(7), orb5);
            float5Y.setFromY(0);
            float5Y.setToY(-20);
            float5Y.setAutoReverse(true);
            float5Y.setCycleCount(Animation.INDEFINITE);
            float5Y.play();
            
            TranslateTransition float5X = new TranslateTransition(Duration.seconds(9), orb5);
            float5X.setFromX(0);
            float5X.setToX(-12);
            float5X.setAutoReverse(true);
            float5X.setCycleCount(Animation.INDEFINITE);
            float5X.play();
            
            FadeTransition fade5 = new FadeTransition(Duration.seconds(5), orb5);
            fade5.setFromValue(0.05);
            fade5.setToValue(0.14);
            fade5.setAutoReverse(true);
            fade5.setCycleCount(Animation.INDEFINITE);
            fade5.play();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ──────────────────────────────────────────────────────────────────
    //  MÉTÉO  (Open-Meteo – gratuit, sans clé API)
    // ──────────────────────────────────────────────────────────────────
    private void chargerMeteo() {
        if (labelMeteoStatus != null) {
            labelMeteoStatus.setText("🔄 Actualisation…");
            labelMeteoStatus.setVisible(true);
        }

        Thread thread = new Thread(() -> {
            try {
                String response = httpGet(buildMeteoUrl());

                double temp     = extraireValeur(response, "temperature_2m");
                double ressenti = extraireValeur(response, "apparent_temperature");
                double humidite = extraireValeur(response, "relative_humidity_2m");
                double vent     = extraireValeur(response, "wind_speed_10m");
                int    wmoCode  = (int) extraireValeur(response, "weather_code");

                String[] iconDesc = wmoToIconDesc(wmoCode);

                Platform.runLater(() -> {
                    labelTemperature.setText(String.format("%.0f°C", temp));
                    labelRessenti.setText(String.format("%.0f°C", ressenti));
                    labelHumidite.setText(String.format("%.0f%%", humidite));
                    labelVent.setText(String.format("%.0f km/h", vent));
                    labelMeteoIcone.setText(iconDesc[0]);
                    labelDescription.setText(iconDesc[1]);
                    labelMeteoStatus.setVisible(false);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    if (labelMeteoStatus != null)
                        labelMeteoStatus.setText("⚠️ Connexion impossible");
                    if (labelDescription != null)
                        labelDescription.setText("Vérifiez votre connexion");
                });
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private String buildMeteoUrl() {
        return "https://api.open-meteo.com/v1/forecast"
             + "?latitude=" + LAT
             + "&longitude=" + LON
             + "&current=temperature_2m,apparent_temperature,relative_humidity_2m,"
             + "wind_speed_10m,weather_code"
             + "&wind_speed_unit=kmh"
             + "&timezone=Africa%2FTunis";
    }

    private String httpGet(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);
        conn.setRequestProperty("Accept", "application/json");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    /** Extrait une valeur numérique du JSON sans lib externe. */
    private double extraireValeur(String json, String cle) {
        Pattern p = Pattern.compile("\"" + cle + "\"\\s*:\\s*([\\d.]+)");
        Matcher m = p.matcher(json);
        return m.find() ? Double.parseDouble(m.group(1)) : 0.0;
    }

    /** Code WMO → émoji + description FR */
    private String[] wmoToIconDesc(int code) {
        if (code == 0)                return new String[]{"☀️",  "Ciel dégagé"};
        if (code == 1)                return new String[]{"🌤️", "Principalement dégagé"};
        if (code == 2)                return new String[]{"⛅",  "Partiellement nuageux"};
        if (code == 3)                return new String[]{"☁️",  "Couvert"};
        if (code >= 45 && code <= 48) return new String[]{"🌫️", "Brouillard"};
        if (code >= 51 && code <= 55) return new String[]{"🌦️", "Bruine légère"};
        if (code >= 61 && code <= 65) return new String[]{"🌧️", "Pluie"};
        if (code >= 71 && code <= 77) return new String[]{"❄️",  "Neige"};
        if (code >= 80 && code <= 82) return new String[]{"🌦️", "Averses"};
        if (code >= 95 && code <= 99) return new String[]{"⛈️",  "Orage"};
        return new String[]{"🌊", "Conditions marines"};
    }

    private void demarrerRafraichissementAuto() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(this::chargerMeteo, 10, 10, TimeUnit.MINUTES);
    }

    @FXML
    private void handleRefreshMeteo() {
        chargerMeteo();
    }

    // ──────────────────────────────────────────────────────────────────
    //  NAVIGATION
    // ──────────────────────────────────────────────────────────────────
    @FXML
    private void handleNouvelleReservation() {
        ouvrirVue("/AjouterReservation.fxml", "Nouvelle Réservation");
    }

   

    @FXML
    private void handleCalendrier() {
        ouvrirVue("/Calendrier.fxml", "Mon Calendrier");
    }

    @FXML
    private void handleActivites() {
        ouvrirVue("/AfficherActivite.fxml", "Activités disponibles");
    }

    @FXML
    private void handleProfil() {
        // Vue optionnelle – à créer si nécessaire
        ouvrirVue("/Profil.fxml", "Mon Profil");
    }

    @FXML
    private void handleAPropos() {
        ouvrirVue("/APropos.fxml", "Kuriat Island");
    }

    @FXML
    private void handleDeconnexion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Confirmer la déconnexion");
        alert.setContentText("Voulez-vous vraiment vous déconnecter ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            arreterScheduler();
            utilisateurConnecte = null;
            nomUtilisateur = "";
            ouvrirLogin();
        }
    }

    // ──────────────────────────────────────────────────────────────────
    //  DONNÉES
    // ──────────────────────────────────────────────────────────────────
    private void chargerNombreReservations() {
        // TODO : appeler ton ReservationService
        // int nb = new ServiceReservation().countByUser(utilisateurConnecte.getId());
        // Platform.runLater(() -> labelNbReservations.setText(nb + " réservation(s)"));
        if (labelNbReservations != null)
            labelNbReservations.setText("0 réservation(s)");
    }

    // ──────────────────────────────────────────────────────────────────
    //  UTILITAIRES
    // ──────────────────────────────────────────────────────────────────
    private void ouvrirVue(String fxmlPath, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(titre);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                "Impossible d'ouvrir : " + fxmlPath,
                ButtonType.OK).showAndWait();
        }
    }

    private void ouvrirLogin() {
        try {
            Stage stage = (Stage) btnDeconnexion.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignIn.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("EcoMarine – Connexion");
            stage.setMaximized(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void arreterScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }
}