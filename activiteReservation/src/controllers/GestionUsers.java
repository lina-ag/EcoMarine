package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.edu.esprit.entities.User;
import tn.edu.esprit.services.ServiceUser;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class GestionUsers implements Initializable {

    // ── Sidebar stats (gardé de l'original) ──
    @FXML private Label adminsLabel;
    @FXML private Label chercheursLabel;
    @FXML private Label utilisateursLabel;
    @FXML private Label totalUsersLabel;

    // ── Titre & session ──
    @FXML private Label lblDateHeure;
    @FXML private Label lblAdminNom;
    @FXML private Label lblSessionEmail;
    @FXML private Label lblSessionRole;

    // ── KPI Cards ──
    @FXML private Label kpiTotal;
    @FXML private Label kpiTotalSub;
    @FXML private Label kpiAdmins;
    @FXML private Label kpiAdminsSub;
    @FXML private Label kpiChercheurs;
    @FXML private Label kpiChercheursSub;
    @FXML private Label kpiUtilisateurs;
    @FXML private Label kpiUtilisateursSub;

    // ── Dernier inscrit ──
    @FXML private Label lblDernierUser;
    @FXML private Label lblDernierUserRole;

    // ── Alerte système ──
    @FXML private Label lblAlerteSystem;
    @FXML private VBox  panneauAlerte;

    // ── Graphiques ──
    @FXML private PieChart pieChart;
    @FXML private BarChart<String, Number> barChart;

    // ── Barres progression ──
    @FXML private ProgressBar barAdmins;
    @FXML private ProgressBar barChercheurs;
    @FXML private ProgressBar barUtilisateurs;
    @FXML private Label pctAdmins;
    @FXML private Label pctChercheurs;
    @FXML private Label pctUtilisateurs;

    // ── Liste récents ──
    @FXML private VBox listeRecentUsers;

    // ── Services ──
    private ServiceUser serviceUser = new ServiceUser();
    private User utilisateurConnecte;

    // ════════════════════════════════════════
    //  INITIALIZE
    // ════════════════════════════════════════
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        demarrerHorloge();
        chargerDashboard();
    }

    // Horloge temps réel
    private void demarrerHorloge() {
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            String now = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy  |  HH:mm:ss"));
            lblDateHeure.setText(now);
        }));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    // ════════════════════════════════════════
    //  CHARGEMENT GLOBAL
    // ════════════════════════════════════════
    private void chargerDashboard() {
        try {
            List<User> tous = serviceUser.getAll();

            int admins = 0, chercheurs = 0, utilisateurs = 0;
            for (User u : tous) {
                String role = u.getRole() != null
                    ? u.getRole().toLowerCase().trim() : "";
                if (role.contains("admin") || role.equals("administrateur")) admins++;
                else if (role.contains("chercheur"))                          chercheurs++;
                else                                                           utilisateurs++;
            }

            int total = tous.size();
            int totalSafe = total == 0 ? 1 : total;

            // ── Sidebar (gardé de l'original) ──
            adminsLabel.setText(String.valueOf(admins));
            chercheursLabel.setText(String.valueOf(chercheurs));
            utilisateursLabel.setText(String.valueOf(utilisateurs));
            totalUsersLabel.setText(String.valueOf(total));

            // ── KPI ──
            kpiTotal.setText(String.valueOf(total));
            kpiTotalSub.setText("comptes enregistrés");

            kpiAdmins.setText(String.valueOf(admins));
            kpiAdminsSub.setText(Math.round((admins * 100.0) / totalSafe) + "% du total");

            kpiChercheurs.setText(String.valueOf(chercheurs));
            kpiChercheursSub.setText(Math.round((chercheurs * 100.0) / totalSafe) + "% du total");

            kpiUtilisateurs.setText(String.valueOf(utilisateurs));
            kpiUtilisateursSub.setText(Math.round((utilisateurs * 100.0) / totalSafe) + "% du total");

            // ── Dernier inscrit ──
            chargerDernierInscrit(tous);

            // ── Alerte système ──
            chargerAlerteSysteme(total, admins);

            // ── Graphiques ──
            chargerPieChart(admins, chercheurs, utilisateurs);
            chargerBarChart(admins, chercheurs, utilisateurs);

            // ── Barres progression ──
            chargerProgressBars(admins, chercheurs, utilisateurs, totalSafe);

            // ── Liste récents ──
            chargerListeRecents(tous);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ════════════════════════════════════════
    //  DERNIER INSCRIT
    // ════════════════════════════════════════
    private void chargerDernierInscrit(List<User> tous) {
        if (tous.isEmpty()) {
            lblDernierUser.setText("Aucun utilisateur");
            lblDernierUserRole.setText("");
            return;
        }
        User dernier = tous.get(tous.size() - 1);
        lblDernierUser.setText(dernier.getEmail());
        String role = dernier.getRole() != null ? dernier.getRole() : "Utilisateur";
        lblDernierUserRole.setText("Rôle : " + role);
    }

    // ════════════════════════════════════════
    //  ALERTE SYSTEME
    // ════════════════════════════════════════
    private void chargerAlerteSysteme(int total, int admins) {
        if (total == 0) {
            lblAlerteSystem.setText("Aucun compte enregistré. Commencez par ajouter des utilisateurs.");
            panneauAlerte.setStyle(
                "-fx-background-color: #fff5f5; -fx-background-radius: 18; -fx-padding: 18;" +
                "-fx-border-color: #ffcccc; -fx-border-width: 1.5; -fx-border-radius: 18;");
        } else if (admins == 0) {
            lblAlerteSystem.setText("Attention : aucun administrateur enregistré !");
            panneauAlerte.setStyle(
                "-fx-background-color: #fff9e6; -fx-background-radius: 18; -fx-padding: 18;" +
                "-fx-border-color: #ffe0a0; -fx-border-width: 1.5; -fx-border-radius: 18;");
        } else {
            lblAlerteSystem.setText("Système opérationnel. " + total + " compte(s) actif(s), " + admins + " admin(s).");
            panneauAlerte.setStyle(
                "-fx-background-color: #f0fff4; -fx-background-radius: 18; -fx-padding: 18;" +
                "-fx-border-color: #b2f5c8; -fx-border-width: 1.5; -fx-border-radius: 18;");
        }
    }

    // ════════════════════════════════════════
    //  PIE CHART
    // ════════════════════════════════════════
    private void chargerPieChart(int admins, int chercheurs, int utilisateurs) {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
            new PieChart.Data("Admins : "       + admins,       admins),
            new PieChart.Data("Chercheurs : "   + chercheurs,   chercheurs),
            new PieChart.Data("Utilisateurs : " + utilisateurs, utilisateurs)
        );
        pieChart.setData(data);
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(true);
        pieChart.setAnimated(true);
    }

    // ════════════════════════════════════════
    //  BAR CHART
    // ════════════════════════════════════════
    private void chargerBarChart(int admins, int chercheurs, int utilisateurs) {
        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Admins",       admins));
        series.getData().add(new XYChart.Data<>("Chercheurs",   chercheurs));
        series.getData().add(new XYChart.Data<>("Utilisateurs", utilisateurs));
        barChart.getData().add(series);
        barChart.setAnimated(true);
        barChart.setLegendVisible(false);
    }

    // ════════════════════════════════════════
    //  BARRES PROGRESSION
    // ════════════════════════════════════════
    private void chargerProgressBars(int admins, int chercheurs,
                                      int utilisateurs, int total) {
        double pA = (double) admins       / total;
        double pC = (double) chercheurs   / total;
        double pU = (double) utilisateurs / total;

        barAdmins.setProgress(pA);
        barChercheurs.setProgress(pC);
        barUtilisateurs.setProgress(pU);

        pctAdmins.setText(Math.round(pA * 100) + "%  (" + admins + ")");
        pctChercheurs.setText(Math.round(pC * 100) + "%  (" + chercheurs + ")");
        pctUtilisateurs.setText(Math.round(pU * 100) + "%  (" + utilisateurs + ")");
    }

    // ════════════════════════════════════════
    //  LISTE 5 DERNIERS INSCRITS
    // ════════════════════════════════════════
    private void chargerListeRecents(List<User> tous) {
        listeRecentUsers.getChildren().clear();

        // Prendre les 5 derniers
        int debut = Math.max(0, tous.size() - 5);
        List<User> recents = tous.subList(debut, tous.size());

        if (recents.isEmpty()) {
            Label lbl = new Label("Aucun utilisateur enregistré");
            lbl.setStyle("-fx-text-fill: #888; -fx-font-size: 13px;");
            listeRecentUsers.getChildren().add(lbl);
            return;
        }

        // Afficher du plus récent au plus ancien
        for (int i = recents.size() - 1; i >= 0; i--) {
            User u = recents.get(i);
            HBox ligne = new HBox(12);
            ligne.setAlignment(Pos.CENTER_LEFT);
            ligne.setStyle(
                "-fx-background-color: #f8f9fa;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 10 14;");

            // Avatar coloré selon le rôle
            String role = u.getRole() != null ? u.getRole().toLowerCase() : "";
            String couleur = role.contains("admin") ? "#e74c3c"
                           : role.contains("chercheur") ? "#1abc9c"
                           : "#f39c12";
            String initiale = u.getEmail() != null && !u.getEmail().isEmpty()
                ? String.valueOf(u.getEmail().charAt(0)).toUpperCase() : "?";

            Label avatar = new Label(initiale);
            avatar.setMinSize(36, 36);
            avatar.setMaxSize(36, 36);
            avatar.setAlignment(Pos.CENTER);
            avatar.setStyle(
                "-fx-background-color: " + couleur + ";" +
                "-fx-background-radius: 18;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 14px;");

            VBox info = new VBox(2);
            Label email = new Label(u.getEmail());
            email.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");

            String roleAffiche = u.getRole() != null ? u.getRole() : "Utilisateur";
            Label roleLbl = new Label(roleAffiche);
            roleLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + couleur + "; -fx-font-weight: bold;");

            info.getChildren().addAll(email, roleLbl);

            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            ligne.getChildren().addAll(avatar, info, spacer);
            listeRecentUsers.getChildren().add(ligne);
        }
    }

    // ════════════════════════════════════════
    //  METHODE RECEVOIR ADMIN CONNECTE (gardé)
    // ════════════════════════════════════════
    public void setUtilisateurConnecte(User user) {
        this.utilisateurConnecte = user;
        if (user != null) {
            lblAdminNom.setText(user.getEmail());
            lblSessionEmail.setText("Email : " + user.getEmail());
            lblSessionRole.setText("Rôle : " + (user.getRole() != null ? user.getRole() : "Admin"));
        }
    }

    // ════════════════════════════════════════
    //  BOUTON ACTUALISER (NOUVEAU)
    // ════════════════════════════════════════
    @FXML
    private void actualiserDashboard() {
        chargerDashboard();
    }

    // ════════════════════════════════════════
    //  NAVIGATION (gardé de l'original)
    // ════════════════════════════════════════
    @FXML
    private void ouvrirAjouterUser() {
        ouvrirFenetre("/AjouterUser.fxml", "Ajouter un utilisateur");
    }

    @FXML
    private void ouvrirAfficherUsers() {
        ouvrirFenetre("/AfficherUsers.fxml", "Liste des utilisateurs");
    }

    @FXML
    private void ouvrirRechercherUser() {
        ouvrirFenetre("/RechercherUser.fxml", "Rechercher un utilisateur");
    }

    private void ouvrirFenetre(String cheminFXML, String titre) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(cheminFXML));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titre);
            stage.show();
        } catch (Exception e) {
            System.err.println("Erreur ouverture : " + cheminFXML);
            e.printStackTrace();
        }
    }

    @FXML
    private void deconnexion(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/SignIn.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Gardé de l'original pour compatibilité
    public void rafraichirStatistiques() {
        chargerDashboard();
    }
}
