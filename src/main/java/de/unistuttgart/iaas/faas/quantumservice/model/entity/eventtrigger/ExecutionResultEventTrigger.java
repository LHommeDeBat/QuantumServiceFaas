package de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("ExecutionResultTrigger")
public class ExecutionResultEventTrigger extends EventTrigger {

    private String executedApplicationName;
}
