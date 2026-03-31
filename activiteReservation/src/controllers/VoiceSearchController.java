package controllers;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.*;
import java.io.File;

public class VoiceSearchController {

    private Model model;
    private TextField searchField;
    private Button voiceButton;
    private volatile boolean isListening = false;
    private Recognizer recognizer;

    // 🔴 MODIFIEZ CE CHEMIN AVEC LE VOTRE 🔴
    // Chemin absolu vers le dossier du modèle
    private static final String MODEL_PATH =  "C:\\Users\\WSI\\Downloads\\vosk-model-small-fr-0.22\\vosk-model-small-fr-0.22";
    
    public void setSearchField(TextField searchField) {
        this.searchField = searchField;
    }

    public void setVoiceButton(Button voiceButton) {
        this.voiceButton = voiceButton;
        if (voiceButton != null) {
            voiceButton.setOnAction(e -> startVoiceRecognition());
        }
    }

    public void initialize() {
        try {
            LibVosk.setLogLevel(LogLevel.WARNINGS);

            // Vérifier que le dossier existe
            File modelDir = new File(MODEL_PATH);
            if (!modelDir.exists()) {
                System.err.println("❌ Dossier modèle introuvable: " + MODEL_PATH);
                System.err.println("Vérifiez que le dossier existe et contient les fichiers.");
                Platform.runLater(() -> {
                    if (voiceButton != null) {
                        voiceButton.setDisable(true);
                        voiceButton.setText("❌");
                    }
                });
                return;
            }

            // Vérifier que le dossier contient les sous-dossiers nécessaires
            File amDir = new File(modelDir, "am");
            if (!amDir.exists() || amDir.listFiles().length == 0) {
                System.err.println("❌ Le dossier 'am' est vide ou n'existe pas dans: " + MODEL_PATH);
                return;
            }

            System.out.println("✅ Chargement du modèle depuis: " + MODEL_PATH);
            model = new Model(MODEL_PATH);
            recognizer = new Recognizer(model, 16000);
            
            System.out.println("🎉 Modèle Vosk chargé avec succès !");

        } catch (Exception ex) {
            ex.printStackTrace();
            Platform.runLater(() -> {
                if (voiceButton != null) {
                    voiceButton.setDisable(true);
                    voiceButton.setText("❌");
                }
            });
        }
    }

    public void startVoiceRecognition() {
        if (isListening || model == null || recognizer == null) {
            System.err.println("Reconnaissance non disponible");
            return;
        }
        
        isListening = true;

        new Thread(() -> {
            TargetDataLine microphone = null;
            try {
                AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                microphone = (TargetDataLine) AudioSystem.getLine(info);

                microphone.open(format);
                microphone.start();

                Platform.runLater(() -> {
                    if (voiceButton != null) {
                        voiceButton.setText("🔴");
                        voiceButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white;");
                    }
                    if (searchField != null) {
                        searchField.setStyle("-fx-border-color: #ef4444; -fx-border-width: 2;");
                        searchField.setPromptText("🎤 Parlez maintenant...");
                    }
                });

                byte[] buffer = new byte[4000];
                int numBytesRead;
                StringBuilder result = new StringBuilder();

                while (isListening) {
                    numBytesRead = microphone.read(buffer, 0, buffer.length);
                    
                    if (numBytesRead > 0) {
                        if (recognizer.acceptWaveForm(buffer, numBytesRead)) {
                            String text = extractText(recognizer.getResult());
                            if (!text.isEmpty()) {
                                result.append(text);
                            }
                            break;
                        } else {
                            String partial = extractPartialText(recognizer.getPartialResult());
                            if (!partial.isEmpty()) {
                                final String partialText = partial;
                                Platform.runLater(() -> {
                                    if (searchField != null) {
                                        searchField.setText(partialText);
                                    }
                                });
                            }
                        }
                    }
                }
                
                String finalResult = extractText(recognizer.getFinalResult());
                if (!finalResult.isEmpty()) {
                    result.append(finalResult);
                }
                
                String spokenText = result.toString().trim();
                if (!spokenText.isEmpty() && searchField != null) {
                    final String finalSpoken = spokenText;
                    Platform.runLater(() -> {
                        searchField.setText(finalSpoken);
                        // Déclencher automatiquement la recherche
                        if (searchField.getScene() != null) {
                            Button searchButton = (Button) searchField.getScene().lookup("#searchButton");
                            if (searchButton != null) {
                                searchButton.fire();
                            }
                        }
                    });
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (microphone != null) {
                    microphone.close();
                }
                isListening = false;
                Platform.runLater(() -> {
                    if (voiceButton != null) {
                        voiceButton.setText("🎤");
                        voiceButton.setStyle("");
                    }
                    if (searchField != null) {
                        searchField.setStyle("");
                        searchField.setPromptText("Rechercher...");
                    }
                });
            }
        }).start();
    }
    
    private String extractText(String json) {
        try {
            int start = json.indexOf("\"text\" : \"") + 10;
            if (start < 10) return "";
            int end = json.indexOf("\"", start);
            if (end < 0) return "";
            return json.substring(start, end).trim();
        } catch (Exception e) {
            return "";
        }
    }
    
    private String extractPartialText(String json) {
        try {
            int start = json.indexOf("\"partial\" : \"") + 12;
            if (start < 12) return "";
            int end = json.indexOf("\"", start);
            if (end < 0) return "";
            return json.substring(start, end).trim();
        } catch (Exception e) {
            return "";
        }
    }
    
    public void stopVoiceRecognition() {
        isListening = false;
    }
}