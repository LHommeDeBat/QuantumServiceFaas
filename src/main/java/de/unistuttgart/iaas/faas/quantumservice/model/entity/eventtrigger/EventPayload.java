package de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EventPayload {
    private EventType eventType;
    private Map<String, Object> additionalProperties = new HashMap<>();
    private Map<String, Object> eventPayloadProperties = new HashMap<>();

    public void addAdditionalProperty(String key, Object value) {
        additionalProperties.put(key, value);
    }
    public void addEventPayloadProperties(String key, Object value) {
        eventPayloadProperties.put(key, value);
    }
}
