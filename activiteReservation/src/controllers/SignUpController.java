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

public class SignUpController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField telephoneField;
    @FXML private DatePicker dateNaissancePicker;
    @FXML private ComboBox<String> roleCombo;
    
    // 🔥 Nouveaux éléments pour la reconnaissance faciale
    @FXML private Button captureFaceButton;
    @FXML private Label faceStatusLabel;
    @FXML private ImageView facePreview;

    private ServiceUser service = new ServiceUser();
    private FaceRecognitionService faceService = new FaceRecognitionService();

    private String faceImage;
    private byte[] faceEncoding;

    private VideoCapture camera;
    private boolean cameraRunning = false;
    private Thread cameraThread;

    @FXML
    public void initialize() {
        roleCombo.getItems().addAll("chercheur", "Utilisateur");
        roleCombo.setValue("Utilisateur"); // valeur par défaut
        
        // 🔥 Ajouter un listener sur le changement de rôle
        roleCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("chercheur".equals(newVal)) {
                // Chercheur : activer la capture faciale
                enableFaceCapture(true);
                showAlertInfo("Reconnaissance faciale", 
                    "En tant que chercheur, vous pouvez activer la reconnaissance faciale.\n" +
                    "Cliquez sur 'Scanner le visage' pour ajouter cette fonctionnalité.");
            } else {
                // Utilisateur simple : désactiver la capture faciale
                enableFaceCapture(false);
                showAlertInfo("Reconnaissance faciale", 
                    "La reconnaissance faciale est réservée aux chercheurs.\n" +
                    "Vous pouvez continuer votre inscription sans cette fonctionnalité.");
            }
        });

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        // Désactiver la capture faciale par défaut (Utilisateur simple)
        enableFaceCapture(false);
    }
    
    // 🔥 Méthode pour activer/désactiver les éléments de reconnaissance faciale
    private void enableFaceCapture(boolean enable) {
        if (captureFaceButton != null) {
            captureFaceButton.setDisable(!enable);
            if (!enable) {
                captureFaceButton.setStyle("-fx-opacity: 0.5; -fx-background-color: #cccccc;");
            } else {
                captureFaceButton.setStyle("-fx-opacity: 1; -fx-background-color: #2196F3;");
            }
        }
        
        if (faceStatusLabel != null) {
            if (enable) {
                if (faceEncoding != null) {
                    faceStatusLabel.setText("✅ Visage capturé !");
                    faceStatusLabel.setStyle("-fx-text-fill: #4CAF50;");
                } else {
                    faceStatusLabel.setText("⚠️ Cliquez sur 'Scanner le visage' pour capturer");
                    faceStatusLabel.setStyle("-fx-text-fill: #FF9800;");
                }
            } else {
                faceStatusLabel.setText("🔒 Réservé aux chercheurs");
                faceStatusLabel.setStyle("-fx-text-fill: #f44336;");
            }
        }
    }
    
    // 🔥 Mettre à jour le statut après capture
    private void updateFaceStatus() {
        if (faceEncoding != null && "chercheur".equals(roleCombo.getValue())) {
            if (faceStatusLabel != null) {
                faceStatusLabel.setText("✅ Visage capturé et encodé !");
                faceStatusLabel.setStyle("-fx-text-fill: #4CAF50;");
            }
        }
    }

    // ================= CAMERA =================

    @FXML
    void captureFace() {
        // 🔥 Vérifier que l'utilisateur est un chercheur
        if (!"chercheur".equals(roleCombo.getValue())) {
            showAlertError("Accès refusé", 
                "La reconnaissance faciale est réservée aux chercheurs.\n" +
                "Veuillez sélectionner le rôle 'chercheur' pour utiliser cette fonctionnalité.");
            return;
        }
        
        showCameraWindow();
    }

    private void showCameraWindow() {
        Stage cameraStage = new Stage();
        cameraStage.setTitle("Capture du visage - Chercheur");

        ImageView previewView = new ImageView();
        previewView.setFitWidth(640);
        previewView.setFitHeight(480);

        Button captureButton = new Button("📸 Capturer");
        captureButton.setOnAction(e -> captureAndEncode(cameraStage));

        Button cancelButton = new Button("❌ Annuler");
        cancelButton.setOnAction(e -> {
            stopCamera();
            cameraStage.close();
        });

        HBox buttons = new HBox(10, captureButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);

        VBox root = new VBox(10, previewView, buttons);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 700, 550);
        cameraStage.setScene(scene);

        startCamera(previewView);

        cameraStage.setOnCloseRequest(e -> stopCamera());
        cameraStage.show();
    }

    private void startCamera(ImageView previewView) {
        camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            showAlertError("Erreur", "Caméra non disponible");
            return;
        }

        cameraRunning = true;

        cameraThread = new Thread(() -> {
            Mat frame = new Mat();

            while (cameraRunning) {
                if (camera.read(frame)) {
                    Image img = matToImage(frame);
                    Platform.runLater(() -> previewView.setImage(img));
                }

                try {
                    Thread.sleep(30);
                } catch (Exception e) {}
            }
            frame.release();
        });

        cameraThread.setDaemon(true);
        cameraThread.start();
    }

    private void captureAndEncode(Stage stage) {
        Mat frame = new Mat();

        if (camera.read(frame)) {

            Mat face = faceService.detectFace(frame);

            if (face == null) {
                showAlertError("Erreur", "Aucun visage détecté");
                return;
            }

            byte[] encoding = faceService.generateFaceEncoding(face);

            if (encoding == null) {
                showAlertError("Erreur", "Encodage échoué");
                return;
            }

            String email = emailField.getText().isEmpty()
                    ? "user_" + System.currentTimeMillis()
                    : emailField.getText();

            String path = faceService.saveFaceImage(face, email);

            faceImage = path;
            faceEncoding = encoding;
            
            // 🔥 Afficher l'aperçu
            if (facePreview != null) {
                Image previewImg = matToImage(face);
                facePreview.setImage(previewImg);
                facePreview.setFitWidth(100);
                facePreview.setFitHeight(100);
                facePreview.setPreserveRatio(true);
            }
            
            // 🔥 Mettre à jour le statut
            updateFaceStatus();

            showAlert(Alert.AlertType.INFORMATION, "Succès", 
                "Visage capturé et encodé avec succès !\n\n" +
                "Cette fonctionnalité vous permettra de vous connecter facilement.");

            stopCamera();
            stage.close();
        }
    }

    private void stopCamera() {
        cameraRunning = false;

        if (camera != null) {
            camera.release();
        }
    }

    private Image matToImage(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    // ================= SIGN UP =================

    @FXML
    void signUp() {

        // Validation
        if (nomField.getText().isEmpty() ||
            prenomField.getText().isEmpty() ||
            emailField.getText().isEmpty() ||
            passwordField.getText().isEmpty() ||
            telephoneField.getText().isEmpty() ||
            dateNaissancePicker.getValue() == null ||
            roleCombo.getValue() == null) {

            showAlertError("Erreur", "Remplir tous les champs");
            return;
        }

        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlertError("Erreur", "Email invalide");
            return;
        }

        if (passwordField.getText().length() < 8) {
            showAlertError("Erreur", "Mot de passe court");
            return;
        }

        if (dateNaissancePicker.getValue().isAfter(LocalDate.now())) {
            showAlertError("Erreur", "Date invalide");
            return;
        }
        
        // 🔥 Vérification pour les chercheurs : la capture faciale est obligatoire ?
        String role = roleCombo.getValue();
        if ("chercheur".equals(role) && faceEncoding == null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Reconnaissance faciale requise");
            confirmAlert.setHeaderText("En tant que chercheur, la reconnaissance faciale est recommandée");
            confirmAlert.setContentText("Voulez-vous continuer sans reconnaissance faciale ?\n\n" +
                                       "Note: Vous pourrez l'ajouter plus tard dans votre profil.");
            
            ButtonType ouiBtn = new ButtonType("Continuer sans");
            ButtonType nonBtn = new ButtonType("Scanner maintenant", ButtonBar.ButtonData.CANCEL_CLOSE);
            
            confirmAlert.getButtonTypes().setAll(ouiBtn, nonBtn);
            
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == nonBtn) {
                    captureFace(); // Ouvrir la caméra
                    return;
                }
                // Continuer sans capture faciale
                enregistrerUtilisateur();
            });
        } else {
            enregistrerUtilisateur();
        }
    }
    
    private void enregistrerUtilisateur() {
        // Création User
        User u = new User();
        u.setNom(nomField.getText());
        u.setPrenom(prenomField.getText());
        u.setEmail(emailField.getText());
        u.setMotDePasse(passwordField.getText());
        u.setTelephone(telephoneField.getText());
        u.setRole(roleCombo.getValue());
        u.setDateNaissance(dateNaissancePicker.getValue());
        u.setFaceImage(faceImage);
        u.setFaceEncoding(faceEncoding);
        
        // DEBUG
        System.out.println("=== INSCRIPTION ===");
        System.out.println("Rôle: " + roleCombo.getValue());
        System.out.println("Encoding: " + (faceEncoding != null ? faceEncoding.length + " bytes" : "NULL"));

        // INSERTION
        service.ajouter(u);

        // VÉRIFICATION APRES INSERTION
        User savedUser = service.findByEmail(u.getEmail());
        System.out.println("Après insertion - Encoding: " + 
            (savedUser != null && savedUser.getFaceEncoding() != null 
                ? savedUser.getFaceEncoding().length + " bytes" 
                : "NULL"));

        String message = "Compte créé avec succès !\n\n";
        if ("chercheur".equals(roleCombo.getValue())) {
            if (faceEncoding != null) {
                message += "🔐 Reconnaissance faciale activée.\n" +
                          "Vous pouvez maintenant vous connecter avec votre visage.";
            } else {
                message += "⚠️ Reconnaissance faciale non activée.\n" +
                          "Vous pourrez l'activer plus tard dans votre profil.";
            }
        } else {
            message += "👤 Compte utilisateur standard créé.";
        }
        
        showAlert(Alert.AlertType.INFORMATION, "Succès", message);
    }

    // ================= NAVIGATION =================

    @FXML
    private void openSignIn(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/SignIn.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    // ================= ALERTS =================

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showAlertError(String title, String msg) {
        showAlert(Alert.AlertType.ERROR, title, msg);
    }
    
    private void showAlertInfo(String title, String msg) {
        showAlert(Alert.AlertType.INFORMATION, title, msg);
    }
}