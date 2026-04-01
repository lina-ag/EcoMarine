package controllers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import tn.edu.esprit.entities.ActiviteEcologique;
import tn.edu.esprit.services.ServiceActivite;

public class AjouterActivite {

    @FXML 
    private TextField tfNom;
    
    @FXML 
    private TextArea tfDescription;
    
    @FXML 
    private TextField tfCapacite;
    
    @FXML 
    private DatePicker datePicker;
    
    @FXML 
    private DatePicker datePickerFin;
    
    @FXML 
    private CheckBox cbRecurrence;
    
    @FXML 
    private CheckBox cbLun, cbMar, cbMer, cbJeu, cbVen, cbSam, cbDim;
    
    @FXML 
    private VBox panneauRecurrence;
    
    @FXML 
    private Label lblApercu;
    
    @FXML 
    private Button btnBack;

    private final ServiceActivite service = new ServiceActivite();

    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
        datePickerFin.setValue(LocalDate.now().plusMonths(1));

        for (CheckBox cb : getJoursChecks()) {
            cb.setOnAction(e -> mettreAJourApercu());
        }
        
        datePickerFin.valueProperty().addListener((o, ov, nv) -> mettreAJourApercu());

        tfCapacite.textProperty().addListener((obs, old, nv) -> {
            if (!nv.matches("\\d*")) {
                tfCapacite.setText(nv.replaceAll("[^\\d]", ""));
            }
        });
        
        // Initialiser le panneau de récurrence caché
        panneauRecurrence.setVisible(false);
        panneauRecurrence.setManaged(false);
    }

    @FXML
    private void toggleRecurrence() {
        boolean actif = cbRecurrence.isSelected();
        panneauRecurrence.setVisible(actif);
        panneauRecurrence.setManaged(actif);
        if (actif) mettreAJourApercu();
    }

    private void mettreAJourApercu() {
        List<String> jours = getJoursSelectionnes();
        if (jours.isEmpty()) {
            lblApercu.setText("Aucun jour sélectionné");
            return;
        }
        
        LocalDate fin = datePickerFin.getValue();
        LocalDate debut = datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now();

        long nb = compterOccurrences(debut, fin, jours);
        lblApercu.setText(String.join(", ", jours) + " — " + nb + " occurrence(s)");
    }

    private long compterOccurrences(LocalDate debut, LocalDate fin, List<String> jours) {
        if (fin == null || fin.isBefore(debut)) return 0;
        long count = 0;
        LocalDate d = debut;
        while (!d.isAfter(fin)) {
            if (jours.contains(nomJour(d.getDayOfWeek()))) count++;
            d = d.plusDays(1);
        }
        return count;
    }

    @FXML
    private void ajouterActivite() {
        try {
            // Validation des champs
            if (tfNom.getText() == null || tfNom.getText().trim().isEmpty()) {
                showAlert("Erreur", "Le nom est requis");
                return;
            }
            
            if (datePicker.getValue() == null) {
                showAlert("Erreur", "La date est requise");
                return;
            }
            
            if (tfCapacite.getText() == null || tfCapacite.getText().trim().isEmpty()) {
                showAlert("Erreur", "La capacité est requise");
                return;
            }

            String nom = tfNom.getText().trim();
            String description = tfDescription.getText().trim();
            LocalDate debut = datePicker.getValue();
            int capacite = Integer.parseInt(tfCapacite.getText().trim());

            // Gestion de la récurrence
            if (cbRecurrence.isSelected()) {
                List<String> jours = getJoursSelectionnes();
                if (jours.isEmpty()) {
                    showAlert("Erreur", "Sélectionnez au moins un jour de récurrence");
                    return;
                }
                
                LocalDate fin = datePickerFin.getValue();
                if (fin == null || fin.isBefore(debut)) {
                    showAlert("Erreur", "La date de fin doit être après la date de début");
                    return;
                }

                int nbCreees = 0;
                LocalDate d = debut;
                while (!d.isAfter(fin)) {
                    if (jours.contains(nomJour(d.getDayOfWeek()))) {
                        ActiviteEcologique a = new ActiviteEcologique(
                            nom, description,
                            d.format(DateTimeFormatter.ISO_LOCAL_DATE),
                            capacite
                        );
                        service.ajouter(a);
                        nbCreees++;
                    }
                    d = d.plusDays(1);
                }

                showAlert("Succès", nbCreees + " activité(s) créée(s) avec récurrence !\n"
                    + "Jours : " + String.join(", ", jours));

            } else {
                // Activité unique
                ActiviteEcologique a = new ActiviteEcologique(
                    nom, description,
                    debut.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    capacite
                );
                service.ajouter(a);
                showAlert("Succès", "Activité ajoutée avec succès !");
            }

            // Fermer la fenêtre
            Stage stage = (Stage) tfNom.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert("Erreur", "La capacité doit être un nombre valide");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur : " + e.getMessage());
        }
    }

    private List<CheckBox> getJoursChecks() {
        return List.of(cbLun, cbMar, cbMer, cbJeu, cbVen, cbSam, cbDim);
    }

    private List<String> getJoursSelectionnes() {
        List<String> jours = new ArrayList<>();
        if (cbLun.isSelected()) jours.add("Lun");
        if (cbMar.isSelected()) jours.add("Mar");
        if (cbMer.isSelected()) jours.add("Mer");
        if (cbJeu.isSelected()) jours.add("Jeu");
        if (cbVen.isSelected()) jours.add("Ven");
        if (cbSam.isSelected()) jours.add("Sam");
        if (cbDim.isSelected()) jours.add("Dim");
        return jours;
    }

    private String nomJour(DayOfWeek d) {
        return switch (d) {
            case MONDAY    -> "Lun";
            case TUESDAY   -> "Mar";
            case WEDNESDAY -> "Mer";
            case THURSDAY  -> "Jeu";
            case FRIDAY    -> "Ven";
            case SATURDAY  -> "Sam";
            case SUNDAY    -> "Dim";
        };
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    private void handleBack() {
        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }
}