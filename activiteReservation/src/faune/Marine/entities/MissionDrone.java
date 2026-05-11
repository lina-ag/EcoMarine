package faune.Marine.entities;

import java.time.LocalDate;
import java.time.LocalTime;

public class MissionDrone {
    private int idMission;
    private LocalDate dateMission;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String zoneSurvolee;
    private double distanceParcourue;
    private int altitudeVol;
    private String conditionsVol;
    private String observations;
    
    public MissionDrone() {}
    
    public MissionDrone(LocalDate dateMission, LocalTime heureDebut, LocalTime heureFin,
                        String zoneSurvolee, double distanceParcourue, int altitudeVol,
                        String conditionsVol, String observations) {
        this.dateMission = dateMission;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.zoneSurvolee = zoneSurvolee;
        this.distanceParcourue = distanceParcourue;
        this.altitudeVol = altitudeVol;
        this.conditionsVol = conditionsVol;
        this.observations = observations;
    }
    
    // Getters et Setters
    public int getIdMission() { return idMission; }
    public void setIdMission(int idMission) { this.idMission = idMission; }
    
    public LocalDate getDateMission() { return dateMission; }
    public void setDateMission(LocalDate dateMission) { this.dateMission = dateMission; }
    
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    
    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }
    
    public String getZoneSurvolee() { return zoneSurvolee; }
    public void setZoneSurvolee(String zoneSurvolee) { this.zoneSurvolee = zoneSurvolee; }
    
    public double getDistanceParcourue() { return distanceParcourue; }
    public void setDistanceParcourue(double distanceParcourue) { this.distanceParcourue = distanceParcourue; }
    
    public int getAltitudeVol() { return altitudeVol; }
    public void setAltitudeVol(int altitudeVol) { this.altitudeVol = altitudeVol; }
    
    public String getConditionsVol() { return conditionsVol; }
    public void setConditionsVol(String conditionsVol) { this.conditionsVol = conditionsVol; }
    
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    
    @Override
    public String toString() {
        return "Mission #" + idMission + " - " + dateMission + " - " + zoneSurvolee;
    }
}