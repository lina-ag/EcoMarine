package faune.Marine.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;

public class OllamaChatService {
    
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private final HttpClient client = HttpClient.newHttpClient();
    
    public void chatStreaming(String modele, String prompt, Consumer<String> onToken, Runnable onComplete) {
        new Thread(() -> {
            try {
                String escapedPrompt = prompt.replace("\\", "\\\\")
                                             .replace("\"", "\\\"")
                                             .replace("\n", "\\n")
                                             .replace("\r", "\\r");
                
                String jsonBody = String.format(
                    "{\"model\":\"%s\",\"prompt\":\"%s\",\"stream\":true,\"options\":{\"temperature\":0.7,\"max_tokens\":2000}}",
                    modele, escapedPrompt
                );
                
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_URL))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(120))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
                
                HttpResponse<InputStream> response = client.send(request, 
                    HttpResponse.BodyHandlers.ofInputStream());
                
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.body()))) {
                    
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("\"response\":\"")) {
                            String token = extraireToken(line);
                            if (token != null && !token.isEmpty()) {
                                onToken.accept(token);
                            }
                        }
                    }
                }
                
                onComplete.run();
                
            } catch (Exception e) {
                e.printStackTrace();
                onToken.accept("❌ Erreur: " + e.getMessage());
                onComplete.run();
            }
        }).start();
    }
    
    private String extraireToken(String jsonLine) {
        try {
            int start = jsonLine.indexOf("\"response\":\"") + 12;
            int end = jsonLine.indexOf("\"", start);
            if (start > 12 && end > start) {
                return jsonLine.substring(start, end);
            }
        } catch (Exception e) {}
        return null;
    }
    
    public boolean testConnexion() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:11434/api/tags"))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}