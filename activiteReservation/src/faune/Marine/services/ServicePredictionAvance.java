package faune.Marine.services;

import faune.Marine.entities.PredictionEchouage;
import faune.Marine.services.ServiceMeteoAPI.MeteoData;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ServicePredictionAvance {
    
    // Facteurs de risque par espèce
    private static final Map<String, Double> FACTEUR_ESPECE = new HashMap<>();
    private static final Map<String, Double> FACTEUR_SAISON = new HashMap<>();
    
    static {
        FACTEUR_ESPECE.put("Dauphin", 1.3);
        FACTEUR_ESPECE.put("Baleine", 1.5);
        FACTEUR_ESPECE.put("Phoque", 1.1);
        FACTEUR_ESPECE.put("Tortue marine", 1.2);
        FACTEUR_ESPECE.put("Requin", 0.8);
        FACTEUR_ESPECE.put("Morse", 1.4);
        FACTEUR_ESPECE.put("Otarie", 1.2);
        
        FACTEUR_SAISON.put("hiver", 1.4);
        FACTEUR_SAISON.put("printemps", 1.2);
        FACTEUR_SAISON.put("été", 0.7);
        FACTEUR_SAISON.put("automne", 1.1);
    }
    
    /**
     * Génère une prédiction automatique basée sur les données météo et les facteurs de risque
     */
    public PredictionEchouage genererPrediction(String zone, String espece) {
        // 1. Récupérer les données météo en temps réel
        MeteoData meteo = ServiceMeteoAPI.getPrevisions(zone);
        
        // 2. Calculer le risque automatiquement
        int niveauRisque = calculerNiveauRisque(meteo, zone, espece);
        
        // 3. Générer des recommandations adaptées
        String recommandations = genererRecommandations(niveauRisque, zone, espece, meteo);
        
        // 4. Créer la prédiction
        PredictionEchouage prediction = new PredictionEchouage(
            LocalDate.now(),
            zone,
            niveauRisque,
            espece,
            meteo.getTemperatureEau(),
            meteo.getConditions(),
            recommandations
        );
        
        return prediction;
    }
    
    /**
     * Calcule le niveau de risque (1-10) selon un algorithme basé sur la recherche scientifique
     */
    private int calculerNiveauRisque(MeteoData meteo, String zone, String espece) {
        double risque = 5.0; // niveau de base
        
        // Facteur vent (plus le vent est fort, plus le risque augmente)
        if (meteo.getVitesseVent() > 50) risque += 2.5;
        else if (meteo.getVitesseVent() > 30) risque += 1.5;
        else if (meteo.getVitesseVent() > 20) risque += 0.8;
        
        // Facteur vagues
        if (meteo.getHauteurVagues() > 4) risque += 2.0;
        else if (meteo.getHauteurVagues() > 2.5) risque += 1.2;
        else if (meteo.getHauteurVagues() > 1.5) risque += 0.5;
        
        // Facteur espèce
        double facteurEspece = FACTEUR_ESPECE.getOrDefault(espece, 1.0);
        risque *= facteurEspece;
        
        // Facteur saison
        double facteurSaison = FACTEUR_SAISON.getOrDefault(meteo.getSaison(), 1.0);
        risque *= facteurSaison;
        
        // Facteur zone (zones sensibles)
        if (zone.equals("Nord") || zone.equals("Ouest")) risque += 1.0; // zones plus exposées
        
        // Conditions météo extrêmes
        if (meteo.getConditions().contains("🌪️") || meteo.getConditions().contains("🌊")) {
            risque += 1.5;
        }
        
        // Arrondir et limiter entre 1 et 10
        int risqueFinal = (int) Math.round(risque);
        return Math.max(1, Math.min(10, risqueFinal));
    }
    
    /**
     * Génère des recommandations personnalisées
     */
    private String genererRecommandations(int risque, String zone, String espece, MeteoData meteo) {
        StringBuilder reco = new StringBuilder();
        
        if (risque >= 8) {
            reco.append("🔴 ALERTE MAXIMALE - ");
            reco.append("Équipes de secours en alerte. ");
            reco.append("Surveillance renforcée nécessaire. ");
            reco.append("Considérer l'activation du plan d'urgence.\n");
        } else if (risque >= 5) {
            reco.append("🟠 RISQUE MODÉRÉ - ");
            reco.append("Surveillance recommandée. ");
            reco.append("Préparer les équipes d'intervention.\n");
        } else {
            reco.append("🟢 RISQUE FAIBLE - ");
            reco.append("Surveillance normale.\n");
        }
        
        reco.append("\n📊 ANALYSE :\n");
        reco.append("- Vent: ").append(String.format("%.1f", meteo.getVitesseVent())).append(" km/h\n");
        reco.append("- Vagues: ").append(String.format("%.1f", meteo.getHauteurVagues())).append(" m\n");
        reco.append("- Saison: ").append(meteo.getSaison()).append("\n");
        reco.append("- Zone: ").append(zone).append("\n");
        
        reco.append("\n💡 RECOMMANDATIONS SPÉCIFIQUES :\n");
        if (risque >= 7 && zone.contains("Récif")) {
            reco.append("- Surveiller particulièrement les zones récifales\n");
        }
        if (espece.equals("Baleine") && meteo.getSaison().equals("hiver")) {
            reco.append("- Période de migration, vigilance accrue\n");
        }
        if (meteo.getHauteurVagues() > 3) {
            reco.append("- Conditions de mer dangereuses, restreindre la navigation\n");
        }
        
        return reco.toString();
    }
}