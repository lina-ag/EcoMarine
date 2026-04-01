package model;

public class Volontaire {
    private int idVolontaire;
    private String nom;
    private String contact;
    private int idAction;
    private String actionNom;

    public Volontaire() {
    }

    public int getIdVolontaire() {
        return idVolontaire;
    }

    public void setIdVolontaire(int idVolontaire) {
        this.idVolontaire = idVolontaire;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getIdAction() {
        return idAction;
    }

    public void setIdAction(int idAction) {
        this.idAction = idAction;
    }

    public String getActionNom() {
        return actionNom;
    }

    public void setActionNom(String actionNom) {
        this.actionNom = actionNom;
    }
}