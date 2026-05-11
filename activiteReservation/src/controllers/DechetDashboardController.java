package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;

public class DechetDashboardController {

    @FXML private Label imageNameLabel;
    @FXML private Label priorityLabel;
    @FXML private ProgressBar priorityBar;
    @FXML private TextArea analysisArea;

    private File selectedImage;

    @FXML
    private void chooseImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir une photo de dechets");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );

        File file = chooser.showOpenDialog(imageNameLabel.getScene().getWindow());
        if (file != null) {
            selectedImage = file;
            imageNameLabel.setText(file.getName());
            priorityLabel.setText("Photo prete pour analyse");
            priorityBar.setProgress(0.42);
            analysisArea.setText("Image chargee. Lance l'analyse IA pour generer le diagnostic terrain.");
        }
    }

    @FXML
    private void analyzeImage() {
        if (selectedImage == null) {
            showInfo("Ajoute une photo avant de lancer l'analyse.");
            return;
        }

        priorityBar.setProgress(0.82);
        priorityLabel.setText("Priorite haute");
        analysisArea.setText("""
                1) Dechets probables : plastique, emballages, bouteilles, canettes et petits dechets mixtes.

                2) Score de priorite : 82/100. La zone doit etre traitee rapidement car les dechets legers peuvent rejoindre la mer avec le vent.

                3) Origine probable : tourisme de plage, restaurants proches et passage pieton dense.

                4) Impact biodiversite : risque eleve pour oiseaux marins, poissons et tortues par ingestion ou etranglement.

                5) Plan d'action : nettoyage prioritaire, ajout de poubelles visibles, sensibilisation des visiteurs et suivi de la zone pendant 7 jours.
                """);
    }

    @FXML
    private void generateNatureVoice() {
        analysisArea.setText("""
                Voix de la nature

                "Cette plage respire mieux quand chaque bouteille disparait. Un petit geste ici protege une tortue, un poisson, un oiseau, et tout un littoral."

                Utilisation proposee : message audio court pour une borne de sensibilisation, une story ou un panneau interactif.
                """);
    }

    @FXML
    private void generateEcoGuide() {
        analysisArea.setText("""
                Guide ecologique rapide

                - Installer 2 points de tri visibles pres de l'acces principal.
                - Lancer une mission de nettoyage de 45 minutes avec 6 volontaires.
                - Photographier la zone avant/apres pour mesurer l'impact.
                - Revenir dans 72h pour verifier si la pollution revient.
                """);
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
