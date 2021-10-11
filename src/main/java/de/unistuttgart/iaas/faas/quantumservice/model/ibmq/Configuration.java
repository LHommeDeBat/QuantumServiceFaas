package de.unistuttgart.iaas.faas.quantumservice.model.ibmq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Configuration {
    private int limit;
    private Capabilities capabilities;
}
