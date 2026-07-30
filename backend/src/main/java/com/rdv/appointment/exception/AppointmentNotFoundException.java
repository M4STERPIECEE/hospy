package com.rdv.appointment.exception;

import com.rdv.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class AppointmentNotFoundException extends ResourceNotFoundException {
    public AppointmentNotFoundException(UUID id) {
        super("Appointment", id);
    }
}
