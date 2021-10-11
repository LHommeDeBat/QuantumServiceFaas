package de.unistuttgart.iaas.faas.quantumservice.model.dto;

import java.util.Set;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Data
@Relation(collectionRelation = "quantumApplications", itemRelation = "quantumApplication")
public class QuantumApplicationDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "The name can only contain letters and numbers!")
    @NotBlank(message = "The name cannot be blank!")
    private String name;

    private String code;
    private String dockerImage;
    private String notificationAddress;

    @JsonIgnore
    private ProviderDto provider;

    @JsonIgnore
    private Set<JobDto> jobs;
}
