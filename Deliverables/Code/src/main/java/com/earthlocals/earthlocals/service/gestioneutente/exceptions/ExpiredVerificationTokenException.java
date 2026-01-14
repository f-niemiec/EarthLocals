package com.earthlocals.earthlocals.service.gestioneutente.exceptions;

public class ExpiredVerificationTokenException extends Exception {
    public ExpiredVerificationTokenException() {
        super();
    }

    public ExpiredVerificationTokenException(String message) {
        super(message);
    }
}
