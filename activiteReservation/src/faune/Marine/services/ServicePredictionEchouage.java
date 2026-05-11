package faune.Marine.services;

import faune.Marine.entities.PredictionEchouage;
import faune.Marine.tools.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicePredictionEchouage implements IService<PredictionEchouage> {
    
    private Connection cnx;
    
    public ServicePredictionEchouage() {
        this.cnx = DataSource.getInstance().getCnx();
    }
    
    public void ajouter(PredictionEchouage p) {
        String req = "INSERT INTO prediction_echouage (date_prediction, zone, niveau_risque, " +
                     "espece_concernee, temperature_eau, conditions_meteo, recommandations) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setDate(1, Date.valueOf(p.getDatePrediction()));
            pst.setString(2, p.getZone());
            pst.setInt(3, p.getNiveauRisque());
            pst.setString(4, p.getEspeceConcernee());
            pst.setDouble(5, p.getTemperatureEau());
            pst.setString(6, p.getConditionsMeteo());
            pst.setString(7, p.getRecommandations());
            
            pst.executeUpdate();
            System.out.println("✅ Prédiction d'échouage ajoutée !");
            
        } catch (SQLException ex) {
            System.err.println("❌ Erreur ajout prédiction: " + ex.getMessage());
        }
    }
    
    public void modifier(PredictionEchouage p) {
        String req = "UPDATE prediction_echouage SET date_prediction=?, zone=?, niveau_risque=?, " +
                     "espece_concernee=?, temperature_eau=?, conditions_meteo=?, recommandations=? " +
                     "WHERE id_prediction=?";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setDate(1, Date.valueOf(p.getDatePrediction()));
            pst.setString(2, p.getZone());
            pst.setInt(3, p.getNiveauRisque());
            pst.setString(4, p.getEspeceConcernee());
            pst.setDouble(5, p.getTemperatureEau());
            pst.setString(6, p.getConditionsMeteo());
            pst.setString(7, p.getRecommandations());
            pst.setInt(8, p.getIdPrediction());
            
            pst.executeUpdate();
            System.out.println("✅ Prédiction modifiée !");
            
        } catch (SQLException ex) {
            System.err.println("❌ Erreur modification: " + ex.getMessage());
        }
    }
    
    public void supprimer(int id) {
        String req = "DELETE FROM prediction_echouage WHERE id_prediction=?";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("✅ Prédiction supprimée !");
            
        } catch (SQLException ex) {
            System.err.println("❌ Erreur suppression: " + ex.getMessage());
        }
    }
    
    public PredictionEchouage getOne(int id) {
        String req = "SELECT * FROM prediction_echouage WHERE id_prediction=?";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                PredictionEchouage p = new PredictionEchouage();
                p.setIdPrediction(rs.getInt("id_prediction"));
                p.setDatePrediction(rs.getDate("date_prediction").toLocalDate());
                p.setZone(rs.getString("zone"));
                p.setNiveauRisque(rs.getInt("niveau_risque"));
                p.setEspeceConcernee(rs.getString("espece_concernee"));
                p.setTemperatureEau(rs.getDouble("temperature_eau"));
                p.setConditionsMeteo(rs.getString("conditions_meteo"));
                p.setRecommandations(rs.getString("recommandations"));
                return p;
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur getOne: " + ex.getMessage());
        }
        return null;
    }
    
    @Override
    public List<PredictionEchouage> getAll() {
        List<PredictionEchouage> list = new ArrayList<>();
        String req = "SELECT * FROM prediction_echouage ORDER BY date_prediction DESC";
        
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(req)) {
            
            while (rs.next()) {
                PredictionEchouage p = new PredictionEchouage();
                p.setIdPrediction(rs.getInt("id_prediction"));
                p.setDatePrediction(rs.getDate("date_prediction").toLocalDate());
                p.setZone(rs.getString("zone"));
                p.setNiveauRisque(rs.getInt("niveau_risque"));
                p.setEspeceConcernee(rs.getString("espece_concernee"));
                p.setTemperatureEau(rs.getDouble("temperature_eau"));
                p.setConditionsMeteo(rs.getString("conditions_meteo"));
                p.setRecommandations(rs.getString("recommandations"));
                list.add(p);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur getAll: " + ex.getMessage());
        }
        return list;
    }
    
    // Méthode innovante : Prédictions à risque élevé
    public List<PredictionEchouage> getPredictionsRisqueEleve(int seuil) {
        List<PredictionEchouage> list = new ArrayList<>();
        String req = "SELECT * FROM prediction_echouage WHERE niveau_risque >= ? ORDER BY niveau_risque DESC";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, seuil);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                PredictionEchouage p = new PredictionEchouage();
                p.setIdPrediction(rs.getInt("id_prediction"));
                p.setDatePrediction(rs.getDate("date_prediction").toLocalDate());
                p.setZone(rs.getString("zone"));
                p.setNiveauRisque(rs.getInt("niveau_risque"));
                p.setEspeceConcernee(rs.getString("espece_concernee"));
                p.setTemperatureEau(rs.getDouble("temperature_eau"));
                p.setConditionsMeteo(rs.getString("conditions_meteo"));
                p.setRecommandations(rs.getString("recommandations"));
                list.add(p);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur getPredictionsRisqueEleve: " + ex.getMessage());
        }
        return list;
    }

	@Override
	public void add(PredictionEchouage t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(PredictionEchouage t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(PredictionEchouage t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PredictionEchouage getOne(PredictionEchouage t) {
		// TODO Auto-generated method stub
		return null;
	}
}