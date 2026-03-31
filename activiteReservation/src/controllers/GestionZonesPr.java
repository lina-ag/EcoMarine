package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import tn.edu.esprit.services.ServiceZoneP;
import tn.edu.esprit.services.ServiceSurv;
import javafx.scene.chart.PieChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;




public class GestionZonesPr {
	@FXML
	private Label lblNbZones;

	@FXML
	private Label lblNbSurv;

	private ServiceZoneP serviceZone = new ServiceZoneP();
	private ServiceSurv serviceSurv = new ServiceSurv();
	
	public void updateStats() {
	    int nbZones = serviceZone.getAll(null).size();
	    int nbSurv = serviceSurv.getAll(null).size();

	    lblNbZones.setText(String.valueOf(nbZones));
	    lblNbSurv.setText(String.valueOf(nbSurv));
	    loadChart();
	    loadEcoScore();
	    loadLastActivity();
	}
	
	@FXML
	private PieChart pieChart;
	
	@FXML
	private Label lblScoreEco;
	
	@FXML
	private Label lblDerniereZone;

	@FXML
	private Label lblDerniereSurv;
	
	
	@FXML
	public void initialize() {

	    int nbZones = serviceZone.getAll(null).size();
	    int nbSurv = serviceSurv.getAll(null).size();

	    lblNbZones.setText(String.valueOf(nbZones));
	    lblNbSurv.setText(String.valueOf(nbSurv));
	    
	    updateStats();
	    loadChart();
	    loadEcoScore();
	    loadLastActivity();
	}
	
	 @FXML
	    private void ouvrirAjouter() {
	        ouvrirFenetre("/AjouterZone.fxml", "Ajouter Zone");
	    }

	    @FXML
	    private void ouvrirAfficher() {
	        ouvrirFenetre("/AfficherZones.fxml", "Liste des Zones");
	    }
	    @FXML
	    private void ouvrirSurveillance() {
	        ouvrirFenetre("/AfficherSurveillance.fxml", "Liste des Zones");
	    }
	    
	    @FXML
	    private void ouvrirAjouterSurv() {
	        ouvrirFenetre("/AjouterSurveillance.fxml", "Liste des Zones");
	    }

	    private void ouvrirFenetre(String chemin, String titre) {
	        try {
	        	FXMLLoader loader = new FXMLLoader(getClass().getResource(chemin));
	            Parent root = loader.load();
	            
	            // Créer le nouveau stage
	            Stage newStage = new Stage();
	            newStage.setTitle(titre);
	            newStage.setScene(new Scene(root));
	            
	            // Récupérer et fermer la fenêtre actuelle
	            Stage currentStage = (Stage) lblNbZones.getScene().getWindow();
	            currentStage.close();
	            
	            // Afficher la nouvelle fenêtre
	            newStage.show();
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    
	    public void loadChart() {

	        int menacée = 0;
	        int active = 0;
	        int restauration = 0;
	        int inactive = 0;

	        for (var zone : serviceZone.getAll(null)) {

	            switch (zone.getStatut()) {

	                case "Menacée":
	                    menacée++;
	                    break;

	                case "Active":
	                    active++;
	                    break;

	                case "En restauration":
	                    restauration++;
	                    break;

	                case "Inactive":
	                    inactive++;
	                    break;
	            }
	        }

	        ObservableList<PieChart.Data> data =
	                FXCollections.observableArrayList(
	                		new PieChart.Data("Menacée : " + menacée, menacée),
	                        new PieChart.Data("Active : " + active, active),
	                        new PieChart.Data("En restauration : " + restauration, restauration),
	                        new PieChart.Data("Inactive : " + inactive, inactive)
	                );

	        pieChart.setData(data);
	        pieChart.setLabelsVisible(true);
	        pieChart.setLegendVisible(true);
	    }
	    
	    
	    public void loadEcoScore() {

	        int menacée = 0;
	        int active = 0;
	        int restauration = 0;
	        int inactive = 0;

	        for (var zone : serviceZone.getAll(null)) {

	            String statut = zone.getStatut().toLowerCase().trim();

	            if (statut.contains("menac")) {
	                menacée++;
	            } else if (statut.contains("active")) {
	                active++;
	            } else if (statut.contains("restauration")) {
	                restauration++;
	            } else if (statut.contains("inactive")) {
	                inactive++;
	            }
	        }

	        int total = menacée + active + restauration + inactive;

	        if (total == 0) {
	            lblScoreEco.setText("0%");
	            return;
	        }

	        int score = ((active + restauration) * 100) / total;

	        lblScoreEco.setText(score + "%");

	        
	        if (score >= 70) {
	            lblScoreEco.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #00ff88;");
	        } else if (score >= 40) {
	            lblScoreEco.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #ffaa00;");
	        } else {
	            lblScoreEco.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #ff4444;");
	        }
	    }
	    
	    public void loadLastActivity() {

	        var zones = serviceZone.getAll(null);
	        var surveillances = serviceSurv.getAll(null);

	        if (!zones.isEmpty()) {
	            var lastZone = zones.get(zones.size() - 1);
	            lblDerniereZone.setText(" Dernière zone ajoutée : " 
	                                    + lastZone.getNomZone());
	        } else {
	            lblDerniereZone.setText(" Aucune zone ajoutée");
	        }

	        if (!surveillances.isEmpty()) {
	            var lastSurv = surveillances.get(surveillances.size() - 1);
	            lblDerniereSurv.setText(" Dernière surveillance Ajoutée : " 
	                                    + lastSurv.getDateSurveillance());
	        } else {
	            lblDerniereSurv.setText(" Aucune surveillance");
	        }
	    }
	    
	    @FXML
	    private void ouvrirAccueil() {
	    	ouvrirFenetre("/acceuil.fxml", "Ajouter une activité");
	    }
	    
	    @FXML
	    private StackPane centerPane;
	    
	    @FXML
	    void ouvrirCarte() {
	        try {
	            Parent root = FXMLLoader.load(getClass().getResource("/CarteZones.fxml"));
	            centerPane.getChildren().setAll(root);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    
	    @FXML
	    private void ouvrirRechercheVocale() {
	        ouvrirFenetre("/RechercheVocale.fxml", "Recherche Vocale");
	    }

}
