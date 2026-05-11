package faune.Marine.entities;

public class FauneMarine {
    private int idAnimal;
    private String espece;
    private String etat;
    private String description;

    public FauneMarine() {}

    public FauneMarine(String espece, String etat, String description) {
        this.espece = espece;
        this.etat = etat;
        this.description = description;
    }

    public FauneMarine(int idAnimal, String espece, String etat, String description) {
        this.idAnimal = idAnimal;
        this.espece = espece;
        this.etat = etat;
        this.description = description;
    }

    public int getIdAnimal() { return idAnimal; }
    public void setIdAnimal(int idAnimal) { this.idAnimal = idAnimal; }
    public String getEspece() { return espece; }
    public void setEspece(String espece) { this.espece = espece; }
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return "FauneMarine{id=" + idAnimal + ", espece='" + espece + "', etat='" + etat + "'}";
    }
}