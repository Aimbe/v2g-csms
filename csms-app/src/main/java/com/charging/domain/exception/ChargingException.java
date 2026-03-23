package com.charging.domain.exception;

public class ChargingException extends RuntimeException {
    public ChargingException(String message) {
        super(message);
    }
    public ChargingException(String message, Throwable cause) {
        super(message, cause);
    }
}
