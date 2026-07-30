package com.rdv.common.exception;

public class DuplicateResourceException extends BusinessException {
    public DuplicateResourceException(String resource, String field, Object value) {
        super("DUPLICATE_RESOURCE", resource + " existe déjà avec " + field + ": " + value);
    }

    public DuplicateResourceException(String message) {
        super("DUPLICATE_RESOURCE", message);
    }
}
