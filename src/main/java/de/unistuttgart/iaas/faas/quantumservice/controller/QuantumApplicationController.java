package de.unistuttgart.iaas.faas.quantumservice.controller;

import java.util.Map;

import de.unistuttgart.iaas.faas.quantumservice.hateoas.QuantumApplicationLinkAssembler;
import de.unistuttgart.iaas.faas.quantumservice.model.dto.QuantumApplicationDto;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.faas.quantumservice.service.QuantumApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * This class represents the REST-Controller of the QuantumApplications. It handles all incoming REST-Requests
 * for the QuantumApplications.
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "quantum-applications")
@RequiredArgsConstructor
public class QuantumApplicationController {

    private final QuantumApplicationService service;
    private final QuantumApplicationLinkAssembler linkAssembler;

    /**
     * This method creates a new QuantumApplication
     *
     * @param file Python-File containing the Qiskit-Function
     * @param name Name of the QuantumApplication
     * @param dockerImage Optional dockerImage name that should be used for execution the function
     * @param notificationAddress Optional notification address to notify some queue when status changes occur (currently not used)
     * @param openWhiskServiceName OpenWhiskService Name
     * @return createdQuantumApplication
     */
    @Transactional
    @PostMapping
    public ResponseEntity<EntityModel<QuantumApplicationDto>> createQuantumApplication(@RequestParam MultipartFile file,
                                                                                       @RequestParam String name,
                                                                                       @RequestParam(required = false) String dockerImage,
                                                                                       @RequestParam(required = false) String notificationAddress,
                                                                                       @RequestParam String openWhiskServiceName) {
        QuantumApplication createdQuantumApplication = service.createQuantumApplication(file, name, dockerImage, notificationAddress, openWhiskServiceName);
        return new ResponseEntity<>(linkAssembler.toModel(createdQuantumApplication, QuantumApplicationDto.class), HttpStatus.CREATED);
    }

    /**
     * This method invokes a specific quantum application with specific input parameters.
     *
     * @param name Name of QuantumApplication
     * @param inputParams Parameters to be submitted as input
     * @return Void
     */
    @Transactional
    @PostMapping(value = "/{name}")
    public ResponseEntity<Void> invokeQuantumApplication(@PathVariable String name, @RequestBody(required = false) Map<String, Object> inputParams) {
        service.invokeQuantumApplication(name, inputParams);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method returns all QuantumApplications.
     *
     * @return quantumApplications
     */
    @Transactional
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<QuantumApplicationDto>>> getQuantumApplications() {
        return new ResponseEntity<>(linkAssembler.toModel(service.findAll(), QuantumApplicationDto.class), HttpStatus.OK);
    }

    /**
     * This method returns a single quantum application.
     *
     * @param name Name of the QuantumApplication
     * @return quantumApplication
     */
    @Transactional
    @GetMapping(value = "/{name}")
    public ResponseEntity<EntityModel<QuantumApplicationDto>> getQuantumApplication(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findByName(name), QuantumApplicationDto.class), HttpStatus.OK);
    }

    /**
     * This method returns all quantum applications of a OpenWhiskService
     *
     * @param name Name of the OpenWhiskService
     * @return quantumApplications
     */
    @Transactional
    @GetMapping(value = "/openwhisk-service/{name}")
    public ResponseEntity<CollectionModel<EntityModel<QuantumApplicationDto>>> getQuantumApplicationsByOpenWhiskService(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findByOpenWhiskService(name), QuantumApplicationDto.class), HttpStatus.OK);
    }

    /**
     * This method deletes a specific quantum application.
     *
     * @param name Name of the QuantumApplication
     * @return Void
     */
    @Transactional
    @DeleteMapping(value = "/{name}")
    public ResponseEntity<Void> deleteQuantumApplication(@PathVariable String name) {
        service.deleteQuantumApplication(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
