package de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("QueueSizeTrigger")
public class QueueSizeEventTrigger extends EventTrigger {

    private int sizeThreshold;

    private ZonedDateTime disabledUntil;
    private Long triggerDelay;

    @ElementCollection
    private List<String> trackedDevices = new ArrayList<>();
}
