package com.rdv.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminResetConfirm(
        @NotBlank(message = "Le jeton est obligatoire")
        String token,

        @NotBlank(message = "Le nouveau mot de passe est obligatoire")
        String newPassword
) {}
