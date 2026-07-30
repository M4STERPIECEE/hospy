package com.rdv.service;

import com.rdv.service.dto.ServiceRDVRequest;
import com.rdv.service.dto.ServiceRDVResponse;
import org.springframework.stereotype.Component;

@Component
public class ServiceRDVMapper {

    public ServiceRDVResponse toResponse(ServiceRDV service) {
        if (service == null) return null;
        return new ServiceRDVResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getDurationMinutes(),
                service.getPrice(),
                service.getStatus(),
                service.getCreatedAt()
        );
    }

    public ServiceRDV toEntity(ServiceRDVRequest request) {
        if (request == null) return null;
        return ServiceRDV.builder()
                .name(request.name())
                .description(request.description())
                .durationMinutes(request.durationMinutes())
                .price(request.price())
                .status(request.status() != null ? request.status() : ServiceStatus.ACTIVE)
                .build();
    }
}
