package com.rdv.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from:noreply@example.com}")
    private String fromEmail;

    @Override
    public void sendResetEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Reinitialisation du mot de passe admin");
        message.setText("Pour reinitialiser votre mot de passe, ouvrez ce lien : " + resetLink);
        mailSender.send(message);
    }
}
