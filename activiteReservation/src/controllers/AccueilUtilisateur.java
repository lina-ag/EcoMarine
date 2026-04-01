package controllers;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import tn.edu.esprit.entities.User;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AccueilUtilisateur implements Initializable {

    @FXML private VBox mainVBox;
    @FXML private VBox cardActivites;
    @FXML private VBox cardReservations;
    @FXML private VBox cardAPropos;
    @FXML private HBox islandStrip;
    @FXML private Button btnReserver;
    @FXML private Circle orb1;
    @FXML private Circle orb2;
    @FXML private Circle orb3;

    private User utilisateurConnecte;

    public void setUtilisateurConnecte(User user) {
        this.utilisateurConnecte = user;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        animerOrbs();
        animerEntree();
        ajouterHoverCartes();
        animerBouton();
    }

    // ══════════════════════════════════════════
    //  NAVIGATION
    // ══════════════════════════════════════════

    @FXML
    private void ouvrirActivites() {
        animerClicCarte(cardActivites, () ->
            chargerVue("/AfficherActivite.fxml", "Activités — EcoMarine")
        );
    }

    @FXML
    private void ouvrirReservations() {
        animerClicCarte(cardReservations, () ->
            chargerVue("/AfficherReservation.fxml", "Mes Réservations — EcoMarine")
        );
    }

    @FXML
    private void ouvrirAPropos() {
        animerClicCarte(cardAPropos, () ->
            chargerVue("/APropos.fxml", "À propos — EcoMarine")
        );
    }

    // ══════════════════════════════════════════
    //  ANIMATIONS
    // ══════════════════════════════════════════

    private void animerEntree() {
        if (mainVBox != null) {
            mainVBox.setOpacity(0);
            mainVBox.setTranslateY(20);
            
            FadeTransition fade = new FadeTransition(Duration.millis(700), mainVBox);
            fade.setFromValue(0);
            fade.setToValue(1);
            
            TranslateTransition slide = new TranslateTransition(Duration.millis(700), mainVBox);
            slide.setFromY(20);
            slide.setToY(0);
            
            ParallelTransition entree = new ParallelTransition(fade, slide);
            entree.setInterpolator(Interpolator.EASE_OUT);
            entree.play();
        }
    }

    private void animerOrbs() {
        animerOrb(orb1, 12, 8);
        animerOrb(orb2, 9, 14);
        animerOrb(orb3, 15, 6);
    }

    private void animerOrb(Circle orb, double dureeX, double dureeY) {
        if (orb == null) return;

        TranslateTransition tx = new TranslateTransition(Duration.seconds(dureeX), orb);
        tx.setFromX(0); tx.setToX(30);
        tx.setAutoReverse(true); tx.setCycleCount(Animation.INDEFINITE);
        tx.setInterpolator(Interpolator.EASE_BOTH);

        TranslateTransition ty = new TranslateTransition(Duration.seconds(dureeY), orb);
        ty.setFromY(0); ty.setToY(20);
        ty.setAutoReverse(true); ty.setCycleCount(Animation.INDEFINITE);
        ty.setInterpolator(Interpolator.EASE_BOTH);

        new ParallelTransition(tx, ty).play();
    }

    private void ajouterHoverCartes() {
        for (VBox card : new VBox[]{cardActivites, cardReservations, cardAPropos}) {
            if (card == null) continue;

            String styleNormal = card.getStyle();
            String styleHover  = styleNormal
                .replace("#0c1e32", "#102840")
                .replace("#1a4a3a", "#2e7a5a");

            ScaleTransition scaleIn  = new ScaleTransition(Duration.millis(160), card);
            scaleIn.setToX(1.025); scaleIn.setToY(1.025);

            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(160), card);
            scaleOut.setToX(1.0); scaleOut.setToY(1.0);

            card.setOnMouseEntered(e -> {
                card.setStyle(styleHover);
                scaleIn.playFromStart();
            });
            card.setOnMouseExited(e -> {
                card.setStyle(styleNormal);
                scaleOut.playFromStart();
            });
        }
    }

    private void animerBouton() {
        if (btnReserver == null) return;

        ScaleTransition pulse = new ScaleTransition(Duration.millis(1200), btnReserver);
        pulse.setFromX(1.0); pulse.setToX(1.015);
        pulse.setFromY(1.0); pulse.setToY(1.015);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setInterpolator(Interpolator.EASE_BOTH);
        pulse.play();

        String styleNormal = btnReserver.getStyle();
        String styleHover  = styleNormal.replace("#7ed8c0", "#9fe5d1");
        btnReserver.setOnMouseEntered(e -> btnReserver.setStyle(styleHover));
        btnReserver.setOnMouseExited(e  -> btnReserver.setStyle(styleNormal));
    }

    private void animerClicCarte(VBox card, Runnable navigation) {
        if (card == null) { navigation.run(); return; }

        ScaleTransition sc = new ScaleTransition(Duration.millis(120), card);
        sc.setToX(0.95); sc.setToY(0.95);
        sc.setOnFinished(e -> navigation.run());
        sc.play();
    }

    // ══════════════════════════════════════════
    //  HELPER NAVIGATION - CORRIGÉ
    // ══════════════════════════════════════════

    private void chargerVue(String fxmlPath, String titre) {
        try {
            // Vérifier que le fichier existe
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.err.println("Fichier non trouvé: " + fxmlPath);
                System.err.println("Chemins disponibles:");
                System.err.println("  - /AfficherActivite.fxml");
                System.err.println("  - /AfficherReservation.fxml");
                System.err.println("  - /APropos.fxml");
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            
            // Changer la vue
            javafx.stage.Stage stage = (javafx.stage.Stage) mainVBox.getScene().getWindow();
            stage.setTitle(titre);
            stage.getScene().setRoot(root);
            
        } catch (IOException e) {
            System.err.println("Erreur chargement vue : " + fxmlPath);
            e.printStackTrace();
        }
    }
}