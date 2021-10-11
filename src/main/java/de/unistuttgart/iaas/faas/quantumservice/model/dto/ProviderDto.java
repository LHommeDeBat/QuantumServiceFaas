package de.unistuttgart.iaas.faas.quantumservice.model.dto;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

@Data
@NoArgsConstructor
@Relation(collectionRelation = "providers", itemRelation = "provider")
public class ProviderDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "The name can only contain letters and numbers!")
    @NotBlank(message = "The name cannot be blank!")
    private String name;

    @Pattern(regexp = "(\\S+):(\\S+)", message = "Basic credentials must be of form 'username:password'!")
    @NotBlank(message = "The credentials cannot be blank!")
    private String basicCredentials;

    @NotBlank(message = "The base URL cannot be blank!")
    private String baseUrl;

    @NotBlank(message = "The namespace cannot be blank!")
    private String namespace;
}
