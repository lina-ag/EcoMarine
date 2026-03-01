package tn.edu.esprit.services;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import tn.edu.esprit.entities.SurveillanceZone;
import tn.edu.esprit.tools.DataSource;

public class ServiceSurv implements IServiceZ <SurveillanceZone> {
	Connection cnx ;
	public ServiceSurv(){
	    this.cnx= DataSource.getInstance().getConnection();
	}

	@Override 
	public boolean ajouter(SurveillanceZone s) { 
		 try {
		        // Vérifier si la zone existe
		        String checkQuery = "SELECT COUNT(*) FROM zonep WHERE idZone = " + s.getIdZone();
		        Statement checkStm = cnx.createStatement();
		        ResultSet rs = checkStm.executeQuery(checkQuery);

		        if (rs.next() && rs.getInt(1) == 0) {
		            System.out.println("Erreur : la zone avec ID " + s.getIdZone() + " n'existe pas !");
		            return false;
		        }

		        //Insertion si la zone existe
		        String req = "INSERT INTO survZone (dateSurv, observation, idZone) VALUES ('"
		                + s.getDateSurveillance() + "','"
		                + s.getObservation() + "',"
		                + s.getIdZone() + ")";

		        Statement stm = cnx.createStatement();
		        stm.executeUpdate(req);

		        System.out.println("Surveillance ajoutée avec succès !");
                return true;
		    } catch (SQLException ex) {
		        System.out.println(ex.getMessage());
		        return false;
		    }
		
	}

	@Override
	public void modifier(SurveillanceZone s) {
		try {
            String req = "UPDATE survZone SET " 
                    + "dateSurv='" + s.getDateSurveillance() + "', "
                    + "observation='" + s.getObservation() + "', "
                    + "idZone=" + s.getIdZone() + " "
                    + "WHERE idSurv=" + s.getIdSurveillance();

            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);

            System.out.println("Surveillance modifiée !");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
		
	}

	@Override
	public void supprimer(int idSurveillance) {
		try {
            String req = "DELETE FROM survZone WHERE idSurv=" + idSurveillance; 

            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);

            System.out.println("Surveillance supprimée !");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
		
	}
 
	@Override
	public SurveillanceZone getOne(int idZone) {
		SurveillanceZone s = null;

        try {
            String req = "SELECT * FROM survZone WHERE idZone=" + idZone;
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);

            if (rs.next()) {
                s = new SurveillanceZone();
                s.setIdSurveillance(rs.getInt("idSurv"));
                s.setDateSurveillance(rs.getString("dateSurv"));
                s.setObservation(rs.getString("observation"));
                s.setIdZone(rs.getInt("idZone"));
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return s;
	}

	@Override
	public List<SurveillanceZone> getAll(SurveillanceZone t) {
        List<SurveillanceZone> list = new ArrayList<>();

        try {
            String req = "SELECT * FROM survZone";
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);

            while (rs.next()) {
                SurveillanceZone s = new SurveillanceZone();
                s.setIdSurveillance(rs.getInt("idSurv"));
                s.setDateSurveillance(rs.getString("dateSurv"));
                s.setObservation(rs.getString("observation"));
                s.setIdZone(rs.getInt("idZone"));

                list.add(s);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return list;
    }

}
