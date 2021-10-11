package de.unistuttgart.iaas.faas.quantumservice.model.entity.job;

public enum JobStatus {
    CREATING,
    CREATED,
    VALIDATING,
    VALIDATED,
    QUEUED,
    RUNNING,
    COMPLETED,
    FAILED
}
