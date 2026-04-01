package faune.Marine.entities;

public class DetectionDrone {
    private int idDetection;
    private int idMission;
    private String espece;
    private int nombreIndividus;
    private double latitude;
    private double longitude;
    private String comportement;
    private String confianceIA;
    private String imagePath;
    private String timestamp;
    
    public DetectionDrone() {}
    
    public DetectionDrone(int idMission, String espece, int nombreIndividus,
                          double latitude, double longitude, String comportement,
                          String confianceIA, String imagePath, String timestamp) {
        this.idMission = idMission;
        this.espece = espece;
        this.nombreIndividus = nombreIndividus;
        this.latitude = latitude;
        this.longitude = longitude;
        this.comportement = comportement;
        this.confianceIA = confianceIA;
        this.imagePath = imagePath;
        this.timestamp = timestamp;
    }
    
    // Getters et Setters
    public int getIdDetection() { return idDetection; }
    public void setIdDetection(int idDetection) { this.idDetection = idDetection; }
    
    public int getIdMission() { return idMission; }
    public void setIdMission(int idMission) { this.idMission = idMission; }
    
    public String getEspece() { return espece; }
    public void setEspece(String espece) { this.espece = espece; }
    
    public int getNombreIndividus() { return nombreIndividus; }
    public void setNombreIndividus(int nombreIndividus) { this.nombreIndividus = nombreIndividus; }
    
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    
    public String getComportement() { return comportement; }
    public void setComportement(String comportement) { this.comportement = comportement; }
    
    public String getConfianceIA() { return confianceIA; }
    public void setConfianceIA(String confianceIA) { this.confianceIA = confianceIA; }
    
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    @Override
    public String toString() {
        return "🔍 " + espece + " (" + nombreIndividus + ") - Confiance: " + confianceIA;
    }
}