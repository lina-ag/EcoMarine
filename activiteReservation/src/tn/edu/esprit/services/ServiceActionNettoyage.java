package tn.edu.esprit.services;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import tn.edu.esprit.entities.ActionNettoyage;
import tn.edu.esprit.tools.DataSource;

public class ServiceActionNettoyage {

    private Connection cnx;

    public ServiceActionNettoyage() {
        cnx = DataSource.getInstance().getConnection();
    }

    public List<ActionNettoyage> findAll() {
        List<ActionNettoyage> list = new ArrayList<>();
        String sql = "SELECT * FROM action_nettoyage ORDER BY id_action DESC";

        try (PreparedStatement ps = cnx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new ActionNettoyage(
                    rs.getInt("id_action"),
                    rs.getString("date_action"),
                    rs.getString("lieu")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public void insert(String dateAction, String lieu) {
        String sql = "INSERT INTO action_nettoyage(date_action, lieu) VALUES(?,?)";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, dateAction);
            ps.setString(2, lieu);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(int idAction, String dateAction, String lieu) {
        String sql = "UPDATE action_nettoyage SET date_action=?, lieu=? WHERE id_action=?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, dateAction);
            ps.setString(2, lieu);
            ps.setInt(3, idAction);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int idAction) {
        String sql = "DELETE FROM action_nettoyage WHERE id_action=?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, idAction);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}