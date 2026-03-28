package app;

public class Volontaire {
    private final int idVolontaire;
    private final String nom;
    private final String contact;
    private final int idAction;

    public Volontaire(int idVolontaire, String nom, String contact, int idAction) {
        this.idVolontaire = idVolontaire;
        this.nom = nom;
        this.contact = contact;
        this.idAction = idAction;
    }

    public int getIdVolontaire() { return idVolontaire; }
    public String getNom() { return nom; }
    public String getContact() { return contact; }
    public int getIdAction() { return idAction; }
}
