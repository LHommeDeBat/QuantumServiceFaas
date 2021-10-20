package de.unistuttgart.iaas.faas.quantumservice.model.entity.openwhiskservice;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface OpenWhiskServiceRepository extends CrudRepository<OpenWhiskService, UUID> {

    Optional<OpenWhiskService> findByName(String name);
    Set<OpenWhiskService> findAll();
}
