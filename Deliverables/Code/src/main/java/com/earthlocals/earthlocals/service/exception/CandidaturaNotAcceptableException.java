package com.earthlocals.earthlocals.service.exception;

public class CandidaturaNotAcceptableException extends RuntimeException {
    public CandidaturaNotAcceptableException(String message) {
        super(message);
    }

    public CandidaturaNotAcceptableException() {
        super();
    }
}