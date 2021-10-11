package de.unistuttgart.iaas.faas.quantumservice.model.entity.scriptexecution;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import de.unistuttgart.iaas.faas.quantumservice.model.entity.HasId;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.provider.Provider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScriptExecution extends HasId {

    private String activationId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Provider provider;

    @ManyToOne(fetch = FetchType.LAZY)
    private QuantumApplication quantumApplication;

    @Column(length = 20971520)
    @ElementCollection
    private List<String> logs = new ArrayList<>();

    @Lob
    @Column
    private String inputParams;
    private ZonedDateTime triggerFiredAt;
    private ZonedDateTime executionStartedAt;
    private ZonedDateTime executionEndedAt;
    private Long duration;

    @Embedded
    private ExecutionResult result;

    @Enumerated(EnumType.STRING)
    private ExecutionStatus status;
}
