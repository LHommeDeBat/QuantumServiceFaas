package de.unistuttgart.iaas.faas.quantumservice.model.exception;

public class OpenWhiskException extends RuntimeException {

    public OpenWhiskException(String message) {
        super(message);
    }
}
