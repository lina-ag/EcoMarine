package tn.edu.esprit.services;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class EmailService {
    
	 private static final String SMTP_HOST = "smtp.elasticemail.com";
	    private static final String SMTP_PORT = "2525";  // Utilisez le port 2525
	    private static final String USERNAME = "ecomarine2026@outlook.com";
	    private static final String PASSWORD = "BFA653B99329A076D1EC066343A5E534B3A7";
	    private static final String EMAIL_EXPEDITEUR = "ecomarine2026@outlook.com";
	    
    
    // Mode développement (true = pas d'envoi réel, false = envoi réel)
    private static final boolean MODE_DEV = false; // Mettre à true pour tester sans email
 // AJOUTEZ CETTE MÉTHODE
    public void testerConnexion() {
        if (MODE_DEV) {
            System.out.println("🔧 [DEV MODE] Test de connexion désactivé");
            return;
        }}
    public void envoyerConfirmationReservation(String destinataire, String nomClient, 
                                               String nomActivite, String dateReservation, 
                                               int nombrePersonnes) {
        
        if (MODE_DEV) {
            // Mode développement : simulation
            System.out.println("=== [DEV] SIMULATION D'ENVOI D'EMAIL ===");
            System.out.println("Destinataire: " + destinataire);
            System.out.println("Nom client: " + nomClient);
            System.out.println("Activité: " + nomActivite);
            System.out.println("Date: " + dateReservation);
            System.out.println("Participants: " + nombrePersonnes);
            System.out.println("=== [DEV] FIN SIMULATION ===");
            return;
        }
        
        // Mode production : envoi réel
        Properties props = new Properties();
        
        // Configuration SMTP pour Outlook
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        
        // Timeouts plus longs
        props.put("mail.smtp.connectiontimeout", "30000");
        props.put("mail.smtp.timeout", "30000");
        props.put("mail.smtp.writetimeout", "30000");
        
        // Créer la session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_EXPEDITEUR, PASSWORD);
            }
        });
        
        // Pour déboguer
        session.setDebug(true);
        
        try {
            // Créer le message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_EXPEDITEUR, "EcoMarine - Gestion Touristique"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject("✅ Confirmation de réservation - EcoMarine");
            
            // Contenu HTML du message
            String contenu = genererEmailHtml(nomClient, nomActivite, dateReservation, nombrePersonnes);
            
            // Envoyer l'email en HTML
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(contenu, "text/html; charset=utf-8");
            
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
            
            // Envoyer
            Transport.send(message);
            System.out.println("✅ Email de confirmation envoyé à : " + destinataire);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String genererEmailHtml(String nomClient, String nomActivite, 
                                    String dateReservation, int nombrePersonnes) {
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "<meta charset='UTF-8'>" +
               "<style>" +
               "body { font-family: 'Segoe UI', Arial, sans-serif; background: #f0f9ff; margin: 0; padding: 20px; }" +
               ".container { max-width: 600px; margin: auto; background: white; border-radius: 20px; overflow: hidden; box-shadow: 0 10px 30px rgba(0,0,0,0.1); }" +
               ".header { background: linear-gradient(135deg, #1e3c72, #2a5298); color: white; padding: 30px; text-align: center; }" +
               ".header h1 { margin: 0; font-size: 28px; }" +
               ".content { padding: 30px; }" +
               ".info { background: #f8fafc; border-radius: 15px; padding: 20px; margin: 20px 0; }" +
               ".info-item { display: flex; padding: 10px 0; border-bottom: 1px solid #e2e8f0; }" +
               ".info-label { font-weight: bold; width: 120px; color: #1e3c72; }" +
               ".footer { background: #f1f5f9; padding: 20px; text-align: center; color: #64748b; font-size: 12px; }" +
               "</style>" +
               "</head>" +
               "<body>" +
               "<div class='container'>" +
               "<div class='header'>" +
               "<h1>🌊 EcoMarine</h1>" +
               "<p>Confirmation de réservation</p>" +
               "</div>" +
               "<div class='content'>" +
               "<h2>Bonjour " + nomClient + ",</h2>" +
               "<p>Nous vous confirmons votre réservation :</p>" +
               "<div class='info'>" +
               "<div class='info-item'>" +
               "<span class='info-label'>🏖️ Activité :</span>" +
               "<span>" + nomActivite + "</span>" +
               "</div>" +
               "<div class='info-item'>" +
               "<span class='info-label'>📅 Date :</span>" +
               "<span>" + dateReservation + "</span>" +
               "</div>" +
               "<div class='info-item'>" +
               "<span class='info-label'>👥 Participants :</span>" +
               "<span>" + nombrePersonnes + "</span>" +
               "</div>" +
               "</div>" +
               "<p>Merci de votre confiance !</p>" +
               "</div>" +
               "<div class='footer'>" +
               "<p>© 2025 EcoMarine - Gestion Touristique Durable</p>" +
               "</div>" +
               "</div>" +
               "</body>" +
               "</html>";
    }
}