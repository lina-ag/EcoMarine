package activiteReservation;

import org.junit.jupiter.api.*;
import tn.edu.esprit.entities.Reservation;
import tn.edu.esprit.services.ServiceActivite;
import tn.edu.esprit.services.ServiceReservation;
import tn.edu.esprit.entities.ActiviteEcologique;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceReservationTest {

    static ServiceReservation serviceReservation;
    static ServiceActivite serviceActivite;

    static int idActiviteTest;
    static int idReservationTest;

    @BeforeAll
    static void setup() {

        serviceReservation = new ServiceReservation();
        serviceActivite = new ServiceActivite();

        ActiviteEcologique a = new ActiviteEcologique();
        a.setNom("ActiviteFK");
        a.setDescription("Test FK");
        a.setDate("2026-03-01");
        a.setCapacite(10);

        serviceActivite.ajouter(a);

        List<ActiviteEcologique> list = serviceActivite.getAll();
        idActiviteTest = list.get(list.size() - 1).getIdActivite();
    }

    @Test
    @Order(1)
    void testAjouterReservation() {

        Reservation r = new Reservation();
        r.setNom("ReservationJUnit");
        r.setEmail("test@mail.com");
        r.setNombre_personnes(3);
        r.setDate_reservation("2026-03-10");
        r.setIdActivite(idActiviteTest);

        serviceReservation.ajouter(r);

        List<Reservation> list = serviceReservation.getAll();

        assertFalse(list.isEmpty());

        boolean existe = list.stream()
                .anyMatch(res -> res.getNom().equals("ReservationJUnit"));

        assertTrue(existe);

        idReservationTest = list.get(list.size() - 1).getId();
    }
    @Test
    @Order(2)
    void testModifierReservation() {

        Reservation r = new Reservation();
        r.setId(idReservationTest);
        r.setNom("ReservationModifie");
        r.setEmail("modifie@mail.com");
        r.setNombre_personnes(5);
        r.setDate_reservation("2026-04-01");
        r.setIdActivite(idActiviteTest);

        serviceReservation.modifier(r);

        List<Reservation> list = serviceReservation.getAll();

        boolean trouve = list.stream()
                .anyMatch(res -> res.getNom().equals("ReservationModifie"));

        assertTrue(trouve);
    }
    @Test
    @Order(3)
    void testSupprimerReservation() {

        serviceReservation.supprimer(idReservationTest);

        List<Reservation> list = serviceReservation.getAll();

        boolean existe = list.stream()
                .anyMatch(res -> res.getId() == idReservationTest);

        assertFalse(existe);
    }

    @AfterAll
    static void cleanup() {
        serviceActivite.supprimer(idActiviteTest);
    }
}