package com.rdv.booking.dto;

import com.rdv.appointment.dto.AppointmentRequest;
import com.rdv.appointment.entity.AppointmentStatus;
import com.rdv.user.dto.UserRequest;
import com.rdv.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

public record PublicBookingRequest(
        @NotBlank(message = "Le prénom est obligatoire")
        String firstName,

        @NotBlank(message = "Le nom est obligatoire")
        String lastName,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Format d'email invalide")
        String email,

        String phone,

        @NotBlank(message = "Le nom du service est obligatoire")
        String service,

        @NotBlank(message = "La date est obligatoire")
        String date,

        @NotBlank(message = "L'heure est obligatoire")
        String time
) {
    public UserRequest toUserRequest() {
        return new UserRequest(firstName, lastName, email, phone, "PUBLIC_BOOKING", UserRole.USER);
    }

    public AppointmentRequest toAppointmentRequest(UUID userId, UUID serviceId) {
        LocalDateTime localDateTime = LocalDateTime.parse(date + "T" + time);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return new AppointmentRequest(userId, serviceId, zonedDateTime, AppointmentStatus.PENDING, null);
    }
}
