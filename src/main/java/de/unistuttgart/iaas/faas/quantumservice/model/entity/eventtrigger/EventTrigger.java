package de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import de.unistuttgart.iaas.faas.quantumservice.model.entity.openwhiskservice.OpenWhiskService;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.HasId;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.quantumapplication.QuantumApplication;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class EventTrigger extends HasId {

    @Column(unique = true)
    private String name;

    @ManyToOne
    private OpenWhiskService openWhiskService;

    @ManyToMany(cascade = CascadeType.MERGE)
    private Set<QuantumApplication> quantumApplications = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private EventType eventType;
}
