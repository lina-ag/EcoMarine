package model;

public class Dechet {
    private int idDechet;
    private String typeDechet;
    private double quantite;
    private int idZone;
    private String nomZone;

    public Dechet() {
    }

    public Dechet(int idDechet, String typeDechet, double quantite, int idZone, String nomZone) {
        this.idDechet = idDechet;
        this.typeDechet = typeDechet;
        this.quantite = quantite;
        this.idZone = idZone;
        this.nomZone = nomZone;
    }

    public int getIdDechet() {
        return idDechet;
    }

    public void setIdDechet(int idDechet) {
        this.idDechet = idDechet;
    }

    public String getTypeDechet() {
        return typeDechet;
    }

    public void setTypeDechet(String typeDechet) {
        this.typeDechet = typeDechet;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
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
}