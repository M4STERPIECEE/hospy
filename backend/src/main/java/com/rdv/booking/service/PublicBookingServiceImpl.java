package com.rdv.booking.service;

import com.rdv.appointment.dto.AppointmentResponse;
import com.rdv.appointment.service.AppointmentService;
import com.rdv.booking.dto.PublicBookingRequest;
import com.rdv.booking.dto.PublicBookingResponse;
import com.rdv.service.dto.ServiceRDVResponse;
import com.rdv.service.service.ServiceRDVService;
import com.rdv.user.dto.UserResponse;
import com.rdv.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PublicBookingServiceImpl implements PublicBookingService {

    private final UserService userService;
    private final ServiceRDVService serviceRdvService;
    private final AppointmentService appointmentService;

    @Override
    @Transactional
    public PublicBookingResponse book(PublicBookingRequest request) {
        UserResponse user = userService.findOrCreateByEmail(request.toUserRequest());
        ServiceRDVResponse service = serviceRdvService.getByNameOrCreateDefault(request.service());
        AppointmentResponse appointment = appointmentService.create(
                request.toAppointmentRequest(user.id(), service.id())
        );
        return new PublicBookingResponse(user, service, appointment);
    }
}
