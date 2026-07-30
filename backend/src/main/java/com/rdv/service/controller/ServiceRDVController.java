package com.rdv.service.controller;

import com.rdv.service.dto.ServiceRDVRequest;
import com.rdv.service.dto.ServiceRDVResponse;
import com.rdv.service.service.ServiceRDVService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceRDVController {

    private final ServiceRDVService serviceRDVService;

    @GetMapping
    public ResponseEntity<List<ServiceRDVResponse>> getAllServices() {
        return ResponseEntity.ok(serviceRDVService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceRDVResponse> getServiceById(@PathVariable UUID id) {
        return ResponseEntity.ok(serviceRDVService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ServiceRDVResponse> createService(@Valid @RequestBody ServiceRDVRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceRDVService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ServiceRDVResponse> updateServiceFields(
            @PathVariable UUID id,
            @RequestBody ServiceRDVRequest request) {
        return ResponseEntity.ok(serviceRDVService.updateFields(
                id,
                request.durationMinutes(),
                request.price(),
                request.status()
        ));
    }
}
