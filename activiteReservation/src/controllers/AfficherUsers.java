package controllers;

import java.io.IOException;

import java.util.List;
import java.util.stream.Collectors;
import tn.edu.esprit.services.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.edu.esprit.entities.User;
import tn.edu.esprit.services.ServiceUser;

public class AfficherUsers {

    @FXML
    private TableView<User> tableUsers;

    @FXML
    private TableColumn<User, Integer> colId;

    @FXML
    private TableColumn<User, String> colNom;

    @FXML
    private TableColumn<User, String> colPrenom;

    @FXML
    private TableColumn<User, String> colEmail;

    @FXML
    private TableColumn<User, String> colTelephone;

    @FXML
    private TableColumn<User, String> colRole;

    @FXML
    private TableColumn<User, String> colDateNaissance;

    @FXML
    private TableColumn<User, Void> colModifier;

    @FXML
    private TableColumn<User, Void> colSupprimer;
    
    // 🔍 Nouveaux composants pour la recherche avancée
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> searchCriteriaCombo;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private Button resetButton;

    private ServiceUser serviceUser = new ServiceUser();
    private ObservableList<User> usersList;
    private ObservableList<User> filteredList;

    @FXML
    public void initialize() {

        // Mapping des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("idUtilisateur"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colDateNaissance.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));

        // Boutons Modifier et Supprimer
        addButtonModifier();
        addButtonSupprimer();

        // Charger les données
        usersList = FXCollections.observableArrayList(serviceUser.getAll());
        filteredList = FXCollections.observableArrayList(usersList);
        tableUsers.setItems(filteredList);

        System.out.println("Nombre d'utilisateurs : " + usersList.size());
        
        // Initialiser le ComboBox des critères de recherche
        initializeSearchCriteria();
        
        // Ajouter un écouteur pour la recherche en temps réel (optionnel)
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (searchCriteriaCombo.getValue() != null) {
                filterUsers(newValue, searchCriteriaCombo.getValue());
            }
        });
    }
    
    // 🔧 Initialiser les critères de recherche
    private void initializeSearchCriteria() {
        searchCriteriaCombo.getItems().addAll(
            "Tous les champs",
            "Nom",
            "Email",
            "Rôle"
        );
        searchCriteriaCombo.setValue("Tous les champs"); // Valeur par défaut
    }
    
    // 🔍 Méthode de recherche appelée par le bouton
    @FXML
    private void handleSearch() {
        String critere = searchCriteriaCombo.getValue();
        filterUsers(searchField.getText(), critere);
    }
    
    // 🔍 Méthode pour filtrer les utilisateurs par critère spécifique
    private void filterUsers(String searchText, String critere) {
        if (searchText == null || searchText.trim().isEmpty()) {
            filteredList.setAll(usersList);
            return;
        }
        
        String lowerCaseSearch = searchText.toLowerCase().trim();
        String critereValue = critere.toLowerCase();
        
        List<User> filtered = usersList.stream()
            .filter(user -> {
                switch(critereValue) {
                    case "nom":
                        return user.getNom().toLowerCase().contains(lowerCaseSearch) ||
                               user.getPrenom().toLowerCase().contains(lowerCaseSearch);
                    
                    case "email":
                        return user.getEmail().toLowerCase().contains(lowerCaseSearch);
                    
                    case "rôle":
                    case "role":
                        return user.getRole().toLowerCase().contains(lowerCaseSearch);
                    
                    case "tous les champs":
                    default: // recherche générale sur tous les champs
                        return user.getNom().toLowerCase().contains(lowerCaseSearch) ||
                               user.getPrenom().toLowerCase().contains(lowerCaseSearch) ||
                               user.getEmail().toLowerCase().contains(lowerCaseSearch) ||
                               user.getTelephone().toLowerCase().contains(lowerCaseSearch) ||
                               user.getRole().toLowerCase().contains(lowerCaseSearch) ||
                               String.valueOf(user.getIdUtilisateur()).contains(lowerCaseSearch) ||
                               user.getDateNaissance().toString().contains(lowerCaseSearch);
                }
            })
            .collect(Collectors.toList());
        
        filteredList.setAll(filtered);
        
        System.out.println("Recherche par '" + critere + "' : " + filtered.size() + " résultat(s)");
    }
    
    // 🔄 Réinitialiser la recherche
    @FXML
    private void resetSearch() {
        searchField.clear();
        searchCriteriaCombo.setValue("Tous les champs");
        filteredList.setAll(usersList);
    }

    // 🔵 Bouton Modifier
    private void addButtonModifier() {

        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = param -> new TableCell<>() {

            private final Button btn = new Button("Modifier");

            {
                btn.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierUser.fxml"));
                        Parent root = loader.load();

                        // Envoyer les données au contrôleur ModifierUser
                        ModifierUser controller = loader.getController();
                        controller.setUser(user);

                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Modifier Utilisateur");
                        stage.showAndWait();

                        refreshTable();

                    } catch (IOException e) {
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

    // 🔴 Bouton Supprimer
    private void addButtonSupprimer() {

        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = param -> new TableCell<>() {

            private final Button btn = new Button("Supprimer");

            {
                btn.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());

                    serviceUser.supprimer(user.getIdUtilisateur());
                    
                    // Supprimer des deux listes
                    usersList.remove(user);
                    filteredList.remove(user);

                    System.out.println("Utilisateur supprimé : " + user.getIdUtilisateur());
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

    // 🔄 Rafraîchir le tableau
    private void refreshTable() {
        usersList.clear();
        usersList.addAll(serviceUser.getAll());
        filteredList.setAll(usersList); // Mettre à jour aussi la liste filtrée
    }
}