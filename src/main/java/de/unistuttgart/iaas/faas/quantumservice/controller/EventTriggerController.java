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

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "event-triggers")
@RequiredArgsConstructor
public class EventTriggerController {

    private final EventTriggerService service;
    private final EventTriggerLinkAssembler linkAssembler;

    @Transactional
    @PostMapping(value = "/emit-event")
    public ResponseEntity<Void> emitEvent(@RequestBody(required = false) EventPayload eventPayload) {
        service.emitEvent(eventPayload);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    @PostMapping
    public ResponseEntity<EntityModel<EventTriggerDto>> createEventTrigger(@Validated @RequestBody EventTriggerDto dto, @RequestParam String providerName) {
        EventTrigger createdEventTrigger = service.createEventTrigger(ModelMapperUtils.convert(dto, EventTrigger.class), providerName);
        return new ResponseEntity<>(linkAssembler.toModel(createdEventTrigger, EventTriggerDto.class), HttpStatus.CREATED);
    }

    @Transactional
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<EventTriggerDto>>> getEventTriggers() {
        return new ResponseEntity<>(linkAssembler.toModel(service.findAll(), EventTriggerDto.class), HttpStatus.OK);
    }

    @Transactional
    @GetMapping(value = "/{name}")
    public ResponseEntity<EntityModel<EventTriggerDto>> getEventTrigger(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findByName(name), EventTriggerDto.class), HttpStatus.OK);
    }

    @Transactional
    @GetMapping(value = "/provider/{name}")
    public ResponseEntity<CollectionModel<EntityModel<EventTriggerDto>>> getEventTriggersByProvider(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findByProviderName(name), EventTriggerDto.class), HttpStatus.OK);
    }

    @Transactional
    @GetMapping(value = "/quantum-applications/{name}")
    public ResponseEntity<CollectionModel<EntityModel<EventTriggerDto>>> getEventTriggersByQuantumApplication(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findByQuantumApplicationName(name), EventTriggerDto.class), HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping(value = "/{name}")
    public ResponseEntity<Void> deleteEventTrigger(@PathVariable String name) {
        service.deleteEventTrigger(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    @PostMapping(value = "/{eventTriggerName}/quantum-applications/{quantumApplicationName}")
    public ResponseEntity<Void> registerQuantumApplication(@PathVariable String eventTriggerName, @PathVariable String quantumApplicationName) {
        service.registerQuantumApplication(eventTriggerName, quantumApplicationName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping(value = "/{eventTriggerName}/quantum-applications/{quantumApplicationName}")
    public ResponseEntity<Void> unregisterQuantumApplication(@PathVariable String eventTriggerName, @PathVariable String quantumApplicationName) {
        service.unregisterQuantumApplication(eventTriggerName, quantumApplicationName);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
