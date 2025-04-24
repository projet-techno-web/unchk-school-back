package com.unchk.unchkBackend.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPasswordEmail(String to, String token) {
        String subject = "Réinitialisation de votre mot de passe";
        String resetUrl = "http://localhost:4200/reset-password?token=" + token; // frontend
        String message = "Cliquez sur ce lien pour réinitialiser votre mot de passe :\n" + resetUrl;
    

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("contact@unchk.com"); 
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setText(message); 

        mailSender.send(mail);
    }


    public void sendStudentCredentialsEmail(String email, String password) {
        String subject = "Vos identifiants de connexion UNCHK";
        String body = "Bonjour,\n\nvotre compte étudiant a été créé avec succès.\n\n" +
                "Voici vos identifiants :\n" +
                "Email : " + email + "\n" +
                "Mot de passe : " + password + "\n\n" +
                "Veuillez vous connecter et changer votre mot de passe dès que possible.";

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("contact@unchk.com"); 
        mail.setTo(email);
        mail.setSubject(subject);
        mail.setText(body); 

        mailSender.send(mail);        

    }
}
