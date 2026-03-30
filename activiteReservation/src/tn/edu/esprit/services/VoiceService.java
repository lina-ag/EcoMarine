package tn.edu.esprit.services;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;

public class VoiceService {

    private Model model;
    private TextField searchField;
    private Button microphoneButton;
    private Consumer<String> onSearchCallback;   // Callback pour lancer la recherche après reconnaissance

    private volatile boolean isListening = false;

    public VoiceService(TextField searchField, Consumer<String> onSearchCallback) {
        this.searchField = searchField;
        this.onSearchCallback = onSearchCallback;
        loadModel();
    }

    private void loadModel() {
        try {
            LibVosk.setLogLevel(LogLevel.WARNINGS);
            model = new Model("resources/vosk-model-small-fr-0.22");
            System.out.println("✅ Modèle Vosk chargé avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erreur chargement modèle Vosk");
        }
    }

    public void setMicrophoneButton(Button button) {
        this.microphoneButton = button;
    }

    public void startVoiceRecognition() {
        if (isListening || model == null) return;

        isListening = true;

        new Thread(() -> {
            try {
                AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
                TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(
                        new DataLine.Info(TargetDataLine.class, format)
                );

                microphone.open(format);
                microphone.start();

                Recognizer recognizer = new Recognizer(model, 16000);

                // Changement visuel du bouton
                Platform.runLater(() -> {
                    if (microphoneButton != null) {
                        microphoneButton.setText("🔴");
                        microphoneButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white;");
                    }
                });

                byte[] buffer = new byte[4000];
                int numBytesRead;

                while (isListening) {
                    numBytesRead = microphone.read(buffer, 0, buffer.length);

                    if (recognizer.acceptWaveForm(buffer, numBytesRead)) {
                        String resultJson = recognizer.getResult();
                        String text = extractText(resultJson);

                        if (!text.isEmpty() && searchField != null) {
                            Platform.runLater(() -> {
                                searchField.setText(text);
                                // Lancer la recherche automatiquement
                                if (onSearchCallback != null) {
                                    onSearchCallback.accept(text);
                                }
                            });
                        }
                        break;
                    }
                }

                microphone.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                isListening = false;
                Platform.runLater(() -> {
                    if (microphoneButton != null) {
                        microphoneButton.setText("🎤");
                        microphoneButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    }
                });
            }
        }).start();
    }

    private String extractText(String json) {
        try {
            int start = json.indexOf("\"text\" : \"") + 10;
            int end = json.indexOf("\"", start);
            if (start > 9 && end > start) {
                return json.substring(start, end).trim();
            }
        } catch (Exception ignored) {}
        return "";
    }

    public boolean isListening() {
        return isListening;
    }

    public void stopListening() {
        isListening = false;
    }
}