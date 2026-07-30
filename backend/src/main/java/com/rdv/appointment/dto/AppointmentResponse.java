package com.rdv.appointment.dto;

import com.rdv.appointment.AppointmentStatus;
import com.rdv.service.dto.ServiceRDVResponse;
import com.rdv.user.dto.UserResponse;

import java.time.ZonedDateTime;
import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        UserResponse user,
        ServiceRDVResponse service,
        ZonedDateTime appointmentDate,
        AppointmentStatus status,
        String notes,
        ZonedDateTime createdAt
) {}
