package faune.Marine.services;

import faune.Marine.entities.DetectionDrone;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IAImageAnalyzer {
    
    private static final Random random = new Random();
    
    // Base de données d'espèces marines que l'IA peut reconnaître
    private static final String[] ESPECES = {
        "Dauphin", "Baleine", "Phoque", "Tortue marine", 
        "Requin", "Morse", "Otarie", "Raie manta",
        "Poisson-lune", "Thon", "Espadon", "Méduse"
    };
    
    // Comportements possibles
    private static final String[] COMPORTEMENTS = {
        "Nage en surface", "Plongée", "Repos", "Alimentation",
        "Migration", "Jeu", "Interaction sociale", "Reproduction"
    };
    
    // Niveaux de confiance
    private static final String[] CONFIANCE = {
        "Faible", "Moyenne", "Haute", "Certaine"
    };
    
    /**
     * Analyse une image et retourne les détections trouvées
     */
    public List<DetectionDrone> analyserImage(File imageFile, int idMission) {
        List<DetectionDrone> detections = new ArrayList<>();
        
        // Simuler un délai de traitement (comme une vraie IA)
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Générer un nombre aléatoire de détections (1 à 5)
        int nombreDetections = random.nextInt(5) + 1;
        
        for (int i = 0; i < nombreDetections; i++) {
            // Simuler les coordonnées GPS (dans une zone aléatoire)
            double lat = 36.5 + (random.nextDouble() * 2); // 36.5-38.5
            double lon = -5.5 + (random.nextDouble() * 3); // -5.5 à -2.5
            
            // Générer une détection
            DetectionDrone detection = new DetectionDrone(
                idMission,
                ESPECES[random.nextInt(ESPECES.length)],
                random.nextInt(15) + 1, // 1-15 individus
                lat,
                lon,
                COMPORTEMENTS[random.nextInt(COMPORTEMENTS.length)],
                CONFIANCE[random.nextInt(CONFIANCE.length)],
                imageFile.getName(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
            
            detections.add(detection);
        }
        
        return detections;
    }
    
    /**
     * Analyse un lot d'images
     */
    public List<DetectionDrone> analyserImages(List<File> images, int idMission) {
        List<DetectionDrone> toutesDetections = new ArrayList<>();
        
        for (File image : images) {
            List<DetectionDrone> detections = analyserImage(image, idMission);
            toutesDetections.addAll(detections);
        }
        
        return toutesDetections;
    }
    
    /**
     * Obtient des statistiques sur les détections
     */
    public String getStatistiques(List<DetectionDrone> detections) {
        if (detections.isEmpty()) {
            return "Aucune détection";
        }
        
        long totalIndividus = detections.stream()
            .mapToInt(DetectionDrone::getNombreIndividus)
            .sum();
        
        long especesUniques = detections.stream()
            .map(DetectionDrone::getEspece)
            .distinct()
            .count();
        
        long hauteConfiance = detections.stream()
            .filter(d -> d.getConfianceIA().equals("Haute") || d.getConfianceIA().equals("Certaine"))
            .count();
        
        return String.format(
            "📊 Statistiques IA:\n" +
            "- %d détections\n" +
            "- %d individus comptés\n" +
            "- %d espèces uniques\n" +
            "- %d détections haute confiance",
            detections.size(), totalIndividus, especesUniques, hauteConfiance
        );
    }
}