package de.unistuttgart.iaas.faas.quantumservice.controller;

import de.unistuttgart.iaas.faas.quantumservice.hateoas.OpenWhiskServiceLinkAssembler;
import de.unistuttgart.iaas.faas.quantumservice.model.dto.OpenWhiskServiceDto;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.openwhiskservice.OpenWhiskService;
import de.unistuttgart.iaas.faas.quantumservice.service.OpenWhiskServiceService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "openwhisk-services")
@RequiredArgsConstructor
public class OpenWhiskServiceController {

    private final OpenWhiskServiceService service;
    private final OpenWhiskServiceLinkAssembler linkAssembler;

    @Transactional
    @PostMapping
    public ResponseEntity<EntityModel<OpenWhiskServiceDto>> createOpenWhiskService(@Validated @RequestBody OpenWhiskServiceDto dto) {
        OpenWhiskService createdOpenWhiskService = service.createOpenWhiskService(ModelMapperUtils.convert(dto, OpenWhiskService.class));
        return new ResponseEntity<>(linkAssembler.toModel(createdOpenWhiskService, OpenWhiskServiceDto.class), HttpStatus.CREATED);
    }

    @Transactional
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<OpenWhiskServiceDto>>> getOpenWhiskServices() {
        return new ResponseEntity<>(linkAssembler.toModel(service.findAll(), OpenWhiskServiceDto.class), HttpStatus.OK);
    }

    @Transactional
    @GetMapping(value = "/{name}")
    public ResponseEntity<EntityModel<OpenWhiskServiceDto>> getOpenWhiskService(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findByName(name), OpenWhiskServiceDto.class), HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping(value = "/{name}")
    public ResponseEntity<Void> deleteOpenWhiskService(@PathVariable String name) {
        service.deleteOpenWhiskService(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
