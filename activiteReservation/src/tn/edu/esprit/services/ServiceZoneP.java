package tn.edu.esprit.services;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


import tn.edu.esprit.entities.ZoneProtegee;
import tn.edu.esprit.tools.DataSource;

public class ServiceZoneP implements IServiceZ <ZoneProtegee> {
	Connection cnx ;
	public ServiceZoneP(){
	    this.cnx= DataSource.getInstance().getConnection();
	}
	@Override 
	public boolean ajouter(ZoneProtegee z) {
		try {
            String req = "INSERT INTO zoneP (nomZone, categorieZone, status) VALUES ('"
                    + z.getNomZone() + "','" + z.getCategorieZone() + "','" + z.getStatut() + "')";

            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);

            System.out.println("Zone protégée ajoutée avec succès !");
            return true;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
		
	}

	@Override
	public void modifier(ZoneProtegee z) {
		try {
            String req = "UPDATE zoneP SET "
                    + "nomZone='" + z.getNomZone() + "', "
                    + "categorieZone='" + z.getCategorieZone() + "', "
                    + "status='" + z.getStatut() + "' "
                    + "WHERE idZone=" + z.getIdZone();

            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);

            System.out.println("Zone protégée modifiée avec succès !");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
	}

	@Override
	public void supprimer(int idZone) {
		try {
            String req = "DELETE FROM zoneP WHERE idZone=" + idZone;

            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);

            System.out.println("Zone protégée supprimée !");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
	}

	@Override
	public ZoneProtegee getOne(int idZone) {
		ZoneProtegee zo = null;

        try {
            String req = "SELECT * FROM zoneP WHERE idZone=" + idZone;
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);

            if (rs.next()) {
                zo = new ZoneProtegee();
                zo.setIdZone(rs.getInt("idZone"));
                zo.setNomZone(rs.getString("nomZone"));
                zo.setCategorieZone(rs.getString("categorieZone"));
                zo.setStatut(rs.getString("status"));
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return zo;	
     }

	@Override
	public List<ZoneProtegee> getAll(ZoneProtegee t) {
		String req = "SELECT * FROM `zoneP`";
	    ArrayList<ZoneProtegee> zones = new ArrayList();
	    Statement stm;
	    try {
	        stm = this.cnx.createStatement();
	    
	    
	        ResultSet rs=  stm.executeQuery(req);
	    while (rs.next()){
	        ZoneProtegee z = new ZoneProtegee();
	        z.setIdZone(rs.getInt(1));
	        z.setNomZone(rs.getString("nomZone"));
	        z.setCategorieZone(rs.getString("categorieZone"));
	        z.setStatut(rs.getString("status"));
	        zones.add(z);
	    }
	        
	        
	    } catch (SQLException ex) {
	    
	        System.out.println(ex.getMessage());
	    
	    }
	    return zones;
	    }
	
	
	
}


