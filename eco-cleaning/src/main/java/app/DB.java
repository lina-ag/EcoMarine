package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
    private static final String URL =
            "jdbc:mysql://localhost:3306/eco_cleaning?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = ""; // XAMPP par défaut


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void init() {
        String sqlAction = """
            CREATE TABLE IF NOT EXISTS action_nettoyage (
              id_action INT AUTO_INCREMENT PRIMARY KEY,
              date_action DATE NOT NULL,
              lieu VARCHAR(150) NOT NULL
            );
        """;

        String sqlVol = """
            CREATE TABLE IF NOT EXISTS volontaire (
              id_volontaire INT AUTO_INCREMENT PRIMARY KEY,
              nom VARCHAR(100) NOT NULL,
              contact VARCHAR(30),
              id_action INT NOT NULL,
              CONSTRAINT fk_volontaire_action
                FOREIGN KEY (id_action) REFERENCES action_nettoyage(id_action)
                ON DELETE CASCADE ON UPDATE CASCADE
            );
        """;

        try (Connection c = getConnection();
             Statement st = c.createStatement()) {
            st.execute(sqlAction);
            st.execute(sqlVol);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}