package controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import tn.edu.esprit.entities.User;
import tn.edu.esprit.services.ServiceUser;
import tn.edu.esprit.services.FaceRecognitionService;
import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.Videoio;

public class SignInController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private ServiceUser service = new ServiceUser();
    private FaceRecognitionService faceService = new FaceRecognitionService();
    
    private static User utilisateurConnecte;
    
    private VideoCapture camera;
    private boolean cameraRunning = false;
    private Thread cameraThread;

    @FXML
    public void initialize() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @FXML
    void login() {
        String email = emailField.getText();
        String mdp = passwordField.getText();

        if (email.isEmpty() || mdp.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs !", Alert.AlertType.ERROR);
            return;
        }
     // 🔥 ADMIN PAR DÉFAUT (hardcoded)
        if (email.equals("admin@gmail.com") && mdp.equals("admin")) {
            User admin = new User();
            admin.setNom("Admin");
            admin.setPrenom("System");
            admin.setEmail("admin@gmail.com");
            admin.setRole("admin");

            utilisateurConnecte = admin;

            ouvrirGestionUsers();
            return;
        }

        User u = service.login(email, mdp);

        if (u == null) {
            showAlert("Connexion échouée", "Email ou mot de passe incorrect !", Alert.AlertType.ERROR);
            return;
        }
        
        utilisateurConnecte = u;

        if (estAdmin(u)) {
            ouvrirGestionUsers();
        } else {
            ouvrirAccueil(u);
        }
    }
    
    @FXML
    void loginWithFace() {
        System.out.println("=== DÉBUT RECONNAISSANCE FACIALE ===");
        
        List<User> users = service.getAll();
        
        // Compter les utilisateurs avec encodage facial
        int countWithEncoding = 0;
        for (User u : users) {
            if (u.getFaceEncoding() != null) {
                countWithEncoding++;
                System.out.println("✅ Utilisateur avec encodage: " + u.getEmail() + 
                                 ", taille encodage: " + u.getFaceEncoding().length);
            } else {
                System.out.println("❌ Utilisateur sans encodage: " + u.getEmail());
            }
        }
        
        System.out.println("Utilisateurs avec encodage facial: " + countWithEncoding + "/" + users.size());
        
        if (countWithEncoding == 0) {
            showAlert("Information", 
                "Aucun utilisateur n'a activé la reconnaissance faciale.\n\n" +
                "Pour activer la reconnaissance faciale:\n" +
                "1. Allez dans l'inscription\n" +
                "2. Cliquez sur 'Scanner visage'\n" +
                "3. Capturez votre visage\n" +
                "4. Terminez l'inscription\n\n" +
                "Ou utilisez la connexion par email/mot de passe.", 
                Alert.AlertType.INFORMATION);
            return;
        }
        
        showCameraWindow();
    }
    
    private void showCameraWindow() {
        Stage cameraStage = new Stage();
        cameraStage.setTitle("🟢 CONNEXION - Reconnaissance faciale");
        
        ImageView previewView = new ImageView();
        previewView.setFitWidth(640);
        previewView.setFitHeight(480);
        previewView.setPreserveRatio(true);
        
        Button authenticateButton = new Button("🔍 Scanner et authentifier");
        authenticateButton.setStyle("-fx-font-size: 14px; -fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 10px;");
        authenticateButton.setOnAction(e -> authenticateFace(cameraStage));
        
        Button cancelButton = new Button("❌ Annuler");
        cancelButton.setStyle("-fx-font-size: 14px; -fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 10px;");
        cancelButton.setOnAction(e -> {
            stopCamera();
            cameraStage.close();
        });
        
        Label instructionLabel = new Label("Placez votre visage face à la caméra et cliquez sur 'Scanner'");
        instructionLabel.setStyle("-fx-font-size: 12px; -fx-padding: 10px; -fx-text-fill: #333;");
        
        HBox buttonBox = new HBox(10, authenticateButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        
        VBox root = new VBox(10, previewView, instructionLabel, buttonBox);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 20px; -fx-background-color: #f0f0f0;");
        
        Scene scene = new Scene(root, 700, 600);
        cameraStage.setScene(scene);
        
        startCamera(previewView);
        
        cameraStage.setOnCloseRequest(e -> stopCamera());
        cameraStage.show();
    }
    
    private void startCamera(ImageView previewView) {
        try {
            camera = new VideoCapture(0, Videoio.CAP_DSHOW);
            
            if (!camera.isOpened()) {
                camera = new VideoCapture(0, Videoio.CAP_MSMF);
            }
            
            if (!camera.isOpened()) {
                Platform.runLater(() -> showAlert("Erreur", "Impossible d'ouvrir la caméra.", Alert.AlertType.ERROR));
                return;
            }
            
            System.out.println("✅ Caméra ouverte avec succès!");
            
            camera.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
            camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);
            
            cameraRunning = true;
            
            cameraThread = new Thread(() -> {
                Mat frame = new Mat();
                while (cameraRunning && camera != null && camera.isOpened()) {
                    if (camera.read(frame) && !frame.empty()) {
                        Image image = matToImage(frame);
                        Platform.runLater(() -> previewView.setImage(image));
                    }
                    try {
                        Thread.sleep(33);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                frame.release();
            });
            cameraThread.setDaemon(true);
            cameraThread.start();
            
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> showAlert("Erreur", "Erreur: " + e.getMessage(), Alert.AlertType.ERROR));
        }
    }
    
    private void authenticateFace(Stage stage) {
        if (camera != null && camera.isOpened() && cameraRunning) {
            Mat frame = new Mat();
            if (camera.read(frame) && !frame.empty()) {
                
                Mat faceImage = faceService.detectFace(frame);
                
                if (faceImage == null || faceImage.empty()) {
                    Platform.runLater(() -> showAlert("Erreur", 
                        "Aucun visage détecté!\n\n" +
                        "Assurez-vous que:\n" +
                        "- Votre visage est bien visible\n" +
                        "- L'éclairage est suffisant\n" +
                        "- Vous êtes face à la caméra", 
                        Alert.AlertType.ERROR));
                    frame.release();
                    return;
                }
                
                byte[] capturedEncoding = faceService.generateFaceEncoding(faceImage);
                
                if (capturedEncoding == null) {
                    Platform.runLater(() -> showAlert("Erreur", "Impossible de générer l'encodage facial.", Alert.AlertType.ERROR));
                    faceImage.release();
                    frame.release();
                    return;
                }
                
                System.out.println("✅ Encodage généré: " + capturedEncoding.length + " bytes");
                
                List<User> users = service.getAll();
                
                final User[] authenticatedUser = {null};
                final double[] bestScore = {0};
                
                System.out.println("=== RECHERCHE DE CORRESPONDANCE FACIALE ===");
                System.out.println("Seuil requis: 60%");
                
                for (User user : users) {
                    if (user.getFaceEncoding() != null) {
                        double similarity = faceService.compareFaceEncodings(capturedEncoding, user.getFaceEncoding());
                        boolean isMatch = similarity > 60; // Seuil de 60%
                        
                        System.out.println("Comparaison avec " + user.getEmail() + 
                                         ": similarité=" + String.format("%.2f", similarity) + "%" +
                                         ", match=" + isMatch);
                        
                        if (isMatch && similarity > bestScore[0]) {
                            bestScore[0] = similarity;
                            authenticatedUser[0] = user;
                        }
                    }
                }
                
                faceImage.release();
                frame.release();
                
                if (authenticatedUser[0] != null) {
                    final User finalUser = authenticatedUser[0];
                    final double finalScore = bestScore[0];
                    
                    Platform.runLater(() -> {
                        showAlert("Succès", 
                            "✅ Authentification réussie!\n\n" +
                            "Bienvenue " + finalUser.getPrenom() + " " + finalUser.getNom() + "\n" +
                            "Email: " + finalUser.getEmail() + "\n" +
                            "Rôle: " + finalUser.getRole() + "\n" +
                            "Confiance: " + String.format("%.2f", finalScore) + "%", 
                            Alert.AlertType.INFORMATION);
                        
                        utilisateurConnecte = finalUser;
                        
                        stopCamera();
                        stage.close();
                        
                        // 🔥 Redirection selon le rôle
                        if (estAdmin(finalUser)) {
                            System.out.println("👑 Redirection vers GestionUsers (Admin)");
                            ouvrirGestionUsers();
                        } else {
                            System.out.println("👤 Redirection vers Accueil (Chercheur/Utilisateur)");
                            ouvrirAccueil(finalUser);
                        }
                    });
                } else {
                    Platform.runLater(() -> showAlert("Échec", 
                        "❌ Reconnaissance faciale échouée.\n\n" +
                        "Aucun utilisateur correspondant trouvé.\n" +
                        "Meilleur score: " + String.format("%.2f", bestScore[0]) + "%\n" +
                        "(Seuil requis: 60%)\n\n" +
                        "Conseils:\n" +
                        "- Assurez-vous d'avoir capturé votre visage lors de l'inscription\n" +
                        "- Vérifiez l'éclairage\n" +
                        "- Placez-vous face à la caméra", 
                        Alert.AlertType.ERROR));
                }
                
            } else {
                Platform.runLater(() -> showAlert("Erreur", "Impossible de capturer l'image.", Alert.AlertType.ERROR));
            }
        }
    }
    
    private void stopCamera() {
        cameraRunning = false;
        if (cameraThread != null) {
            cameraThread.interrupt();
            cameraThread = null;
        }
        if (camera != null && camera.isOpened()) {
            camera.release();
            camera = null;
            System.out.println("✅ Caméra libérée");
        }
    }
    
    private Image matToImage(Mat mat) {
        try {
            MatOfByte bytes = new MatOfByte();
            Imgcodecs.imencode(".jpg", mat, bytes);
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes.toArray());
            return new Image(bis);
        } catch (Exception e) {
            return null;
        }
    }
    
    // 🔥 Ouverture de GestionUsers pour les ADMINS
    private void ouvrirGestionUsers() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionUsers.fxml"));
            Scene scene = new Scene(loader.load());
            
            GestionUsers controller = loader.getController();
            if (controller != null) {
                controller.setUtilisateurConnecte(utilisateurConnecte);
            }
            
            stage.setScene(scene);
            stage.setTitle("Gestion des utilisateurs - Admin");
            stage.setMaximized(true);
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir GestionUsers : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    // 🔥 Ouverture de Accueil pour les CHERCHEURS et UTILISATEURS
    private void ouvrirAccueil(User user) {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Acceuil.fxml"));
            Scene scene = new Scene(loader.load());
            
            AccueilController controller = loader.getController();
            if (controller != null) {
                controller.setUtilisateurConnecte(user);
            }
            
            stage.setScene(scene);
            stage.setTitle("Accueil - " + user.getPrenom() + " " + user.getNom());
            stage.setMaximized(true);
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir l'accueil : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void openSignUp() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/SignUp.fxml")));
            stage.setScene(scene);
            stage.setTitle("Inscription");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void openSignUp(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/SignUp.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    // 🔥 Vérification du rôle ADMIN
    private boolean estAdmin(User user) {
        if (user == null || user.getRole() == null) return false;
        
        String role = user.getRole().toLowerCase().trim();
        return role.equals("admin") || 
               role.equals("administrateur") || 
               role.contains("admin");
    }
    
    public static User getUtilisateurConnecte() {
        return utilisateurConnecte;
    }
    
    public void setUtilisateurConnecte(User user) {
        utilisateurConnecte = user;
    }

    private void showAlert(String titre, String msg, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(titre);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}