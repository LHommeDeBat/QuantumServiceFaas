package de.unistuttgart.iaas.faas.quantumservice.model.ibmq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Device {
    private int priority;
    private String name;
    private Boolean deleted;
    private SpecificConfiguration specificConfiguration;
    private Configuration configuration;
}
