package com.rdv.email.service;

public interface EmailService {
    void sendResetEmail(String to, String resetLink);
}
