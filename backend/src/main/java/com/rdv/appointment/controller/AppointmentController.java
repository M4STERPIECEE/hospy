package com.rdv.appointment.controller;

import com.rdv.appointment.dto.AppointmentRequest;
import com.rdv.appointment.dto.AppointmentResponse;
import com.rdv.appointment.entity.AppointmentStatus;
import com.rdv.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<?> getAllAppointments(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            return ResponseEntity.ok(appointmentService.findAll(PageRequest.of(page, size)));
        }
        return ResponseEntity.ok(appointmentService.findAll());
    }

    @GetMapping("/calendar")
    public ResponseEntity<List<AppointmentResponse>> getCalendarAppointments(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(appointmentService.findByMonth(year, month));
    }

    @GetMapping("/{id:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.getById(id));
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(@Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        String statusStr = body.get("status");
        AppointmentStatus status = AppointmentStatus.valueOf(statusStr.toUpperCase());
        return ResponseEntity.ok(appointmentService.updateStatus(id, status));
    }
}
