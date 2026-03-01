package test.tn.eprit.service;


import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.*;

import tn.edu.esprit.entities.ZoneProtegee;
import tn.edu.esprit.services.ServiceZoneP;

import static org.junit.jupiter.api.Assertions.*;

public class ZonesPrServiceTest {
	static ServiceZoneP service;
    static int idZoneTest;

    @BeforeAll
    static void setup() {
        service = new ServiceZoneP();
    }
    
    @Test
    @Order(1)
    void testAjouterZone() {

        ZoneProtegee z = new ZoneProtegee("ZoneTest", "Lagune", "Active");
        service.ajouter(z);

        List<ZoneProtegee> zones = service.getAll(null);

        assertFalse(zones.isEmpty());

        assertTrue(
                zones.stream()
                        .anyMatch(zone -> zone.getNomZone().equals("ZoneTest"))
        );

        idZoneTest = zones.get(zones.size() - 1).getIdZone();
        System.out.println("ID ajouté: " + idZoneTest);
    }
    
    @Test
    @Order(2)
    void testModifierZone() {

        ZoneProtegee z = new ZoneProtegee();
        z.setIdZone(idZoneTest);
        z.setNomZone("ZoneModifiee");
        z.setCategorieZone("Aire Marine");
        z.setStatut("Inactive");

        service.modifier(z);

        List<ZoneProtegee> zones = service.getAll(null);

        boolean trouve = zones.stream()
                .anyMatch(zone -> zone.getNomZone().equals("ZoneModifiee"));

        assertTrue(trouve);
    }
    
    @Test
    @Order(3)
    void testSupprimerZone() {

        service.supprimer(idZoneTest);

        List<ZoneProtegee> zones = service.getAll(null);

        boolean existe = zones.stream()
                .anyMatch(zone -> zone.getIdZone() == idZoneTest);

        assertFalse(existe);
    }
    
    @AfterEach
    void cleanUp() {
        List<ZoneProtegee> zones = service.getAll(null);

        if (!zones.isEmpty()) {
            ZoneProtegee last = zones.get(zones.size() - 1);
            service.supprimer(last.getIdZone());
        }
    }



}
