package faune.Marine.services;

import faune.Marine.entities.FauneMarine;
import faune.Marine.entities.Observation;
import faune.Marine.tools.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceFauneMarine implements IService<FauneMarine> {

    private Connection cnx;

    public ServiceFauneMarine() {
        this.cnx = DataSource.getInstance().getCnx();
    }

    public void ajouter(FauneMarine f) {
        String req = "INSERT INTO faune_marine (espece, etat, description) VALUES (?, ?, ?)";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setString(1, f.getEspece());
            pst.setString(2, f.getEtat());
            pst.setString(3, f.getDescription());
            
            pst.executeUpdate();
            System.out.println("✅ Faune marine ajoutée !");
            
        } catch (SQLException ex) {
            System.err.println("❌ Erreur ajout: " + ex.getMessage());
        }
    }

    public void modifier(FauneMarine f) {
        String req = "UPDATE faune_marine SET espece=?, etat=?, description=? WHERE id_animal=?";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setString(1, f.getEspece());
            pst.setString(2, f.getEtat());
            pst.setString(3, f.getDescription());
            pst.setInt(4, f.getIdAnimal());
            
            pst.executeUpdate();
            System.out.println("✅ Faune marine modifiée !");
            
        } catch (SQLException ex) {
            System.err.println("❌ Erreur modification: " + ex.getMessage());
        }
    }

    public void supprimer(int id) {
        String req = "DELETE FROM faune_marine WHERE id_animal=?";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("✅ Faune marine supprimée !");
            
        } catch (SQLException ex) {
            System.err.println("❌ Erreur suppression: " + ex.getMessage());
        }
    }

    public FauneMarine getOne(int id) {
        FauneMarine f = null;
        String req = "SELECT * FROM faune_marine WHERE id_animal=?";
        
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                f = new FauneMarine();
                f.setIdAnimal(rs.getInt("id_animal"));
                f.setEspece(rs.getString("espece"));
                f.setEtat(rs.getString("etat"));
                f.setDescription(rs.getString("description"));
            }
            rs.close();
            
        } catch (SQLException ex) {
            System.err.println("❌ Erreur getOne: " + ex.getMessage());
        }
        return f;
    }

    @Override
    public List<FauneMarine> getAll() {
        List<FauneMarine> list = new ArrayList<>();
        String req = "SELECT * FROM faune_marine";
        
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(req)) {
            
            while (rs.next()) {
                FauneMarine f = new FauneMarine();
                f.setIdAnimal(rs.getInt("id_animal"));
                f.setEspece(rs.getString("espece"));
                f.setEtat(rs.getString("etat"));
                f.setDescription(rs.getString("description"));
                list.add(f);
            }
            
        } catch (SQLException ex) {
            System.err.println("❌ Erreur getAll: " + ex.getMessage());
        }
        return list;
    }

    // Méthodes non utilisées (à implémenter si nécessaire)
    @Override
    public FauneMarine getOne(FauneMarine f) { return null; }

    public List<FauneMarine> getAll(FauneMarine t) { return getAll(); }

    public List<Observation> getAll(Observation t) { return null; }

    public Observation getOne(Observation t) { return null; }

	@Override
	public void add(FauneMarine t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(FauneMarine t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(FauneMarine t) {
		// TODO Auto-generated method stub
		
	}
}