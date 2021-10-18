package de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface EventTriggerRepository extends CrudRepository<EventTrigger, UUID> {

    Optional<EventTrigger> findByName(String name);
    Set<EventTrigger> findAll();
    Set<EventTrigger> findByEventType(EventType eventType);

    @Query("SELECT trigger FROM EventTrigger trigger WHERE trigger.eventType = 'BASIC' AND trigger.name = :name")
    Set<EventTrigger> findAllBasicTriggers(@Param("name") String name);

    @Query("SELECT trigger FROM QueueSizeEventTrigger trigger WHERE trigger.eventType = 'QUEUE_SIZE' AND trigger.sizeThreshold >= :queueSize AND :device MEMBER OF trigger.trackedDevices AND trigger.disabledUntil <= :currentTimestamp")
    Set<EventTrigger> findAllQueueSizeTriggers(@Param("queueSize") Integer sizeThreshold, @Param("device") String device, @Param("currentTimestamp") ZonedDateTime currentTimestamp);

    @Query("SELECT trigger FROM ExecutionResultEventTrigger trigger WHERE trigger.eventType = 'EXECUTION_RESULT' AND trigger.executedApplicationName = :quantumApplicationName")
    Set<EventTrigger> findAllExecutionResultTriggers(@Param("quantumApplicationName") String quantumApplicationName);

    Set<EventTrigger> findByOpenWhiskServiceName(String name);

    default Set<EventTrigger> findByEventType(EventPayload payload) {
        switch (payload.getEventType()) {
            case QUEUE_SIZE:
                return findAllQueueSizeTriggers((int) payload.getAdditionalProperties().get("queueSize"), (String) payload.getEventPayloadProperties().get("device"), ZonedDateTime.now());
            case EXECUTION_RESULT:
                return findAllExecutionResultTriggers((String) payload.getAdditionalProperties().get("quantumApplicationName"));
            case BASIC:
                return findAllBasicTriggers((String) payload.getAdditionalProperties().get("triggerName"));
            default:
                return findAll();
        }
    }
}
