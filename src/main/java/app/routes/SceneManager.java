package app.routes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.net.URL;

public class SceneManager {

    private final BorderPane root;

    public SceneManager(BorderPane root) {
        this.root = root;
    }

    public void setCenter(String fxmlFile) {
        try {
            if (!fxmlFile.endsWith(".fxml")) {
                fxmlFile += ".fxml";
            }

            String resourcePath = "/view/" + fxmlFile;
            URL url = getClass().getResource(resourcePath);

            if (url == null) {
                throw new RuntimeException("FXML introuvable : " + resourcePath);
            }

            Parent view = FXMLLoader.load(url);
            root.setCenter(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}