package de.unistuttgart.iaas.faas.quantumservice.model.dto;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.unistuttgart.iaas.faas.quantumservice.model.entity.scriptexecution.ExecutionStatus;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.server.core.Relation;

@Data
@Relation(collectionRelation = "scriptExecutions", itemRelation = "scriptExecution")
public class ScriptExecutionDto {

    private UUID id;
    private String activationId;

    @JsonIgnore
    private ProviderDto provider;

    @JsonIgnore
    private QuantumApplicationDto quantumApplication;

    private List<String> logs = new ArrayList<>();

    private String inputParams;
    private ZonedDateTime triggerFiredAt;
    private ZonedDateTime executionStartedAt;
    private ZonedDateTime executionEndedAt;
    private Long duration;
    private ExecutionResultDto result;
    private ExecutionStatus status;
}
