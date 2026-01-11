package com.earthlocals.earthlocals.service.gestioneutente.exceptions;

public class ExpiredResetTokenException extends Exception {
    public ExpiredResetTokenException() {
        super();
    }

    public ExpiredResetTokenException(String message) {
        super(message);
    }
}
