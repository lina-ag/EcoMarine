package app;

import java.sql.*;
import java.util.*;

public class VolontaireDAO {

    public List<Volontaire> findByAction(int idAction) {
        List<Volontaire> list = new ArrayList<>();
        String sql = "SELECT * FROM volontaire WHERE id_action=? ORDER BY id_volontaire DESC";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idAction);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Volontaire(
                            rs.getInt("id_volontaire"),
                            rs.getString("nom"),
                            rs.getString("contact"),
                            rs.getInt("id_action")
                    ));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public void insert(String nom, String contact, int idAction) {
        String sql = "INSERT INTO volontaire(nom, contact, id_action) VALUES(?,?,?)";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nom);
            ps.setString(2, contact);
            ps.setInt(3, idAction);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void update(int idVol, String nom, String contact) {
        String sql = "UPDATE volontaire SET nom=?, contact=? WHERE id_volontaire=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nom);
            ps.setString(2, contact);
            ps.setInt(3, idVol);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void delete(int idVol) {
        String sql = "DELETE FROM volontaire WHERE id_volontaire=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idVol);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}