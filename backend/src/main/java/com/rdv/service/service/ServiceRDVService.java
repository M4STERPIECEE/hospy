package com.rdv.service.service;

import com.rdv.service.dto.ServiceRDVRequest;
import com.rdv.service.dto.ServiceRDVResponse;
import com.rdv.service.entity.ServiceRDV;
import com.rdv.service.entity.ServiceStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ServiceRDVService {
    List<ServiceRDVResponse> findAll();
    ServiceRDVResponse getById(UUID id);
    ServiceRDV findEntityById(UUID id);
    ServiceRDVResponse getByNameOrCreateDefault(String name);
    ServiceRDVResponse create(ServiceRDVRequest request);
    ServiceRDVResponse updateFields(UUID id, Integer durationMinutes, BigDecimal price, ServiceStatus status);
}
