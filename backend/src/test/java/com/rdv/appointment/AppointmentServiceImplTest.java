package com.rdv.appointment;

import com.rdv.appointment.dto.AppointmentRequest;
import com.rdv.appointment.dto.AppointmentResponse;
import com.rdv.appointment.entity.Appointment;
import com.rdv.appointment.entity.AppointmentStatus;
import com.rdv.appointment.exception.AppointmentNotFoundException;
import com.rdv.appointment.mapper.AppointmentMapper;
import com.rdv.appointment.repository.AppointmentRepository;
import com.rdv.appointment.service.AppointmentServiceImpl;
import com.rdv.service.service.ServiceRDVService;
import com.rdv.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserService userService;

    @Mock
    private ServiceRDVService serviceRDVService;

    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Appointment sampleAppointment;
    private AppointmentResponse sampleResponse;
    private UUID appointmentId;

    @BeforeEach
    void setUp() {
        appointmentId = UUID.randomUUID();
        sampleAppointment = Appointment.builder()
                .id(appointmentId)
                .appointmentDate(ZonedDateTime.now())
                .status(AppointmentStatus.PENDING)
                .build();

        sampleResponse = new AppointmentResponse(appointmentId, null, null, ZonedDateTime.now(), AppointmentStatus.PENDING, null, null);
    }

    @Test
    void getById_Success() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(sampleAppointment));
        when(appointmentMapper.toResponse(sampleAppointment)).thenReturn(sampleResponse);

        AppointmentResponse response = appointmentService.getById(appointmentId);

        assertNotNull(response);
        assertEquals(appointmentId, response.id());
    }

    @Test
    void getById_NotFound_ThrowsException() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        assertThrows(AppointmentNotFoundException.class, () -> appointmentService.getById(appointmentId));
    }
}
