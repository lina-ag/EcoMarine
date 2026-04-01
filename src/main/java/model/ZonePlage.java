package model;

public class ZonePlage {
    private int idZone;
    private String nomZone;
    private String localisation;
    private String statut;

    public ZonePlage() {
    }

    public ZonePlage(int idZone, String nomZone, String localisation, String statut) {
        this.idZone = idZone;
        this.nomZone = nomZone;
        this.localisation = localisation;
        this.statut = statut;
    }

    public int getIdZone() {
        return idZone;
    }

    public void setIdZone(int idZone) {
        this.idZone = idZone;
    }

    public String getNomZone() {
        return nomZone;
    }

    public void setNomZone(String nomZone) {
        this.nomZone = nomZone;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}