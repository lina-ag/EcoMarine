package tn.edu.esprit.entities;
import java.time.LocalDate;
import java.util.Objects;

public class User {
	private int idUtilisateur;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;
    private String role;
    private LocalDate dateNaissance;
    private String faceImage;
    private byte[] faceEncoding;

    // ----- Constructeurs -----
    public User() {}
    
    
    public User(int idUtilisateur, String nom, String prenom, String email, String motDePasse, String telephone,
			String role, LocalDate dateNaissance, String faceImage, byte[] faceEncoding) {
		super();
		this.idUtilisateur = idUtilisateur;
		this.nom = nom;
		this.prenom = prenom;
		this.email = email;
		this.motDePasse = motDePasse;
		this.telephone = telephone;
		this.role = role;
		this.dateNaissance = dateNaissance;
		this.faceImage = faceImage;
		this.faceEncoding = faceEncoding;
	}


	// ----- Getters & Setters -----

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
    public String getFaceImage() {
    	return faceImage; 
    }
    public void setFaceImage(String faceImage) {
    	this.faceImage = faceImage; 
    }
    
    public byte[] getFaceEncoding() {
    	return faceEncoding; 
    }
    public void setFaceEncoding(byte[] faceEncoding) {
    	this.faceEncoding = faceEncoding; 
    }

    // ----- Méthode utilitaire : vérification email -----
    public boolean emailValide() {
        return this.email != null && this.email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");
    }

    // ----- toString -----
    @Override
    public String toString() {
        return "Utilisateur{" +
                "idUtilisateur=" + idUtilisateur +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", role='" + role + '\'' +
                ", dateNaissance=" + dateNaissance +
                '}';
    }

    // ----- equals & hashCode -----
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return idUtilisateur == that.idUtilisateur;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUtilisateur);
    }
    public User(int id, String nom, String prenom, String email, String motDePasse, 
            String telephone, String role, LocalDate dateNaissance) {
    this.idUtilisateur = idUtilisateur;
    this.nom = nom;
    this.prenom = prenom;
    this.email = email;
    this.motDePasse = motDePasse;
    this.telephone = telephone;
    this.role = role;
    this.dateNaissance = dateNaissance;
}
}

