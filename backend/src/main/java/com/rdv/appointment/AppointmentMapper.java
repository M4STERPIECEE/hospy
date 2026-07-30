package com.rdv.appointment;

import com.rdv.appointment.dto.AppointmentRequest;
import com.rdv.appointment.dto.AppointmentResponse;
import com.rdv.service.ServiceRDVMapper;
import com.rdv.service.ServiceRDV;
import com.rdv.user.UserMapper;
import com.rdv.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentMapper {

    private final UserMapper userMapper;
    private final ServiceRDVMapper serviceRDVMapper;

    public AppointmentResponse toResponse(Appointment appointment) {
        if (appointment == null) return null;
        return new AppointmentResponse(
                appointment.getId(),
                userMapper.toResponse(appointment.getUser()),
                serviceRDVMapper.toResponse(appointment.getService()),
                appointment.getAppointmentDate(),
                appointment.getStatus(),
                appointment.getNotes(),
                appointment.getCreatedAt()
        );
    }

    public Appointment toEntity(AppointmentRequest request, User user, ServiceRDV service) {
        if (request == null) return null;
        return Appointment.builder()
                .user(user)
                .service(service)
                .appointmentDate(request.appointmentDate())
                .status(request.status() != null ? request.status() : AppointmentStatus.PENDING)
                .notes(request.notes())
                .build();
    }
}
