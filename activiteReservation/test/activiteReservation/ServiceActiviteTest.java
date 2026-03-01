package activiteReservation;

import org.junit.jupiter.api.*;
import tn.edu.esprit.entities.ActiviteEcologique;
import tn.edu.esprit.services.ServiceActivite;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceActiviteTest {

    static ServiceActivite service;
    static int idActiviteTest;

    @BeforeAll
    static void setup() {
        service = new ServiceActivite();
        
    }

    @Test
    @Order(1)
    void testAjouterActivite() {

        ActiviteEcologique a = new ActiviteEcologique();
        a.setNom("ActiviteJUnit");
        a.setDescription("Test activité");
        a.setDate("2026-03-01");
        a.setCapacite(15);

        service.ajouter(a);

        List<ActiviteEcologique> list = service.getAll();

        assertFalse(list.isEmpty());

        boolean existe = list.stream()
                .anyMatch(act -> act.getNom().equals("ActiviteJUnit"));

        assertTrue(existe);

        idActiviteTest = list.get(list.size() - 1).getIdActivite();
    }

    @Test
    @Order(2)
    void testModifierActivite() {

        ActiviteEcologique a = new ActiviteEcologique();
        a.setIdActivite(idActiviteTest);
        a.setNom("ActiviteModifie");
        a.setDescription("Description modifiée");
        a.setDate("2026-04-01");
        a.setCapacite(25);

        service.modifier(a);

        List<ActiviteEcologique> list = service.getAll();

        boolean trouve = list.stream()
                .anyMatch(act -> act.getNom().equals("ActiviteModifie"));

        assertTrue(trouve);
    }

    @Test
    @Order(3)
    void testSupprimerActivite() {

        service.supprimer(idActiviteTest);

        List<ActiviteEcologique> list = service.getAll();

        boolean existe = list.stream()
                .anyMatch(act -> act.getIdActivite() == idActiviteTest);

        assertFalse(existe);
    }
}