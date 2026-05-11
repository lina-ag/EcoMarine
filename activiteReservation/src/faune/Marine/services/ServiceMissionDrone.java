package faune.Marine.services;

import faune.Marine.entities.MissionDrone;
import faune.Marine.tools.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceMissionDrone implements IService<MissionDrone> {
    
    private Connection cnx;
    
    public ServiceMissionDrone() {
        this.cnx = DataSource.getInstance().getCnx();
    }
    
    public void ajouter(MissionDrone m) {
        String req = "INSERT INTO mission_drone (date_mission, heure_debut, heure_fin, zone_survolee, " +
                     "distance_parcourue, altitude_vol, conditions_vol, observations) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setDate(1, Date.valueOf(m.getDateMission()));
            pst.setTime(2, Time.valueOf(m.getHeureDebut()));
            pst.setTime(3, Time.valueOf(m.getHeureFin()));
            pst.setString(4, m.getZoneSurvolee());
            pst.setDouble(5, m.getDistanceParcourue());
            pst.setInt(6, m.getAltitudeVol());
            pst.setString(7, m.getConditionsVol());
            pst.setString(8, m.getObservations());
            
            pst.executeUpdate();
            System.out.println("✅ Mission drone ajoutée !");
            
        } catch (SQLException ex) {
            System.err.println("❌ Erreur ajout mission: " + ex.getMessage());
        }
    }
    
    public void modifier(MissionDrone m) {
        String req = "UPDATE mission_drone SET date_mission=?, heure_debut=?, heure_fin=?, " +
                     "zone_survolee=?, distance_parcourue=?, altitude_vol=?, conditions_vol=?, observations=? " +
                     "WHERE id_mission=?";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setDate(1, Date.valueOf(m.getDateMission()));
            pst.setTime(2, Time.valueOf(m.getHeureDebut()));
            pst.setTime(3, Time.valueOf(m.getHeureFin()));
            pst.setString(4, m.getZoneSurvolee());
            pst.setDouble(5, m.getDistanceParcourue());
            pst.setInt(6, m.getAltitudeVol());
            pst.setString(7, m.getConditionsVol());
            pst.setString(8, m.getObservations());
            pst.setInt(9, m.getIdMission());
            
            pst.executeUpdate();
            System.out.println("✅ Mission drone modifiée !");
            
        } catch (SQLException ex) {
            System.err.println("❌ Erreur modification mission: " + ex.getMessage());
        }
    }
    
    public void supprimer(int id) {
        String req = "DELETE FROM mission_drone WHERE id_mission=?";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("✅ Mission drone supprimée !");
            
        } catch (SQLException ex) {
            System.err.println("❌ Erreur suppression mission: " + ex.getMessage());
        }
    }
    
    public MissionDrone getOne(int id) {
        String req = "SELECT * FROM mission_drone WHERE id_mission=?";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                MissionDrone m = new MissionDrone();
                m.setIdMission(rs.getInt("id_mission"));
                m.setDateMission(rs.getDate("date_mission").toLocalDate());
                m.setHeureDebut(rs.getTime("heure_debut").toLocalTime());
                m.setHeureFin(rs.getTime("heure_fin").toLocalTime());
                m.setZoneSurvolee(rs.getString("zone_survolee"));
                m.setDistanceParcourue(rs.getDouble("distance_parcourue"));
                m.setAltitudeVol(rs.getInt("altitude_vol"));
                m.setConditionsVol(rs.getString("conditions_vol"));
                m.setObservations(rs.getString("observations"));
                return m;
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur getOne mission: " + ex.getMessage());
        }
        return null;
    }
    
    @Override
    public List<MissionDrone> getAll() {
        List<MissionDrone> list = new ArrayList<>();
        String req = "SELECT * FROM mission_drone ORDER BY date_mission DESC";
        
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(req)) {
            
            while (rs.next()) {
                MissionDrone m = new MissionDrone();
                m.setIdMission(rs.getInt("id_mission"));
                m.setDateMission(rs.getDate("date_mission").toLocalDate());
                m.setHeureDebut(rs.getTime("heure_debut").toLocalTime());
                m.setHeureFin(rs.getTime("heure_fin").toLocalTime());
                m.setZoneSurvolee(rs.getString("zone_survolee"));
                m.setDistanceParcourue(rs.getDouble("distance_parcourue"));
                m.setAltitudeVol(rs.getInt("altitude_vol"));
                m.setConditionsVol(rs.getString("conditions_vol"));
                m.setObservations(rs.getString("observations"));
                list.add(m);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur getAll missions: " + ex.getMessage());
        }
        return list;
    }

	@Override
	public void add(MissionDrone t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(MissionDrone t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(MissionDrone t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MissionDrone getOne(MissionDrone t) {
		// TODO Auto-generated method stub
		return null;
	}
}