package tn.edu.esprit.entities;

import java.util.Objects;

public class SurveillanceZone {
	private int idSurveillance;
    private String dateSurveillance;
    private String observation;
    private int idZone;
	public SurveillanceZone() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SurveillanceZone( String dateSurveillance, String observation,int idZone) {
		super();
		//this.idSurveillance = idSurveillance;
		this.dateSurveillance = dateSurveillance;
		this.observation = observation;
		this.idZone = idZone;
	}

	public int getIdSurveillance() {
		return idSurveillance;
	}

	public void setIdSurveillance(int idSurveillance) {
		this.idSurveillance = idSurveillance;
	}

	public String getDateSurveillance() {
		return dateSurveillance;
	}

	public void setDateSurveillance(String dateSurveillance) {
		this.dateSurveillance = dateSurveillance;
	}

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	public int getIdZone() {
		return idZone;
	}

	public void setIdZone(int idZone) {
		this.idZone = idZone;
	}

	@Override
	public int hashCode() {
		int hash = 7;
	    hash = 17 * hash + this.idSurveillance;
	    return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) 
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SurveillanceZone other = (SurveillanceZone) obj;
        if (this.idSurveillance != other.idSurveillance) {
            return false;
        }
        return true;
	}
    
    

}
