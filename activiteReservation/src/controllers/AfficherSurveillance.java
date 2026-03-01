package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.edu.esprit.entities.SurveillanceZone;
import tn.edu.esprit.services.ServiceSurv;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public class AfficherSurveillance {

    @FXML
    private TableView<SurveillanceZone> tableSurv;

    @FXML
    private TableColumn<SurveillanceZone, Integer> colId;

    @FXML
    private TableColumn<SurveillanceZone, String> colDate;

    @FXML
    private TableColumn<SurveillanceZone, String> colObservation;

    @FXML
    private TableColumn<SurveillanceZone, Integer> colIdZone;

    @FXML
    private TableColumn<SurveillanceZone, Void> colModifier;

    @FXML
    private TableColumn<SurveillanceZone, Void> colSupprimer;

    private ServiceSurv service = new ServiceSurv();
    private ObservableList<SurveillanceZone> survList;
    
    @FXML
    private TextField txtRecherche;

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("idSurveillance"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateSurveillance"));
        colObservation.setCellValueFactory(new PropertyValueFactory<>("observation"));
        colIdZone.setCellValueFactory(new PropertyValueFactory<>("idZone"));

        addDeleteButton();
        addModifyButton();
        survList = FXCollections.observableArrayList(service.getAll(null));
        tableSurv.setItems(survList);

        refreshTable();
        
        survList = FXCollections.observableArrayList(service.getAll(null));

        FilteredList<SurveillanceZone> filteredData =
                new FilteredList<>(survList, b -> true);

        txtRecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(surv -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                try {
                    int idRecherche = Integer.parseInt(newValue);
                    return surv.getIdZone() == idRecherche;
                } catch (NumberFormatException e) {
                    return false;
                }
            });
        });

        SortedList<SurveillanceZone> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableSurv.comparatorProperty());

        tableSurv.setItems(sortedData);
    }

    private void refreshTable() {
    	survList.clear();
    	survList.addAll(service.getAll(null));
    }

    private void addDeleteButton() {
        Callback<TableColumn<SurveillanceZone, Void>, TableCell<SurveillanceZone, Void>> cellFactory =
                param -> new TableCell<>() {

                    private final Button btn = new Button("Supprimer");

                    {
                        btn.setOnAction(event -> {
                            SurveillanceZone data = getTableView().getItems().get(getIndex());
                            service.supprimer(data.getIdSurveillance());
                            refreshTable();
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btn);
                    }
                };

        colSupprimer.setCellFactory(cellFactory);
    }

    private void addModifyButton() {
    	

    	    Callback<TableColumn<SurveillanceZone, Void>, TableCell<SurveillanceZone, Void>> cellFactory =
    	            param -> new TableCell<>() {

    	                private final Button btn = new Button("Modifier");

    	                {
    	                    btn.setOnAction(event -> {

    	                        try {
    	                            SurveillanceZone data = getTableView().getItems().get(getIndex());

    	                            FXMLLoader loader = new FXMLLoader(
    	                                    getClass().getResource("/ModifierSurveillance.fxml")
    	                            );

    	                            Parent root = loader.load();

    	                            // récupérer le controller
    	                            ModifierSurveillance controller = loader.getController();

    	                            // envoyer la surveillance sélectionnée
    	                            controller.setSurveillance(data);

    	                            Stage stage = new Stage();
    	                            stage.setScene(new Scene(root));
    	                            stage.setTitle("Modifier Surveillance");
    	                            stage.showAndWait();

    	                            // rafraîchir la table après modification
    	                            refreshTable();

    	                        } catch (Exception e) {
    	                            e.printStackTrace();
    	                        }
    	                    });
    	                }

    	                @Override
    	                protected void updateItem(Void item, boolean empty) {
    	                    super.updateItem(item, empty);
    	                    setGraphic(empty ? null : btn);
    	                }
    	            };

    	    colModifier.setCellFactory(cellFactory);
    	}
}