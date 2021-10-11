package de.unistuttgart.iaas.faas.quantumservice.model.ibmq;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Hub {
    private String name;
    private String title;
    private String description;
    private ZonedDateTime creationDate;
    private Boolean deleted;
    private Object ui;
    private Map<String, Group> groups = new HashMap<>();

    @JsonProperty("private")
    private Boolean isPrivate;

    private Boolean licenseNotRequired;
    private Boolean isDefault;
}

