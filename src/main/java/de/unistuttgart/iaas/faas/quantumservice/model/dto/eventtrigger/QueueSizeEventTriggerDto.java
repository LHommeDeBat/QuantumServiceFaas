package de.unistuttgart.iaas.faas.quantumservice.model.dto.eventtrigger;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "eventTriggers", itemRelation = "eventTrigger")
@Data
@JsonTypeName("QUEUE_SIZE")
public class QueueSizeEventTriggerDto extends EventTriggerDto {

    @Min(value = 0, message = "The threshold for the queue size must not be negative!")
    private int sizeThreshold;

    private ZonedDateTime disabledUntil = ZonedDateTime.now();
    private Long triggerDelay;

    @NotEmpty(message = "Queue-Size-Event must at least track one device!")
    private List<String> trackedDevices = new ArrayList<>();
}
