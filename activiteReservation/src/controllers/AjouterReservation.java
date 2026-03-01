package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import tn.edu.esprit.entities.Reservation;
import tn.edu.esprit.services.ServiceReservation;
import tn.edu.esprit.services.ServiceActivite;

import java.time.LocalDate;

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
        if (!serviceActivite.existeActivite(idAct)) {
            showError("❌ Cette activité n'existe pas !");
            return;
        }

        Reservation r = new Reservation(nom, email, nb, date.toString(), idAct);

        service.ajouter(r);

        showSuccess("Réservation ajoutée avec succès !");

        clearFields();
    }

    private void clearFields() {
        tfNom.clear();
        tfEmail.clear();
        tfNombre_personnes.clear();
        dpDate.setValue(null); 
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