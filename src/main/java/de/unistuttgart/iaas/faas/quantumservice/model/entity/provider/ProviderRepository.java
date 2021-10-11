package de.unistuttgart.iaas.faas.quantumservice.model.entity.provider;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface ProviderRepository extends CrudRepository<Provider, UUID> {

    Optional<Provider> findByName(String name);
    Set<Provider> findAll();
}
