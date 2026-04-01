package faune.Marine.services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ServiceMeteoAPI {
    
    private static final Map<String, Double> TEMPERATURES_MOYENNES = new HashMap<>();
    private static final Map<String, String> CONDITIONS_TYPES = new HashMap<>();
    private static final Random random = new Random();
    
    static {
        // Initialisation des données par zone
        TEMPERATURES_MOYENNES.put("Nord", 14.5);
        TEMPERATURES_MOYENNES.put("Sud", 22.3);
        TEMPERATURES_MOYENNES.put("Est", 18.7);
        TEMPERATURES_MOYENNES.put("Ouest", 16.2);
        TEMPERATURES_MOYENNES.put("Centre", 20.1);
        TEMPERATURES_MOYENNES.put("Lagons", 25.4);
        TEMPERATURES_MOYENNES.put("Récifs", 24.8);
        TEMPERATURES_MOYENNES.put("Haute mer", 15.3);
        
        CONDITIONS_TYPES.put("Nord", "🌊 Fortes vagues");
        CONDITIONS_TYPES.put("Sud", "☀️ Ensoleillé");
        CONDITIONS_TYPES.put("Est", "⛅ Nuageux");
        CONDITIONS_TYPES.put("Ouest", "🌧️ Pluvieux");
        CONDITIONS_TYPES.put("Centre", "☀️ Ensoleillé");
        CONDITIONS_TYPES.put("Lagons", "☀️ Ensoleillé");
        CONDITIONS_TYPES.put("Récifs", "☀️ Ensoleillé");
        CONDITIONS_TYPES.put("Haute mer", "🌊 Fortes vagues");
    }
    
    public static class MeteoData {
        private double temperatureEau;
        private String conditions;
        private double vitesseVent;
        private double hauteurVagues;
        private String saison;
        
        public MeteoData(double temperatureEau, String conditions, double vitesseVent, double hauteurVagues) {
            this.temperatureEau = temperatureEau;
            this.conditions = conditions;
            this.vitesseVent = vitesseVent;
            this.hauteurVagues = hauteurVagues;
            
            // Déterminer la saison
            int mois = LocalDate.now().getMonthValue();
            if (mois >= 3 && mois <= 5) this.saison = "printemps";
            else if (mois >= 6 && mois <= 8) this.saison = "été";
            else if (mois >= 9 && mois <= 11) this.saison = "automne";
            else this.saison = "hiver";
        }
        
        public double getTemperatureEau() { return temperatureEau; }
        public String getConditions() { return conditions; }
        public double getVitesseVent() { return vitesseVent; }
        public double getHauteurVagues() { return hauteurVagues; }
        public String getSaison() { return saison; }
    }
    
    public static MeteoData getPrevisions(String zone) {
        double baseTemp = TEMPERATURES_MOYENNES.getOrDefault(zone, 18.0);
        String baseCondition = CONDITIONS_TYPES.getOrDefault(zone, "⛅ Nuageux");
        
        // Ajouter des variations aléatoires pour simuler le temps réel
        double variationTemp = (random.nextDouble() * 6) - 3; // -3 à +3 degrés
        double temperature = baseTemp + variationTemp;
        
        double vitesseVent = 10 + random.nextDouble() * 40; // 10 à 50 km/h
        double hauteurVagues = 0.5 + random.nextDouble() * 4.5; // 0.5 à 5 mètres
        
        return new MeteoData(temperature, baseCondition, vitesseVent, hauteurVagues);
    }
}