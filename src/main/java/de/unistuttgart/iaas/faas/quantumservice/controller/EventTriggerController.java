package de.unistuttgart.iaas.faas.quantumservice.controller;

import de.unistuttgart.iaas.faas.quantumservice.hateoas.EventTriggerLinkAssembler;
import de.unistuttgart.iaas.faas.quantumservice.model.dto.eventtrigger.EventTriggerDto;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventPayload;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventTrigger;
import de.unistuttgart.iaas.faas.quantumservice.service.EventTriggerService;
import de.unistuttgart.iaas.faas.quantumservice.utils.ModelMapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * This class represents the REST-Controller of the Event-Triggers. It handles all incoming REST-Requests
 * for the EventTriggers.
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "event-triggers")
@RequiredArgsConstructor
public class EventTriggerController {

    private final EventTriggerService service;
    private final EventTriggerLinkAssembler linkAssembler;

    /**
     * This method handles events that are emitted by external event sources. If fires all appropriate EventTriggers.
     *
     * @param eventPayload EventPayload that is used to filter appropriate EventTriggers and be used as input for the QuantumApplications
     * @return Void
     */
    @Transactional
    @PostMapping(value = "/emit-event")
    public ResponseEntity<Void> emitEvent(@RequestBody(required = false) EventPayload eventPayload) {
        service.emitEvent(eventPayload);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method creates a new Event Trigger.
     *
     * @param dto EventTriggerDTO
     * @param openWhiskServiceName OpenWhiskService Name of the OpenWhiskService where the new Trigger should be created
     * @return createdEventTrigger
     */
    @Transactional
    @PostMapping
    public ResponseEntity<EntityModel<EventTriggerDto>> createEventTrigger(@Validated @RequestBody EventTriggerDto dto, @RequestParam String openWhiskServiceName) {
        EventTrigger createdEventTrigger = service.createEventTrigger(ModelMapperUtils.convert(dto, EventTrigger.class), openWhiskServiceName);
        return new ResponseEntity<>(linkAssembler.toModel(createdEventTrigger, EventTriggerDto.class), HttpStatus.CREATED);
    }

    /**
     * This method returns all available EventTriggers.
     *
     * @return eventTriggers
     */
    @Transactional
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<EventTriggerDto>>> getEventTriggers() {
        return new ResponseEntity<>(linkAssembler.toModel(service.findAll(), EventTriggerDto.class), HttpStatus.OK);
    }

    /**
     * This method returns an event trigger with a specific name.
     *
     * @param name Name of the event trigger
     * @return eventTrigger
     */
    @Transactional
    @GetMapping(value = "/{name}")
    public ResponseEntity<EntityModel<EventTriggerDto>> getEventTrigger(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findByName(name), EventTriggerDto.class), HttpStatus.OK);
    }

    /**
     * This method returns event triggers of a OpenWhiskService.
     *
     * @param name Name of the OpenWhiskService
     * @return eventTriggers
     */
    @Transactional
    @GetMapping(value = "/openwhisk-service/{name}")
    public ResponseEntity<CollectionModel<EntityModel<EventTriggerDto>>> getEventTriggersByOpenWhiskService(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findByOpenWhiskServiceName(name), EventTriggerDto.class), HttpStatus.OK);
    }

    /**
     * This method returns event triggers linked with a QuantumApplication.
     *
     * @param name Name of the QuantumApplication
     * @return eventTriggers
     */
    @Transactional
    @GetMapping(value = "/quantum-applications/{name}")
    public ResponseEntity<CollectionModel<EntityModel<EventTriggerDto>>> getEventTriggersByQuantumApplication(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findByQuantumApplicationName(name), EventTriggerDto.class), HttpStatus.OK);
    }

    /**
     * This method removes a event trigger.
     *
     * @param name Name of the EventTrigger
     * @return Void
     */
    @Transactional
    @DeleteMapping(value = "/{name}")
    public ResponseEntity<Void> deleteEventTrigger(@PathVariable String name) {
        service.deleteEventTrigger(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method links an event trigger with a quantum application.
     *
     * @param eventTriggerName Name of the EventTrigger
     * @param quantumApplicationName Name of the QuantumApplication
     * @return Void
     */
    @Transactional
    @PostMapping(value = "/{eventTriggerName}/quantum-applications/{quantumApplicationName}")
    public ResponseEntity<Void> registerQuantumApplication(@PathVariable String eventTriggerName, @PathVariable String quantumApplicationName) {
        service.registerQuantumApplication(eventTriggerName, quantumApplicationName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method unlinks an event trigger from a quantum application.
     *
     * @param eventTriggerName Name of the EventTrigger
     * @param quantumApplicationName Name of the QuantumApplication
     * @return Void
     */
    @Transactional
    @DeleteMapping(value = "/{eventTriggerName}/quantum-applications/{quantumApplicationName}")
    public ResponseEntity<Void> unregisterQuantumApplication(@PathVariable String eventTriggerName, @PathVariable String quantumApplicationName) {
        service.unregisterQuantumApplication(eventTriggerName, quantumApplicationName);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
