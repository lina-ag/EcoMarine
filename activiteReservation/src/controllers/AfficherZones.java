package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.edu.esprit.entities.ZoneProtegee;
import tn.edu.esprit.services.ServiceZoneP;
import javafx.scene.control.TextField;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import java.io.IOException;
import java.sql.SQLException;



public class AfficherZones {

    @FXML
    private TableView<ZoneProtegee> tableZones;

    @FXML
    private TableColumn<ZoneProtegee, Integer> colId;

    @FXML
    private TableColumn<ZoneProtegee, String> colNom;

    @FXML
    private TableColumn<ZoneProtegee, String> colCategorie;

    @FXML
    private TableColumn<ZoneProtegee, String> colStatut;

    @FXML
    private TableColumn<ZoneProtegee, Void> colModifier;

    @FXML
    private TableColumn<ZoneProtegee, Void> colSupprimer;

    private ServiceZoneP zs = new ServiceZoneP();
    private ObservableList<ZoneProtegee> zonesList;
    
    @FXML
    private TextField txtRecherche;

    @FXML
    public void initialize() throws SQLException {

       
        colId.setCellValueFactory(new PropertyValueFactory<>("idZone"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomZone"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorieZone"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Ajouter boutons
        addButtonToTableModifier();
        
        addButtonToTableSupprimer();
        zonesList =FXCollections.observableArrayList(zs.getAll(null));

		System.out.println("Nombre de zones récupérées : " + zonesList.size());
		tableZones.setItems(zonesList); 
        
		//recherche
		FilteredList<ZoneProtegee> filteredData = new FilteredList<>(zonesList, b -> true);

		txtRecherche.textProperty().addListener((observable, oldValue, newValue) -> {
		    filteredData.setPredicate(zone -> {

		        if (newValue == null || newValue.isEmpty()) {
		            return true;
		        }

		        String lowerCaseFilter = newValue.toLowerCase();

		        if (zone.getNomZone().toLowerCase().startsWith(lowerCaseFilter)) {
		            return true;
		        } else {
		            return false;
		        }
		    });
		});

		SortedList<ZoneProtegee> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(tableZones.comparatorProperty());
		tableZones.setItems(sortedData);

        

    }

    // Bouton Modifier
    private void addButtonToTableModifier() {

        Callback<TableColumn<ZoneProtegee, Void>, TableCell<ZoneProtegee, Void>> cellFactory =
                param -> new TableCell<>() {

            private final Button btn = new Button("Modifier");

            {
                btn.setOnAction(event -> {

                    ZoneProtegee data = getTableView().getItems().get(getIndex());

                    try {
                        // 🔹 Charger la fenêtre Modifier
                        FXMLLoader loader = new FXMLLoader(
                                getClass().getResource("/ModifierZone.fxml")
                        );

                        Parent root = loader.load();

                        // 🔹 Récupérer le controller 
                        ModifierZone controller = loader.getController();

                        // 🔹 Envoyer les données sélectionnées
                        controller.setZone(data);

                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Modifier Zone");
                        stage.showAndWait();
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

    

    //  Bouton Supprimer
    private void addButtonToTableSupprimer() {

        Callback<TableColumn<ZoneProtegee, Void>, TableCell<ZoneProtegee, Void>> cellFactory = param -> new TableCell<>() {

            private final Button btn = new Button("Supprimer");

            {
                btn.setOnAction(event -> {
                    ZoneProtegee data = getTableView().getItems().get(getIndex());

                    zs.supprimer(data.getIdZone());
                    zonesList.remove(data); 

                    System.out.println("Zone supprimée: " + data.getIdZone());
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
    
    private void refreshTable() {
    	
    	zonesList.clear();
    	zonesList.addAll(zs.getAll(null));
    }
    
    

}