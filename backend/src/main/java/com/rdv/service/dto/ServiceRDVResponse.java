package com.rdv.service.dto;

import com.rdv.service.ServiceStatus;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public record ServiceRDVResponse(
        UUID id,
        String name,
        String description,
        Integer durationMinutes,
        BigDecimal price,
        ServiceStatus status,
        ZonedDateTime createdAt
) {}
