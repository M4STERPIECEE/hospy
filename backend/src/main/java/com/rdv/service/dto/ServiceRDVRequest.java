package com.rdv.service.dto;

import com.rdv.service.ServiceStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ServiceRDVRequest(
        @NotBlank(message = "Le nom du service est obligatoire")
        String name,

        String description,

        @NotNull(message = "La durée est obligatoire")
        @Min(value = 1, message = "La durée doit être d'au moins 1 minute")
        Integer durationMinutes,

        @NotNull(message = "Le prix est obligatoire")
        BigDecimal price,

        ServiceStatus status
) {}
