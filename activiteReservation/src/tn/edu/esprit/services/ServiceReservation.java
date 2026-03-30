package tn.edu.esprit.services;

import java.sql.Connection;
import java.sql.ResultSet;

import java.sql.PreparedStatement;
import tn.edu.esprit.entities.Reservation;
import tn.edu.esprit.tools.DataSource;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class ServiceReservation {
    
    Connection cnx = DataSource.getInstance().getConnection();

    public void ajouter(Reservation r) {
        try {

            String checkQuery = "SELECT COUNT(*) FROM activite_ecologique WHERE id_activite = ?";
            PreparedStatement checkStmt = cnx.prepareStatement(checkQuery);
            checkStmt.setInt(1, r.getIdActivite());

            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("Erreur : activité inexistante !");
                return;
            }

            String req = "INSERT INTO reservation (nom, email, nombre_personnes, date_reservation, id_activite) VALUES (?, ?, ?, ?, ?)";

            PreparedStatement pstm = cnx.prepareStatement(req);
            pstm.setString(1, r.getNom());
            pstm.setString(2, r.getEmail());
            pstm.setInt(3, r.getNombre_personnes());
            pstm.setString(4, r.getDate_reservation());
            pstm.setInt(5, r.getIdActivite());

            pstm.executeUpdate();

            System.out.println("Réservation ajoutée avec succès !");

        } catch (Exception e) {
      
        	e.printStackTrace();
        }
        }
    
  
    public void supprimer(int id) {
        try {
            String req = "DELETE FROM reservation WHERE id_reservation = " + id;
            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);
            System.out.println("Réservation supprimée !");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public void modifier(Reservation r) {
        try {
            String req = "UPDATE reservation SET "
                    + "nom='" + r.getNom() + "',"
                    + "email='" + r.getEmail() + "',"
                    + "nombre_personnes=" + r.getNombre_personnes() + ","
                    + "date_reservation='" + r.getDate_reservation() + "',"
                    + "id_activite=" + r.getIdActivite()
                    + " WHERE id_reservation=" + r.getId();

            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);
            System.out.println("Réservation modifiée !");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public List<Reservation> getAll() {

        String req = "SELECT * FROM reservation";
        ArrayList<Reservation> list = new ArrayList<>();

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);

            while (rs.next()) {
                Reservation r = new Reservation();

                r.setId(rs.getInt("id_reservation"));
                r.setNom(rs.getString("nom"));
                r.setEmail(rs.getString("email"));
                r.setNombre_personnes(rs.getInt("nombre_personnes"));
                r.setDate_reservation(rs.getString("date_reservation"));
                r.setIdActivite(rs.getInt("id_activite"));

                list.add(r);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return list;
    }
    
    public int getTotalReservations() {
        try {
            ResultSet rs = cnx.createStatement()
                .executeQuery("SELECT COUNT(*) FROM reservation");
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public int getTotalParticipants() {
        try {
            ResultSet rs = cnx.createStatement()
                .executeQuery("SELECT SUM(nombre_personnes) FROM reservation");
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public List<String[]> getReservationsParMois() {
        List<String[]> list = new ArrayList<>();
        try {
            String sql = """
                SELECT DATE_FORMAT(date_reservation, '%M') as mois,
                       MONTH(date_reservation) as num_mois,
                       COUNT(*) as total
                FROM reservation
                WHERE YEAR(date_reservation) = YEAR(CURDATE())
                GROUP BY num_mois, mois
                ORDER BY num_mois
                """;
            ResultSet rs = cnx.createStatement().executeQuery(sql);
            while (rs.next())
                list.add(new String[]{rs.getString("mois"),
                                       rs.getString("total")});
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<String[]> getTopActivites() {
        List<String[]> list = new ArrayList<>();
        try {
            String sql = """
                SELECT a.nom_activite, COUNT(r.id_reservation) as nb
                FROM reservation r
                JOIN activite_ecologique a ON r.id_activite = a.id_activite
                GROUP BY a.nom_activite
                ORDER BY nb DESC
                LIMIT 5
                """;
            ResultSet rs = cnx.createStatement().executeQuery(sql);
            while (rs.next())
                list.add(new String[]{rs.getString("nom_activite"),
                                       rs.getString("nb")});
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

}