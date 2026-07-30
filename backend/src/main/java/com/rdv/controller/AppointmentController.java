package com.rdv.controller;

import com.rdv.dto.PublicBookingRequest;
import com.rdv.entity.Appointment;
import com.rdv.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.DELETE,
        RequestMethod.PATCH,
        RequestMethod.OPTIONS
})
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
    public ResponseEntity<List<Appointment>> getCalendarAppointments(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(appointmentService.findByMonth(year, month));
    }

    @GetMapping("/{id:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}")
    public Appointment getAppointmentById(@PathVariable UUID id) {
        return appointmentService.findById(id);
    }

    @PostMapping
    public Appointment createAppointment(@RequestBody Appointment appointment) {
        return appointmentService.create(appointment);
    }

    @PostMapping("/public")
    public ResponseEntity<Appointment> publicBooking(@RequestBody PublicBookingRequest request) {
        Appointment saved = appointmentService.createPublicBooking(request);
        return ResponseEntity.ok(saved);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Appointment> updateStatus(@PathVariable UUID id,
            @RequestBody java.util.Map<String, String> body) {
        String status = body.get("status");
        Appointment updated = appointmentService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }
}