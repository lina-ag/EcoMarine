package faune.Marine.services;

import faune.Marine.entities.Observation;
import faune.Marine.entities.FauneMarine;
import faune.Marine.tools.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceObservation implements IService<Observation> {

    private Connection cnx;
    private ServiceFauneMarine serviceFaune;

    public ServiceObservation() {
        this.cnx = DataSource.getInstance().getCnx();
        this.serviceFaune = new ServiceFauneMarine();
    }

    public void ajouter(Observation o) {
        String req = "INSERT INTO observation (date_observation, temperature, meteo, id_animal) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setDate(1, Date.valueOf(o.getDateObservation()));
            pst.setDouble(2, o.getTemperature());
            pst.setString(3, o.getMeteo());
            pst.setInt(4, o.getAnimal().getIdAnimal());
            
            pst.executeUpdate();
            System.out.println("✅ Observation ajoutée !");
            
        } catch (SQLException ex) {
            System.err.println("❌ Erreur ajout observation: " + ex.getMessage());
        }
    }

    public void modifier(Observation o) {
        String req = "UPDATE observation SET date_observation=?, temperature=?, meteo=?, id_animal=? WHERE id_observation=?";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setDate(1, Date.valueOf(o.getDateObservation()));
            pst.setDouble(2, o.getTemperature());
            pst.setString(3, o.getMeteo());
            pst.setInt(4, o.getAnimal().getIdAnimal());
            pst.setInt(5, o.getIdObservation());
            
            pst.executeUpdate();
            System.out.println("✅ Observation modifiée !");
            
        } catch (SQLException ex) {
            System.err.println("❌ Erreur modification observation: " + ex.getMessage());
        }
    }

    public void supprimer(int id) {
        String req = "DELETE FROM observation WHERE id_observation=?";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("✅ Observation supprimée !");
            
        } catch (SQLException ex) {
            System.err.println("❌ Erreur suppression observation: " + ex.getMessage());
        }
    }

    public Observation getOne(int id) {
        Observation o = null;
        String req = "SELECT * FROM observation WHERE id_observation=?";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                o = new Observation();
                o.setIdObservation(rs.getInt("id_observation"));
                o.setDateObservation(rs.getDate("date_observation").toLocalDate());
                o.setTemperature(rs.getDouble("temperature"));
                o.setMeteo(rs.getString("meteo"));
                
                int idAnimal = rs.getInt("id_animal");
                FauneMarine animal = serviceFaune.getOne(idAnimal);
                o.setAnimal(animal);
            }
            rs.close();
            
        } catch (SQLException ex) {
            System.err.println("❌ Erreur getOne observation: " + ex.getMessage());
        }
        return o;
    }

    @Override
    public List<Observation> getAll() {
        List<Observation> list = new ArrayList<>();
        String req = "SELECT * FROM observation";
        
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(req)) {
            
            while (rs.next()) {
                Observation o = new Observation();
                o.setIdObservation(rs.getInt("id_observation"));
                o.setDateObservation(rs.getDate("date_observation").toLocalDate());
                o.setTemperature(rs.getDouble("temperature"));
                o.setMeteo(rs.getString("meteo"));
                
                int idAnimal = rs.getInt("id_animal");
                FauneMarine animal = serviceFaune.getOne(idAnimal);
                o.setAnimal(animal);
                
                list.add(o);
            }
            
        } catch (SQLException ex) {
            System.err.println("❌ Erreur getAll observations: " + ex.getMessage());
        }
        return list;
    }

    // Méthodes non utilisées
    @Override
    public Observation getOne(Observation t) { return null; }

    public List<Observation> getAll(Observation t) { return getAll(); }

    public FauneMarine getOne(FauneMarine f) { return null; }

    public List<FauneMarine> getAll(FauneMarine t) { return null; }

	@Override
	public void add(Observation t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Observation t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Observation t) {
		// TODO Auto-generated method stub
		
	}
}