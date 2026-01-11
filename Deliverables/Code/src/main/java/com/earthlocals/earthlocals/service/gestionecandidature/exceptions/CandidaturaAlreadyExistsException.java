package com.earthlocals.earthlocals.service.gestionecandidature.exceptions;

public class CandidaturaAlreadyExistsException extends RuntimeException {
    public CandidaturaAlreadyExistsException(String message) {
        super(message);
    }

    public CandidaturaAlreadyExistsException() {
        super();
    }
}

