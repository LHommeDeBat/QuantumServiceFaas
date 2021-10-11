package de.unistuttgart.iaas.faas.quantumservice.events;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;

import de.unistuttgart.iaas.faas.quantumservice.api.OpenWhiskClient;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.scriptexecution.ScriptExecution;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.scriptexecution.ScriptExecutionRepository;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.scriptexecution.ExecutionResult;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.scriptexecution.ExecutionStatus;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.job.Job;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.job.JobRepository;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.job.JobStatus;
import de.unistuttgart.iaas.faas.quantumservice.model.openwhisk.OpenWhiskActivation;
import de.unistuttgart.iaas.faas.quantumservice.utils.ModelMapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is responsible for polling the OpenWhisk-REST-API and gather Activation data to update existing ScriptExecutions.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ScriptExecutionChecker {

    private final ScriptExecutionRepository repository;
    private final JobRepository jobRepository;
    private final OpenWhiskClient openWhiskClient;

    /**
     * This scheduled method is repeatedly executed in process that is running in the background.
     */
    @Transactional
    @Scheduled(initialDelay = 10000, fixedDelay = 60000)
    public void checkRunningActivations() {
        try {
            // Retrieve running ScriptExecutions from database
            Set<ScriptExecution> runningScriptExecutions = repository.findByStatus(ExecutionStatus.RUNNING);
            log.info("Found {} running script executions", runningScriptExecutions.size());
            for (ScriptExecution scriptExecution : runningScriptExecutions) {
                // Poll activation from openWhisk
                OpenWhiskActivation openWhiskActivation = openWhiskClient.getActivation(scriptExecution.getProvider(), scriptExecution.getActivationId());
                if (!Objects.isNull(openWhiskActivation)) {
                    // Update ScriptExecution
                    scriptExecution.setStatus(openWhiskActivation.getResponse().getSuccess() ? ExecutionStatus.SUCCESS : ExecutionStatus.ERROR);
                    scriptExecution.setExecutionStartedAt(ZonedDateTime.ofInstant(Instant.ofEpochMilli(openWhiskActivation.getStart()), ZoneId.of("UTC")));
                    scriptExecution.setExecutionEndedAt(ZonedDateTime.ofInstant(Instant.ofEpochMilli(openWhiskActivation.getEnd()), ZoneId.of("UTC")));
                    scriptExecution.setDuration(openWhiskActivation.getDuration());
                    scriptExecution.setLogs(openWhiskActivation.getLogs());

                    // Try to parse activation result and store it inside the script execution
                    Object activationResult = openWhiskActivation.getResponse().getResult();
                    if (!Objects.isNull(activationResult)) {
                        scriptExecution.setResult(ModelMapperUtils.convert(activationResult, ExecutionResult.class));
                    }
                    // Update ScriptExecution inside the database
                    scriptExecution = repository.save(scriptExecution);
                    log.info("Activation '{}' of action '{}' finished processing", scriptExecution.getActivationId(), scriptExecution.getQuantumApplication().getName());

                    // Create Job if script execution was successful
                    if (scriptExecution.getStatus().equals(ExecutionStatus.SUCCESS)) {
                        // Create empty job using the Job-ID that is stored inside the ScriptExecution-Result with initial status
                        Job job = new Job();
                        job.setIbmqId(scriptExecution.getResult().getJobId());
                        job.setStatus(JobStatus.CREATING);
                        job.setQuantumApplication(scriptExecution.getQuantumApplication());
                        job.setInputParams(scriptExecution.getInputParams());
                        job.setDevice(new JSONObject(scriptExecution.getInputParams()).getString("device"));
                        jobRepository.save(job);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Something went wrong accessing the OpenWhisk-API!", e);
        }
    }
}
