package de.unistuttgart.iaas.faas.quantumservice.configuration;

import java.util.UUID;

import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

/**
 * This class represents a converter that converts incoming HTTP-Request-Parameters from a String to a UUID.
 */
@Configuration
public class ConverterConfiguration implements Converter<String, UUID> {
    @Override
    public UUID convert(@NonNull String s) {
        return UUID.fromString(s);
    }
}
