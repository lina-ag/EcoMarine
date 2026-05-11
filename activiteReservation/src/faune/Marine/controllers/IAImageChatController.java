package faune.Marine.controllers;

import faune.Marine.services.OllamaChatService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.application.Platform;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class IAImageChatController implements Initializable {
    
    @FXML
    private VBox chatContainer;
    
    @FXML
    private TextField txtMessage;
    
    @FXML
    private Button btnEnvoyer;
    
    @FXML
    private Button btnEffacer;
    
    @FXML
    private Label lblStatut;
    
    @FXML
    private ScrollPane scrollPane;
    
    private OllamaChatService chatService;
    private String contexteAnalyse = "";
    private String imageName = "";
    private boolean isWaitingResponse = false;
    
    // Modèle DeepSeek par défaut (fixe)
    private final String MODELE_DEEPSEEK = "deepseek-r1:7b-qwen-distill-q4_K_M";
    
    // Largeur fixe pour tous les messages
    private final double MESSAGE_MAX_WIDTH = 400;
    private final double TEXT_MAX_WIDTH = MESSAGE_MAX_WIDTH - 20;
    
    // File d'attente des messages
    private Queue<MessageRequest> messageQueue = new LinkedList<>();
    private boolean isProcessing = false;
    
    // Classe interne pour les requêtes de message
    private static class MessageRequest {
        String message;
        long timestamp;
        
        MessageRequest(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("✅ IAImageChatController initialisé");
        System.out.println("🚀 Modèle fixe: " + MODELE_DEEPSEEK);
        
        chatService = new OllamaChatService();
        
        // Messages de bienvenue
        ajouterMessageBot("👋 Bonjour ! Je suis ton assistant MARIA.");
        ajouterMessageBot("Pose-moi une question !");
        
        txtMessage.setOnAction(e -> envoyerMessage());
        
        // Vérifier la connexion silencieusement
        verifierConnexionSilencieuse();
    }
    
    private void verifierConnexionSilencieuse() {
        new Thread(() -> {
            boolean connecte = chatService.testConnexion();
            if (!connecte) {
                System.err.println("⚠️ Attention: Ollama non connecté");
            }
        }).start();
    }
    
    public void initChat(String analyse, String image) {
        System.out.println("📥 initChat appelé");
        System.out.println("   Image: " + image);
        System.out.println("   Analyse: " + (analyse != null ? analyse.length() : 0) + " caractères");
        
        if (analyse == null || analyse.isEmpty()) {
            analyse = "Aucune analyse disponible pour cette image.";
        }
        
        this.contexteAnalyse = analyse;
        this.imageName = image;
        
        String message = "🔍 **Image analysée : " + image + "**\n\n" +
                        "Je peux répondre à tes questions sur cette analyse.";
        
        ajouterMessageSysteme(message);
    }
    
    @FXML
    private void envoyerMessage() {
        String message = txtMessage.getText().trim();
        if (message.isEmpty()) return;
        
        // Ajouter à la file d'attente
        messageQueue.add(new MessageRequest(message));
        txtMessage.clear();
        
        // Démarrer le traitement si pas déjà en cours
        if (!isProcessing) {
            processNextMessage();
        }
    }
    
    private void processNextMessage() {
        if (messageQueue.isEmpty()) {
            isProcessing = false;
            return;
        }
        
        isProcessing = true;
        MessageRequest request = messageQueue.poll();
        String message = request.message;
        
        // 1️⃣ Afficher le message utilisateur IMMÉDIATEMENT
        Platform.runLater(() -> {
            ajouterMessageUser(message);
            scrollToBottom();
        });
        
        // 2️⃣ Attendre un très court instant pour garantir l'affichage
        // Puis ajouter le message d'attente du bot
        CompletableFuture.runAsync(() -> {
            try {
                // Petit délai pour garantir que l'UI a bien affiché le message utilisateur
                Thread.sleep(50);
                
                Platform.runLater(() -> {
                    // Créer le message d'attente
                    HBox waitingBox = creerMessageAttente();
                    chatContainer.getChildren().add(waitingBox);
                    scrollToBottom();
                    
                    HBox contentBox = (HBox) waitingBox.getChildren().get(1);
                    VBox textBox = (VBox) contentBox.getChildren().get(0);
                    Label messageLabel = (Label) textBox.getChildren().get(0);
                    
                    // Lancer le streaming
                    streamResponse(message, waitingBox, messageLabel, request.timestamp);
                });
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
    
    private void streamResponse(String message, HBox waitingBox, Label messageLabel, long requestTimestamp) {
        new Thread(() -> {
            try {
                String prompt = construirePrompt(message);
                
                System.out.println("📤 Envoi à " + MODELE_DEEPSEEK + "...");
                
                StringBuilder reponse = new StringBuilder();
                final boolean[] firstToken = {true};
                
                chatService.chatStreaming(
                    MODELE_DEEPSEEK,
                    prompt,
                    token -> {
                        reponse.append(token);
                        
                        Platform.runLater(() -> {
                            // Vérifier que c'est toujours le message en cours
                            // (évite les réponses qui arrivent dans le désordre)
                            if (waitingBox.getParent() != null) {
                                if (firstToken[0]) {
                                    // Premier token : enlever le style italique
                                    messageLabel.setStyle("-fx-text-fill: #000000; -fx-font-size: 14px; -fx-wrap-text: true;");
                                    firstToken[0] = false;
                                }
                                messageLabel.setText(reponse.toString());
                                scrollToBottom();
                            }
                        });
                    },
                    () -> {
                        System.out.println("✅ Réponse terminée");
                        Platform.runLater(() -> {
                            // Traiter le message suivant dans la file
                            processNextMessage();
                        });
                    }
                );
                
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    if (waitingBox.getParent() != null) {
                        messageLabel.setText("❌ Erreur: " + e.getMessage());
                    }
                    processNextMessage();
                });
            }
        }).start();
    }
    
    private String construirePrompt(String message) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Tu es un assistant amical. Réponds en français de façon naturelle.\n\n");
        
        if (contexteAnalyse != null && !contexteAnalyse.isEmpty()) {
            prompt.append("CONTEXTE : Une image a été analysée. Voici les résultats :\n");
            prompt.append(contexteAnalyse).append("\n\n");
        }
        
        prompt.append("QUESTION : ").append(message).append("\n\n");
        prompt.append("RÉPONSE :");
        
        return prompt.toString();
    }
    
    private HBox creerMessageAttente() {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(5, 10, 5, 10));
        box.setMaxWidth(Double.MAX_VALUE);
        
        Label avatar = new Label("🤖");
        avatar.setStyle("-fx-font-size: 24px; -fx-padding: 0 10 0 0;");
        
        HBox content = new HBox();
        content.setStyle("-fx-background-color: #E9E9EB; -fx-background-radius: 15; -fx-padding: 10;");
        content.setMaxWidth(MESSAGE_MAX_WIDTH);
        
        VBox textBox = new VBox();
        textBox.setAlignment(Pos.CENTER_LEFT);
        textBox.setMaxWidth(TEXT_MAX_WIDTH);
        
        Label msg = new Label("🤔 En train d'écrire...");
        msg.setStyle("-fx-text-fill: #666; -fx-font-style: italic; -fx-font-size: 14px; -fx-wrap-text: true;");
        msg.setWrapText(true);
        msg.setMaxWidth(TEXT_MAX_WIDTH);
        
        Label heure = new Label(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        heure.setStyle("-fx-text-fill: #8E8E93; -fx-font-size: 10px; -fx-padding: 5 0 0 0;");
        heure.setAlignment(Pos.CENTER_LEFT);
        
        textBox.getChildren().addAll(msg, heure);
        content.getChildren().add(textBox);
        box.getChildren().addAll(avatar, content);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        box.getChildren().add(spacer);
        
        return box;
    }
    
    private void ajouterMessageUser(String texte) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setPadding(new Insets(5, 10, 5, 10));
        box.setMaxWidth(Double.MAX_VALUE);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox content = new HBox();
        content.setStyle("-fx-background-color: #007AFF; -fx-background-radius: 15; -fx-padding: 10;");
        content.setMaxWidth(MESSAGE_MAX_WIDTH);
        content.setAlignment(Pos.CENTER_RIGHT);
        
        VBox textBox = new VBox();
        textBox.setAlignment(Pos.CENTER_RIGHT);
        textBox.setMaxWidth(TEXT_MAX_WIDTH);
        
        Label msg = new Label(texte);
        msg.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-wrap-text: true;");
        msg.setWrapText(true);
        msg.setMaxWidth(TEXT_MAX_WIDTH);
        msg.setAlignment(Pos.CENTER_RIGHT);
        
        Label heure = new Label(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        heure.setStyle("-fx-text-fill: rgba(255,255,255,0.7); -fx-font-size: 10px; -fx-padding: 5 0 0 0;");
        heure.setAlignment(Pos.CENTER_RIGHT);
        
        textBox.getChildren().addAll(msg, heure);
        content.getChildren().add(textBox);
        
        box.getChildren().addAll(spacer, content);
        
        Platform.runLater(() -> {
            chatContainer.getChildren().add(box);
            scrollToBottom();
        });
    }
    
    private void ajouterMessageBot(String texte) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(5, 10, 5, 10));
        box.setMaxWidth(Double.MAX_VALUE);
        
        Label avatar = new Label("🤖");
        avatar.setStyle("-fx-font-size: 24px; -fx-padding: 0 10 0 0;");
        
        HBox content = new HBox();
        content.setStyle("-fx-background-color: #E9E9EB; -fx-background-radius: 15; -fx-padding: 10;");
        content.setMaxWidth(MESSAGE_MAX_WIDTH);
        content.setAlignment(Pos.CENTER_LEFT);
        
        VBox textBox = new VBox();
        textBox.setAlignment(Pos.CENTER_LEFT);
        textBox.setMaxWidth(TEXT_MAX_WIDTH);
        
        Label msg = new Label(texte);
        msg.setStyle("-fx-text-fill: #000000; -fx-font-size: 14px; -fx-wrap-text: true;");
        msg.setWrapText(true);
        msg.setMaxWidth(TEXT_MAX_WIDTH);
        msg.setAlignment(Pos.CENTER_LEFT);
        
        Label heure = new Label(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        heure.setStyle("-fx-text-fill: #8E8E93; -fx-font-size: 10px; -fx-padding: 5 0 0 0;");
        heure.setAlignment(Pos.CENTER_LEFT);
        
        textBox.getChildren().addAll(msg, heure);
        content.getChildren().add(textBox);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        box.getChildren().addAll(avatar, content, spacer);
        
        Platform.runLater(() -> {
            chatContainer.getChildren().add(box);
            scrollToBottom();
        });
    }
    
    private void ajouterMessageSysteme(String message) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        
        VBox content = new VBox();
        content.setStyle("-fx-background-color: #5856D6; -fx-background-radius: 15; -fx-padding: 15;");
        content.setMaxWidth(500);
        
        Label msg = new Label(message);
        msg.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-wrap-text: true;");
        msg.setWrapText(true);
        
        content.getChildren().add(msg);
        box.getChildren().add(content);
        
        Platform.runLater(() -> {
            chatContainer.getChildren().add(box);
            scrollToBottom();
        });
    }
    
    @FXML
    private void effacerChat() {
        chatContainer.getChildren().clear();
        messageQueue.clear();
        isProcessing = false;
        ajouterMessageBot("👋 Chat effacé. Pose-moi une question !");
    }
    
    private void scrollToBottom() {
        Platform.runLater(() -> {
            scrollPane.setVvalue(1.0);
            scrollPane.layout();
        });
    }
}