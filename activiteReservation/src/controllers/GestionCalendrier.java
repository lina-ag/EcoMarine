package controllers;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;

import tn.edu.esprit.entities.ActiviteEcologique;
import tn.edu.esprit.services.ServiceActivite;

public class GestionCalendrier {

    @FXML
    private BorderPane root;
    @FXML
    private GridPane calendarGrid;
    @FXML
    private Label monthYearLabel;
    @FXML
    private VBox activitiesList;
    @FXML
    private Button prevMonthBtn;
    @FXML
    private Button nextMonthBtn;
    @FXML
    private Button jourBtn;
    @FXML
    private Button semaineBtn;
    @FXML
    private Button moisBtn;
    @FXML
    private Button anneeBtn;

    private YearMonth currentYearMonth;
    private LocalDate currentDate;
    private ServiceActivite service;
    private String currentView = "mois"; // "mois", "annee", "semaine", "jour"
    private int currentYear;
    private LocalDate weekStart;
    private LocalDate selectedDate; // Jour sélectionné

    @FXML
    public void initialize() {
    	
        currentYearMonth = YearMonth.now();
        currentYear = currentYearMonth.getYear();
        currentDate = LocalDate.now();
        weekStart = currentDate.minusDays(currentDate.getDayOfWeek().getValue() - 1);
        service = new ServiceActivite();

        // Initialiser l'affichage
        showMonthView();
        updateButtonStyles();
        

        // Gestion des boutons de navigation
        prevMonthBtn.setOnAction(e -> {
            switch (currentView) {
                case "mois":
                    currentYearMonth = currentYearMonth.minusMonths(1);
                    showMonthView();
                    break;
                case "annee":
                    currentYear--;
                    showYearView();
                    break;
                case "semaine":
                    weekStart = weekStart.minusWeeks(1);
                    showWeekView();
                    break;
                case "jour":
                    currentDate = currentDate.minusDays(1);
                    showDayView();
                    break;
            }
            updateActivitiesList();
        });

        nextMonthBtn.setOnAction(e -> {
            switch (currentView) {
                case "mois":
                    currentYearMonth = currentYearMonth.plusMonths(1);
                    showMonthView();
                    break;
                case "annee":
                    currentYear++;
                    showYearView();
                    break;
                case "semaine":
                    weekStart = weekStart.plusWeeks(1);
                    showWeekView();
                    break;
                case "jour":
                    currentDate = currentDate.plusDays(1);
                    showDayView();
                    break;
            }
            updateActivitiesList();
        });

        // Boutons de changement de vue
        jourBtn.setOnAction(e -> {
            currentView = "jour";
            showDayView();
            updateActivitiesList();
            updateButtonStyles();
        });

        semaineBtn.setOnAction(e -> {
            currentView = "semaine";
            showWeekView();
            updateActivitiesList();
            updateButtonStyles();
        });

        moisBtn.setOnAction(e -> {
            currentView = "mois";
            showMonthView();
            updateActivitiesList();
            updateButtonStyles();
        });

        anneeBtn.setOnAction(e -> {
            currentView = "annee";
            showYearView();
            updateActivitiesList();
            updateButtonStyles();
        });
        updateActivitiesList();
        }

    private void updateButtonStyles() {
        String activeStyle = "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 20;";
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #64748b;";

        jourBtn.setStyle(currentView.equals("jour") ? activeStyle : inactiveStyle);
        semaineBtn.setStyle(currentView.equals("semaine") ? activeStyle : inactiveStyle);
        moisBtn.setStyle(currentView.equals("mois") ? activeStyle : inactiveStyle);
        anneeBtn.setStyle(currentView.equals("annee") ? activeStyle : inactiveStyle);
    }

 // Ajoutez ces méthodes dans votre controller (remplacez les anciennes méthodes showDayView et showWeekView)

 // ==================== VUE JOUR AMÉLIORÉE ====================
 // ==================== VUE JOUR AVEC CADRE BLANC ====================
    private void showDayView() {
        calendarGrid.getChildren().clear();
        calendarGrid.setHgap(0);
        calendarGrid.setVgap(0);
        
        // Mettre à jour le label principal
        monthYearLabel.setText(currentDate.format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", Locale.FRENCH)).toUpperCase());
        monthYearLabel.setStyle("-fx-cursor: hand; -fx-font-size: 20px; -fx-font-weight: bold;");

        // Créer un conteneur principal avec cadre blanc
        VBox dayViewContainer = new VBox(20);
        dayViewContainer.getStyleClass().add("day-view-container"); // Classe pour le cadre blanc
        dayViewContainer.setMaxWidth(Double.MAX_VALUE);
        dayViewContainer.setMaxHeight(Double.MAX_VALUE);
        dayViewContainer.setPrefHeight(600);
        
        // Padding intérieur
        dayViewContainer.setPadding(new Insets(25));

        // En-tête avec la date (dans le cadre blanc)
        HBox dateHeader = new HBox(15);
        dateHeader.setAlignment(Pos.CENTER_LEFT);
        dateHeader.getStyleClass().add("day-view-header-container");
        
        Label dayNameLabel = new Label(currentDate.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH)).toUpperCase());
        dayNameLabel.getStyleClass().add("day-view-header");
        
        Label dateLabel = new Label(currentDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        dateLabel.getStyleClass().add("day-view-date");
        
        dateHeader.getChildren().addAll(dayNameLabel, dateLabel);
        
        // Séparateur décoratif
        Separator separator = new Separator();
        separator.getStyleClass().add("day-view-separator");
        
        // Section des activités (dans le cadre blanc)
        VBox activitiesContainer = new VBox(15);
        activitiesContainer.getStyleClass().add("day-view-activities-container");
        
        Label activitiesTitle = new Label("Activités du jour");
        activitiesTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #1e3c72;");
        
        activitiesContainer.getChildren().add(activitiesTitle);
        
        // Récupérer les activités du jour
        List<ActiviteEcologique> activitesDuJour = getActivitesForDate(currentDate);
        
        if (activitesDuJour.isEmpty()) {
            VBox emptyBox = new VBox(15);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPrefHeight(300);
            emptyBox.getStyleClass().add("empty-activities-box");
            
            Label emptyIcon = new Label("📅");
            emptyIcon.setStyle("-fx-font-size: 64px;");
            
            Label emptyText = new Label("Aucune activité prévue ce jour");
            emptyText.setStyle("-fx-font-size: 16px; -fx-text-fill: #64748b; -fx-font-weight: 500;");
            
            Label addHint = new Label("Cliquez sur le bouton '+' pour ajouter une activité");
            addHint.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8;");
            
            emptyBox.getChildren().addAll(emptyIcon, emptyText, addHint);
            activitiesContainer.getChildren().add(emptyBox);
        } else {
            // Créer une ScrollPane pour les activités si nombreuses
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(400);
            scrollPane.getStyleClass().add("activities-scroll-pane");
            
            VBox activitiesBox = new VBox(10);
            activitiesBox.setPadding(new Insets(5));
            
            for (ActiviteEcologique a : activitesDuJour) {
                VBox activityBox = createDayViewActivity(a);
                activitiesBox.getChildren().add(activityBox);
            }
            
            scrollPane.setContent(activitiesBox);
            activitiesContainer.getChildren().add(scrollPane);
        }
        
        // Assembler tous les éléments dans le conteneur principal
        dayViewContainer.getChildren().addAll(dateHeader, separator, activitiesContainer);
        
        // Ajouter le conteneur avec cadre blanc à la grille
        calendarGrid.add(dayViewContainer, 0, 0, 7, 6);
    }

    private VBox createDayViewActivity(ActiviteEcologique a) {
        VBox box = new VBox(12);
        box.getStyleClass().add("day-view-activity");
        box.setMaxWidth(Double.MAX_VALUE);
        
        // Ligne 1: Nom et capacité
        HBox topLine = new HBox(15);
        topLine.setAlignment(Pos.CENTER_LEFT);
        topLine.getStyleClass().add("day-view-activity-header");
        
        Label nameLabel = new Label(a.getNom());
        nameLabel.getStyleClass().add("day-view-activity-name");
        
        Label capacityLabel = new Label("👥 " + a.getCapacite() + " pers.");
        capacityLabel.getStyleClass().add("day-view-activity-capacity");
        
        // Badge ID (optionnel)
        Label idLabel = new Label("ID: " + a.getIdActivite());
        idLabel.getStyleClass().add("day-view-activity-id");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        topLine.getChildren().addAll(nameLabel, capacityLabel, spacer, idLabel);
        
        // Ligne 2: Description
        if (a.getDescription() != null && !a.getDescription().isEmpty()) {
            Label descLabel = new Label(a.getDescription());
            descLabel.getStyleClass().add("day-view-activity-desc");
            descLabel.setWrapText(true);
            box.getChildren().add(descLabel);
        }
        
        box.getChildren().add(0, topLine);
        
        // Rendre cliquable
        box.setOnMouseClicked(e -> showActivityDetails(a, LocalDate.parse(a.getDate())));
        
        return box;
    }

 // ==================== VUE SEMAINE AMÉLIORÉE ====================
 // ==================== VUE SEMAINE AVEC CADRE BLANC ====================
    private void showWeekView() {
        calendarGrid.getChildren().clear();
        calendarGrid.setHgap(12);
        calendarGrid.setVgap(12);
        
        // Mettre à jour le label
        LocalDate weekEnd = weekStart.plusDays(6);
        String weekLabel = String.format("SEMAINE DU %s AU %s",
            weekStart.format(DateTimeFormatter.ofPattern("dd MMMM")),
            weekEnd.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        monthYearLabel.setText(weekLabel.toUpperCase());
        monthYearLabel.setStyle("-fx-cursor: hand; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Conteneur principal pour toute la semaine
        VBox weekContainer = new VBox(20);
        weekContainer.getStyleClass().add("week-view-container");
        weekContainer.setMaxWidth(Double.MAX_VALUE);
        weekContainer.setMaxHeight(Double.MAX_VALUE);
        weekContainer.setPadding(new Insets(20));

        // En-tête de la semaine
        HBox weekHeader = new HBox(15);
        weekHeader.setAlignment(Pos.CENTER_LEFT);
        weekHeader.getStyleClass().add("week-view-header-container");
        
        Label weekIcon = new Label("📆");
        weekIcon.setStyle("-fx-font-size: 32px;");
        
        Label weekTitle = new Label("Planning Hebdomadaire");
        weekTitle.getStyleClass().add("week-view-title");
        
        Label weekDates = new Label(weekStart.format(DateTimeFormatter.ofPattern("dd MMMM")) + " - " + 
                                    weekEnd.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        weekDates.getStyleClass().add("week-view-dates");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Statistiques rapides de la semaine
        Label statsLabel = new Label(getWeekStats());
        statsLabel.getStyleClass().add("week-view-stats");
        
        weekHeader.getChildren().addAll(weekIcon, weekTitle, weekDates, spacer, statsLabel);
        
        // Séparateur
        Separator separator = new Separator();
        separator.getStyleClass().add("week-view-separator");
        
        // Grille des jours (7 colonnes)
        GridPane weekGrid = new GridPane();
        weekGrid.getStyleClass().add("week-grid");
        weekGrid.setHgap(12);
        weekGrid.setVgap(12);
        weekGrid.setMaxWidth(Double.MAX_VALUE);
        
        // Ajouter les en-têtes des jours (ligne 0)
        String[] jours = {"LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI", "DIMANCHE"};
        String[] joursAbrev = {"LUN", "MAR", "MER", "JEU", "VEN", "SAM", "DIM"};
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            
            VBox headerBox = new VBox(8);
            headerBox.setAlignment(Pos.CENTER);
            headerBox.getStyleClass().add("week-day-header-box");
            
            // Si c'est aujourd'hui
            if (date.equals(LocalDate.now())) {
                headerBox.getStyleClass().add("today");
            }
            
            // Nom du jour (complet en petit, abrégé en grand)
            Label dayFullName = new Label(jours[i]);
            dayFullName.getStyleClass().add("week-day-fullname");
            
            Label dayAbrev = new Label(joursAbrev[i]);
            dayAbrev.getStyleClass().add("week-day-abrev");
            
            // Numéro du jour
            Label dayNumber = new Label(String.valueOf(date.getDayOfMonth()));
            dayNumber.getStyleClass().add("week-day-number");
            
            // Mois (pour le premier jour et les changements de mois)
            Label monthName = new Label();
            if (i == 0 || date.getMonth() != weekStart.plusDays(i-1).getMonth()) {
                monthName.setText(date.getMonth().getDisplayName(TextStyle.SHORT, Locale.FRENCH).toUpperCase());
                monthName.getStyleClass().add("week-day-month");
            }
            
            headerBox.getChildren().addAll(dayFullName, dayNumber, monthName);
            
            // Rendre l'en-tête cliquable pour aller à la vue jour
            LocalDate finalDate = date;
            headerBox.setOnMouseClicked(e -> {
                currentDate = finalDate;
                currentView = "jour";
                showDayView();
                updateActivitiesList();
                updateButtonStyles();
            });
            
            weekGrid.add(headerBox, i, 0);
        }

        // Ajouter les cellules pour chaque jour (ligne 1)
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            VBox dayCell = createWeekDayCell(date);
            weekGrid.add(dayCell, i, 1);
        }
        
        // Pied de page avec résumé
        HBox weekFooter = new HBox(20);
        weekFooter.setAlignment(Pos.CENTER_RIGHT);
        weekFooter.getStyleClass().add("week-view-footer");
        
        int totalActivities = getWeekTotalActivities();
        int totalParticipants = getWeekTotalParticipants();
        
        Label totalActivitiesLabel = new Label("📊 Total activités: " + totalActivities);
        totalActivitiesLabel.getStyleClass().add("week-footer-label");
        
        Label totalParticipantsLabel = new Label("👥 Total participants: " + totalParticipants);
        totalParticipantsLabel.getStyleClass().add("week-footer-label");
        
        weekFooter.getChildren().addAll(totalActivitiesLabel, totalParticipantsLabel);
        
        // Assembler tous les éléments
        weekContainer.getChildren().addAll(weekHeader, separator, weekGrid, weekFooter);
        
        // Ajouter le conteneur à la grille principale
        calendarGrid.add(weekContainer, 0, 0, 7, 6);
    }

    // ==================== STATISTIQUES DE LA SEMAINE ====================
    private String getWeekStats() {
        int total = 0;
        LocalDate weekEnd = weekStart.plusDays(6);
        
        for (LocalDate date = weekStart; !date.isAfter(weekEnd); date = date.plusDays(1)) {
            total += getActivitesForDate(date).size();
        }
        
        return total + " activité(s) cette semaine";
    }

    private int getWeekTotalActivities() {
        int total = 0;
        LocalDate weekEnd = weekStart.plusDays(6);
        
        for (LocalDate date = weekStart; !date.isAfter(weekEnd); date = date.plusDays(1)) {
            total += getActivitesForDate(date).size();
        }
        return total;
    }

    private int getWeekTotalParticipants() {
        int total = 0;
        LocalDate weekEnd = weekStart.plusDays(6);
        
        for (LocalDate date = weekStart; !date.isAfter(weekEnd); date = date.plusDays(1)) {
            for (ActiviteEcologique a : getActivitesForDate(date)) {
                total += a.getCapacite();
            }
        }
        return total;
    }

    // ==================== CELLULE DE JOUR AMÉLIORÉE ====================
    private VBox createWeekDayCell(LocalDate date) {
        VBox cell = new VBox(10);
        cell.getStyleClass().add("week-day-cell");
        
        if (date.equals(selectedDate)) {
            cell.getStyleClass().add("selected");
        }
        
        cell.setPrefHeight(350);
        cell.setPrefWidth(180);
        cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        // En-tête de la cellule avec heure
        HBox cellHeader = new HBox();
        cellHeader.setAlignment(Pos.CENTER_LEFT);
        cellHeader.getStyleClass().add("week-cell-header");
        
        Label dayNum = new Label(String.valueOf(date.getDayOfMonth()));
        dayNum.getStyleClass().add("week-cell-daynum");
        
        Label dayName = new Label(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.FRENCH).toUpperCase());
        dayName.getStyleClass().add("week-cell-dayname");
        
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        
        // Indicateur de météo (optionnel)
        Label weatherIcon = new Label(getWeatherIcon());
        weatherIcon.getStyleClass().add("week-cell-weather");
        
        cellHeader.getChildren().addAll(dayNum, dayName, headerSpacer, weatherIcon);
        
        // Ligne de séparation
        Separator cellSeparator = new Separator();
        cellSeparator.getStyleClass().add("week-cell-separator");
        
        // Conteneur pour les activités
        VBox activitiesContainer = new VBox(8);
        activitiesContainer.getStyleClass().add("week-cell-activities");
        activitiesContainer.setPadding(new Insets(5, 0, 0, 0));
        
        List<ActiviteEcologique> activites = getActivitesForDate(date);
        
        if (!activites.isEmpty()) {
            // Trier les activités par nom ou heure (si disponible)
            int count = 0;
            for (ActiviteEcologique a : activites) {
                if (count < 4) { // Afficher max 4 activités
                    HBox activityItem = createWeekActivityItem(a);
                    activitiesContainer.getChildren().add(activityItem);
                    count++;
                }
            }
            
            // S'il y a plus d'activités
            if (activites.size() > 4) {
                HBox moreBox = new HBox(5);
                moreBox.setAlignment(Pos.CENTER_LEFT);
                
                Label moreIcon = new Label("➕");
                moreIcon.setStyle("-fx-font-size: 10px;");
                
                Label moreLabel = new Label((activites.size() - 4) + " autre(s)");
                moreLabel.getStyleClass().add("week-cell-more");
                
                moreBox.getChildren().addAll(moreIcon, moreLabel);
                activitiesContainer.getChildren().add(moreBox);
            }
            
            // Petit badge avec le nombre total
            Label countBadge = new Label(activites.size() + " activité(s)");
            countBadge.getStyleClass().add("week-cell-count");
            activitiesContainer.getChildren().add(countBadge);
            
        } else {
            VBox emptyBox = new VBox(8);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPrefHeight(150);
            
            Label emptyIcon = new Label("🌊");
            emptyIcon.setStyle("-fx-font-size: 32px; -fx-opacity: 0.3;");
            
            Label emptyText = new Label("Pas d'activité");
            emptyText.getStyleClass().add("week-cell-empty");
            
            emptyBox.getChildren().addAll(emptyIcon, emptyText);
            activitiesContainer.getChildren().add(emptyBox);
        }
        
        cell.getChildren().addAll(cellHeader, cellSeparator, activitiesContainer);
        
        // Rendre la cellule cliquable
        cell.setOnMouseClicked(e -> {
            selectedDate = date;
            
            if (e.getClickCount() == 2) {
                // Double-clic pour aller à la vue jour
                currentDate = date;
                currentView = "jour";
                showDayView();
                updateActivitiesList();
                updateButtonStyles();
            } else {
                // Simple clic pour mettre à jour la sélection
                refreshWeekView();
                showDateActivities(date);
            }
        });
        
        return cell;
    }

    // ==================== CRÉATION D'UN ÉLÉMENT D'ACTIVITÉ POUR LA SEMAINE ====================
    private HBox createWeekActivityItem(ActiviteEcologique a) {
        HBox item = new HBox(8);
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("week-activity-item");
        item.setMaxWidth(Double.MAX_VALUE);
        
        // Icône selon le type
        String icone = getIconeForActivite(a);
        Label iconLabel = new Label(icone);
        iconLabel.getStyleClass().add("week-activity-icon");
        
        // Nom de l'activité (tronqué si trop long)
        String nom = a.getNom();
        if (nom.length() > 15) {
            nom = nom.substring(0, 12) + "...";
        }
        Label nameLabel = new Label(nom);
        nameLabel.getStyleClass().add("week-activity-name");
        
        // Heure (si disponible dans votre entité)
        Label timeLabel = new Label("09:00"); // À adapter selon vos données
        timeLabel.getStyleClass().add("week-activity-time");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Petit badge de capacité
        Label capacityBadge = new Label("👥 " + a.getCapacite());
        capacityBadge.getStyleClass().add("week-activity-capacity");
        
        item.getChildren().addAll(iconLabel, nameLabel, spacer, timeLabel, capacityBadge);
        
        // Rendre cliquable
        item.setOnMouseClicked(e -> showActivityDetails(a, LocalDate.parse(a.getDate())));
        
        return item;
    }

    // ==================== ICÔNE SELON LE TYPE D'ACTIVITÉ ====================
    private String getIconeForActivite(ActiviteEcologique a) {
        String nom = a.getNom().toLowerCase();
        if (nom.contains("plage")) return "🏖️";
        if (nom.contains("mer")) return "🌊";
        if (nom.contains("nettoyage")) return "🧹";
        if (nom.contains("atelier")) return "🔧";
        if (nom.contains("formation")) return "📚";
        if (nom.contains("sortie")) return "🚶";
        if (nom.contains("excursion")) return "🚌";
        if (nom.contains("ecotourisme")) return "🌿";
        if (nom.contains("conference")) return "🎤";
        if (nom.contains("réunion")) return "👥";
        return "📌";
    }

    // ==================== ICÔNE MÉTÉO (OPTIONNEL) ====================
    private String getWeatherIcon() {
        // Vous pouvez implémenter une vraie météo plus tard
        String[] weathers = {"☀️", "⛅", "☁️", "🌧️", "⛈️", "🌤️"};
        return weathers[(int)(Math.random() * weathers.length)];
    }

    private void refreshWeekView() {
        // Recharger la vue semaine pour mettre à jour la sélection
        showWeekView();
    }

/* private VBox createWeekDayCell(LocalDate date) {
     VBox cell = new VBox(8);
     cell.getStyleClass().add("week-day-cell");
     cell.setPrefHeight(350);
     cell.setPrefWidth(150);
     cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
     
     // Petite en-tête avec le numéro du jour
     HBox miniHeader = new HBox();
     miniHeader.setAlignment(Pos.CENTER_RIGHT);
     
     Label dayNum = new Label(String.valueOf(date.getDayOfMonth()));
     dayNum.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #64748b;");
     
     if (date.equals(LocalDate.now())) {
         dayNum.setStyle("-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: #f59e0b;");
     }
     
     miniHeader.getChildren().add(dayNum);
     
     // Activités du jour
     VBox activitiesBox = new VBox(4);
     activitiesBox.getStyleClass().add("week-day-activities");
     
     List<ActiviteEcologique> activites = getActivitesForDate(date);
     
     if (!activites.isEmpty()) {
         // Afficher max 3 activités
         int count = 0;
         for (ActiviteEcologique a : activites) {
             if (count < 3) {
                 Label actLabel = new Label("• " + a.getNom());
                 actLabel.getStyleClass().add("week-activity-item");
                 actLabel.setMaxWidth(Double.MAX_VALUE);
                 
                 String nom = a.getNom().toLowerCase();
                 if (nom.contains("plage") || nom.contains("mer")) {
                     actLabel.setStyle(actLabel.getStyle() + "-fx-background-color: #dbeafe; -fx-border-color: #3b82f6;");
                 } else if (nom.contains("atelier")) {
                     actLabel.setStyle(actLabel.getStyle() + "-fx-background-color: #fef3c7; -fx-border-color: #f59e0b;");
                 } else if (nom.contains("sortie")) {
                     actLabel.setStyle(actLabel.getStyle() + "-fx-background-color: #dcfce7; -fx-border-color: #10b981;");
                 }
                 
                 activitiesBox.getChildren().add(actLabel);
                 count++;
             }
         }
         
         // S'il y a plus d'activités
         if (activites.size() > 3) {
             Label moreLabel = new Label("+ " + (activites.size() - 3) + " autres");
             moreLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #94a3b8; -fx-padding: 2 0 0 5;");
             activitiesBox.getChildren().add(moreLabel);
         }
     } else {
         Label emptyLabel = new Label("Aucune activité");
         emptyLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #cbd5e1; -fx-padding: 5;");
         emptyLabel.setAlignment(Pos.CENTER);
         emptyLabel.setMaxWidth(Double.MAX_VALUE);
         activitiesBox.getChildren().add(emptyLabel);
     }
     
     cell.getChildren().addAll(miniHeader, activitiesBox);
     
     // Rendre la cellule cliquable
     cell.setOnMouseClicked(e -> {
         if (e.getClickCount() == 2) {
             // Double-clic pour aller à la vue jour
             currentDate = date;
             currentView = "jour";
             showDayView();
             updateActivitiesList();
             updateButtonStyles();
         } else {
             showDateActivities(date);
         }
     });
     
     return cell;
 }*/
    // ==================== VUE MOIS ====================
 // ==================== VUE MOIS AVEC CADRE BLANC ====================
 // ==================== VUE MOIS (SEULEMENT LE MOIS COURANT) ====================
    private void showMonthView() {
        currentView = "mois";
        
        // Mettre à jour le label
        String monthName = currentYearMonth.getMonth().getDisplayName(
            TextStyle.FULL, Locale.FRENCH);
        monthYearLabel.setText(monthName.toUpperCase() + " " + currentYearMonth.getYear());
        monthYearLabel.setStyle("-fx-cursor: hand; -fx-font-size: 20px; -fx-font-weight: bold;");

        // Vider la grille
        calendarGrid.getChildren().clear();
        calendarGrid.setHgap(0);
        calendarGrid.setVgap(0);

        // Conteneur principal avec cadre blanc
        VBox monthContainer = new VBox(20);
        monthContainer.getStyleClass().add("month-view-container");
        monthContainer.setMaxWidth(Double.MAX_VALUE);
        monthContainer.setMaxHeight(Double.MAX_VALUE);
        monthContainer.setPadding(new Insets(15));

        // En-tête du mois
        HBox monthHeader = new HBox(20);
        monthHeader.setAlignment(Pos.CENTER_LEFT);
        monthHeader.getStyleClass().add("month-view-header");
        
        Label monthIcon = new Label("📅");
        monthIcon.setStyle("-fx-font-size: 32px;");
        
        VBox monthTitleBox = new VBox(5);
        Label monthTitle = new Label(monthName.toUpperCase());
        monthTitle.getStyleClass().add("month-view-title");
        
        Label yearLabel = new Label(String.valueOf(currentYearMonth.getYear()));
        yearLabel.getStyleClass().add("month-view-year");
        
        monthTitleBox.getChildren().addAll(monthTitle, yearLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Statistiques du mois
        HBox monthStats = createMonthStats();
        
        monthHeader.getChildren().addAll(monthIcon, monthTitleBox, spacer, monthStats);
        
        // Séparateur décoratif
        Separator separator = new Separator();
        separator.getStyleClass().add("month-view-separator");
        
        // Grille des jours de la semaine
        GridPane weekDaysGrid = new GridPane();
        weekDaysGrid.getStyleClass().add("month-weekdays-grid");
        weekDaysGrid.setHgap(5);
        weekDaysGrid.setVgap(5);
        
        // Ajouter les en-têtes des jours
        String[] jours = {"LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI", "DIMANCHE"};
        String[] joursAbrev = {"LUN", "MAR", "MER", "JEU", "VEN", "SAM", "DIM"};
        
        for (int i = 0; i < 7; i++) {
            VBox dayHeaderBox = new VBox(5);
            dayHeaderBox.setAlignment(Pos.CENTER);
            dayHeaderBox.getStyleClass().add("month-day-header");
            
            Label dayAbrev = new Label(joursAbrev[i]);
            dayAbrev.getStyleClass().add("month-day-abrev");
            
            Label dayFull = new Label(jours[i]);
            dayFull.getStyleClass().add("month-day-full");
            
            // Indicateur de weekend
            if (i >= 5) {
                dayHeaderBox.getStyleClass().add("weekend");
            }
            
            dayHeaderBox.getChildren().addAll(dayAbrev, dayFull);
            weekDaysGrid.add(dayHeaderBox, i, 0);
        }

        // Grille principale des jours
        GridPane daysGrid = new GridPane();
        daysGrid.getStyleClass().add("month-days-grid");
        daysGrid.setHgap(8);
        daysGrid.setVgap(8);
        daysGrid.setMaxWidth(Double.MAX_VALUE);

        // Obtenir le premier jour du mois
        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue(); // 1 = Lundi, 7 = Dimanche
        int startColumn = dayOfWeek - 1;

        // Remplir les jours du mois courant SEULEMENT
        int daysInMonth = currentYearMonth.lengthOfMonth();
        int row = 0;
        int col = startColumn;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);
            VBox dayCell = createMonthDayCell(date);
            
            daysGrid.add(dayCell, col, row);
            
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }

        // Légende du mois
        HBox monthLegend = createMonthLegend();
        
        // Assembler tous les éléments
        monthContainer.getChildren().addAll(monthHeader, separator, weekDaysGrid, daysGrid, monthLegend);
        
        // Ajouter le conteneur à la grille principale
        calendarGrid.add(monthContainer, 0, 0, 7, 6);
        
        updateButtonStyles();
    }

    // ==================== CELLULE DE JOUR POUR LA VUE MOIS ====================
 // ==================== CELLULE DE JOUR POUR LA VUE MOIS ====================
 // ==================== CELLULE DE JOUR POUR LA VUE MOIS ====================
    private VBox createMonthDayCell(LocalDate date) {
        VBox cell = new VBox(5); // Réduit l'espacement de 8 à 5
        cell.getStyleClass().add("month-day-cell");
        
        // Si c'est aujourd'hui
        if (date.equals(LocalDate.now())) {
            cell.getStyleClass().add("today");
        }
        
        // Si c'est le jour sélectionné
        if (date.equals(selectedDate)) {
            cell.getStyleClass().add("selected");
        }
        
        cell.setPrefHeight(60); // Réduit de 80 à 60
        cell.setMinHeight(60);
        cell.setMaxHeight(60);
        cell.setPrefWidth(100);
        cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // En-tête de la cellule avec le numéro du jour
        HBox cellHeader = new HBox();
        cellHeader.setAlignment(Pos.CENTER_RIGHT);
        cellHeader.getStyleClass().add("month-cell-header");
        cellHeader.setPadding(new Insets(0, 2, 0, 0)); // Réduit le padding
        
        Label dayNumber = new Label(String.valueOf(date.getDayOfMonth()));
        dayNumber.getStyleClass().add("month-day-number");
        
        cellHeader.getChildren().add(dayNumber);
        
        // Séparateur subtil (optionnel - peut être supprimé pour gagner de la hauteur)
        // Separator cellSeparator = new Separator();
        // cellSeparator.getStyleClass().add("month-cell-separator");
        
        // Conteneur pour les activités
        VBox activitiesContainer = new VBox(2); // Réduit l'espacement de 4 à 2
        activitiesContainer.getStyleClass().add("month-cell-activities");
        activitiesContainer.setMaxHeight(35); // Réduit de 50 à 35
        activitiesContainer.setPadding(new Insets(1, 0, 0, 0));
        
        List<ActiviteEcologique> activites = getActivitesForDate(date);
        
        if (!activites.isEmpty()) {
            // Afficher max 2 activités
            int count = 0;
            for (ActiviteEcologique a : activites) {
                if (count < 2) {
                    HBox activityItem = createMonthActivityItem(a);
                    activitiesContainer.getChildren().add(activityItem);
                    count++;
                }
            }
            
            // S'il y a plus d'activités
            if (activites.size() > 2) {
                Label moreLabel = new Label("+ " + (activites.size() - 2));
                moreLabel.getStyleClass().add("month-cell-more");
                moreLabel.setPadding(new Insets(0));
                activitiesContainer.getChildren().add(moreLabel);
            }
        }
        
        // Ajouter sans le séparateur pour gagner de la hauteur
        cell.getChildren().addAll(cellHeader, activitiesContainer);
        
        // Rendre la cellule cliquable
        cell.setOnMouseClicked(e -> {
            selectedDate = date;
            
            if (e.getClickCount() == 2) {
                // Double-clic pour aller à la vue jour
                currentDate = date;
                currentView = "jour";
                showDayView();
                updateActivitiesList();
                updateButtonStyles();
            } else {
                // Simple clic pour sélectionner
                showMonthView();
                showDateActivities(date);
            }
        });
        
        return cell;
    }
    // ==================== ÉLÉMENT D'ACTIVITÉ POUR LA VUE MOIS ====================
    private HBox createMonthActivityItem(ActiviteEcologique a) {
        HBox item = new HBox(6);
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("month-activity-item");
        item.setMaxWidth(Double.MAX_VALUE);
        
        // Icône selon le type
        String icone = getIconeForActivite(a);
        Label iconLabel = new Label(icone);
        iconLabel.getStyleClass().add("month-activity-icon");
        
        // Nom de l'activité (tronqué)
        String nom = a.getNom();
        if (nom.length() > 12) {
            nom = nom.substring(0, 10) + "…";
        }
        Label nameLabel = new Label(nom);
        nameLabel.getStyleClass().add("month-activity-name");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Petit badge de capacité
        Label capacityBadge = new Label(String.valueOf(a.getCapacite()));
        capacityBadge.getStyleClass().add("month-activity-capacity");
        
        item.getChildren().addAll(iconLabel, nameLabel, spacer, capacityBadge);
        
        // Rendre cliquable
        item.setOnMouseClicked(e -> showActivityDetails(a, LocalDate.parse(a.getDate())));
        
        return item;
    }

    // ==================== STATISTIQUES DU MOIS ====================
    private HBox createMonthStats() {
        HBox stats = new HBox(15);
        stats.setAlignment(Pos.CENTER_RIGHT);
        stats.getStyleClass().add("month-stats");
        
        // Compter les activités du mois
        int totalActivites = 0;
        int totalCapacite = 0;
        
        for (int day = 1; day <= currentYearMonth.lengthOfMonth(); day++) {
            LocalDate date = currentYearMonth.atDay(day);
            List<ActiviteEcologique> activites = getActivitesForDate(date);
            totalActivites += activites.size();
            for (ActiviteEcologique a : activites) {
                totalCapacite += a.getCapacite();
            }
        }
        
        // Jours avec activités
        long joursAvecActivites = 0;
        for (int day = 1; day <= currentYearMonth.lengthOfMonth(); day++) {
            if (!getActivitesForDate(currentYearMonth.atDay(day)).isEmpty()) {
                joursAvecActivites++;
            }
        }
        
        VBox stat1 = new VBox(2);
        stat1.setAlignment(Pos.CENTER);
        Label stat1Value = new Label(String.valueOf(totalActivites));
        stat1Value.getStyleClass().add("month-stat-value");
        Label stat1Label = new Label("activités");
        stat1Label.getStyleClass().add("month-stat-label");
        stat1.getChildren().addAll(stat1Value, stat1Label);
        
        VBox stat2 = new VBox(2);
        stat2.setAlignment(Pos.CENTER);
        Label stat2Value = new Label(String.valueOf(totalCapacite));
        stat2Value.getStyleClass().add("month-stat-value");
        Label stat2Label = new Label("places");
        stat2Label.getStyleClass().add("month-stat-label");
        stat2.getChildren().addAll(stat2Value, stat2Label);
        
        VBox stat3 = new VBox(2);
        stat3.setAlignment(Pos.CENTER);
        Label stat3Value = new Label(joursAvecActivites + "/" + currentYearMonth.lengthOfMonth());
        stat3Value.getStyleClass().add("month-stat-value");
        Label stat3Label = new Label("jours remplis");
        stat3Label.getStyleClass().add("month-stat-label");
        stat3.getChildren().addAll(stat3Value, stat3Label);
        
        stats.getChildren().addAll(stat1, stat2, stat3);
        
        return stats;
    }

    // ==================== LÉGENDE DU MOIS ====================
    private HBox createMonthLegend() {
        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER_LEFT);
        legend.getStyleClass().add("month-legend");
        
        // Aujourd'hui
        HBox todayItem = new HBox(8);
        todayItem.setAlignment(Pos.CENTER_LEFT);
        Region todayColor = new Region();
        todayColor.getStyleClass().add("legend-color-today");
        Label todayLabel = new Label("Aujourd'hui");
        todayLabel.getStyleClass().add("legend-label");
        todayItem.getChildren().addAll(todayColor, todayLabel);
        
        // Sélectionné
        HBox selectedItem = new HBox(8);
        selectedItem.setAlignment(Pos.CENTER_LEFT);
        Region selectedColor = new Region();
        selectedColor.getStyleClass().add("legend-color-selected");
        Label selectedLabel = new Label("Sélectionné");
        selectedLabel.getStyleClass().add("legend-label");
        selectedItem.getChildren().addAll(selectedColor, selectedLabel);
        
        // Avec activités
        HBox activityItem = new HBox(8);
        activityItem.setAlignment(Pos.CENTER_LEFT);
        Region activityColor = new Region();
        activityColor.getStyleClass().add("legend-color-activity");
        Label activityLabel = new Label("Avec activités");
        activityLabel.getStyleClass().add("legend-label");
        activityItem.getChildren().addAll(activityColor, activityLabel);
        
        // Autre mois
        HBox otherMonthItem = new HBox(8);
        otherMonthItem.setAlignment(Pos.CENTER_LEFT);
        Region otherMonthColor = new Region();
        otherMonthColor.getStyleClass().add("legend-color-other");
        Label otherMonthLabel = new Label("Autre mois");
        otherMonthLabel.getStyleClass().add("legend-label");
        otherMonthItem.getChildren().addAll(otherMonthColor, otherMonthLabel);
        
        legend.getChildren().addAll(todayItem, selectedItem, activityItem, otherMonthItem);
        
        return legend;
    }

    // ==================== VUE ANNEE ====================
    private void showYearView() {
        currentView = "annee";
        
        // Mettre à jour le label
        monthYearLabel.setText(String.valueOf(currentYear));
        monthYearLabel.setStyle("-fx-cursor: hand; -fx-font-size: 20px; -fx-font-weight: bold;");

        // Vider la grille
        calendarGrid.getChildren().clear();
        calendarGrid.setHgap(10);
        calendarGrid.setVgap(10);

        // Créer une grille de 4x3 pour les 12 mois
        String[] moisNoms = {"JANVIER", "FÉVRIER", "MARS", "AVRIL", "MAI", "JUIN",
                             "JUILLET", "AOÛT", "SEPTEMBRE", "OCTOBRE", "NOVEMBRE", "DÉCEMBRE"};

        int moisIndex = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                if (moisIndex < 12) {
                    VBox monthBox = createMonthView(moisIndex + 1, moisNoms[moisIndex]);
                    calendarGrid.add(monthBox, col, row);
                    moisIndex++;
                }
            }
        }
        
        updateButtonStyles();
    }

    // ==================== CRÉATION DES CELLULES ====================
    private VBox createDayCell(LocalDate date, int dayNumber) {
        VBox dayCell = new VBox(3);
        dayCell.getStyleClass().add("day-cell");
        dayCell.setPrefHeight(100);
        dayCell.setPrefWidth(120);
        dayCell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        // Si c'est aujourd'hui
        if (date.equals(LocalDate.now())) {
            dayCell.getStyleClass().add("today");
        }
        
        // Si c'est le jour sélectionné
        if (date.equals(selectedDate)) {
            dayCell.getStyleClass().add("selected");
        }

        // Numéro du jour
        Label dayNumLabel = new Label(String.valueOf(dayNumber));
        dayNumLabel.getStyleClass().add("day-number");
        
        HBox numberContainer = new HBox(dayNumLabel);
        numberContainer.setAlignment(Pos.TOP_RIGHT);
        dayCell.getChildren().add(numberContainer);

        // Ajouter les activités du jour
        List<ActiviteEcologique> activitesDuJour = getActivitesForDate(date);
        for (ActiviteEcologique a : activitesDuJour) {
            Label activityLabel = createActivityLabel(a);
            dayCell.getChildren().add(activityLabel);
        }

        // Rendre la cellule cliquable
        dayCell.setOnMouseClicked(e -> {
            // Mettre à jour la sélection
            selectedDate = date;
            // Recharger la vue mois pour mettre à jour les styles
            showMonthView();
            
            if (e.getClickCount() == 2) {
                // Double-clic pour aller à la vue jour
                currentDate = date;
                currentView = "jour";
                showDayView();
                updateActivitiesList();
                updateButtonStyles();
            } else {
                showDateActivities(date);
            }
        });

        return dayCell;
    }

    private VBox createMonthView(int mois, String nomMois) {
    	
        VBox monthBox = new VBox(5);
        monthBox.getStyleClass().add("month-box");
        monthBox.setPrefSize(200, 180);
        monthBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        YearMonth currentMonth = YearMonth.now();
        if (currentMonth.getMonthValue() == mois && currentMonth.getYear() == currentYear) {
            monthBox.getStyleClass().add("current-month");
        }
        // Rendre le mois cliquable
        YearMonth yearMonth = YearMonth.of(currentYear, mois);
        monthBox.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                currentYearMonth = yearMonth;
                showMonthView();
                updateActivitiesList();
            }
        });

        // Nom du mois
        Label monthLabel = new Label(nomMois);
        monthLabel.getStyleClass().add("month-name");
        monthLabel.setAlignment(Pos.CENTER);
        monthLabel.setMaxWidth(Double.MAX_VALUE);

        // En-tête des jours
        HBox weekHeader = new HBox(2);
        weekHeader.setAlignment(Pos.CENTER);
        String[] jours = {"L", "M", "M", "J", "V", "S", "D"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(jours[i]);
            dayLabel.getStyleClass().add("mini-day-header");
            dayLabel.setPrefWidth(20);
            dayLabel.setAlignment(Pos.CENTER);
            weekHeader.getChildren().add(dayLabel);
        }

        // Grille des jours
        GridPane daysGrid = new GridPane();
        daysGrid.setHgap(2);
        daysGrid.setVgap(2);
        daysGrid.setAlignment(Pos.CENTER);

        // Premier jour du mois
        LocalDate firstDay = yearMonth.atDay(1);
        int firstDayOfWeek = firstDay.getDayOfWeek().getValue() - 1; // 0 = Lundi
        
        int daysInMonth = yearMonth.lengthOfMonth();
        int gridRow = 0;
        int gridCol = firstDayOfWeek;

        for (int day = 1; day <= daysInMonth; day++) {
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.getStyleClass().add("mini-day-number");
            dayLabel.setPrefWidth(20);
            dayLabel.setPrefHeight(20);
            dayLabel.setAlignment(Pos.CENTER);
            
            // Vérifier s'il y a des activités ce jour
            LocalDate date = yearMonth.atDay(day);
            List<ActiviteEcologique> activites = getActivitesForDate(date);
            if (!activites.isEmpty()) {
                dayLabel.getStyleClass().add("has-activity");
            }
            
            daysGrid.add(dayLabel, gridCol, gridRow);
            
            gridCol++;
            if (gridCol > 6) {
                gridCol = 0;
                gridRow++;
            }
        }

        monthBox.getChildren().addAll(monthLabel, weekHeader, daysGrid);
        return monthBox;
    }

    private Label createActivityLabel(ActiviteEcologique a) {
        Label label = new Label("• " + a.getNom());
        label.getStyleClass().add("activity-item");
        label.setMaxWidth(Double.MAX_VALUE);
        label.setOnMouseClicked(e -> showActivityDetails(a, LocalDate.parse(a.getDate())));
        return label;
    }

    // ==================== UTILITAIRES ====================
    private List<ActiviteEcologique> getActivitesForDate(LocalDate date) {
        List<ActiviteEcologique> toutesActivites = service.getAll();
        return toutesActivites.stream()
            .filter(a -> LocalDate.parse(a.getDate()).equals(date))
            .toList();
    }

    private void updateActivitiesList() {
        activitiesList.getChildren().clear();

        List<ActiviteEcologique> toutesActivites = service.getAll();
        
        // Filtrer les activités selon la vue
        List<ActiviteEcologique> activitesAffichees;
        
        switch (currentView) {
            case "annee":
                activitesAffichees = toutesActivites.stream()
                    .filter(a -> LocalDate.parse(a.getDate()).getYear() == currentYear)
                    .sorted((a1, a2) -> a1.getDate().compareTo(a2.getDate()))
                    .toList();
                break;
            case "mois":
                activitesAffichees = toutesActivites.stream()
                    .filter(a -> {
                        LocalDate date = LocalDate.parse(a.getDate());
                        return date.getMonth() == currentYearMonth.getMonth() 
                            && date.getYear() == currentYearMonth.getYear();
                    })
                    .sorted((a1, a2) -> a1.getDate().compareTo(a2.getDate()))
                    .toList();
                break;
            case "semaine":
                LocalDate weekEnd = weekStart.plusDays(6);
                activitesAffichees = toutesActivites.stream()
                    .filter(a -> {
                        LocalDate date = LocalDate.parse(a.getDate());
                        return !date.isBefore(weekStart) && !date.isAfter(weekEnd);
                    })
                    .sorted((a1, a2) -> a1.getDate().compareTo(a2.getDate()))
                    .toList();
                break;
            case "jour":
                activitesAffichees = toutesActivites.stream()
                    .filter(a -> LocalDate.parse(a.getDate()).equals(currentDate))
                    .sorted((a1, a2) -> a1.getDate().compareTo(a2.getDate()))
                    .toList();
                break;
            default:
                activitesAffichees = List.of();
        }

        for (ActiviteEcologique a : activitesAffichees) {
            HBox item = createActivityListItem(a);
            activitiesList.getChildren().add(item);
        }
        
        if (activitesAffichees.isEmpty()) {
            Label emptyLabel = new Label("Aucune activité");
            emptyLabel.setStyle("-fx-text-fill: #999; -fx-padding: 10;");
            activitiesList.getChildren().add(emptyLabel);
        }
    }

    private HBox createActivityListItem(ActiviteEcologique a) {
        HBox item = new HBox(10);
        item.getStyleClass().add("activity-list-item");
        item.setAlignment(Pos.CENTER_LEFT);
        
        LocalDate date = LocalDate.parse(a.getDate());
        
        Label dateLabel = new Label(
            date.format(DateTimeFormatter.ofPattern("dd/MM")));
        dateLabel.getStyleClass().add("activity-date");
        
        Label nameLabel = new Label(a.getNom());
        nameLabel.getStyleClass().add("activity-name");
        
        Label capacityLabel = new Label("Cap: " + a.getCapacite());
        capacityLabel.getStyleClass().add("activity-capacity");
        
        item.getChildren().addAll(dateLabel, nameLabel, capacityLabel);
        item.setOnMouseClicked(e -> showActivityDetails(a, date));
        
        return item;
    }

    private void showActivityDetails(ActiviteEcologique a, LocalDate date) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Détails de l'activité");
        alert.setHeaderText(a.getNom());
        
        String content = String.format(
            "Date: %s\nCapacité: %d personnes\nDescription: %s",
            date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            a.getCapacite(),
            a.getDescription() != null ? a.getDescription() : "Aucune description"
        );
        
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showDateActivities(LocalDate date) {
        List<ActiviteEcologique> activites = getActivitesForDate(date);
        
        if (activites.isEmpty()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Aucune activité");
            alert.setHeaderText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            alert.setContentText("Aucune activité prévue ce jour.");
            alert.showAndWait();
        } else {
            String message = activites.stream()
                .map(a -> "• " + a.getNom() + " (Cap: " + a.getCapacite() + ")")
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
            
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Activités du jour");
            alert.setHeaderText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            alert.setContentText(message);
            alert.showAndWait();
        }
    }
}