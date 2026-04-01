package dao;

import model.Dechet;
import model.ZonePlage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DechetDAO {

    public List<Dechet> findAll() {
        List<Dechet> list = new ArrayList<>();

        String sql = """
                SELECT d.id_dechet, d.type_dechet, d.quantite, d.id_zone, z.nom_zone
                FROM dechet d
                JOIN zone_plage z ON d.id_zone = z.id_zone
                ORDER BY d.id_dechet DESC
                """;

        try {
            Connection cnx = DBConnection.getConnection();
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Dechet d = new Dechet();
                d.setIdDechet(rs.getInt("id_dechet"));
                d.setTypeDechet(rs.getString("type_dechet"));
                d.setQuantite(rs.getDouble("quantite"));
                d.setIdZone(rs.getInt("id_zone"));
                d.setNomZone(rs.getString("nom_zone"));
                list.add(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void insert(Dechet d) {
        String sql = "INSERT INTO dechet (type_dechet, quantite, id_zone) VALUES (?, ?, ?)";

        try {
            Connection cnx = DBConnection.getConnection();
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, d.getTypeDechet());
            ps.setDouble(2, d.getQuantite());
            ps.setInt(3, d.getIdZone());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Dechet d) {
        String sql = "UPDATE dechet SET type_dechet=?, quantite=?, id_zone=? WHERE id_dechet=?";

        try {
            Connection cnx = DBConnection.getConnection();
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, d.getTypeDechet());
            ps.setDouble(2, d.getQuantite());
            ps.setInt(3, d.getIdZone());
            ps.setInt(4, d.getIdDechet());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM dechet WHERE id_dechet=?";

        try {
            Connection cnx = DBConnection.getConnection();
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ZonePlage> getZones() {
        return new ZonePlageDAO().findAll();
    }
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM dechet";
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

    public java.util.Map<String, Double> getDechetDistribution() {
        java.util.Map<String, Double> map = new java.util.LinkedHashMap<>();
        String sql = "SELECT type_dechet, SUM(quantite) as total FROM dechet GROUP BY type_dechet ORDER BY total DESC";

        try {
            Connection cnx = DBConnection.getConnection();
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                map.put(rs.getString("type_dechet"), rs.getDouble("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
}