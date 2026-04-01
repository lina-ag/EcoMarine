package dao;

import model.ActionNettoyage;
import model.Volontaire;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VolontaireDAO {

    public List<Volontaire> findAll() {
        List<Volontaire> list = new ArrayList<>();
        String sql = """
                SELECT v.id_volontaire, v.nom, v.contact, v.id_action, a.lieu
                FROM volontaire v
                JOIN action_nettoyage a ON v.id_action = a.id_action
                ORDER BY v.id_volontaire DESC
                """;

        try {
            Connection cnx = DBConnection.getConnection();
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Volontaire v = new Volontaire();
                v.setIdVolontaire(rs.getInt("id_volontaire"));
                v.setNom(rs.getString("nom"));
                v.setContact(rs.getString("contact"));
                v.setIdAction(rs.getInt("id_action"));
                v.setActionNom(rs.getString("lieu"));
                list.add(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void insert(Volontaire v) {
        String sql = "INSERT INTO volontaire (nom, contact, id_action) VALUES (?, ?, ?)";

        try {
            Connection cnx = DBConnection.getConnection();
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, v.getNom());
            ps.setString(2, v.getContact());
            ps.setInt(3, v.getIdAction());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Volontaire v) {
        String sql = "UPDATE volontaire SET nom=?, contact=?, id_action=? WHERE id_volontaire=?";

        try {
            Connection cnx = DBConnection.getConnection();
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, v.getNom());
            ps.setString(2, v.getContact());
            ps.setInt(3, v.getIdAction());
            ps.setInt(4, v.getIdVolontaire());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM volontaire WHERE id_volontaire=?";

        try {
            Connection cnx = DBConnection.getConnection();
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ActionNettoyage> getActions() {
        return new ActionNettoyageDAO().findAll();
    }
}