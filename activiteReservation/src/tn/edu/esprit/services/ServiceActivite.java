package tn.edu.esprit.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import tn.edu.esprit.entities.ActiviteEcologique;
import tn.edu.esprit.tools.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceActivite {

    Connection cnx = DataSource.getInstance().getConnection();
    public void ajouter(ActiviteEcologique a) {
        try {
        	
            String req = "INSERT INTO activite_ecologique (nom_activite, description, date_activite, capacite) VALUES ('"
                    + a.getNom() + "','" + a.getDescription() + "','" + a.getDate() + "'," + a.getCapacite() + ")";

            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);
            System.out.println("Activité ajoutée !");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void supprimer(int id) {
        try {
            String req = "DELETE FROM activite_ecologique WHERE id_activite = " + id;
            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);
            System.out.println("Activité supprimée !");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void modifier(ActiviteEcologique a) {
        try {
            String req = "UPDATE activite_ecologique SET "
                    + "nom_activite='" + a.getNom() + "',"
                    + "description='" + a.getDescription() + "',"
                    + "date_activite='" + a.getDate() + "',"
                    + "capacite=" + a.getCapacite()
                    + " WHERE id_activite=" + a.getIdActivite();

            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);
            System.out.println("Activité modifiée !");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public List<ActiviteEcologique> getAll() {

        String req = "SELECT * FROM activite_ecologique";
        ArrayList<ActiviteEcologique> list = new ArrayList<>();

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);

            while (rs.next()) {
                ActiviteEcologique a = new ActiviteEcologique();
                a.setIdActivite(rs.getInt(1));
                //a.setIdActivite(rs.getInt("id_activite"));
                a.setNom(rs.getString("nom_activite"));
                a.setDescription(rs.getString("description"));
                a.setDate(rs.getString("date_activite"));
                a.setCapacite(rs.getInt("capacite"));

                list.add(a);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return list;
    }

    public boolean existeActivite(int id) {

        String sql = "SELECT id_activite FROM activite_ecologique WHERE id_activite = ?";

        try {
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
