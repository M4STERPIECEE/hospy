package com.rdv.auth.dto;

public record LoginResponse(
        boolean success,
        String message,
        String token
) {
    public LoginResponse(boolean success, String message) {
        this(success, message, null);
    }
}
