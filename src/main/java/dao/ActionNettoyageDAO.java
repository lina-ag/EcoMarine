package dao;

import model.ActionNettoyage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ActionNettoyageDAO {

    public List<ActionNettoyage> findAll() {
        List<ActionNettoyage> list = new ArrayList<>();
        String sql = "SELECT * FROM action_nettoyage ORDER BY id_action DESC";

        try {
            Connection cnx = DBConnection.getConnection();
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                ActionNettoyage a = new ActionNettoyage();
                a.setIdAction(rs.getInt("id_action"));
                a.setDateAction(rs.getString("date_action"));
                a.setLieu(rs.getString("lieu"));
                list.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void insert(ActionNettoyage a) {
        String sql = "INSERT INTO action_nettoyage (date_action, lieu) VALUES (?, ?)";

        try {
            Connection cnx = DBConnection.getConnection();
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, a.getDateAction());
            ps.setString(2, a.getLieu());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(ActionNettoyage a) {
        String sql = "UPDATE action_nettoyage SET date_action=?, lieu=? WHERE id_action=?";

        try {
            Connection cnx = DBConnection.getConnection();
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, a.getDateAction());
            ps.setString(2, a.getLieu());
            ps.setInt(3, a.getIdAction());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM action_nettoyage WHERE id_action=?";

        try {
            Connection cnx = DBConnection.getConnection();
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}