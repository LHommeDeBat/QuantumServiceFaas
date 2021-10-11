package de.unistuttgart.iaas.faas.quantumservice.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import de.unistuttgart.iaas.faas.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.quantumapplication.QuantumApplicationRepository;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.scriptexecution.ScriptExecution;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.scriptexecution.ScriptExecutionRepository;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.scriptexecution.ExecutionStatus;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * This Service-Class implements functions that operate on ScriptExecution objects.
 */
@Service
@RequiredArgsConstructor
public class ScriptExecutionService {

    private final ScriptExecutionRepository repository;
    private final QuantumApplicationRepository quantumApplicationRepository;

    /**
     * This method stores a new ScriptExecution inside the database.
     *
     * @param scriptExecution New ScriptExecution
     */
    public void createScriptExecution(ScriptExecution scriptExecution) {
        repository.save(scriptExecution);
    }

    /**
     * This method generates a ScriptExecution from OpenWhisk Trigger-Activation-Logs.
     *
     * @param triggerPayload Payload used to fire trigger
     * @param logs Logs of a OpenWhisk-Activation
     * @param triggerTime Timestamp of the time when the Trigger was fired
     */
    public void createScriptExecutionsFromLogs(Map<String, String> triggerPayload, List<String> logs, Long triggerTime) {
        // Check Trigger-Activation-Logs for Action-Activations and create a new ScriptExecution of those Actions-Activations
        for (String log : logs) {
            ScriptExecution scriptExecution = new ScriptExecution();
            try {
                JSONObject object = new JSONObject(log);
                JSONObject inputParamsJson = new JSONObject(triggerPayload);
                inputParamsJson.put("apiToken", "**********");

                // Get Action from the name of the log
                String fullyQualifiedActionName = object.getString("action");
                Optional<QuantumApplication> actionOptional = quantumApplicationRepository.findByName(fullyQualifiedActionName.split("/")[1]);
                if (actionOptional.isEmpty()) {
                    throw new RuntimeException("There is no Action that belongs to this activation!");
                }

                scriptExecution.setQuantumApplication(actionOptional.get());
                scriptExecution.setProvider(actionOptional.get().getProvider());
                scriptExecution.setInputParams(inputParamsJson.toString());
                scriptExecution.setActivationId(object.getString("activationId"));
                scriptExecution.setStatus(ExecutionStatus.RUNNING);
                scriptExecution.setTriggerFiredAt(ZonedDateTime.ofInstant(Instant.ofEpochMilli(triggerTime), ZoneId.of("UTC")));

                createScriptExecution(scriptExecution);
            } catch (JSONException e) {
                throw new RuntimeException("Cannot read Activation-Log!");
            }
        }
    }

    /**
     * This method returns all ScriptExecutions.
     *
     * @return scriptExecutions
     */
    public Set<ScriptExecution> findAll() {
        return repository.findAll();
    }

    /**
     * This method returns a specific ScriptExecutions.
     *
     * @param id Unique ID of a ScriptExecution
     * @return scriptExecution
     */
    public ScriptExecution findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("ScriptExecution does not exist!"));
    }

    /**
     * This method returns ScriptExectutions that belong to a specific namespace of a specific provider.
     *
     * @param providerName Name of the provider
     * @return providerScriptExecutions
     */
    public Set<ScriptExecution> findByProvider(String providerName) {
        return repository.findByProviderName(providerName);
    }

    /**
     * This method returns ScriptExecutions that belong to a specific Quantum-Application.
     *
     * @param quantumApplicationName Unique Quantum-Application-Name
     * @return quantumApplicationScripExecutions
     */
    public Set<ScriptExecution> findByQuantumApplication(String quantumApplicationName) {
        return repository.findByQuantumApplicationName(quantumApplicationName);
    }
}
