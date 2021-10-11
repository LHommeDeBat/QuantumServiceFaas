package de.unistuttgart.iaas.faas.quantumservice.model.dto;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.job.JobStatusDetails;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.job.JobStatus;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Data
@Relation(collectionRelation = "jobs", itemRelation = "job")
public class JobDto {

    private UUID id;
    private String ibmqId;
    private Map<JobStatus, JobStatusDetails> statusDetails = new HashMap<>();
    private JobStatus status;
    private String result;
    private String inputParams;
    private String device;
    private ZonedDateTime creationDate;
    private ZonedDateTime endDate;
    private Boolean success;

    @JsonIgnore
    private QuantumApplication quantumApplication;
}
