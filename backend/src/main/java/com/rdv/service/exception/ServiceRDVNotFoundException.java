package com.rdv.service.exception;

import com.rdv.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class ServiceRDVNotFoundException extends ResourceNotFoundException {
    public ServiceRDVNotFoundException(UUID id) {
        super("ServiceRDV", id);
    }

    public ServiceRDVNotFoundException(String name) {
        super("ServiceRDV introuvable avec nom: " + name);
    }
}
