package controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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

public class AjouterUser {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField motDePasseField;

    @FXML
    private TextField telephoneField;

    @FXML
    private ComboBox<String> roleCombo;

    @FXML
    private DatePicker dateNaissancePicker;
    
    @FXML
    private Label lblFaceStatus; // Ajouter un label pour le statut du visage
    
    @FXML
    private ImageView facePreview; // Pour afficher un aperçu du visage capturé

    private ServiceUser serviceUser = new ServiceUser();
    private FaceRecognitionService faceService = new FaceRecognitionService();
    
    private String faceImage;
    private byte[] faceEncoding;
    
    private VideoCapture camera;
    private boolean cameraRunning = false;
    private Thread cameraThread;

    @FXML
    public void initialize() {
        roleCombo.getItems().addAll(
                "Admin",
                "chercheur",
                "Utilisateur"
        );
        
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        if (lblFaceStatus != null) {
            lblFaceStatus.setText("⚠️ Aucun visage capturé");
            lblFaceStatus.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
        }
    }

    // -----------------------------
    // VALIDATION DES CHAMPS
    // -----------------------------
    private boolean validateInputs() {

        // Champs vides
        if (nomField.getText().isEmpty() ||
            prenomField.getText().isEmpty() ||
            emailField.getText().isEmpty() ||
            motDePasseField.getText().isEmpty() ||
            telephoneField.getText().isEmpty() ||
            dateNaissancePicker.getValue() == null ||
            roleCombo.getValue() == null) {

            showAlert("Champs vides", "Veuillez remplir tous les champs.");
            return false;
        }

        // Email valide
        String email = emailField.getText();
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert("Email invalide", "Veuillez saisir un email valide.");
            return false;
        }

        // Téléphone tunisien
        String tel = telephoneField.getText();
        if (!tel.matches("^(2|5|9)[0-9]{7}$")) {
            showAlert("Téléphone invalide", "Numéro tunisien valide requis (8 chiffres : commence par 2,5 ou 9).");
            return false;
        }

        // Mot de passe ≥ 6 caractères
        if (motDePasseField.getText().length() < 6) {
            showAlert("Mot de passe court", "Le mot de passe doit contenir au moins 6 caractères.");
            return false;
        }

        // Date de naissance
        LocalDate dn = dateNaissancePicker.getValue();
        if (dn.isAfter(LocalDate.now())) {
            showAlert("Date invalide", "La date de naissance ne peut pas être dans le futur.");
            return false;
        }

        return true;
    }

    // -----------------------------
    // FENÊTRE ALERT
    // -----------------------------
    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfoAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // -----------------------------
    // CAPTURE DU VISAGE
    // -----------------------------
    @FXML
    void scannerVisage() {
        showCameraWindow();
    }
    
    private void showCameraWindow() {
        Stage cameraStage = new Stage();
        cameraStage.setTitle("🔵 CAPTURE DE VISAGE - Ajout utilisateur");
        
        ImageView previewView = new ImageView();
        previewView.setFitWidth(640);
        previewView.setFitHeight(480);
        previewView.setPreserveRatio(true);
        
        Button captureButton = new Button("📸 Capturer et encoder le visage");
        captureButton.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10px;");
        captureButton.setOnAction(e -> captureAndEncode(cameraStage));
        
        Button cancelButton = new Button("❌ Annuler");
        cancelButton.setStyle("-fx-font-size: 14px; -fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 10px;");
        cancelButton.setOnAction(e -> {
            stopCamera();
            cameraStage.close();
        });
        
        Label instructionLabel = new Label("Placez votre visage dans le cadre et cliquez sur 'Capturer'");
        instructionLabel.setStyle("-fx-font-size: 12px; -fx-padding: 10px; -fx-text-fill: #333;");
        
        HBox buttonBox = new HBox(10, captureButton, cancelButton);
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
                Platform.runLater(() -> showAlert("Erreur", "Impossible d'ouvrir la caméra."));
                return;
            }
            
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
            Platform.runLater(() -> showAlert("Erreur", "Erreur: " + e.getMessage()));
        }
    }
    
    private void captureAndEncode(Stage stage) {
        if (camera != null && camera.isOpened() && cameraRunning) {
            Mat frame = new Mat();
            if (camera.read(frame) && !frame.empty()) {
                
                // Détecter et extraire le visage
                Mat detectedFace = faceService.detectFace(frame);
                
                if (detectedFace == null || detectedFace.empty()) {
                    Platform.runLater(() -> showAlert("Erreur", 
                        "Aucun visage détecté!\n\n" +
                        "Assurez-vous que:\n" +
                        "- Le visage est bien visible\n" +
                        "- L'éclairage est suffisant\n" +
                        "- La personne est face à la caméra"));
                    frame.release();
                    return;
                }
                
                // Générer l'encodage facial
                byte[] encoding = faceService.generateFaceEncoding(detectedFace);
                
                if (encoding == null) {
                    Platform.runLater(() -> showAlert("Erreur", "Impossible de générer l'encodage facial."));
                    detectedFace.release();
                    frame.release();
                    return;
                }
                
                // Sauvegarder l'image
                String email = emailField.getText();
                if (email.isEmpty()) {
                    email = "user_" + System.currentTimeMillis();
                }
                String path = faceService.saveFaceImage(detectedFace, email);
                
                if (path != null) {
                    faceImage = path;
                    faceEncoding = encoding;
                    
                    // Mettre à jour l'interface
                    Platform.runLater(() -> {
                        if (lblFaceStatus != null) {
                            lblFaceStatus.setText("✅ Visage capturé et encodé avec succès!");
                            lblFaceStatus.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                        }
                        
                        // Afficher un aperçu si disponible
                        if (facePreview != null) {
                            Image previewImage = matToImage(detectedFace);
                            facePreview.setImage(previewImage);
                            facePreview.setFitWidth(100);
                            facePreview.setFitHeight(100);
                            facePreview.setPreserveRatio(true);
                        }
                        
                        showInfoAlert("Succès", 
                            "Visage capturé et encodé avec succès!\n\n" +
                            "Chemin: " + path + "\n" +
                            "Taille de l'encodage: " + encoding.length + " bytes");
                    });
                    
                    stopCamera();
                    stage.close();
                } else {
                    Platform.runLater(() -> showAlert("Erreur", "Impossible de sauvegarder l'image."));
                }
                
                detectedFace.release();
                frame.release();
            } else {
                Platform.runLater(() -> showAlert("Erreur", "Impossible de capturer l'image."));
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

    // -----------------------------
    // BOUTON AJOUTER UTILISATEUR
    // -----------------------------
    @FXML
    void ajouterUser() {

        // Vérification complète
        if (!validateInputs()) {
            return;
        }
        
        // Vérifier si le visage a été capturé
        if (faceEncoding == null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Visage manquant");
            confirmAlert.setHeaderText("Aucun visage n'a été capturé");
            confirmAlert.setContentText("Voulez-vous continuer sans reconnaissance faciale ?");
            
            if (confirmAlert.showAndWait().get() != ButtonType.OK) {
                return;
            }
        }

        // Création objet
        User u = new User();
        u.setNom(nomField.getText());
        u.setPrenom(prenomField.getText());
        u.setEmail(emailField.getText());
        u.setMotDePasse(motDePasseField.getText());
        u.setTelephone(telephoneField.getText());
        u.setRole(roleCombo.getValue());
        u.setDateNaissance(dateNaissancePicker.getValue());
        u.setFaceImage(faceImage);
        u.setFaceEncoding(faceEncoding);

        // Ajout DB
        serviceUser.ajouter(u);

        // Confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Utilisateur ajouté avec succès !\n\n" +
                  (faceEncoding != null ? "🔐 Reconnaissance faciale activée" : "⚠️ Reconnaissance faciale non activée"));
        alert.showAndWait();

        // Fermer la fenêtre
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    @FXML
    void annuler() {
        stopCamera();
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void retourner(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionUsers.fxml"));
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setTitle("Gestion des Zones Protégées");
            newStage.setScene(new Scene(root));
            newStage.setMaximized(true);

            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}