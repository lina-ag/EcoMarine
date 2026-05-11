package faune.Marine.entities;

import java.time.LocalDate;

public class PredictionEchouage {
    private int idPrediction;
    private LocalDate datePrediction;
    private String zone; // "Nord", "Sud", "Est", "Ouest"
    private int niveauRisque; // 1-10
    private String especeConcernee;
    private double temperatureEau;
    private String conditionsMeteo;
    private String recommandations;
    
    public PredictionEchouage() {}
    
    public PredictionEchouage(LocalDate datePrediction, String zone, int niveauRisque, 
                              String especeConcernee, double temperatureEau, 
                              String conditionsMeteo, String recommandations) {
        this.datePrediction = datePrediction;
        this.zone = zone;
        this.niveauRisque = niveauRisque;
        this.especeConcernee = especeConcernee;
        this.temperatureEau = temperatureEau;
        this.conditionsMeteo = conditionsMeteo;
        this.recommandations = recommandations;
    }
    
    // Getters et Setters
    public int getIdPrediction() { return idPrediction; }
    public void setIdPrediction(int idPrediction) { this.idPrediction = idPrediction; }
    
    public LocalDate getDatePrediction() { return datePrediction; }
    public void setDatePrediction(LocalDate datePrediction) { this.datePrediction = datePrediction; }
    
    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }
    
    public int getNiveauRisque() { return niveauRisque; }
    public void setNiveauRisque(int niveauRisque) { this.niveauRisque = niveauRisque; }
    
    public String getEspeceConcernee() { return especeConcernee; }
    public void setEspeceConcernee(String especeConcernee) { this.especeConcernee = especeConcernee; }
    
    public double getTemperatureEau() { return temperatureEau; }
    public void setTemperatureEau(double temperatureEau) { this.temperatureEau = temperatureEau; }
    
    public String getConditionsMeteo() { return conditionsMeteo; }
    public void setConditionsMeteo(String conditionsMeteo) { this.conditionsMeteo = conditionsMeteo; }
    
    public String getRecommandations() { return recommandations; }
    public void setRecommandations(String recommandations) { this.recommandations = recommandations; }
    
    @Override
    public String toString() {
        return "🔮 Prédiction - Zone: " + zone + ", Risque: " + niveauRisque + "/10";
    }
}