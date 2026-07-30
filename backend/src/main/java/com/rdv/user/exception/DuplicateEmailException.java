package com.rdv.user.exception;

import com.rdv.common.exception.DuplicateResourceException;

public class DuplicateEmailException extends DuplicateResourceException {
    public DuplicateEmailException(String email) {
        super("User", "email", email);
    }
}
