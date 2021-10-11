package de.unistuttgart.iaas.faas.quantumservice.model.openwhisk;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class OpenWhiskRule {

    private String name;
    private String version;
    private Boolean publish;
    private String status;
    private String trigger;
    private String action;

    public void setStatus(Boolean active) {
        status = active ? "active" : "inactive";
    }
}
