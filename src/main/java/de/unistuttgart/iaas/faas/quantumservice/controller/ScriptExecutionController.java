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

/**
 * This class represents the REST-Controller of the ScriptExecutions. It handles all incoming REST-Requests
 * for the ScriptExecutions.
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "script-executions")
@RequiredArgsConstructor
public class ScriptExecutionController {

    private final ScriptExecutionService service;
    private final ScriptExecutionLinkAssembler linkAssembler;

    /**
     * This method returns all script executions.
     *
     * @return scriptExecutions
     */
    @Transactional
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ScriptExecutionDto>>> getScriptExecutions() {
        return new ResponseEntity<>(linkAssembler.toModel(service.findAll(), ScriptExecutionDto.class), HttpStatus.OK);
    }

    /**
     * This method returns a specific script execution with some id.
     *
     * @param id ID of the script execution
     * @return scriptExecution
     */
    @Transactional
    @GetMapping(value = "/{id}")
    public ResponseEntity<EntityModel<ScriptExecutionDto>> getScriptExecution(@PathVariable UUID id) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findById(id), ScriptExecutionDto.class), HttpStatus.OK);
    }

    /**
     * This method returns all script executions of some OpenWhiskService.
     *
     * @param name Name of the OpenWhiskService
     * @return scriptExecutions
     */
    @Transactional
    @GetMapping(value = "/openwhisk-service/{name}")
    public ResponseEntity<CollectionModel<EntityModel<ScriptExecutionDto>>> getScriptExecutionsByOpenWhiskService(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findByOpenWhiskService(name), ScriptExecutionDto.class), HttpStatus.OK);
    }

    /**
     * This method returns all script executions of some QuantumApplication.
     *
     * @param name Name of the QuantumApplication
     * @return scriptExecutions
     */
    @Transactional
    @GetMapping(value = "/quantum-application/{name}")
    public ResponseEntity<CollectionModel<EntityModel<ScriptExecutionDto>>> getScriptExecutionsByQuantumApplication(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findByQuantumApplication(name), ScriptExecutionDto.class), HttpStatus.OK);
    }
}
