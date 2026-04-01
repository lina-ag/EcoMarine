package model;

public class Biodiversite {
    private int idBiodiversite;
    private String espece;
    private String zone;
    private int nombre;
    private String dateObservation;

    public Biodiversite() {
    }

    public int getIdBiodiversite() {
        return idBiodiversite;
    }

    public void setIdBiodiversite(int idBiodiversite) {
        this.idBiodiversite = idBiodiversite;
    }

    public String getEspece() {
        return espece;
    }

    public void setEspece(String espece) {
        this.espece = espece;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public int getNombre() {
        return nombre;
    }

    public void setNombre(int nombre) {
        this.nombre = nombre;
    }

    public String getDateObservation() {
        return dateObservation;
    }

    public void setDateObservation(String dateObservation) {
        this.dateObservation = dateObservation;
    }
}