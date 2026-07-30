package com.rdv.user.exception;

import com.rdv.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(UUID id) {
        super("User", id);
    }

    public UserNotFoundException(String email) {
        super("User introuvable avec email: " + email);
    }
}
