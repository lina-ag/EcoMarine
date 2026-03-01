package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.edu.esprit.entities.Reservation;
import tn.edu.esprit.services.ServiceReservation;

public class ModifierReservation {

    @FXML
    private TextField idField;

    @FXML
    private TextField nomField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField nombre_personnesField;

    @FXML
    private TextField dateField;

    @FXML
    private TextField idActiviteField;

    private ServiceReservation service = new ServiceReservation();
    private Reservation reservationActuelle;

    public void setReservation(Reservation r) {
        this.reservationActuelle = r;

        idField.setText(String.valueOf(r.getId()));
        nomField.setText(r.getNom());
        emailField.setText(r.getEmail());
        nombre_personnesField.setText(String.valueOf(r.getNombre_personnes()));
        dateField.setText(r.getDate_reservation());
        idActiviteField.setText(String.valueOf(r.getIdActivite()));
    }

    @FXML
    void modifierReservation() {
        if (reservationActuelle != null) {
            reservationActuelle.setNom(nomField.getText());
            reservationActuelle.setEmail(emailField.getText());
            reservationActuelle.setNombre_personnes(Integer.parseInt(nombre_personnesField.getText()));
            reservationActuelle.setDate_reservation(dateField.getText());
            reservationActuelle.setIdActivite(Integer.parseInt(idActiviteField.getText()));

            service.modifier(reservationActuelle);

            Stage stage = (Stage) idField.getScene().getWindow();
            stage.close();
        }
    }
    @FXML
    void annuler() {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }
}