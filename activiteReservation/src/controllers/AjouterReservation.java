package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.edu.esprit.entities.Reservation;
import tn.edu.esprit.services.ServiceReservation;
import tn.edu.esprit.services.ServiceActivite;
import tn.edu.esprit.services.EmailService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AjouterReservation {

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfNombre_personnes;

    @FXML
    private DatePicker dpDate;

    @FXML
    private TextField tfIdActivite;

    private ServiceReservation service = new ServiceReservation();
    private ServiceActivite serviceActivite = new ServiceActivite();
    private EmailService emailService = new EmailService();
    @FXML
    public void initialize() {
        System.out.println("Fenêtre d'ajout de réservation ouverte");
        // Test de connexion email
        emailService.testerConnexion();
    }

    @FXML
    void ajouterReservation() {

        String nom = tfNom.getText().trim();
        String email = tfEmail.getText().trim();
        String nbStr = tfNombre_personnes.getText().trim();
        String idActStr = tfIdActivite.getText().trim();
        LocalDate date = dpDate.getValue(); 

        if (nom.isEmpty() || email.isEmpty() || nbStr.isEmpty() || date == null || idActStr.isEmpty()) {
            showError("Veuillez remplir tous les champs !");
            return;
        }

        if (!nom.matches("[a-zA-Z ]{3,}")) {
            showError("Le nom doit contenir au moins 3 lettres !");
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showError("Email invalide !");
            return;
        }

        int nb;
        try {
            nb = Integer.parseInt(nbStr);
            if (nb <= 0) {
                showError("Le nombre de personnes doit être > 0 !");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Nombre de personnes doit être un nombre !");
            return;
        }
        if (date.isBefore(LocalDate.now())) {
            showError("La date doit être future !");
            return;
        }

        int idAct;
        try {
            idAct = Integer.parseInt(idActStr);
        } catch (NumberFormatException e) {
            showError("ID activité doit être un nombre !");
            return;
        }
        
        // Vérifier si l'activité existe et récupérer son nom
        if (!serviceActivite.existeActivite(idAct)) {
            showError("❌ Cette activité n'existe pas !");
            return;
        }
        
        // Récupérer le nom de l'activité pour l'email
        String nomActivite = serviceActivite.getById(idAct).getNom();

        // Créer la réservation
        Reservation r = new Reservation(nom, email, nb, date.toString(), idAct);

        // Ajouter à la base de données
        service.ajouter(r);
        
        // ENVOI DE L'EMAIL DE CONFIRMATION
        try {
            String dateFormatee = date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH));
            emailService.envoyerConfirmationReservation(email, nom, nomActivite, dateFormatee, nb);
            showSuccess("✅ Réservation ajoutée avec succès !\n📧 Un email de confirmation a été envoyé à " + email);
        } catch (Exception e) {
            System.err.println("Erreur d'envoi d'email: " + e.getMessage());
            showSuccess("✅ Réservation ajoutée avec succès !\n⚠️ Mais l'email de confirmation n'a pas pu être envoyé.");
        }

        clearFields();
        
        // Fermer la fenêtre
        Stage stage = (Stage) tfNom.getScene().getWindow();
        stage.close();
    }

    private void clearFields() {
        tfNom.clear();
        tfEmail.clear();
        tfNombre_personnes.clear();
        dpDate.setValue(null); 
        tfIdActivite.clear();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Erreur");
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showSuccess(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Succès");
        a.setContentText(msg);
        a.showAndWait();
    }
}