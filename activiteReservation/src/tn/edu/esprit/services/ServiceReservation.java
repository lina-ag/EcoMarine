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
    


}