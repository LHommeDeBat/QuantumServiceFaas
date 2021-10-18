package de.unistuttgart.iaas.faas.quantumservice.service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import de.unistuttgart.iaas.faas.quantumservice.api.OpenWhiskClient;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.openwhiskservice.OpenWhiskService;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.quantumapplication.QuantumApplicationRepository;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.scriptexecution.ScriptExecution;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.scriptexecution.ScriptExecutionRepository;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.job.Job;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.job.JobRepository;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.openwhiskservice.OpenWhiskServiceRepository;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventTrigger;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventTriggerRepository;
import de.unistuttgart.iaas.faas.quantumservice.model.exception.ElementAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

/**
 * This Service-Class implements functions that operate on OpenWhiskService objects.
 */
@Service
@RequiredArgsConstructor
public class OpenWhiskServiceService {

    private final OpenWhiskServiceRepository repository;
    private final QuantumApplicationRepository quantumApplicationRepository;
    private final EventTriggerRepository eventTriggerRepository;
    private final ScriptExecutionRepository scriptExecutionRepository;
    private final JobRepository jobRepository;
    private final OpenWhiskClient openWhiskClient;

    /**
     * This method creates a new provider and stores inside the database.
     *
     * @param openWhiskService Incoming provider that should be stored inside the database
     * @return storedProvider Stored provider that has its ID generated
     */
    public OpenWhiskService createOpenWhiskService(OpenWhiskService openWhiskService) {
        checkForConflict(openWhiskService.getName());
        encodeCredentials(openWhiskService);
        return repository.save(openWhiskService);
    }

    /**
     * This method returns all existing providers
     * @return providers
     */
    public Set<OpenWhiskService> findAll() {
        return repository.findAll();
    }

    /**
     * This method returns a specific provider using a unique provider name.
     *
     * @param name Unique provider name
     * @return provider
     */
    public OpenWhiskService findByName(String name) {
        return repository.findByName(name).orElseThrow(() -> new NoSuchElementException("No such provider exists!"));
    }

    /**
     * This method deletes a exiting OpenWhisk service using its unique name.
     *
     * @param name Unique provider name
     */
    public void deleteOpenWhiskService(String name) {
        // Retrieve provider from database
        OpenWhiskService openWhiskService = findByName(name);
        // Remove all elements that belong to provider that will be deleted
        deleteProviderElements(openWhiskService);
        // Delete provider
        repository.delete(openWhiskService);
    }

    /**
     * This method encodes the incoming basic authentication credentials as a Base64-String.
     *
     * @param openWhiskService provider that needs his credentials encoded
     */
    private void encodeCredentials(OpenWhiskService openWhiskService) {
        openWhiskService.setBasicCredentials(Base64.encodeBase64String(openWhiskService.getBasicCredentials().getBytes()));
    }

    /**
     * This method removes all elements that belong to a specific provider (EventTriggers, Jobs, QuantumApplications, etc.)
     * @param openWhiskService used provider
     */
    private void deleteProviderElements(OpenWhiskService openWhiskService) {
        // Delete all rules from OpenWhisk
        for (EventTrigger eventTrigger : eventTriggerRepository.findByOpenWhiskServiceName(openWhiskService.getName())) {
            // Remove Rules from Open-Whisk
            for (QuantumApplication quantumApplication : eventTrigger.getQuantumApplications()) {
                openWhiskClient.removeRuleFromFaas(eventTrigger, quantumApplication);
            }

            // Unlink all Quantum-Applications and Event-Triggers
            eventTrigger.setQuantumApplications(new HashSet<>());
            eventTriggerRepository.save(eventTrigger);
        }

        // Delete all Event-Triggers and OpenWhisk-Triggers
        Set<EventTrigger> providerEventTriggers = eventTriggerRepository.findByOpenWhiskServiceName(openWhiskService.getName());
        eventTriggerRepository.deleteAll(providerEventTriggers);
        providerEventTriggers.forEach(openWhiskClient::removeTriggerFromFaas);

        // Remove all Script-Executions and OpenWhisk-Activations
        Set<ScriptExecution> providerScriptExecutions = scriptExecutionRepository.findByOpenWhiskServiceName(openWhiskService.getName());
        scriptExecutionRepository.deleteAll(providerScriptExecutions);

        // Remove all Quantum-Applications, Jobs and OpenWhisk-Actions
        Set<QuantumApplication> providerQuantumApplications = quantumApplicationRepository.findByOpenWhiskServiceName(openWhiskService.getName());
        for (QuantumApplication quantumApplication : providerQuantumApplications) {
            // Remove Jobs
            Set<Job> actionJobs = jobRepository.findByQuantumApplicationName(quantumApplication.getName());
            jobRepository.deleteAll(actionJobs);
        }
        quantumApplicationRepository.deleteAll(providerQuantumApplications);
        providerQuantumApplications.forEach(openWhiskClient::removeActionFromFaas);
    }

    /**
     * This method checks if a provider with the given unique name already exists.
     * @param name Provider-Name
     */
    private void checkForConflict(String name) {
        if (repository.findByName(name).isPresent()) {
            throw new ElementAlreadyExistsException("Provider with name '" + name + "' already exists!");
        }
    }
}