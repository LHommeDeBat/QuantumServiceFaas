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

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "quantum-applications")
@RequiredArgsConstructor
public class QuantumApplicationController {

    private final QuantumApplicationService service;
    private final QuantumApplicationLinkAssembler linkAssembler;

    @Transactional
    @PostMapping
    public ResponseEntity<EntityModel<QuantumApplicationDto>> createQuantumApplication(@RequestParam MultipartFile file,
                                                                                       @RequestParam String name,
                                                                                       @RequestParam(required = false) String dockerImage,
                                                                                       @RequestParam(required = false) String notificationAddress,
                                                                                       @RequestParam String providerName) {
        QuantumApplication createdQuantumApplication = service.createQuantumApplication(file, name, dockerImage, notificationAddress, providerName);
        return new ResponseEntity<>(linkAssembler.toModel(createdQuantumApplication, QuantumApplicationDto.class), HttpStatus.CREATED);
    }

    @Transactional
    @PostMapping(value = "/{name}")
    public ResponseEntity<EntityModel<QuantumApplicationDto>> invokeQuantumApplication(@PathVariable String name, @RequestBody(required = false) Map<String, Object> inputParams) {
        service.invokeQuantumApplication(name, inputParams);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<QuantumApplicationDto>>> getQuantumApplications() {
        return new ResponseEntity<>(linkAssembler.toModel(service.findAll(), QuantumApplicationDto.class), HttpStatus.OK);
    }

    @Transactional
    @GetMapping(value = "/{name}")
    public ResponseEntity<EntityModel<QuantumApplicationDto>> getQuantumApplication(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findByName(name), QuantumApplicationDto.class), HttpStatus.OK);
    }

    @Transactional
    @GetMapping(value = "/provider/{name}")
    public ResponseEntity<CollectionModel<EntityModel<QuantumApplicationDto>>> getQuantumApplicationsByProvider(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findByProvider(name), QuantumApplicationDto.class), HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping(value = "/{name}")
    public ResponseEntity<Void> deleteQuantumApplication(@PathVariable String name) {
        service.deleteQuantumApplication(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
