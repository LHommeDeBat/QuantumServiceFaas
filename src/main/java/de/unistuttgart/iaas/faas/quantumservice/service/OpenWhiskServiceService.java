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
     * This method creates a new openWhiskService and stores inside the database.
     *
     * @param openWhiskService Incoming openWhiskService that should be stored inside the database
     * @return storedOpenWhiskService Stored openWhiskService that has its ID generated
     */
    public OpenWhiskService createOpenWhiskService(OpenWhiskService openWhiskService) {
        checkForConflict(openWhiskService.getName());
        encodeCredentials(openWhiskService);
        return repository.save(openWhiskService);
    }

    /**
     * This method returns all existing openWhiskServices
     * @return openWhiskServices
     */
    public Set<OpenWhiskService> findAll() {
        return repository.findAll();
    }

    /**
     * This method returns a specific openWhiskService using a unique openWhiskService name.
     *
     * @param name Unique openWhiskService name
     * @return openWhiskService
     */
    public OpenWhiskService findByName(String name) {
        return repository.findByName(name).orElseThrow(() -> new NoSuchElementException("No such openWhiskService exists!"));
    }

    /**
     * This method deletes an exiting OpenWhiskService using its unique name.
     *
     * @param name Unique openWhiskService name
     */
    public void deleteOpenWhiskService(String name) {
        // Retrieve openWhiskService from database
        OpenWhiskService openWhiskService = findByName(name);
        // Remove all elements that belong to openWhiskService that will be deleted
        deleteOpenWhiskServiceElements(openWhiskService);
        // Delete openWhiskService
        repository.delete(openWhiskService);
    }

    /**
     * This method encodes the incoming basic authentication credentials as a Base64-String.
     *
     * @param openWhiskService openWhiskService that needs his credentials encoded
     */
    private void encodeCredentials(OpenWhiskService openWhiskService) {
        openWhiskService.setBasicCredentials(Base64.encodeBase64String(openWhiskService.getBasicCredentials().getBytes()));
    }

    /**
     * This method removes all elements that belong to a specific openWhiskService (EventTriggers, Jobs, QuantumApplications, etc.)
     *
     * @param openWhiskService used openWhiskService
     */
    private void deleteOpenWhiskServiceElements(OpenWhiskService openWhiskService) {
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
        Set<EventTrigger> openWhiskServiceTriggers = eventTriggerRepository.findByOpenWhiskServiceName(openWhiskService.getName());
        eventTriggerRepository.deleteAll(openWhiskServiceTriggers);
        openWhiskServiceTriggers.forEach(openWhiskClient::removeTriggerFromFaas);

        // Remove all Script-Executions and OpenWhisk-Activations
        Set<ScriptExecution> openWhiskServiceExecutions = scriptExecutionRepository.findByOpenWhiskServiceName(openWhiskService.getName());
        scriptExecutionRepository.deleteAll(openWhiskServiceExecutions);

        // Remove all Quantum-Applications, Jobs and OpenWhisk-Actions
        Set<QuantumApplication> openWhiskServiceQuantumApplications = quantumApplicationRepository.findByOpenWhiskServiceName(openWhiskService.getName());
        for (QuantumApplication quantumApplication : openWhiskServiceQuantumApplications) {
            // Remove Jobs
            Set<Job> actionJobs = jobRepository.findByQuantumApplicationName(quantumApplication.getName());
            jobRepository.deleteAll(actionJobs);
        }
        quantumApplicationRepository.deleteAll(openWhiskServiceQuantumApplications);
        openWhiskServiceQuantumApplications.forEach(openWhiskClient::removeActionFromFaas);
    }

    /**
     * This method checks if a openWhiskService with the given unique name already exists.
     * @param name PpenWhiskService-Name
     */
    private void checkForConflict(String name) {
        if (repository.findByName(name).isPresent()) {
            throw new ElementAlreadyExistsException("OpenWhiskService with name '" + name + "' already exists!");
        }
    }
}
