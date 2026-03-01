package tn.edu.esprit.services;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import tn.edu.esprit.entities.User;
import tn.edu.esprit.tools.DataSource;

public class ServiceUser implements IService<User> {

    private Connection cnx;

    public ServiceUser() {
        cnx = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(User u) {
        String req = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, telephone, role, date_naissance) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setString(1, u.getNom());
            pst.setString(2, u.getPrenom());
            pst.setString(3, u.getEmail());
            pst.setString(4, u.getMotDePasse());
            pst.setString(5, u.getTelephone());
            pst.setString(6, u.getRole());
            pst.setDate(7, Date.valueOf(u.getDateNaissance()));

            pst.executeUpdate();
            System.out.println("Utilisateur ajouté !");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void modifier(User u) {
        String req = "UPDATE utilisateur SET nom=?, prenom=?, email=?, mot_de_passe=?, telephone=?, role=?, date_naissance=? " +
                     "WHERE id_utilisateur=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);

            pst.setString(1, u.getNom());
            pst.setString(2, u.getPrenom());
            pst.setString(3, u.getEmail());
            pst.setString(4, u.getMotDePasse());
            pst.setString(5, u.getTelephone());
            pst.setString(6, u.getRole());
            pst.setDate(7, Date.valueOf(u.getDateNaissance()));
            pst.setInt(8, u.getIdUtilisateur());

            pst.executeUpdate();
            System.out.println("Utilisateur modifié !");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {
        String req = "DELETE FROM utilisateur WHERE id_utilisateur=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Utilisateur supprimé !");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public User getOne(int id) {
        String req = "SELECT * FROM utilisateur WHERE id_utilisateur=?";
        User u = null;

        try {
            PreparedStatement pst = cnx.prepareStatement(req);
            pst.setInt(1, id);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                u = extractUser(rs);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return u;
    }

    @Override
    public List<User> getAll() {
        String req = "SELECT * FROM utilisateur";
        List<User> users = new ArrayList<>();

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);

            while (rs.next()) {
                users.add(extractUser(rs));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return users;
    }

    // Méthode utilitaire pour éviter duplication du code
    private User extractUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id_utilisateur"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("email"),
                rs.getString("mot_de_passe"),
                rs.getString("telephone"),
                rs.getString("role"),
                rs.getDate("date_naissance").toLocalDate()
        );
    }
    public User login(String email, String mdp) {
        try {
            String req = "SELECT * FROM utilisateur WHERE email=? AND mot_de_passe=?";
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, email);
            ps.setString(2, mdp);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    
}