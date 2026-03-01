package tn.edu.esprit.services;

import java.sql.SQLException;
import java.util.List;

import tn.edu.esprit.entities.ZoneProtegee;

public interface IServiceZ <T> {
	public boolean ajouter(T t);
    public void modifier(T t);
    public void supprimer(int idZone);
    public T getOne(int idZone);
    public List<T> getAll(T t);
	
}
