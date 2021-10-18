package de.unistuttgart.iaas.faas.quantumservice.model.dto.eventtrigger;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.unistuttgart.iaas.faas.quantumservice.model.dto.OpenWhiskServiceDto;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventType;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "eventTriggers", itemRelation = "eventTrigger")
@JsonTypeName("BASIC")
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "eventType", visible = true)
@JsonSubTypes( {@JsonSubTypes.Type(value = EventTriggerDto.class, name = "BASIC"), @JsonSubTypes.Type(value = QueueSizeEventTriggerDto.class, name = "QUEUE_SIZE"), @JsonSubTypes.Type(value = ExecutionResultEventTriggerDto.class, name = "EXECUTION_RESULT")})
public class EventTriggerDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "The name can only contain letters and numbers!")
    @NotBlank(message = "The name cannot be blank!")
    private String name;

    @JsonIgnore
    private OpenWhiskServiceDto openWhiskService;

    @NotNull(message = "EventType must not be null!")
    private EventType eventType;
}
