package com.rdv.auth.exception;

import com.rdv.common.exception.BusinessException;

public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException() {
        super("INVALID_CREDENTIALS", "Identifiants invalides");
    }

    public InvalidCredentialsException(String message) {
        super("INVALID_CREDENTIALS", message);
    }
}
