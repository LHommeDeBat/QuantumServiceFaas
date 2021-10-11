package de.unistuttgart.iaas.faas.quantumservice.model.exception;

public class ElementAlreadyExistsException extends RuntimeException {

    public ElementAlreadyExistsException(String message) {
        super(message);
    }
}
