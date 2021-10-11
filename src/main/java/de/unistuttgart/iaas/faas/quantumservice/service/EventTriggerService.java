package de.unistuttgart.iaas.faas.quantumservice.service;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import de.unistuttgart.iaas.faas.quantumservice.api.OpenWhiskClient;
import de.unistuttgart.iaas.faas.quantumservice.configuration.IBMQProperties;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventPayload;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventTrigger;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventTriggerRepository;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.QueueSizeEventTrigger;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.provider.Provider;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.quantumapplication.QuantumApplicationRepository;
import de.unistuttgart.iaas.faas.quantumservice.model.exception.ElementAlreadyExistsException;
import de.unistuttgart.iaas.faas.quantumservice.model.openwhisk.ActivationResult;
import de.unistuttgart.iaas.faas.quantumservice.model.openwhisk.OpenWhiskActivation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * This Service-Class implements functions that operate on Event-Trigger objects.
 */
@Service
@RequiredArgsConstructor
public class EventTriggerService {

    private final EventTriggerRepository repository;
    private final QuantumApplicationRepository quantumApplicationRepository;
    private final ProviderService providerService;
    private final OpenWhiskClient openWhiskClient;
    private final ScriptExecutionService scriptExecutionService;
    private final IBMQProperties ibmqProperties;

    /**
     * This method stores a new EventTrigger in the database and also creates a Trigger at the given OpenWhisk-Provider.
     *
     * @param eventTrigger EventTrigger that should be created
     * @param providerName OpenWhisk-Provider that should be used
     * @return createdTrigger
     */
    public EventTrigger createEventTrigger(EventTrigger eventTrigger, String providerName) {
        // Check if Trigger-Name is not already used
        checkForConflict(eventTrigger.getName());
        // Retrieve Provider from database
        Provider existingProvider = providerService.findByName(providerName);
        // Fill EventTrigger-Object
        eventTrigger.setProvider(existingProvider);

        // Create Trigger at the OpenWhisk-Provider
        openWhiskClient.deployTriggerToFaas(eventTrigger);
        // Store EventTrigger in database
        return repository.save(eventTrigger);
    }

    /**
     * This method fires triggers with some payload.
     *
     * @param eventPayload - EventPayload
     */
    public void emitEvent(EventPayload eventPayload) {
        Set<EventTrigger> eventTriggerToDelete = new HashSet<>();
        // Add apiToken to payload
        eventPayload.addEventPayloadProperties("apiToken", ibmqProperties.getApiToken());
        for (EventTrigger trigger : findByEventType(eventPayload)) {
            fireEventTrigger(trigger, eventPayload);

            if (trigger instanceof QueueSizeEventTrigger) {
                QueueSizeEventTrigger queueSizeEventTrigger = (QueueSizeEventTrigger) trigger;
                if (Objects.isNull(queueSizeEventTrigger.getTriggerDelay())) {
                    eventTriggerToDelete.add(trigger);
                } else {
                    updateDelayUntilTime(trigger.getName());
                }
            }
        }

        // Delete triggers without a delay (one-time-use QueueSizeEventTriggers)
        for (EventTrigger trigger : eventTriggerToDelete) {
            deleteEventTrigger(trigger.getName());
        }
    }

    /**
     * This method fires a trigger with some payload. The Trigger activation is used to retrieve the Activation-ID
     * of all executed actions. Then ScriptExecutions are generated from the Activation-IDs of the executed actions.
     *
     * @param eventTrigger EventTrigger
     * @param eventPayload Event payload
     * @return openWhiskActivationId
     */
    public ActivationResult fireEventTrigger(EventTrigger eventTrigger, EventPayload eventPayload) {
        ActivationResult result = openWhiskClient.fireTrigger(eventTrigger, eventPayload.getEventPayloadProperties());
        OpenWhiskActivation activation = openWhiskClient.getActivation(eventTrigger.getProvider(), result.getActivationId());
        scriptExecutionService.createScriptExecutionsFromLogs((Map<String, String>) activation.getResponse().getResult(), activation.getLogs(), activation.getStart());
        return result;
    }

    /**
     * This method returns all stored EventTriggers.
     *
     * @return eventTriggers
     */
    public Set<EventTrigger> findAll() {
        return repository.findAll();
    }

    public EventTrigger findByName(String name) {
        return repository.findByName(name).orElseThrow(() -> new NoSuchElementException("Trigger does not exist!"));
    }

    /**
     * This method returns all EventTriggers that belong to a specific provider.
     *
     * @param name Name of the used provider
     * @return eventTriggers
     */
    public Set<EventTrigger> findByProviderName(String name) {
        return repository.findByProviderName(name);
    }

    /**
     * This method returns all EventTriggers that belong to a specific quantum application.
     *
     * @param name Name of the used quantum application
     * @return eventTriggers
     */
    public Set<EventTrigger> findByQuantumApplicationName(String name) {
        return quantumApplicationRepository.getQuantumApplicationTriggers(name);
    }

    // TODO: Adjust this method to be more general and work with different kinds of events
    public Set<EventTrigger> findByEventType(EventPayload payload) {
        return repository.findByEventType(payload);
    }

    /**
     * This method deletes an existing trigger
     *
     * @param name Unique EventTrigger name of the EventTrigger that should be deleted
     */
    public void deleteEventTrigger(String name) {
        // Get existing trigger
        EventTrigger existingEventTrigger = findByName(name);

        // Delete Rules of the Trigger from the DB and from OpenWhisk-Provider
        deleteEventTriggerRules(existingEventTrigger);

        // Unregister all applications
        existingEventTrigger.setQuantumApplications(new HashSet<>());
        existingEventTrigger = repository.save(existingEventTrigger);

        // Delete Trigger from DB and from OpenWhisk-Provider
        repository.delete(existingEventTrigger);
        openWhiskClient.removeTriggerFromFaas(existingEventTrigger);
    }

    /**
     * This method registers a QuantumApplication to a EventTrigger.
     *
     * @param eventTriggerName Name of the EventTrigger
     * @param quantumApplicationName Name of the QuantumApplication
     */
    public void registerQuantumApplication(String eventTriggerName, String quantumApplicationName) {
        // Retrieve QuantumApplication and EventTrigger from database
        EventTrigger eventTrigger = findByName(eventTriggerName);
        QuantumApplication quantumApplication = quantumApplicationRepository.findByName(quantumApplicationName).orElseThrow(() -> new NoSuchElementException("There is no QuantumApplication with name=" + quantumApplicationName + "!"));
        // Check if both objects are using the same Provider and Namespace
        checkConflictingNamespace(eventTrigger, quantumApplication);
        // Link Application with Trigger
        eventTrigger.getQuantumApplications().add(quantumApplication);
        repository.save(eventTrigger);
        // Create a Rule in OpenWhisk-Provider to link the Action with the Trigger
        openWhiskClient.deployRuleToFaas(eventTrigger, quantumApplication);
    }

    /**
     * This method unregisters a QuantumApplication from a EventTrigger.
     *
     * @param eventTriggerName Name of the EventTrigger
     * @param quantumApplicationName Name of the QuantumApplication
     */
    public void unregisterQuantumApplication(String eventTriggerName, String quantumApplicationName) {
        // Retrieve QuantumApplication and EventTrigger from database
        EventTrigger eventTrigger = findByName(eventTriggerName);
        QuantumApplication quantumApplication = quantumApplicationRepository.findByName(quantumApplicationName).orElseThrow(() -> new NoSuchElementException("There is no QuantumApplication with name=" + quantumApplicationName + "!"));
        // Unlink Application from Trigger
        eventTrigger.getQuantumApplications().removeIf(application -> application.getName().equals(quantumApplicationName));
        repository.save(eventTrigger);
        // Remove Rule from OpenWhisk-Provider to unlink Action and Trigger
        openWhiskClient.removeRuleFromFaas(eventTrigger, quantumApplication);
    }

    // This method removes all Rules from the OpenWhisk-Provider
    private void deleteEventTriggerRules(EventTrigger eventTrigger) {
        for (QuantumApplication quantumApplication : eventTrigger.getQuantumApplications()) {
            openWhiskClient.removeRuleFromFaas(eventTrigger, quantumApplication);
        }
    }

    /**
     * This method checks if a EventTrigger already exists.
     *
     * @param name Unique name of the trigger
     */
    private void checkForConflict(String name) {
        if (repository.findByName(name).isPresent()) {
            throw new ElementAlreadyExistsException("Trigger with name '" + name + "' already exists!");
        }
    }

    /**
     * This method checks if a EventTrigger and QuantumApplication belong to the same Namespace of the same Openwhisk-Provider.
     *
     * @param eventTrigger EventTrigger
     * @param quantumApplication QuantumApplication
     */
    private void checkConflictingNamespace(EventTrigger eventTrigger, QuantumApplication quantumApplication) {
        String eventTriggerUniqueNamespace = eventTrigger.getProvider().getName() + eventTrigger.getProvider().getNamespace();
        String quantumApplicationUniqueNamespace = quantumApplication.getProvider().getName() + quantumApplication.getProvider().getNamespace();
        if (!eventTriggerUniqueNamespace.equals(quantumApplicationUniqueNamespace)) {
            throw new RuntimeException("Provider-Namespace of EventTrigger and QuantumApplication do not match!");
        }
    }

    /**
     * This method updates disables a QueueSizeEventTrigger after it was fired by the configured delayed value
     *
     * @param name Name of the QueueSizeTrigger
     */
    private void updateDelayUntilTime(String name) {
        QueueSizeEventTrigger eventTrigger = (QueueSizeEventTrigger) findByName(name);
        eventTrigger.setDisabledUntil(ZonedDateTime.now().plusMinutes(eventTrigger.getTriggerDelay()));
        repository.save(eventTrigger);
    }
}
