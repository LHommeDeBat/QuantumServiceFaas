package de.unistuttgart.iaas.faas.quantumservice.controller;

import java.util.UUID;

import de.unistuttgart.iaas.faas.quantumservice.hateoas.ScriptExecutionLinkAssembler;
import de.unistuttgart.iaas.faas.quantumservice.model.dto.ScriptExecutionDto;
import de.unistuttgart.iaas.faas.quantumservice.service.ScriptExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "script-executions")
@RequiredArgsConstructor
public class ScriptExecutionController {

    private final ScriptExecutionService service;
    private final ScriptExecutionLinkAssembler linkAssembler;

    @Transactional
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ScriptExecutionDto>>> getScriptExecutions() {
        return new ResponseEntity<>(linkAssembler.toModel(service.findAll(), ScriptExecutionDto.class), HttpStatus.OK);
    }

    @Transactional
    @GetMapping(value = "/{id}")
    public ResponseEntity<EntityModel<ScriptExecutionDto>> getScriptExecution(@PathVariable UUID id) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findById(id), ScriptExecutionDto.class), HttpStatus.OK);
    }

    @Transactional
    @GetMapping(value = "/provider/{name}")
    public ResponseEntity<CollectionModel<EntityModel<ScriptExecutionDto>>> getScriptExecutionsByProvider(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findByProvider(name), ScriptExecutionDto.class), HttpStatus.OK);
    }

    @Transactional
    @GetMapping(value = "/quantum-application/{name}")
    public ResponseEntity<CollectionModel<EntityModel<ScriptExecutionDto>>> getScriptExecutionsByAction(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findByQuantumApplication(name), ScriptExecutionDto.class), HttpStatus.OK);
    }
}
