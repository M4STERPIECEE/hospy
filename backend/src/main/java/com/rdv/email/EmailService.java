package com.rdv.email;

public interface EmailService {
    void sendResetEmail(String to, String resetLink);
}
