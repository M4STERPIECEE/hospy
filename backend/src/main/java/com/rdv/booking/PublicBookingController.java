package com.rdv.booking;

import com.rdv.appointment.dto.AppointmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class PublicBookingController {

    private final PublicBookingService publicBookingService;

    @PostMapping("/api/public/bookings")
    public ResponseEntity<PublicBookingResponse> createPublicBooking(@Valid @RequestBody PublicBookingRequest request) {
        PublicBookingResponse response = publicBookingService.book(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/appointments/public")
    public ResponseEntity<AppointmentResponse> legacyPublicBooking(@Valid @RequestBody PublicBookingRequest request) {
        PublicBookingResponse response = publicBookingService.book(request);
        return ResponseEntity.ok(response.appointment());
    }
}
