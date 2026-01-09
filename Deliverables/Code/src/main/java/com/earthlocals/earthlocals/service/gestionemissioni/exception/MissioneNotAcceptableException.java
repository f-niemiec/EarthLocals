package com.earthlocals.earthlocals.service.gestionemissioni.exception;

public class MissioneNotAcceptableException extends RuntimeException {
    public MissioneNotAcceptableException(String message) {
        super(message);
    }

    public MissioneNotAcceptableException() {
        super();
    }
}