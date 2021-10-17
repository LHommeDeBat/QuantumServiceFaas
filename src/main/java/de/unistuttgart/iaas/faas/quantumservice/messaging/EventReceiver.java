package de.unistuttgart.iaas.faas.quantumservice.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.iaas.faas.quantumservice.configuration.IBMQProperties;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventPayload;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventTrigger;
import de.unistuttgart.iaas.faas.quantumservice.model.exception.OpenWhiskException;
import de.unistuttgart.iaas.faas.quantumservice.service.EventTriggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This class represents a JMS Event-Driven Consumer.
 */
@RequiredArgsConstructor
@Slf4j
public class EventReceiver implements MessageListener {

    private final IBMQProperties ibmqProperties;
    private final EventTriggerService eventTriggerService;
    private final ObjectMapper objectMapper;

    /**
     * This method uses JMS to receive messages from the topic 'EVENT.TOPIC'. These messages represent events that were
     * generated by the EventSource.
     *
     * @param message Incoming event message
     */
    @Override
    public void onMessage(Message message) {
        // TextMessages are expected that contain the JobResult as a JSON-String
        log.info("Got new event message");
        if (message instanceof TextMessage) {
            try {
                String eventAsJson = ((TextMessage) message).getText();
                EventPayload eventPayload = objectMapper.readValue(eventAsJson, EventPayload.class);
                eventPayload.getEventPayloadProperties().put("apiToken", ibmqProperties.getApiToken());
                log.info("Got Event: " + eventAsJson);

                for (EventTrigger eventTrigger: eventTriggerService.findByEventType(eventPayload)) {
                    eventTriggerService.fireEventTrigger(eventTrigger, eventPayload);
                }
            } catch (JMSException | JsonProcessingException e) {
                log.error("Could not understand message");
            } catch (OpenWhiskException e) {
                log.warn("OpenWhisk error occurred! Maybe some trigger was invoked that does not have any registered actions");
            }

        }
    }
}
