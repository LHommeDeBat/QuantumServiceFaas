package de.unistuttgart.iaas.faas.quantumservice.model.dto.eventtrigger;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "eventTriggers", itemRelation = "eventTrigger")
@Data
@JsonTypeName("EXECUTION_RESULT")
public class ExecutionResultEventTriggerDto extends EventTriggerDto{

    private String executedApplicationName;
}
