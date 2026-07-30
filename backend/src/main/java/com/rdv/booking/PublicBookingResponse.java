package com.rdv.booking;

import com.rdv.appointment.dto.AppointmentResponse;
import com.rdv.service.dto.ServiceRDVResponse;
import com.rdv.user.dto.UserResponse;

public record PublicBookingResponse(
        UserResponse user,
        ServiceRDVResponse service,
        AppointmentResponse appointment
) {}
