package dao;

import model.ZonePlage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ZonePlageDAO {

    public List<ZonePlage> findAll() {
        List<ZonePlage> list = new ArrayList<>();
        String sql = "SELECT * FROM zone_plage";

        try {
            Connection cnx = DBConnection.getConnection();
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                ZonePlage z = new ZonePlage();
                z.setIdZone(rs.getInt("id_zone"));
                z.setNomZone(rs.getString("nom_zone"));
                z.setLocalisation(rs.getString("localisation"));
                z.setStatut(rs.getString("statut"));
                list.add(z);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void insert(ZonePlage z) {
        String sql = "INSERT INTO zone_plage (nom_zone, localisation, statut) VALUES (?, ?, ?)";

        try {
            Connection cnx = DBConnection.getConnection();
            PreparedStatement ps = cnx.prepareStatement(sql);

            ps.setString(1, z.getNomZone());
            ps.setString(2, z.getLocalisation());
            ps.setString(3, z.getStatut());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(ZonePlage z) {
        String sql = "UPDATE zone_plage SET nom_zone = ?, localisation = ?, statut = ? WHERE id_zone = ?";

        try {
            Connection cnx = DBConnection.getConnection();
            PreparedStatement ps = cnx.prepareStatement(sql);

            ps.setString(1, z.getNomZone());
            ps.setString(2, z.getLocalisation());
            ps.setString(3, z.getStatut());
            ps.setInt(4, z.getIdZone());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM zone_plage WHERE id_zone = ?";

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