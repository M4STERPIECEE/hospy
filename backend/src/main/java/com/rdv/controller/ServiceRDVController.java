package com.rdv.controller;

import com.rdv.entity.ServiceRDV;
import com.rdv.service.ServiceRDVService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@Validated
public class ServiceRDVController {
    private final ServiceRDVService serviceRDVService;

    @GetMapping
    public List<ServiceRDV> getAllServices() {
        return serviceRDVService.findAll();
    }

    @GetMapping("/{id}")
    public ServiceRDV getServiceById(@PathVariable UUID id) {
        return serviceRDVService.findById(id);
    }

    @PostMapping
    public ServiceRDV createService(@RequestBody ServiceRDV serviceRDV) {
        return serviceRDVService.create(serviceRDV);
    }

    @PatchMapping("/{id}")
    public ServiceRDV updateServiceFields(@PathVariable UUID id, @Valid @RequestBody ServiceUpdateRequest request) {
        return serviceRDVService.updateFields(id, request.durationMinutes(), request.price(), request.status());
    }

    public record ServiceUpdateRequest(
            @jakarta.validation.constraints.NotNull Integer durationMinutes,
            @jakarta.validation.constraints.NotNull java.math.BigDecimal price,
            @jakarta.validation.constraints.NotBlank String status
    ) {}
}