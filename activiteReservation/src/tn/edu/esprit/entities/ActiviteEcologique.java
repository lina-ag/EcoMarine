package tn.edu.esprit.entities;

public class ActiviteEcologique {

    private String nom;
    private String description;
    private String date;
    private int capacite;
    private int idActivite;

    public int getIdActivite() { return idActivite; }
    public ActiviteEcologique() {}

    public ActiviteEcologique(String nom, String description, String date, int capacite) {
        this.nom = nom;
        this.description = description;
        this.date = date;
        this.capacite = capacite;
    }


    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
    public void setIdActivite(int idActivite) {
        this.idActivite= idActivite;
    }
}
