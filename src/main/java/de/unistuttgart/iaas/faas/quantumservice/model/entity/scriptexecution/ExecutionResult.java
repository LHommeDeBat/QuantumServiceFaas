package de.unistuttgart.iaas.faas.quantumservice.model.entity.scriptexecution;

import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ExecutionResult {

    private String jobId;
}
