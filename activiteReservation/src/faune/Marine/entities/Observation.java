package faune.Marine.entities;

import java.time.LocalDate;

public class Observation {
    private int idObservation;
    private LocalDate dateObservation;
    private double temperature;
    private String meteo;
    private FauneMarine animal;

    public Observation() {}

    public Observation(LocalDate dateObservation, double temperature, String meteo, FauneMarine animal) {
        this.dateObservation = dateObservation;
        this.temperature = temperature;
        this.meteo = meteo;
        this.animal = animal;
    }

    public Observation(int idObservation, LocalDate dateObservation, double temperature, 
                       String meteo, FauneMarine animal) {
        this.idObservation = idObservation;
        this.dateObservation = dateObservation;
        this.temperature = temperature;
        this.meteo = meteo;
        this.animal = animal;
    }

    public int getIdObservation() { return idObservation; }
    public void setIdObservation(int idObservation) { this.idObservation = idObservation; }
    public LocalDate getDateObservation() { return dateObservation; }
    public void setDateObservation(LocalDate dateObservation) { this.dateObservation = dateObservation; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public String getMeteo() { return meteo; }
    public void setMeteo(String meteo) { this.meteo = meteo; }
    public FauneMarine getAnimal() { return animal; }
    public void setAnimal(FauneMarine animal) { this.animal = animal; }
    
    @Override
    public String toString() {
        return "Observation{id=" + idObservation + ", date=" + dateObservation + 
               ", meteo='" + meteo + "', animal=" + (animal != null ? animal.getEspece() : "null") + "}";
    }
}