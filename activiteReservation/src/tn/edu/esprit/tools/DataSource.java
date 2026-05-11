package tn.edu.esprit.tools;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {

    private Connection cnx;
    private static DataSource instance;
    private final String url = "jdbc:mysql://localhost:3306/EcoMarine?useSSL=false&serverTimezone=UTC";
    private final String user = "root";
    private final String password = "";

    private DataSource() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            cnx = DriverManager.getConnection(url, user, password);
            System.out.println(" Connecté à la base EcoMarine !");
        } catch (ClassNotFoundException e) {
            System.out.println(" Driver MySQL non trouvé!");
        } catch (SQLException e) {
            System.out.println(" Erreur de connexion: " + e.getMessage());
        }
    }

    public static DataSource getInstance() {
        if (instance == null) {
            instance = new DataSource();
        }
        return instance;
    }

    public Connection getConnection() {
        return cnx;
    }
}