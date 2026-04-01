package controllers;

import java.io.File; 
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// Import pour Excel
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


// Import pour PDF
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
// Import pour JSON
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
    private String currentView = "mois";
    private int currentYear;
    private LocalDate weekStart;
    private LocalDate selectedDate;

    @FXML
    public void initialize() {
        currentYearMonth = YearMonth.now();
        currentYear = currentYearMonth.getYear();
        currentDate = LocalDate.now();
        weekStart = currentDate.minusDays(currentDate.getDayOfWeek().getValue() - 1);
        service = new ServiceActivite();

        showMonthView();
        updateButtonStyles();

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

    // ==================== VUE JOUR ====================
    private void showDayView() {
        calendarGrid.getChildren().clear();
        calendarGrid.setHgap(0);
        calendarGrid.setVgap(0);
        
        monthYearLabel.setText(currentDate.format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", Locale.FRENCH)).toUpperCase());
        monthYearLabel.setStyle("-fx-cursor: hand; -fx-font-size: 20px; -fx-font-weight: bold;");

        VBox dayViewContainer = new VBox(20);
        dayViewContainer.getStyleClass().add("day-view-container");
        dayViewContainer.setMaxWidth(Double.MAX_VALUE);
        dayViewContainer.setMaxHeight(Double.MAX_VALUE);
        dayViewContainer.setPrefHeight(600);
        dayViewContainer.setPadding(new Insets(25));

        HBox dateHeader = new HBox(15);
        dateHeader.setAlignment(Pos.CENTER_LEFT);
        dateHeader.getStyleClass().add("day-view-header-container");
        
        Label dayNameLabel = new Label(currentDate.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH)).toUpperCase());
        dayNameLabel.getStyleClass().add("day-view-header");
        
        Label dateLabel = new Label(currentDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        dateLabel.getStyleClass().add("day-view-date");
        
        dateHeader.getChildren().addAll(dayNameLabel, dateLabel);
        
        Separator separator = new Separator();
        separator.getStyleClass().add("day-view-separator");
        
        VBox activitiesContainer = new VBox(15);
        activitiesContainer.getStyleClass().add("day-view-activities-container");
        
        Label activitiesTitle = new Label("Activités du jour");
        activitiesTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #1e3c72;");
        activitiesContainer.getChildren().add(activitiesTitle);
        
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
        
        dayViewContainer.getChildren().addAll(dateHeader, separator, activitiesContainer);
        calendarGrid.add(dayViewContainer, 0, 0, 7, 6);
    }

    private VBox createDayViewActivity(ActiviteEcologique a) {
        VBox box = new VBox(12);
        box.getStyleClass().add("day-view-activity");
        box.setMaxWidth(Double.MAX_VALUE);
        
        HBox topLine = new HBox(15);
        topLine.setAlignment(Pos.CENTER_LEFT);
        topLine.getStyleClass().add("day-view-activity-header");
        
        Label nameLabel = new Label(a.getNom());
        nameLabel.getStyleClass().add("day-view-activity-name");
        
        Label capacityLabel = new Label("👥 " + a.getCapacite() + " pers.");
        capacityLabel.getStyleClass().add("day-view-activity-capacity");
        
        Label idLabel = new Label("ID: " + a.getIdActivite());
        idLabel.getStyleClass().add("day-view-activity-id");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        topLine.getChildren().addAll(nameLabel, capacityLabel, spacer, idLabel);
        
        if (a.getDescription() != null && !a.getDescription().isEmpty()) {
            Label descLabel = new Label(a.getDescription());
            descLabel.getStyleClass().add("day-view-activity-desc");
            descLabel.setWrapText(true);
            box.getChildren().add(descLabel);
        }
        
        box.getChildren().add(0, topLine);
        box.setOnMouseClicked(e -> showActivityDetails(a, LocalDate.parse(a.getDate())));
        
        return box;
    }

    // ==================== VUE SEMAINE ====================
    private void showWeekView() {
        calendarGrid.getChildren().clear();
        calendarGrid.setHgap(12);
        calendarGrid.setVgap(12);
        
        LocalDate weekEnd = weekStart.plusDays(6);
        String weekLabel = String.format("SEMAINE DU %s AU %s",
            weekStart.format(DateTimeFormatter.ofPattern("dd MMMM")),
            weekEnd.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        monthYearLabel.setText(weekLabel.toUpperCase());
        monthYearLabel.setStyle("-fx-cursor: hand; -fx-font-size: 18px; -fx-font-weight: bold;");

        VBox weekContainer = new VBox(20);
        weekContainer.getStyleClass().add("week-view-container");
        weekContainer.setMaxWidth(Double.MAX_VALUE);
        weekContainer.setMaxHeight(Double.MAX_VALUE);
        weekContainer.setPadding(new Insets(20));

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
        
        Label statsLabel = new Label(getWeekStats());
        statsLabel.getStyleClass().add("week-view-stats");
        
        weekHeader.getChildren().addAll(weekIcon, weekTitle, weekDates, spacer, statsLabel);
        
        Separator separator = new Separator();
        separator.getStyleClass().add("week-view-separator");
        
        GridPane weekGrid = new GridPane();
        weekGrid.getStyleClass().add("week-grid");
        weekGrid.setHgap(12);
        weekGrid.setVgap(12);
        weekGrid.setMaxWidth(Double.MAX_VALUE);
        
        String[] jours = {"LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI", "DIMANCHE"};
        String[] joursAbrev = {"LUN", "MAR", "MER", "JEU", "VEN", "SAM", "DIM"};
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            
            VBox headerBox = new VBox(8);
            headerBox.setAlignment(Pos.CENTER);
            headerBox.getStyleClass().add("week-day-header-box");
            
            if (date.equals(LocalDate.now())) {
                headerBox.getStyleClass().add("today");
            }
            
            Label dayFullName = new Label(jours[i]);
            dayFullName.getStyleClass().add("week-day-fullname");
            Label dayNumber = new Label(String.valueOf(date.getDayOfMonth()));
            dayNumber.getStyleClass().add("week-day-number");
            
            Label monthName = new Label();
            if (i == 0 || date.getMonth() != weekStart.plusDays(i-1).getMonth()) {
                monthName.setText(date.getMonth().getDisplayName(TextStyle.SHORT, Locale.FRENCH).toUpperCase());
                monthName.getStyleClass().add("week-day-month");
            }
            
            headerBox.getChildren().addAll(dayFullName, dayNumber, monthName);
            
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

        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            VBox dayCell = createWeekDayCell(date);
            weekGrid.add(dayCell, i, 1);
        }
        
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
        
        weekContainer.getChildren().addAll(weekHeader, separator, weekGrid, weekFooter);
        calendarGrid.add(weekContainer, 0, 0, 7, 6);
    }

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

    private VBox createWeekDayCell(LocalDate date) {
        VBox cell = new VBox(10);
        cell.getStyleClass().add("week-day-cell");
        
        if (date.equals(selectedDate)) {
            cell.getStyleClass().add("selected");
        }
        
        cell.setPrefHeight(350);
        cell.setPrefWidth(180);
        cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        HBox cellHeader = new HBox();
        cellHeader.setAlignment(Pos.CENTER_LEFT);
        cellHeader.getStyleClass().add("week-cell-header");
        
        Label dayNum = new Label(String.valueOf(date.getDayOfMonth()));
        dayNum.getStyleClass().add("week-cell-daynum");
        
        Label dayName = new Label(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.FRENCH).toUpperCase());
        dayName.getStyleClass().add("week-cell-dayname");
        
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        
        Label weatherIcon = new Label(getWeatherIcon());
        weatherIcon.getStyleClass().add("week-cell-weather");
        
        cellHeader.getChildren().addAll(dayNum, dayName, headerSpacer, weatherIcon);
        
        Separator cellSeparator = new Separator();
        cellSeparator.getStyleClass().add("week-cell-separator");
        
        VBox activitiesContainer = new VBox(8);
        activitiesContainer.getStyleClass().add("week-cell-activities");
        activitiesContainer.setPadding(new Insets(5, 0, 0, 0));
        
        List<ActiviteEcologique> activites = getActivitesForDate(date);
        
        if (!activites.isEmpty()) {
            int count = 0;
            for (ActiviteEcologique a : activites) {
                if (count < 4) {
                    HBox activityItem = createWeekActivityItem(a);
                    activitiesContainer.getChildren().add(activityItem);
                    count++;
                }
            }
            
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
        
        cell.setOnMouseClicked(e -> {
            selectedDate = date;
            if (e.getClickCount() == 2) {
                currentDate = date;
                currentView = "jour";
                showDayView();
                updateActivitiesList();
                updateButtonStyles();
            } else {
                refreshWeekView();
                showDateActivities(date);
            }
        });
        
        return cell;
    }

    private HBox createWeekActivityItem(ActiviteEcologique a) {
        HBox item = new HBox(8);
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("week-activity-item");
        item.setMaxWidth(Double.MAX_VALUE);
        
        String icone = getIconeForActivite(a);
        Label iconLabel = new Label(icone);
        iconLabel.getStyleClass().add("week-activity-icon");
        
        String nom = a.getNom();
        if (nom.length() > 15) {
            nom = nom.substring(0, 12) + "...";
        }
        Label nameLabel = new Label(nom);
        nameLabel.getStyleClass().add("week-activity-name");
        
        Label timeLabel = new Label("09:00");
        timeLabel.getStyleClass().add("week-activity-time");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label capacityBadge = new Label("👥 " + a.getCapacite());
        capacityBadge.getStyleClass().add("week-activity-capacity");
        
        item.getChildren().addAll(iconLabel, nameLabel, spacer, timeLabel, capacityBadge);
        item.setOnMouseClicked(e -> showActivityDetails(a, LocalDate.parse(a.getDate())));
        
        return item;
    }

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

    private String getWeatherIcon() {
        String[] weathers = {"☀️", "⛅", "☁️", "🌧️", "⛈️", "🌤️"};
        return weathers[(int)(Math.random() * weathers.length)];
    }

    private void refreshWeekView() {
        showWeekView();
    }

    // ==================== VUE MOIS ====================
    private void showMonthView() {
        currentView = "mois";
        
        String monthName = currentYearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
        monthYearLabel.setText(monthName.toUpperCase() + " " + currentYearMonth.getYear());
        monthYearLabel.setStyle("-fx-cursor: hand; -fx-font-size: 20px; -fx-font-weight: bold;");

        calendarGrid.getChildren().clear();
        calendarGrid.setHgap(0);
        calendarGrid.setVgap(0);

        VBox monthContainer = new VBox(20);
        monthContainer.getStyleClass().add("month-view-container");
        monthContainer.setMaxWidth(Double.MAX_VALUE);
        monthContainer.setMaxHeight(Double.MAX_VALUE);
        monthContainer.setPadding(new Insets(15));

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
        
        HBox monthStats = createMonthStats();
        monthHeader.getChildren().addAll(monthIcon, monthTitleBox, spacer, monthStats);
        
        Separator separator = new Separator();
        separator.getStyleClass().add("month-view-separator");
        
        GridPane weekDaysGrid = new GridPane();
        weekDaysGrid.getStyleClass().add("month-weekdays-grid");
        weekDaysGrid.setHgap(5);
        weekDaysGrid.setVgap(5);
        
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
            
            if (i >= 5) {
                dayHeaderBox.getStyleClass().add("weekend");
            }
            
            dayHeaderBox.getChildren().addAll(dayAbrev, dayFull);
            weekDaysGrid.add(dayHeaderBox, i, 0);
        }

        GridPane daysGrid = new GridPane();
        daysGrid.getStyleClass().add("month-days-grid");
        daysGrid.setHgap(8);
        daysGrid.setVgap(8);
        daysGrid.setMaxWidth(Double.MAX_VALUE);

        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();
        int startColumn = dayOfWeek - 1;

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

        HBox monthLegend = createMonthLegend();
        
        monthContainer.getChildren().addAll(monthHeader, separator, weekDaysGrid, daysGrid, monthLegend);
        calendarGrid.add(monthContainer, 0, 0, 7, 6);
        
        updateButtonStyles();
    }

    private VBox createMonthDayCell(LocalDate date) {
        VBox cell = new VBox(5);
        cell.getStyleClass().add("month-day-cell");
        
        if (date.equals(LocalDate.now())) {
            cell.getStyleClass().add("today");
        }
        if (date.equals(selectedDate)) {
            cell.getStyleClass().add("selected");
        }
        
        cell.setPrefHeight(60);
        cell.setMinHeight(60);
        cell.setMaxHeight(60);
        cell.setPrefWidth(100);
        cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        HBox cellHeader = new HBox();
        cellHeader.setAlignment(Pos.CENTER_RIGHT);
        cellHeader.getStyleClass().add("month-cell-header");
        cellHeader.setPadding(new Insets(0, 2, 0, 0));
        
        Label dayNumber = new Label(String.valueOf(date.getDayOfMonth()));
        dayNumber.getStyleClass().add("month-day-number");
        cellHeader.getChildren().add(dayNumber);
        
        VBox activitiesContainer = new VBox(2);
        activitiesContainer.getStyleClass().add("month-cell-activities");
        activitiesContainer.setMaxHeight(35);
        activitiesContainer.setPadding(new Insets(1, 0, 0, 0));
        
        List<ActiviteEcologique> activites = getActivitesForDate(date);
        
        if (!activites.isEmpty()) {
            int count = 0;
            for (ActiviteEcologique a : activites) {
                if (count < 2) {
                    HBox activityItem = createMonthActivityItem(a);
                    activitiesContainer.getChildren().add(activityItem);
                    count++;
                }
            }
            
            if (activites.size() > 2) {
                Label moreLabel = new Label("+ " + (activites.size() - 2));
                moreLabel.getStyleClass().add("month-cell-more");
                moreLabel.setPadding(new Insets(0));
                activitiesContainer.getChildren().add(moreLabel);
            }
        }
        
        cell.getChildren().addAll(cellHeader, activitiesContainer);
        
        cell.setOnMouseClicked(e -> {
            selectedDate = date;
            if (e.getClickCount() == 2) {
                currentDate = date;
                currentView = "jour";
                showDayView();
                updateActivitiesList();
                updateButtonStyles();
            } else {
                showMonthView();
                showDateActivities(date);
            }
        });
        
        return cell;
    }

    private HBox createMonthActivityItem(ActiviteEcologique a) {
        HBox item = new HBox(6);
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("month-activity-item");
        item.setMaxWidth(Double.MAX_VALUE);
        
        String icone = getIconeForActivite(a);
        Label iconLabel = new Label(icone);
        iconLabel.getStyleClass().add("month-activity-icon");
        
        String nom = a.getNom();
        if (nom.length() > 12) {
            nom = nom.substring(0, 10) + "…";
        }
        Label nameLabel = new Label(nom);
        nameLabel.getStyleClass().add("month-activity-name");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label capacityBadge = new Label(String.valueOf(a.getCapacite()));
        capacityBadge.getStyleClass().add("month-activity-capacity");
        
        item.getChildren().addAll(iconLabel, nameLabel, spacer, capacityBadge);
        item.setOnMouseClicked(e -> showActivityDetails(a, LocalDate.parse(a.getDate())));
        
        return item;
    }

    private HBox createMonthStats() {
        HBox stats = new HBox(15);
        stats.setAlignment(Pos.CENTER_RIGHT);
        stats.getStyleClass().add("month-stats");
        
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

    private HBox createMonthLegend() {
        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER_LEFT);
        legend.getStyleClass().add("month-legend");
        
        HBox todayItem = new HBox(8);
        todayItem.setAlignment(Pos.CENTER_LEFT);
        Region todayColor = new Region();
        todayColor.getStyleClass().add("legend-color-today");
        Label todayLabel = new Label("Aujourd'hui");
        todayLabel.getStyleClass().add("legend-label");
        todayItem.getChildren().addAll(todayColor, todayLabel);
        
        HBox selectedItem = new HBox(8);
        selectedItem.setAlignment(Pos.CENTER_LEFT);
        Region selectedColor = new Region();
        selectedColor.getStyleClass().add("legend-color-selected");
        Label selectedLabel = new Label("Sélectionné");
        selectedLabel.getStyleClass().add("legend-label");
        selectedItem.getChildren().addAll(selectedColor, selectedLabel);
        
        HBox activityItem = new HBox(8);
        activityItem.setAlignment(Pos.CENTER_LEFT);
        Region activityColor = new Region();
        activityColor.getStyleClass().add("legend-color-activity");
        Label activityLabel = new Label("Avec activités");
        activityLabel.getStyleClass().add("legend-label");
        activityItem.getChildren().addAll(activityColor, activityLabel);
        
        legend.getChildren().addAll(todayItem, selectedItem, activityItem);
        return legend;
    }

    // ==================== VUE ANNEE ====================
    private void showYearView() {
        currentView = "annee";
        
        monthYearLabel.setText(String.valueOf(currentYear));
        monthYearLabel.setStyle("-fx-cursor: hand; -fx-font-size: 20px; -fx-font-weight: bold;");

        calendarGrid.getChildren().clear();
        calendarGrid.setHgap(10);
        calendarGrid.setVgap(10);

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

    private VBox createMonthView(int mois, String nomMois) {
        VBox monthBox = new VBox(5);
        monthBox.getStyleClass().add("month-box");
        monthBox.setPrefSize(200, 180);
        monthBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        YearMonth currentMonth = YearMonth.now();
        if (currentMonth.getMonthValue() == mois && currentMonth.getYear() == currentYear) {
            monthBox.getStyleClass().add("current-month");
        }
        
        YearMonth yearMonth = YearMonth.of(currentYear, mois);
        monthBox.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                currentYearMonth = yearMonth;
                showMonthView();
                updateActivitiesList();
            }
        });

        Label monthLabel = new Label(nomMois);
        monthLabel.getStyleClass().add("month-name");
        monthLabel.setAlignment(Pos.CENTER);
        monthLabel.setMaxWidth(Double.MAX_VALUE);

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

        GridPane daysGrid = new GridPane();
        daysGrid.setHgap(2);
        daysGrid.setVgap(2);
        daysGrid.setAlignment(Pos.CENTER);

        LocalDate firstDay = yearMonth.atDay(1);
        int firstDayOfWeek = firstDay.getDayOfWeek().getValue() - 1;
        
        int daysInMonth = yearMonth.lengthOfMonth();
        int gridRow = 0;
        int gridCol = firstDayOfWeek;

        for (int day = 1; day <= daysInMonth; day++) {
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.getStyleClass().add("mini-day-number");
            dayLabel.setPrefWidth(20);
            dayLabel.setPrefHeight(20);
            dayLabel.setAlignment(Pos.CENTER);
            
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

    private VBox createDayCell(LocalDate date, int dayNumber) {
        VBox dayCell = new VBox(3);
        dayCell.getStyleClass().add("day-cell");
        dayCell.setPrefHeight(100);
        dayCell.setPrefWidth(120);
        dayCell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        
        if (date.equals(LocalDate.now())) {
            dayCell.getStyleClass().add("today");
        }
        if (date.equals(selectedDate)) {
            dayCell.getStyleClass().add("selected");
        }

        Label dayNumLabel = new Label(String.valueOf(dayNumber));
        dayNumLabel.getStyleClass().add("day-number");
        
        HBox numberContainer = new HBox(dayNumLabel);
        numberContainer.setAlignment(Pos.TOP_RIGHT);
        dayCell.getChildren().add(numberContainer);

        List<ActiviteEcologique> activitesDuJour = getActivitesForDate(date);
        for (ActiviteEcologique a : activitesDuJour) {
            Label activityLabel = createActivityLabel(a);
            dayCell.getChildren().add(activityLabel);
        }

        dayCell.setOnMouseClicked(e -> {
            selectedDate = date;
            showMonthView();
            if (e.getClickCount() == 2) {
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
        
        Label dateLabel = new Label(date.format(DateTimeFormatter.ofPattern("dd/MM")));
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

    // ==================== EXPORTS ====================
    
    @FXML
    private void exporterExcel() {
        List<ActiviteEcologique> toutes = service.getAll();
        
        if (toutes.isEmpty()) {
            afficherAlerte("Aucune donnée", "Il n'y a aucune activité à exporter.");
            return;
        }
        
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Exporter en Excel");
            chooser.setInitialFileName("activites_ecomarine_" + LocalDate.now() + ".xlsx");
            chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx")
            );
            
            File file = chooser.showSaveDialog(calendarGrid.getScene().getWindow());
            if (file != null) {
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Activités EcoMarine");
                
                // Création de l'en-tête
                org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);  // Utilisation du nom complet
                String[] colonnes = {"ID", "Nom", "Description", "Date", "Capacité"};
                for (int i = 0; i < colonnes.length; i++) {
                    Cell cell = header.createCell(i);
                    cell.setCellValue(colonnes[i]);
                }
                
                // Remplissage des données
                int rowNum = 1;
                for (ActiviteEcologique a : toutes) {
                    org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);  // Utilisation du nom complet
                    row.createCell(0).setCellValue(a.getIdActivite());
                    row.createCell(1).setCellValue(a.getNom());
                    row.createCell(2).setCellValue(a.getDescription() != null ? a.getDescription() : "");
                    row.createCell(3).setCellValue(a.getDate());
                    row.createCell(4).setCellValue(a.getCapacite());
                }
                
                for (int i = 0; i < colonnes.length; i++) {
                    sheet.autoSizeColumn(i);
                }
                
                FileOutputStream fos = new FileOutputStream(file);
                workbook.write(fos);
                workbook.close();
                fos.close();
                
                afficherSucces("Export Excel réussi", 
                    "Fichier : " + file.getName() + "\n" + toutes.size() + " activité(s) exportée(s)");
            }
        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur d'export Excel", e.getMessage());
        }
    }
    @FXML
    private void exporterPDF() {
        List<ActiviteEcologique> toutes = service.getAll();
        
        if (toutes.isEmpty()) {
            afficherAlerte("Aucune donnée", "Il n'y a aucune activité à exporter.");
            return;
        }
        
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Exporter en PDF");
            chooser.setInitialFileName("activites_ecomarine_" + LocalDate.now() + ".pdf");
            chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf")
            );
            
            File file = chooser.showSaveDialog(calendarGrid.getScene().getWindow());
            if (file != null) {
                Document document = new Document(PageSize.A4.rotate());
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                
                Paragraph title = new Paragraph("🌊 EcoMarine - Liste des Activités",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
                document.add(new Paragraph(" "));
                
                document.add(new Paragraph("Date d'export : " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
                document.add(new Paragraph(" "));
                
                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10);
                table.setSpacingAfter(10);
                
                String[] headers = {"ID", "Nom", "Description", "Date", "Capacité"};
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }
                
                for (ActiviteEcologique a : toutes) {
                    table.addCell(String.valueOf(a.getIdActivite()));
                    table.addCell(a.getNom());
                    table.addCell(a.getDescription() != null ? a.getDescription() : "");
                    table.addCell(a.getDate());
                    table.addCell(String.valueOf(a.getCapacite()));
                }
                
                document.add(table);
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Total : " + toutes.size() + " activité(s)", 
                    FontFactory.getFont(FontFactory.HELVETICA, 10)));
                
                document.close();
                afficherSucces("Export PDF réussi", "Fichier : " + file.getName() + "\n" + toutes.size() + " activité(s) exportée(s)");
            }
        } catch (Exception e) {
            e.printStackTrace();
            afficherErreur("Erreur d'export PDF", e.getMessage());
        }
    }
    
    @FXML
    private void exporterCSV() {
        List<ActiviteEcologique> toutes = service.getAll();
        
        if (toutes.isEmpty()) {
            afficherAlerte("Aucune donnée", "Il n'y a aucune activité à exporter.");
            return;
        }
        
        StringBuilder csv = new StringBuilder();
        csv.append("ID;Nom;Description;Date;Capacité\n");
        
        for (ActiviteEcologique a : toutes) {
            csv.append(a.getIdActivite()).append(";")
               .append(a.getNom()).append(";")
               .append(a.getDescription() != null ? a.getDescription().replace(";", ",").replace("\n", " ") : "").append(";")
               .append(a.getDate()).append(";")
               .append(a.getCapacite()).append("\n");
        }
        
        sauvegarderFichier(csv.toString(), "activites_ecomarine_" + LocalDate.now() + ".csv", "CSV (*.csv)");
    }
    
    @FXML
    private void exporterHTML() {
        List<ActiviteEcologique> toutes = service.getAll();
        
        if (toutes.isEmpty()) {
            afficherAlerte("Aucune donnée", "Il n'y a aucune activité à exporter.");
            return;
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html>\n<head>\n");
        html.append("<meta charset='UTF-8'>\n");
        html.append("<title>EcoMarine - Activités</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: 'Segoe UI', Arial; margin: 40px; background: #f0f9ff; }\n");
        html.append(".container { max-width: 1200px; margin: auto; background: white; padding: 30px; border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.1); }\n");
        html.append("h1 { color: #1e3c72; text-align: center; }\n");
        html.append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }\n");
        html.append("th { background: #1e3c72; color: white; padding: 12px; }\n");
        html.append("td { border: 1px solid #ddd; padding: 10px; }\n");
        html.append("tr:nth-child(even) { background: #f8fafc; }\n");
        html.append("tr:hover { background: #e0f2fe; }\n");
        html.append(".footer { text-align: center; margin-top: 30px; color: #64748b; }\n");
        html.append("</style>\n</head>\n<body>\n");
        html.append("<div class='container'>\n");
        html.append("<h1>🌊 EcoMarine - Liste des Activités</h1>\n");
        html.append("<p style='text-align: center;'>Exporté le ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</p>\n");
        html.append("<table>\n");
        html.append("<tr><th>ID</th><th>Nom</th><th>Description</th><th>Date</th><th>Capacité</th></tr>\n");
        
        for (ActiviteEcologique a : toutes) {
            html.append("<tr>");
            html.append("<td>").append(String.valueOf(a.getIdActivite())).append("</td>");
            html.append("<td>").append(a.getNom()).append("</td>");
            html.append("<td>").append(a.getDescription() != null ? a.getDescription() : "").append("</td>");
            html.append("<td>").append(a.getDate()).append("</td>");
            html.append("<td>").append(String.valueOf(a.getCapacite())).append("</td>");
            html.append("</tr>\n");
        }
        
        html.append("</table>\n");
        html.append("<div class='footer'>© EcoMarine - ").append(LocalDate.now().getYear()).append("</div>\n");
        html.append("</div>\n</body>\n</html>");
        
        sauvegarderFichier(html.toString(), "activites_ecomarine_" + LocalDate.now() + ".html", "HTML (*.html)");
    }
    
    @FXML
    private void exporterJSON() {
        List<ActiviteEcologique> toutes = service.getAll();
        
        if (toutes.isEmpty()) {
            afficherAlerte("Aucune donnée", "Il n'y a aucune activité à exporter.");
            return;
        }
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(toutes);
        
        sauvegarderFichier(json, "activites_ecomarine_" + LocalDate.now() + ".json", "JSON (*.json)");
    }
    
    @FXML
    private void exporterICal() {
        List<ActiviteEcologique> toutes = service.getAll();
        
        if (toutes.isEmpty()) {
            afficherAlerte("Aucune donnée", "Il n'y a aucune activité à exporter.");
            return;
        }

        StringBuilder ics = new StringBuilder();
        ics.append("BEGIN:VCALENDAR\r\n");
        ics.append("VERSION:2.0\r\n");
        ics.append("PRODID:-//EcoMarine//FR\r\n");
        ics.append("CALSCALE:GREGORIAN\r\n");
        ics.append("METHOD:PUBLISH\r\n");

        DateTimeFormatter icsDate = DateTimeFormatter.ofPattern("yyyyMMdd");
        String now = LocalDate.now().format(icsDate) + "T000000Z";

        for (ActiviteEcologique a : toutes) {
            LocalDate date = LocalDate.parse(a.getDate());
            String uid = "ecomarine-" + a.getIdActivite() + "@kuriat.tn";
            String dtstart = date.format(icsDate);
            String dtend   = date.plusDays(1).format(icsDate);
            String desc = (a.getDescription() != null ? a.getDescription() : "")
                          .replace("\n", "\\n").replace(",", "\\,");

            ics.append("BEGIN:VEVENT\r\n");
            ics.append("UID:").append(uid).append("\r\n");
            ics.append("DTSTAMP:").append(now).append("\r\n");
            ics.append("DTSTART;VALUE=DATE:").append(dtstart).append("\r\n");
            ics.append("DTEND;VALUE=DATE:").append(dtend).append("\r\n");
            ics.append("SUMMARY:").append(a.getNom()).append("\r\n");
            ics.append("DESCRIPTION:").append(desc)
               .append(" - Capacité: ").append(a.getCapacite()).append(" pers.").append("\r\n");
            ics.append("LOCATION:Kuriat\\, Monastir\\, Tunisie\r\n");
            ics.append("END:VEVENT\r\n");
        }

        ics.append("END:VCALENDAR\r\n");
        
        sauvegarderFichier(ics.toString(), "ecomarine-activites.ics", "iCalendar (*.ics)");
    }
    
    // ==================== MÉTHODES UTILITAIRES ====================
    
    private void sauvegarderFichier(String contenu, String nomFichier, String description) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exporter");
        chooser.setInitialFileName(nomFichier);
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(description, "*." + nomFichier.substring(nomFichier.lastIndexOf(".") + 1))
        );
        
        File fichier = chooser.showSaveDialog(calendarGrid.getScene().getWindow());
        if (fichier != null) {
            try (FileWriter fw = new FileWriter(fichier)) {
                fw.write(contenu);
                afficherSucces("Export réussi", "Fichier : " + fichier.getName());
            } catch (IOException ex) {
                afficherErreur("Erreur", ex.getMessage());
            }
        }
    }
    
    private void afficherSucces(String titre, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ==================== DISPONIBILITÉS ====================
    @FXML
    private void afficherDisponibilites() {
        javafx.stage.Stage popup = new javafx.stage.Stage();
        popup.setTitle("Disponibilités — Semaine du "
            + weekStart.format(DateTimeFormatter.ofPattern("dd/MM")));
        popup.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        VBox content = new VBox(12);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        Label titre = new Label("Créneaux disponibles cette semaine");
        titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");
        content.getChildren().add(titre);

        int CAPACITE_MAX_JOUR = 50;

        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            List<ActiviteEcologique> activites = getActivitesForDate(date);

            int placesOccupees = activites.stream()
                .mapToInt(ActiviteEcologique::getCapacite).sum();
            int placesLibres = Math.max(0, CAPACITE_MAX_JOUR - placesOccupees);
            double tauxRemplissage = (double) placesOccupees / CAPACITE_MAX_JOUR;

            HBox ligne = new HBox(15);
            ligne.setAlignment(Pos.CENTER_LEFT);
            ligne.setStyle("-fx-padding: 10; -fx-background-radius: 8;"
                + "-fx-background-color: " + (date.equals(LocalDate.now()) ? "#eff6ff" : "#f8fafc") + ";");

            VBox jourBox = new VBox(2);
            Label jourLabel = new Label(
                date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.FRENCH).toUpperCase());
            jourLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
            Label dateLabel = new Label(date.format(DateTimeFormatter.ofPattern("dd/MM")));
            dateLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
            jourBox.getChildren().addAll(jourLabel, dateLabel);
            jourBox.setPrefWidth(60);

            VBox barreBox = new VBox(4);
            barreBox.setPrefWidth(200);
            javafx.scene.layout.StackPane barre = new javafx.scene.layout.StackPane();
            Region fond = new Region();
            fond.setPrefHeight(10);
            fond.setStyle("-fx-background-color: #e2e8f0; -fx-background-radius: 5;");
            Region rempli = new Region();
            rempli.setPrefHeight(10);
            rempli.setPrefWidth(200 * tauxRemplissage);
            String couleurBarre = tauxRemplissage < 0.5 ? "#10b981"
                                : tauxRemplissage < 0.8 ? "#f59e0b" : "#ef4444";
            rempli.setStyle("-fx-background-color: " + couleurBarre + "; -fx-background-radius: 5;");
            barre.getChildren().addAll(fond, rempli);
            barre.setAlignment(Pos.CENTER_LEFT);

            Label pourcent = new Label(Math.round(tauxRemplissage * 100) + "% rempli");
            pourcent.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
            barreBox.getChildren().addAll(barre, pourcent);

            VBox placesBox = new VBox(2);
            placesBox.setAlignment(Pos.CENTER_RIGHT);
            Label placesLibresLabel = new Label(placesLibres + " places libres");
            placesLibresLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;"
                + "-fx-text-fill: " + (placesLibres > 10 ? "#10b981" : "#ef4444") + ";");
            Label activitesLabel = new Label(activites.size() + " activité(s)");
            activitesLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");
            placesBox.getChildren().addAll(placesLibresLabel, activitesLabel);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            ligne.getChildren().addAll(jourBox, barreBox, spacer, placesBox);
            content.getChildren().add(ligne);
        }

        HBox legende = new HBox(20);
        legende.setPadding(new Insets(10, 0, 0, 0));
        legende.setAlignment(Pos.CENTER);
        for (String[] item : new String[][]{
            {"#10b981", "< 50% — Disponible"},
            {"#f59e0b", "50-80% — Limité"},
            {"#ef4444", "> 80% — Complet"}
        }) {
            HBox dot = new HBox(6);
            dot.setAlignment(Pos.CENTER_LEFT);
            Region circle = new Region();
            circle.setPrefSize(10, 10);
            circle.setStyle("-fx-background-color: " + item[0] + "; -fx-background-radius: 5;");
            Label lbl = new Label(item[1]);
            lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
            dot.getChildren().addAll(circle, lbl);
            legende.getChildren().add(dot);
        }
        content.getChildren().add(legende);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPrefSize(520, 480);
        popup.setScene(new javafx.scene.Scene(scroll));
        popup.show();
    }

    // ==================== RAPPELS ====================
    @FXML
    private void afficherRappels() {
        LocalDate aujourd = LocalDate.now();
        LocalDate dans7j  = aujourd.plusDays(7);

        List<ActiviteEcologique> prochaines = service.getAll().stream()
            .filter(a -> {
                LocalDate d = LocalDate.parse(a.getDate());
                return !d.isBefore(aujourd) && !d.isAfter(dans7j);
            })
            .sorted((a1, a2) -> a1.getDate().compareTo(a2.getDate()))
            .toList();

        javafx.stage.Stage popup = new javafx.stage.Stage();
        popup.setTitle("Rappels — 7 prochains jours");
        popup.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        VBox content = new VBox(12);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        Label titre = new Label("Activités à venir (7 jours)");
        titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e3c72;");

        Label sous = new Label(prochaines.size() + " activité(s) planifiée(s)");
        sous.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        content.getChildren().addAll(titre, sous);

        if (prochaines.isEmpty()) {
            Label vide = new Label("Aucune activité dans les 7 prochains jours.");
            vide.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8; -fx-padding: 20;");
            content.getChildren().add(vide);
        } else {
            for (ActiviteEcologique a : prochaines) {
                LocalDate date = LocalDate.parse(a.getDate());
                long joursRestants = aujourd.until(date, java.time.temporal.ChronoUnit.DAYS);

                String urgence = joursRestants == 0 ? "#ef4444"
                               : joursRestants <= 2 ? "#f59e0b"
                               : "#10b981";

                String joursLabel = joursRestants == 0 ? "Aujourd'hui !"
                                  : joursRestants == 1 ? "Demain"
                                  : "Dans " + joursRestants + " jours";

                HBox rappel = new HBox(15);
                rappel.setAlignment(Pos.CENTER_LEFT);
                rappel.setPadding(new Insets(12));
                rappel.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10;"
                    + "-fx-border-color: " + urgence + "; -fx-border-width: 0 0 0 4;"
                    + "-fx-border-radius: 0;");

                VBox countBox = new VBox(2);
                countBox.setAlignment(Pos.CENTER);
                countBox.setPrefWidth(70);
                Label countLabel = new Label(joursLabel);
                countLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;"
                    + "-fx-text-fill: " + urgence + "; -fx-wrap-text: true;");
                countLabel.setWrapText(true);
                countBox.getChildren().add(countLabel);

                VBox detailBox = new VBox(4);
                Label nomLabel = new Label(a.getNom());
                nomLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

                Label dateDetailLabel = new Label(
                    date.format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy", Locale.FRENCH)));
                dateDetailLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");

                Label capLabel = new Label("Capacité : " + a.getCapacite() + " participants");
                capLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

                detailBox.getChildren().addAll(nomLabel, dateDetailLabel, capLabel);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Label iconeUrgence = new Label(joursRestants == 0 ? "🔴" : joursRestants <= 2 ? "🟡" : "🟢");
                iconeUrgence.setStyle("-fx-font-size: 16px;");

                rappel.getChildren().addAll(countBox, detailBox, spacer, iconeUrgence);
                content.getChildren().add(rappel);
            }
        }

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPrefSize(480, 420);
        popup.setScene(new javafx.scene.Scene(scroll));
        popup.show();
    }
    @FXML
    private Button btnBack;

    @FXML
    private void handleBack() {
        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }
}
