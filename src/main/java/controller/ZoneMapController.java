package controller;

import dao.ZonePlageDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import model.ZonePlage;

import java.util.List;

public class ZoneMapController {

    @FXML
    private WebView webView;

    @FXML
    public void initialize() {
        if (webView == null) {
            showError("WebView non initialisé.");
            return;
        }

        try {
            WebEngine engine = webView.getEngine();
            List<ZonePlage> zones = new ZonePlageDAO().findAll();

            StringBuilder markers = new StringBuilder();

            for (ZonePlage z : zones) {
                double[] coords = getCoordinates(z.getLocalisation());

                String nom = escapeJs(z.getNomZone());
                String localisation = escapeJs(z.getLocalisation());
                String statut = escapeJs(z.getStatut());

                markers.append("L.marker([")
                        .append(coords[0]).append(",").append(coords[1]).append("])")
                        .append(".addTo(map)")
                        .append(".bindPopup('<b>")
                        .append(nom)
                        .append("</b><br/>")
                        .append(localisation)
                        .append("<br/>Statut: ")
                        .append(statut)
                        .append("');");
            }

            String html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="utf-8"/>
                        <title>Carte Zones</title>
                        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
                        <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                        <style>
                            html, body, #map { height: 100%%; margin: 0; }
                        </style>
                    </head>
                    <body>
                        <div id="map"></div>
                        <script>
                            var map = L.map('map').setView([36.8, 10.18], 7);
                            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                                maxZoom: 18
                            }).addTo(map);
                            %s
                        </script>
                    </body>
                    </html>
                    """.formatted(markers);

            engine.loadContent(html);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Impossible de charger la carte des zones.");
        }
    }

    private double[] getCoordinates(String localisation) {
        String loc = localisation == null ? "" : localisation.toLowerCase();

        if (loc.contains("marsa")) return new double[]{36.8782, 10.3247};
        if (loc.contains("hammamet")) return new double[]{36.4000, 10.6167};
        if (loc.contains("sousse")) return new double[]{35.8256, 10.6084};
        if (loc.contains("bizerte")) return new double[]{37.2744, 9.8739};
        if (loc.contains("nabeul")) return new double[]{36.4561, 10.7376};

        return new double[]{36.8065, 10.1815};
    }

    private String escapeJs(String value) {
        if (value == null) return "";
        return value
                .replace("\\", "\\\\")
                .replace("'", " ")
                .replace("\"", " ")
                .replace("\n", " ")
                .replace("\r", " ");
    }

    private void showError(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}