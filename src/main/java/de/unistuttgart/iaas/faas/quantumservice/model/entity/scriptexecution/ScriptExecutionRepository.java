package de.unistuttgart.iaas.faas.quantumservice.model.entity.scriptexecution;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface ScriptExecutionRepository extends CrudRepository<ScriptExecution, UUID> {

    Set<ScriptExecution> findAll();
    Set<ScriptExecution> findByStatus(ExecutionStatus status);
    Set<ScriptExecution> findByOpenWhiskServiceName(String name);
    Set<ScriptExecution> findByQuantumApplicationName(String name);
}
