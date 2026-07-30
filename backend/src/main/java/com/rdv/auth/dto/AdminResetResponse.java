package com.rdv.auth.dto;

public record AdminResetResponse(
        boolean success,
        String message,
        String token
) {
    public AdminResetResponse(boolean success, String message) {
        this(success, message, null);
    }
}
