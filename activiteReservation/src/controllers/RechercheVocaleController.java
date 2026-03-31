package controllers;

import org.vosk.Model;
import org.vosk.Recognizer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import tn.edu.esprit.entities.ZoneProtegee;
import tn.edu.esprit.services.ServiceZoneP;

import javax.sound.sampled.*;
import java.util.List;
import java.util.stream.Collectors;

public class RechercheVocaleController {

    @FXML private TextField txtResultatVocal;
    @FXML private Label lblResultats;
    @FXML private Label lblStatut;
    @FXML private Button btnMicro;

    private ServiceZoneP serviceZone = new ServiceZoneP();
    private Model model;
    private boolean isListening = false;
    private Thread recognitionThread;

    // ⚠️ mets ton chemin
    private static final String MODEL_PATH =
            "C:\\Users\\WSI\\Downloads\\vosk-model-small-fr-0.22\\vosk-model-small-fr-0.22";

    @FXML
    public void initialize() {
        lblStatut.setText("Chargement du modèle...");
        btnMicro.setDisable(true);

        new Thread(() -> {
            try {
                model = new Model(MODEL_PATH);
                Platform.runLater(() -> {
                    lblStatut.setText("Prêt — clique pour parler");
                    btnMicro.setDisable(false);
                });
            } catch (Exception e) {
                Platform.runLater(() ->
                        lblStatut.setText("Erreur modèle : " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void toggleMicro() {
        if (!isListening) {
            demarrerEcoute();
        } else {
            arreterEcoute();
        }
    }

    private void demarrerEcoute() {
        isListening = true;
        btnMicro.setText("⏹ Arrêter");

        lblStatut.setText("Écoute en cours...");

        recognitionThread = new Thread(() -> {
            try (Recognizer recognizer = new Recognizer(model, 16000)) {

                AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

                if (!AudioSystem.isLineSupported(info)) {
                    Platform.runLater(() ->
                            lblStatut.setText("Microphone non supporté"));
                    return;
                }

                TargetDataLine microphone =
                        (TargetDataLine) AudioSystem.getLine(info);

                microphone.open(format);
                microphone.start();

                byte[] buffer = new byte[4096];

                while (isListening) {
                    int bytesRead = microphone.read(buffer, 0, buffer.length);

                    if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                        String result = recognizer.getResult();
                        String texte = extraireTexte(result);

                        if (!texte.isBlank()) {
                            Platform.runLater(() -> {
                                txtResultatVocal.setText(texte);
                                rechercherAvecTexte(texte);
                                isListening = false;
                            });
                            break;
                        }
                    } else {
                        String partial =
                                extraireTexte(recognizer.getPartialResult());

                        if (!partial.isBlank()) {
                            Platform.runLater(() ->
                                    lblStatut.setText("..." + partial));
                        }
                    }
                }

                microphone.stop();
                microphone.close();

            } catch (Exception e) {
                Platform.runLater(() ->
                        lblStatut.setText("Erreur : " + e.getMessage()));
                e.printStackTrace();
            } finally {
                Platform.runLater(() -> {
                    isListening = false;
                    btnMicro.setText("🎤 Parler");
                    lblStatut.setText("Prêt");
                });
            }
        });

        recognitionThread.setDaemon(true);
        recognitionThread.start();
    }

    private void arreterEcoute() {
        isListening = false;
    }

    private String extraireTexte(String json) {
        if (json == null) return "";
        json = json.trim();

        int idx = json.indexOf("\"text\"");
        if (idx == -1) idx = json.indexOf("\"partial\"");
        if (idx == -1) return "";

        int start = json.indexOf("\"", idx + 7) + 1;
        int end = json.indexOf("\"", start);

        if (start <= 0 || end <= 0) return "";

        return json.substring(start, end).trim();
    }

    // 🔍 RECHERCHE PAR NOM + AFFICHAGE DETAILS
    private void rechercherAvecTexte(String texte) {

        if (texte == null || texte.isBlank()) {
            lblResultats.setText("Rien reconnu, réessaie.");
            return;
        }

        String texteMin = texte.toLowerCase().trim();

        List<ZoneProtegee> toutes = serviceZone.getAll(null);

        List<ZoneProtegee> resultats = toutes.stream()
                .filter(z -> z.getNomZone().toLowerCase().contains(texteMin))
                .collect(Collectors.toList());

        if (resultats.isEmpty()) {

            lblResultats.setText("Aucune zone trouvée pour : " + texte);

        } else {

            StringBuilder details = new StringBuilder();

            for (ZoneProtegee z : resultats) {
                details.append("ID : ").append(z.getIdZone()).append("\n")
                        .append("Nom : ").append(z.getNomZone()).append("\n")
                        .append("Catégorie : ").append(z.getCategorieZone()).append("\n")
                        .append("Statut : ").append(z.getStatut()).append("\n")
                        .append("---------------------------\n");
            }

            lblResultats.setText(details.toString());
        }

        lblStatut.setText("Recherche terminée ✓");
    }
}