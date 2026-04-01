package faune.Marine.services;

import faune.Marine.entities.DetectionDrone;
import faune.Marine.tools.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDetectionDrone {
    
    private Connection cnx;
    
    public ServiceDetectionDrone() {
        this.cnx = DataSource.getInstance().getCnx();
    }
    
    public void ajouter(DetectionDrone d) {
        String req = "INSERT INTO detection_drone (id_mission, espece, nombre_individus, " +
                     "latitude, longitude, comportement, confiance_ia, image_path, timestamp) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, d.getIdMission());
            pst.setString(2, d.getEspece());
            pst.setInt(3, d.getNombreIndividus());
            pst.setDouble(4, d.getLatitude());
            pst.setDouble(5, d.getLongitude());
            pst.setString(6, d.getComportement());
            pst.setString(7, d.getConfianceIA());
            pst.setString(8, d.getImagePath());
            pst.setString(9, d.getTimestamp());
            
            pst.executeUpdate();
            System.out.println("✅ Détection drone ajoutée !");
            
        } catch (SQLException ex) {
            System.err.println("❌ Erreur ajout détection: " + ex.getMessage());
        }
    }
    
    public void modifier(DetectionDrone d) {
        String req = "UPDATE detection_drone SET espece=?, nombre_individus=?, " +
                     "latitude=?, longitude=?, comportement=?, confiance_ia=?, image_path=?, timestamp=? " +
                     "WHERE id_detection=?";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setString(1, d.getEspece());
            pst.setInt(2, d.getNombreIndividus());
            pst.setDouble(3, d.getLatitude());
            pst.setDouble(4, d.getLongitude());
            pst.setString(5, d.getComportement());
            pst.setString(6, d.getConfianceIA());
            pst.setString(7, d.getImagePath());
            pst.setString(8, d.getTimestamp());
            pst.setInt(9, d.getIdDetection());
            
            pst.executeUpdate();
            System.out.println("✅ Détection modifiée !");
            
        } catch (SQLException ex) {
            System.err.println("❌ Erreur modification détection: " + ex.getMessage());
        }
    }
    
    public void supprimer(int id) {
        String req = "DELETE FROM detection_drone WHERE id_detection=?";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("✅ Détection supprimée !");
            
        } catch (SQLException ex) {
            System.err.println("❌ Erreur suppression détection: " + ex.getMessage());
        }
    }
    
    public List<DetectionDrone> getDetectionsByMission(int idMission) {
        List<DetectionDrone> list = new ArrayList<>();
        String req = "SELECT * FROM detection_drone WHERE id_mission = ? ORDER BY timestamp DESC";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, idMission);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                DetectionDrone d = new DetectionDrone();
                d.setIdDetection(rs.getInt("id_detection"));
                d.setIdMission(rs.getInt("id_mission"));
                d.setEspece(rs.getString("espece"));
                d.setNombreIndividus(rs.getInt("nombre_individus"));
                d.setLatitude(rs.getDouble("latitude"));
                d.setLongitude(rs.getDouble("longitude"));
                d.setComportement(rs.getString("comportement"));
                d.setConfianceIA(rs.getString("confiance_ia"));
                d.setImagePath(rs.getString("image_path"));
                d.setTimestamp(rs.getString("timestamp"));
                list.add(d);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur getDetectionsByMission: " + ex.getMessage());
        }
        return list;
    }
    
    public List<DetectionDrone> getAllDetections() {
        List<DetectionDrone> list = new ArrayList<>();
        String req = "SELECT * FROM detection_drone ORDER BY timestamp DESC";
        
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(req)) {
            
            while (rs.next()) {
                DetectionDrone d = new DetectionDrone();
                d.setIdDetection(rs.getInt("id_detection"));
                d.setIdMission(rs.getInt("id_mission"));
                d.setEspece(rs.getString("espece"));
                d.setNombreIndividus(rs.getInt("nombre_individus"));
                d.setLatitude(rs.getDouble("latitude"));
                d.setLongitude(rs.getDouble("longitude"));
                d.setComportement(rs.getString("comportement"));
                d.setConfianceIA(rs.getString("confiance_ia"));
                d.setImagePath(rs.getString("image_path"));
                d.setTimestamp(rs.getString("timestamp"));
                list.add(d);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur getAllDetections: " + ex.getMessage());
        }
        return list;
    }
}