package com.rdv.appointment.dto;

import com.rdv.appointment.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.UUID;

public record AppointmentRequest(
        @NotNull(message = "L'utilisateur est obligatoire")
        UUID userId,

        @NotNull(message = "Le service est obligatoire")
        UUID serviceId,

        @NotNull(message = "La date du rendez-vous est obligatoire")
        ZonedDateTime appointmentDate,

        AppointmentStatus status,

        String notes
) {}
