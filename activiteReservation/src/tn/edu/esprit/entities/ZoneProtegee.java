package tn.edu.esprit.entities;

import java.util.Objects;

public class ZoneProtegee {
	 private int idZone;
	 private String nomZone;
	 private String categorieZone;
	 private String statut;
	 
	 public ZoneProtegee() {
		super();
		// TODO Auto-generated constructor stub
	}

	 public ZoneProtegee( String nomZone, String categorieZone, String statut) {
		super();
		this.nomZone = nomZone;
		this.categorieZone = categorieZone;
		this.statut = statut;
	 }

	 public int getIdZone() {
		 return idZone;
	 }

	 public void setIdZone(int idZone) {
		 this.idZone = idZone;
	 }

	 public String getNomZone() {
		 return nomZone;
	 }

	 public void setNomZone(String nomZone) {
		 this.nomZone = nomZone;
	 }

	 public String getCategorieZone() {
		 return categorieZone;
	 }

	 public void setCategorieZone(String categorieZone) {
		 this.categorieZone = categorieZone;
	 }

	 public String getStatut() {
		 return statut;
	 }

	 public void setStatut(String statut) {
		 this.statut = statut;
	 }

	 @Override
	 public int hashCode() {
		 int hash = 7;
	     hash = 17 * hash + this.idZone;
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
		final ZoneProtegee other = (ZoneProtegee) obj;
        if (this.idZone != other.idZone) {
            return false;
        }
        return true;
    }
	
     
	 
}
