package dao;

import model.Biodiversite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BiodiversiteDAO {

    public List<Biodiversite> findAll() {
        List<Biodiversite> list = new ArrayList<>();
        String sql = "SELECT * FROM biodiversite ORDER BY id_biodiversite DESC";

        try {
            Connection cnx = DBConnection.getConnection();
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Biodiversite b = new Biodiversite();
                b.setIdBiodiversite(rs.getInt("id_biodiversite"));
                b.setEspece(rs.getString("espece"));
                b.setZone(rs.getString("zone"));
                b.setNombre(rs.getInt("nombre"));
                b.setDateObservation(rs.getString("date_observation"));
                list.add(b);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public void insert(Biodiversite b) {
        String sql = "INSERT INTO biodiversite (espece, zone, nombre, date_observation) VALUES (?, ?, ?, ?)";

        try {
            Connection cnx = DBConnection.getConnection();
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, b.getEspece());
            ps.setString(2, b.getZone());
            ps.setInt(3, b.getNombre());
            ps.setString(4, b.getDateObservation());
            ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void update(Biodiversite b) {
        String sql = "UPDATE biodiversite SET espece=?, zone=?, nombre=?, date_observation=? WHERE id_biodiversite=?";

        try {
            Connection cnx = DBConnection.getConnection();
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, b.getEspece());
            ps.setString(2, b.getZone());
            ps.setInt(3, b.getNombre());
            ps.setString(4, b.getDateObservation());
            ps.setInt(5, b.getIdBiodiversite());
            ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM biodiversite WHERE id_biodiversite=?";

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
        String sql = "SELECT COUNT(*) FROM biodiversite";
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

    public java.util.Map<String, Integer> getEvolutionData() {
        java.util.Map<String, Integer> map = new java.util.LinkedHashMap<>();
        String sql = """
            SELECT date_observation, SUM(nombre) as total
            FROM biodiversite
            GROUP BY date_observation
            ORDER BY date_observation
            """;

        try {
            Connection cnx = DBConnection.getConnection();
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                map.put(rs.getString("date_observation"), rs.getInt("total"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
}