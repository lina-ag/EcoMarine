package tn.edu.esprit.entities;

public class FAQ {
    private int id;
    private String question;
    private String reponse;
    private String categorie; // "horaire", "tarif", "condition", "activite"
    
    public FAQ() {}
    
    public FAQ(String question, String reponse, String categorie) {
        this.question = question;
        this.reponse = reponse;
        this.categorie = categorie;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    
    public String getReponse() { return reponse; }
    public void setReponse(String reponse) { this.reponse = reponse; }
    
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
}