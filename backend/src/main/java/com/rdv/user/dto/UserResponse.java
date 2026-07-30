package com.rdv.user.dto;

import com.rdv.user.entity.UserRole;
import java.time.ZonedDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phone,
        UserRole role,
        ZonedDateTime createdAt
) {}
