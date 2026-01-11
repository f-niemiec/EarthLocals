package com.earthlocals.earthlocals.service.gestionemissioni.exceptions;

public class MissioneNotAcceptableException extends RuntimeException {
    public MissioneNotAcceptableException(String message) {
        super(message);
    }

    public MissioneNotAcceptableException() {
        super();
    }
}