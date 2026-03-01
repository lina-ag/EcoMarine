package test.tn.eprit.service;


import org.junit.jupiter.api.*;
import java.sql.SQLException;
import tn.edu.esprit.entities.SurveillanceZone;
import tn.edu.esprit.entities.ZoneProtegee;
import tn.edu.esprit.services.ServiceSurv;
import tn.edu.esprit.services.ServiceZoneP;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
public class SurvZoneServiceTest {
	
	 static ServiceSurv serviceSurv;
	    static ServiceZoneP serviceZone;

	    static int idZoneTest;
	    static int idSurvTest;

	    @BeforeAll 
	    static void setup() {
	        serviceSurv = new ServiceSurv();
	        serviceZone = new ServiceZoneP();

	        // Créer une zone pour pouvoir ajouter une surve illance
	        ZoneProtegee z = new ZoneProtegee("ZoneSurvTest", "Lagune", "Active");
	        serviceZone.ajouter(z);

	        List<ZoneProtegee> zones = serviceZone.getAll(null);
	        idZoneTest = zones.get(zones.size() - 1).getIdZone();
	    }
	    
	    @Test
	    @Order(1)
	    void testAjouterSurveillance() {

	        SurveillanceZone s = new SurveillanceZone();
	        s.setDateSurveillance("2026-02-21");
	        s.setObservation("Test observation");
	        s.setIdZone(idZoneTest);

	        serviceSurv.ajouter(s);

	        List<SurveillanceZone> surveillances = serviceSurv.getAll(null);

	        assertFalse(surveillances.isEmpty());

	        assertTrue(
	                surveillances.stream()
	                        .anyMatch(surv -> surv.getObservation().equals("Test observation"))
	        );

	        idSurvTest = surveillances.get(surveillances.size() - 1).getIdSurveillance();
	    }
	    
	    @Test
	    @Order(2)
	    void testModifierSurveillance() {

	        SurveillanceZone s = new SurveillanceZone();
	        s.setIdSurveillance(idSurvTest);
	        s.setDateSurveillance("2026-03-01");
	        s.setObservation("Observation modifiée");
	        s.setIdZone(idZoneTest);

	        serviceSurv.modifier(s);

	        List<SurveillanceZone> surveillances = serviceSurv.getAll(null);

	        boolean trouve = surveillances.stream()
	                .anyMatch(surv -> surv.getObservation().equals("Observation modifiée"));

	        assertTrue(trouve);
	    }
	    
	    @Test
	    @Order(3)
	    void testSupprimerSurveillance() {

	        serviceSurv.supprimer(idSurvTest);

	        List<SurveillanceZone> surveillances = serviceSurv.getAll(null);

	        boolean existe = surveillances.stream()
	                .anyMatch(surv -> surv.getIdSurveillance() == idSurvTest);

	        assertFalse(existe);
	    }

	    @AfterAll
	    static void cleanUp() {
	        // Supprimer la zone test (et les surveillances restantes si cascade activé)
	        serviceZone.supprimer(idZoneTest);
	    }



}
