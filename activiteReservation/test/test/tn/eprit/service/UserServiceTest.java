package test.tn.eprit.service;
import org.junit.jupiter.api.*;


import tn.edu.esprit.entities.User;
import tn.edu.esprit.services.ServiceUser;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

	    static ServiceUser serviceUser;
	    static int idUserTest;

	    @BeforeAll
	    static void setup() {
	        serviceUser = new ServiceUser();
	    }

	    @Test
	    @Order(1)
	    void testAjouterUtilisateur() {

	        User u = new User(
	                0,
	                "TestNom",
	                "TestPrenom",
	                "test@test.com",
	                "123456",
	                "55667788",
	                "Utilisateur",
	                LocalDate.of(2000, 1, 1)
	        );

	        serviceUser.ajouter(u);

	        List<User> users = serviceUser.getAll();
	        assertFalse(users.isEmpty());

	        User last = users.get(users.size() - 1);
	        idUserTest = last.getIdUtilisateur();

	        assertEquals("TestNom", last.getNom());
	        assertEquals("test@test.com", last.getEmail());
	    }

	    @Test
	    @Order(2)
	    void testModifierUtilisateur() {

	        User u = new User(
	                idUserTest,
	                "ModifiedNom",
	                "ModifiedPrenom",
	                "modified@test.com",
	                "987654",
	                "11223344",
	                "Admin",
	                LocalDate.of(1999, 5, 15)
	        );

	        serviceUser.modifier(u);

	        User updated = serviceUser.getOne(idUserTest);

	        assertEquals("ModifiedNom", updated.getNom());
	        assertEquals("modified@test.com", updated.getEmail());
	        assertEquals("Admin", updated.getRole());
	    }

	    @Test
	    @Order(3)
	    void testSupprimerUtilisateur() {

	        serviceUser.supprimer(idUserTest);

	        User deleted = serviceUser.getOne(idUserTest);

	        assertNull(deleted);
	    }
}
