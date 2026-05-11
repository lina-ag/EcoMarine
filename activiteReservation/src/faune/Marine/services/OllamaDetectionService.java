package faune.Marine.services;

import faune.Marine.entities.DetectionDrone;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OllamaDetectionService {
    
    private final OllamaService ollamaService;
    
    public OllamaDetectionService() {
        this.ollamaService = new OllamaService();
    }
    
    /**
     * Analyse une image et retourne une détection structurée avec un rapport clair
     */
    public DetectionResult analyserImage(File imageFile, int idMission, String modele) {
        try {
            // Prompt spécialisé pour la faune marine
            String prompt = "Analyse cette image de faune marine en français. Réponds de façon structurée avec :\n" +
                           "1. Les espèces présentes (nom commun)\n" +
                           "2. Le nombre d'individus\n" +
                           "3. Leur comportement\n" +
                           "4. Leur état de santé\n" +
                           "5. L'environnement";
            
            // Appeler Ollama
            String reponse = ollamaService.analyserImage(imageFile, prompt, modele);
            System.out.println("📥 Réponse brute d'Ollama: " + reponse.substring(0, Math.min(200, reponse.length())) + "...");
            
            // Extraire le texte d'analyse de la réponse JSON (sans Gson)
            String texteAnalyse = extraireTexteAnalyse(reponse);
            
            // Extraire les informations
            DetectionDrone detection = extraireDetection(texteAnalyse, idMission, imageFile.getName());
            
            // Générer un rapport clair et lisible
            String rapport = genererRapportClair(texteAnalyse, detection, imageFile.getName());
            
            return new DetectionResult(detection, rapport);
            
        } catch (Exception e) {
            e.printStackTrace();
            return new DetectionResult(null, "❌ Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Extrait le champ "response" du JSON sans utiliser Gson
     */
    private String extraireTexteAnalyse(String jsonResponse) {
        try {
            // Chercher le champ "response"
            String key = "\"response\":\"";
            int startIndex = jsonResponse.indexOf(key);
            
            if (startIndex == -1) {
                return jsonResponse; // Pas de champ response, retourner la réponse brute
            }
            
            startIndex += key.length();
            
            // Trouver la fin de la chaîne (en gérant les caractères échappés)
            StringBuilder result = new StringBuilder();
            boolean inEscape = false;
            
            for (int i = startIndex; i < jsonResponse.length(); i++) {
                char c = jsonResponse.charAt(i);
                
                if (inEscape) {
                    result.append(c);
                    inEscape = false;
                } else if (c == '\\') {
                    inEscape = true;
                } else if (c == '"') {
                    // Fin de la chaîne
                    break;
                } else {
                    result.append(c);
                }
            }
            
            return result.toString().replace("\\n", "\n").replace("\\t", "\t");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur extraction JSON: " + e.getMessage());
            return jsonResponse;
        }
    }
    
    /**
     * Extrait les informations de la réponse texte
     */
    private DetectionDrone extraireDetection(String texte, int idMission, String imageName) {
        // 1. Extraire l'espèce
        String espece = extraireEspece(texte);
        
        // 2. Extraire le nombre
        int nombre = extraireNombre(texte);
        
        // 3. Extraire le comportement
        String comportement = extraireComportement(texte);
        
        // 4. Extraire l'état de santé
        String confiance = extraireSante(texte);
        
        // Coordonnées simulées (à remplacer par des données GPS réelles)
        double lat = 36.5 + Math.random() * 2;
        double lon = -5.5 + Math.random() * 3;
        
        return new DetectionDrone(
            idMission,
            espece,
            nombre,
            lat,
            lon,
            comportement,
            confiance,
            imageName,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }
    
    /**
     * Extrait l'espèce de la réponse
     */
    private String extraireEspece(String texte) {
        // Chercher des mots-clés d'espèces marines
        String[] especesConnues = {
            "baleine", "dauphin", "phoque", "tortue", "requin", "poisson", 
            "whale", "dolphin", "seal", "turtle", "shark", "fish",
            "orca", "épaulard", "morse", "otarie", "lion de mer"
        };
        
        for (String espece : especesConnues) {
            if (texte.toLowerCase().contains(espece.toLowerCase())) {
                // Chercher le contexte autour du mot
                Pattern pattern = Pattern.compile("([^.]*?" + espece + "[^.]*\\.)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(texte);
                if (matcher.find()) {
                    return matcher.group(1).trim();
                }
                return espece;
            }
        }
        
        // Si aucune espèce connue, prendre la première phrase
        String[] phrases = texte.split("\\.");
        if (phrases.length > 0) {
            return phrases[0].trim();
        }
        
        return "Non identifié";
    }
    
    /**
     * Extrait le nombre d'individus
     */
    private int extraireNombre(String texte) {
        // Chercher des motifs comme "2 individuals", "two whales", etc.
        Pattern[] patterns = {
            Pattern.compile("(\\d+)\\s*(?:individu|individus|animal|animaux|baleine|dauphin|phoque)"),
            Pattern.compile("(?:mother and calf|mère et son petit)"),
            Pattern.compile("(?:pair|couple|deux)"),
            Pattern.compile("(\\d+)\\s*(?:whales?|dolphins?|seals?|turtles?|sharks?)")
        };
        
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(texte.toLowerCase());
            if (matcher.find()) {
                if (matcher.group(1) != null) {
                    return Integer.parseInt(matcher.group(1));
                } else if (matcher.group().contains("mother and calf") || 
                           matcher.group().contains("mère et son petit")) {
                    return 2;
                } else if (matcher.group().contains("pair") || 
                           matcher.group().contains("couple") || 
                           matcher.group().contains("deux")) {
                    return 2;
                }
            }
        }
        
        return 1;
    }
    
    /**
     * Extrait le comportement
     */
    private String extraireComportement(String texte) {
        String[] comportements = {
            "swimming", "nage", "nageant",
            "resting", "repos", "couché",
            "feeding", "alimentation", "mange",
            "migrating", "migration",
            "playing", "jeu", "joue",
            "jumping", "saute", "saut",
            "breaching", "sauté"
        };
        
        for (String c : comportements) {
            if (texte.toLowerCase().contains(c.toLowerCase())) {
                if (c.equals("swimming") || c.equals("nage") || c.equals("nageant")) return "Nage";
                if (c.equals("resting") || c.equals("repos") || c.equals("couché")) return "Repos";
                if (c.equals("feeding") || c.equals("alimentation") || c.equals("mange")) return "Alimentation";
                if (c.equals("migrating") || c.equals("migration")) return "Migration";
                if (c.equals("playing") || c.equals("jeu") || c.equals("joue")) return "Jeu";
                if (c.equals("jumping") || c.equals("saute") || c.equals("saut")) return "Saut";
                if (c.equals("breaching") || c.equals("sauté")) return "Saut";
                return c;
            }
        }
        
        return "Observé";
    }
    
    /**
     * Extrait l'état de santé
     */
    private String extraireSante(String texte) {
        if (texte.toLowerCase().contains("good health") || 
            texte.toLowerCase().contains("bonne santé") ||
            texte.toLowerCase().contains("healthy") ||
            texte.toLowerCase().contains("sain")) {
            return "Haute";
        } else if (texte.toLowerCase().contains("malade") ||
                   texte.toLowerCase().contains("blessé") ||
                   texte.toLowerCase().contains("weak") ||
                   texte.toLowerCase().contains("faible")) {
            return "Faible";
        } else {
            return "Moyenne";
        }
    }
    
    /**
     * Génère un rapport clair et lisible
     */
    private String genererRapportClair(String analyse, DetectionDrone detection, String nomFichier) {
        StringBuilder rapport = new StringBuilder();
        
        // En-tête
        rapport.append("🔬 **RAPPORT D'ANALYSE OLLAMA**\n");
        rapport.append("═══════════════════════════\n\n");
        
        // Informations générales
        rapport.append("📁 **Fichier :** ").append(nomFichier).append("\n");
        rapport.append("🕒 **Date/heure :** ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n");
        rapport.append("🤖 **Modèle :** bakllava\n\n");
        
        // Résumé de l'analyse
        rapport.append("📝 **RÉSUMÉ DE L'ANALYSE**\n");
        rapport.append("────────────────────────\n\n");
        rapport.append(analyse.trim()).append("\n\n");
        
        // Détails extraits
        if (detection != null) {
            rapport.append("📊 **DÉTAILS EXTRAITS**\n");
            rapport.append("─────────────────────\n\n");
            rapport.append(String.format("🐋 **Espèce :** %s\n", detection.getEspece()));
            rapport.append(String.format("🔢 **Nombre d'individus :** %d\n", detection.getNombreIndividus()));
            rapport.append(String.format("🏊 **Comportement :** %s\n", detection.getComportement()));
            rapport.append(String.format("✅ **État de santé (confiance IA) :** %s\n", detection.getConfianceIA()));
            
            // Ajouter les coordonnées
            rapport.append(String.format("📍 **Position :** %.4f, %.4f\n", detection.getLatitude(), detection.getLongitude()));
        }
        
        // Note de fin
        rapport.append("\n─────────────────────────────\n");
        rapport.append("Analyse générée par Ollama avec le modèle bakllava");
        
        return rapport.toString();
    }
    
    /**
     * Classe pour encapsuler les résultats
     */
    public static class DetectionResult {
        private final DetectionDrone detection;
        private final String rapport;
        
        public DetectionResult(DetectionDrone detection, String rapport) {
            this.detection = detection;
            this.rapport = rapport;
        }
        
        public DetectionDrone getDetection() { return detection; }
        public String getRapport() { return rapport; }
        public boolean hasDetection() { return detection != null; }
    }
}