package de.unistuttgart.iaas.faas.quantumservice.service;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import de.unistuttgart.iaas.faas.quantumservice.api.OpenWhiskClient;
import de.unistuttgart.iaas.faas.quantumservice.configuration.IBMQProperties;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventTrigger;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventTriggerRepository;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.quantumapplication.QuantumApplicationRepository;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.scriptexecution.ExecutionStatus;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.scriptexecution.ScriptExecution;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.scriptexecution.ScriptExecutionRepository;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.job.Job;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.job.JobRepository;
import de.unistuttgart.iaas.faas.quantumservice.model.exception.ElementAlreadyExistsException;
import de.unistuttgart.iaas.faas.quantumservice.model.openwhisk.ActivationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * This Service-Class implements functions that operate on QuantumApplication objects.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuantumApplicationService {

    private final QuantumApplicationRepository repository;
    private final EventTriggerRepository eventTriggerRepository;
    private final ScriptExecutionRepository scriptExecutionRepository;
    private final JobRepository jobRepository;
    private final ProviderService providerService;
    private final OpenWhiskClient openWhiskClient;
    private final IBMQProperties ibmqProperties;

    /**
     * This method takes submitted Quantum-Application data, creates a QuantumApplication and then creates an equivalent
     * Action at the OpenWhisk-Provider.
     *
     * @param file Python-File containing Qiskit code
     * @param name Name of the QuantumApplication
     * @param dockerImage Tagged docker image name
     * @param providerName Name of the OpenWhisk-Provider that should be used for execution the QuantumApplication
     * @return createdQuantumApplication
     */
    public QuantumApplication createQuantumApplication(MultipartFile file, String name, String dockerImage, String notificationAddress, String providerName) {
        try {
            // Check if QuantumApplication with given name already exists
            checkForConflict(name);
            // Create QuantumApplication and fill it with data
            QuantumApplication quantumApplication = new QuantumApplication();
            quantumApplication.setName(name);
            quantumApplication.setProvider(providerService.findByName(providerName));
            quantumApplication.setCode(Base64.encodeBase64String(file.getBytes()));
            if (Objects.isNull(dockerImage)) {
                dockerImage = "sykes360gtx/python-qiskit:latest";
            }
            quantumApplication.setDockerImage(dockerImage);
            quantumApplication.setNotificationAddress(notificationAddress);

            // Save QuantumApplication
            QuantumApplication createdQuantumApplication = repository.save(quantumApplication);
            // Create an Action inside the OpenWhisk-Provider
            openWhiskClient.deployActionToFaas(quantumApplication);
            return createdQuantumApplication;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Could not read file!");
        }
    }

    /**
     * This method invokes the application using the given input.
     *
     * @param name name of the quantum application that should be invoked
     * @param inputParams input parameters that should be passed to the openwhisk action
     */
    public void invokeQuantumApplication(String name, Map<String, Object> inputParams) {
        QuantumApplication quantumApplication = findByName(name);
        inputParams.put("apiToken", ibmqProperties.getApiToken());
        ActivationResult result = openWhiskClient.invokeAction(quantumApplication, inputParams);
        ScriptExecution scriptExecution = new ScriptExecution();
        scriptExecution.setActivationId(result.getActivationId());
        scriptExecution.setQuantumApplication(quantumApplication);
        scriptExecution.setProvider(quantumApplication.getProvider());
        inputParams.put("apiToken", "**********");
        scriptExecution.setInputParams(new JSONObject(inputParams).toString());
        scriptExecution.setStatus(ExecutionStatus.RUNNING);
        scriptExecutionRepository.save(scriptExecution);
    }

    /**
     * This method returns all existing QuantumApplications.
     *
     * @return quantumApplications
     */
    public Set<QuantumApplication> findAll() {
        return repository.findAll();
    }

    public QuantumApplication findByName(String name) {
        return repository.findByName(name).orElseThrow(() -> new NoSuchElementException("Quantum-Application with name '" + name + "' does not exist!"));
    }

    /**
     * This method returns all QuantumApplications that belong to a specific OpenWhisk-Provider.
     *
     * @param providerName Unique name of the provider
     * @return providerApplications
     */
    public Set<QuantumApplication> findByProvider(String providerName) {
        return repository.findByProviderName(providerName);
    }

    /**
     * This method deletes an existing QuantumApplication using its unique name.
     *
     * @param name Unique QuantumApplication name
     */
    public void deleteQuantumApplication(String name) {
        QuantumApplication quantumApplication = findByName(name);
        // Unregister quantum application from all events
        for (EventTrigger event: quantumApplication.getEventTriggers()) {
            event.getQuantumApplications().removeIf(application -> application.getName().equals(name));
            eventTriggerRepository.save(event);
        }
        // Delete Rules of the Action from the DB and from OpenWhisk-Provider
        deleteQuantumApplicationElements(quantumApplication);
        repository.delete(quantumApplication);
        openWhiskClient.removeActionFromFaas(quantumApplication);
    }

    /**
     * This method deletes all elements that belong to a QuantumApplication.
     *
     * @param quantumApplication QuantumApplication which elements should be deleted
     */
    private void deleteQuantumApplicationElements(QuantumApplication quantumApplication) {
        // Remove all Rules from OpenWhisk-Provider
        for (EventTrigger eventTrigger : quantumApplication.getEventTriggers()) {
            openWhiskClient.removeRuleFromFaas(eventTrigger, quantumApplication);
        }

        // Remove script executions
        Set<ScriptExecution> actionScriptExecutions = scriptExecutionRepository.findByQuantumApplicationName(quantumApplication.getName());
        scriptExecutionRepository.deleteAll(actionScriptExecutions);

        // Remove jobs
        Set<Job> actionJobs = jobRepository.findByQuantumApplicationName(quantumApplication.getName());
        jobRepository.deleteAll(actionJobs);
    }

    /**
     * This method checks if a QuantumApplication already exists.
     *
     * @param name Unique QuantumApplication name
     */
    private void checkForConflict(String name) {
        if (repository.findByName(name).isPresent()) {
            throw new ElementAlreadyExistsException("Quantum-Application with name '" + name + "' already exists!");
        }
    }
}
