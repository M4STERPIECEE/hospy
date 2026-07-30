package com.rdv.booking;

import com.rdv.appointment.dto.AppointmentRequest;
import com.rdv.appointment.dto.AppointmentResponse;
import com.rdv.appointment.entity.AppointmentStatus;
import com.rdv.appointment.service.AppointmentService;
import com.rdv.booking.dto.PublicBookingRequest;
import com.rdv.booking.dto.PublicBookingResponse;
import com.rdv.booking.service.PublicBookingServiceImpl;
import com.rdv.service.dto.ServiceRDVResponse;
import com.rdv.service.entity.ServiceStatus;
import com.rdv.service.service.ServiceRDVService;
import com.rdv.user.dto.UserRequest;
import com.rdv.user.dto.UserResponse;
import com.rdv.user.entity.UserRole;
import com.rdv.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicBookingServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private ServiceRDVService serviceRdvService;

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private PublicBookingServiceImpl publicBookingService;

    private PublicBookingRequest bookingRequest;
    private UserResponse userResponse;
    private ServiceRDVResponse serviceResponse;
    private AppointmentResponse appointmentResponse;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID appointmentId = UUID.randomUUID();

        bookingRequest = new PublicBookingRequest(
                "Claire",
                "Martin",
                "claire.martin@example.com",
                "0600000000",
                "Consultation Standard",
                "2026-08-15",
                "10:30:00"
        );

        userResponse = new UserResponse(userId, "Claire", "Martin", "claire.martin@example.com", "0600000000", UserRole.USER, null);
        serviceResponse = new ServiceRDVResponse(serviceId, "Consultation Standard", null, 30, BigDecimal.valueOf(50), ServiceStatus.ACTIVE, null);
        appointmentResponse = new AppointmentResponse(appointmentId, userResponse, serviceResponse, ZonedDateTime.now(), AppointmentStatus.PENDING, null, null);
    }

    @Test
    void book_Success() {
        when(userService.findOrCreateByEmail(any(UserRequest.class))).thenReturn(userResponse);
        when(serviceRdvService.getByNameOrCreateDefault("Consultation Standard")).thenReturn(serviceResponse);
        when(appointmentService.create(any(AppointmentRequest.class))).thenReturn(appointmentResponse);

        PublicBookingResponse result = publicBookingService.book(bookingRequest);

        assertNotNull(result);
        assertEquals(userResponse, result.user());
        assertEquals(serviceResponse, result.service());
        assertEquals(appointmentResponse, result.appointment());

        verify(userService).findOrCreateByEmail(any(UserRequest.class));
        verify(serviceRdvService).getByNameOrCreateDefault("Consultation Standard");
        verify(appointmentService).create(any(AppointmentRequest.class));
    }
}
