package com.rdv.common.exception;

import java.time.ZonedDateTime;

public record ErrorResponse(
        String code,
        String message,
        ZonedDateTime timestamp
) {
    public ErrorResponse(String code, String message) {
        this(code, message, ZonedDateTime.now());
    }
}
