package tn.edu.esprit.entities;

public class Reservation {
    private int id;
    private String nom;
    private String email;
    private int nombre_personnes;
    private String date_reservation;
    private int idActivite; 

    public Reservation() {}

    public Reservation(String nom, String email, int nombre_Personnes, String date_reservation, int idActivite) {  
        this.nom = nom;
        this.email = email;
        this.nombre_personnes = nombre_Personnes;
        this.date_reservation = date_reservation;
        this.idActivite = idActivite; 
    }

    // Getters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getEmail() { return email; }
    public int getNombre_personnes() { return nombre_personnes; }
    public String getDate_reservation() { return date_reservation; }
    public int getIdActivite() { return idActivite; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setEmail(String email) { this.email = email; }
    public void setNombre_personnes(int nombre_personnes) { this.nombre_personnes = nombre_personnes; }
    public void setDate_reservation(String date_reservation) { this.date_reservation = date_reservation; }
    public void setIdActivite(int idActivite) { this.idActivite = idActivite; } 
}