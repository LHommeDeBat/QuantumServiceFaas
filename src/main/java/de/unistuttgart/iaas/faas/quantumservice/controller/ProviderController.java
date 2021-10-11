package de.unistuttgart.iaas.faas.quantumservice.controller;

import de.unistuttgart.iaas.faas.quantumservice.hateoas.ProviderLinkAssembler;
import de.unistuttgart.iaas.faas.quantumservice.model.dto.ProviderDto;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.provider.Provider;
import de.unistuttgart.iaas.faas.quantumservice.service.ProviderService;
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
@RequestMapping(value = "providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService service;
    private final ProviderLinkAssembler linkAssembler;

    @Transactional
    @PostMapping
    public ResponseEntity<EntityModel<ProviderDto>> createProvider(@Validated @RequestBody ProviderDto dto) {
        Provider createdProvider = service.createProvider(ModelMapperUtils.convert(dto, Provider.class));
        return new ResponseEntity<>(linkAssembler.toModel(createdProvider, ProviderDto.class), HttpStatus.CREATED);
    }

    @Transactional
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ProviderDto>>> getProviders() {
        return new ResponseEntity<>(linkAssembler.toModel(service.findAll(), ProviderDto.class), HttpStatus.OK);
    }

    @Transactional
    @GetMapping(value = "/{name}")
    public ResponseEntity<EntityModel<ProviderDto>> getProvider(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findByName(name), ProviderDto.class), HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping(value = "/{name}")
    public ResponseEntity<Void> deleteProvider(@PathVariable String name) {
        service.deleteProvider(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
