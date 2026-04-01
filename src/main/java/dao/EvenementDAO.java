package dao;

import model.Evenement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EvenementDAO {

    public List<Evenement> findAll() {
        List<Evenement> list = new ArrayList<>();
        String sql = "SELECT * FROM evenement ORDER BY id_evenement DESC";

        try {
            Connection cnx = DBConnection.getConnection();
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Evenement e = new Evenement();
                e.setIdEvenement(rs.getInt("id_evenement"));
                e.setNomEvenement(rs.getString("nom_evenement"));
                e.setDateEvenement(rs.getString("date_evenement"));
                e.setLieu(rs.getString("lieu"));
                e.setDescription(rs.getString("description"));
                list.add(e);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public void insert(Evenement e) {
        String sql = "INSERT INTO evenement (nom_evenement, date_evenement, lieu, description) VALUES (?, ?, ?, ?)";

        try {
            Connection cnx = DBConnection.getConnection();
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, e.getNomEvenement());
            ps.setString(2, e.getDateEvenement());
            ps.setString(3, e.getLieu());
            ps.setString(4, e.getDescription());
            ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void update(Evenement e) {
        String sql = "UPDATE evenement SET nom_evenement=?, date_evenement=?, lieu=?, description=? WHERE id_evenement=?";

        try {
            Connection cnx = DBConnection.getConnection();
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, e.getNomEvenement());
            ps.setString(2, e.getDateEvenement());
            ps.setString(3, e.getLieu());
            ps.setString(4, e.getDescription());
            ps.setInt(5, e.getIdEvenement());
            ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM evenement WHERE id_evenement=?";

        try {
            Connection cnx = DBConnection.getConnection();
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM evenement";
        try {
            Connection cnx = DBConnection.getConnection();
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public java.util.Map<String, Integer> getEventsPerMonth() {
        java.util.Map<String, Integer> map = new java.util.LinkedHashMap<>();
        String sql = """
            SELECT DATE_FORMAT(date_evenement, '%Y-%m') as mois, COUNT(*) as total
            FROM evenement
            GROUP BY DATE_FORMAT(date_evenement, '%Y-%m')
            ORDER BY mois
            """;

        try {
            Connection cnx = DBConnection.getConnection();
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                map.put(rs.getString("mois"), rs.getInt("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
}