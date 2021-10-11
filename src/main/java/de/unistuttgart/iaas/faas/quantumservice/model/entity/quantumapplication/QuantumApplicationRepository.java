package de.unistuttgart.iaas.faas.quantumservice.model.entity.quantumapplication;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventTrigger;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface QuantumApplicationRepository extends CrudRepository<QuantumApplication, UUID> {

    Optional<QuantumApplication> findByName(String name);
    Set<QuantumApplication> findAll();
    Set<QuantumApplication> findByProviderName(String providerName);

    @Query("SELECT e FROM QuantumApplication qa JOIN qa.eventTriggers e WHERE qa.name = :name")
    Set<EventTrigger> getQuantumApplicationTriggers(@Param("name") String name);
}
