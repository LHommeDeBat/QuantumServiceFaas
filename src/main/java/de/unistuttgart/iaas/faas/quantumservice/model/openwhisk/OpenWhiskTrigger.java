package de.unistuttgart.iaas.faas.quantumservice.model.openwhisk;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class OpenWhiskTrigger {

    private String namespace;
    private String name;
    private String version;
    private Boolean publish;
}
