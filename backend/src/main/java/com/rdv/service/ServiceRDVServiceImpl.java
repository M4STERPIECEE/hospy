package com.rdv.service;

import com.rdv.service.dto.ServiceRDVRequest;
import com.rdv.service.dto.ServiceRDVResponse;
import com.rdv.service.exception.ServiceRDVNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceRDVServiceImpl implements ServiceRDVService {

    private final ServiceRDVRepository serviceRDVRepository;
    private final ServiceRDVMapper serviceRDVMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ServiceRDVResponse> findAll() {
        return serviceRDVRepository.findAll().stream()
                .map(serviceRDVMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceRDVResponse getById(UUID id) {
        return serviceRDVMapper.toResponse(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceRDV findEntityById(UUID id) {
        return serviceRDVRepository.findById(id)
                .orElseThrow(() -> new ServiceRDVNotFoundException(id));
    }

    @Override
    @Transactional
    public ServiceRDVResponse getByNameOrCreateDefault(String name) {
        ServiceRDV service = serviceRDVRepository.findByName(name)
                .orElseGet(() -> serviceRDVRepository.save(ServiceRDV.builder()
                        .name(name)
                        .durationMinutes(30)
                        .price(BigDecimal.valueOf(50))
                        .status(ServiceStatus.ACTIVE)
                        .build()));
        return serviceRDVMapper.toResponse(service);
    }

    @Override
    @Transactional
    public ServiceRDVResponse create(ServiceRDVRequest request) {
        ServiceRDV service = serviceRDVMapper.toEntity(request);
        ServiceRDV saved = serviceRDVRepository.save(service);
        return serviceRDVMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ServiceRDVResponse updateFields(UUID id, Integer durationMinutes, BigDecimal price, ServiceStatus status) {
        ServiceRDV service = findEntityById(id);
        if (durationMinutes != null) service.setDurationMinutes(durationMinutes);
        if (price != null) service.setPrice(price);
        if (status != null) service.setStatus(status);
        ServiceRDV saved = serviceRDVRepository.save(service);
        return serviceRDVMapper.toResponse(saved);
    }
}
