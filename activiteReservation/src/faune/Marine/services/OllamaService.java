package faune.Marine.services;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.Base64;

public class OllamaService {
    
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private final HttpClient client = HttpClient.newHttpClient();
    
    public String analyserImage(File imageFile, String prompt, String modele) throws IOException, InterruptedException {
        // Lire l'image et la convertir en base64
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        
        // Échapper le prompt pour JSON
        String escapedPrompt = prompt.replace("\\", "\\\\")
                                     .replace("\"", "\\\"")
                                     .replace("\n", "\\n")
                                     .replace("\r", "\\r");
        
        // Construire la requête JSON
        String jsonBody = String.format(
            "{\"model\":\"%s\",\"prompt\":\"%s\",\"images\":[\"%s\"],\"stream\":false,\"options\":{\"temperature\":0.1,\"max_tokens\":2000}}",
            modele, escapedPrompt, base64Image
        );
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(OLLAMA_URL))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();
        
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            return "{\"response\":\"Erreur: " + e.getMessage() + "\"}";
        }
    }
    
    public boolean testConnexion() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:11434/api/tags"))
                .GET()
                .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
    
    public String listerModeles() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:11434/api/tags"))
            .GET()
            .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}