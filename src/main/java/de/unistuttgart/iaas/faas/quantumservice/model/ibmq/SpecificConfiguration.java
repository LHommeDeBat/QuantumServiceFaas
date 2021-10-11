package de.unistuttgart.iaas.faas.quantumservice.model.ibmq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpecificConfiguration {
    @JsonProperty("open_pulse")
    private Boolean openPulse;
}
