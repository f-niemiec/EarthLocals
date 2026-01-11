package com.earthlocals.earthlocals.service.gestioneutente.exceptions;

public class PasswordResetTokenNotFoundException extends Exception {
    public PasswordResetTokenNotFoundException(String message) {
        super(message);
    }

    public PasswordResetTokenNotFoundException() {
        super();
    }
}
