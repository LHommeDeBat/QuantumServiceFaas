package de.unistuttgart.iaas.faas.quantumservice.events;

import java.time.ZonedDateTime;
import java.util.List;

import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventPayload;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventType;
import de.unistuttgart.iaas.faas.quantumservice.model.ibmq.Device;
import de.unistuttgart.iaas.faas.quantumservice.model.ibmq.Group;
import de.unistuttgart.iaas.faas.quantumservice.model.ibmq.Hub;
import de.unistuttgart.iaas.faas.quantumservice.model.ibmq.Project;
import de.unistuttgart.iaas.faas.quantumservice.model.ibmq.QueueStatus;
import de.unistuttgart.iaas.faas.quantumservice.service.EventTriggerService;
import de.unistuttgart.iaas.faas.quantumservice.api.IBMQClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is responsible for generating QueueSizeEvents by collecting data from the IBMQ-REST-API.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class QueueSizeChecker {

    private final IBMQClient ibmqClient;
    private final EventTriggerService triggerService;

    /**
     * This scheduled method is repeatedly executed in process that is running in the background.
     */
    @Transactional
    @Scheduled(initialDelay = 30000, fixedDelay = 60000)
    public void gatherQueueSizeData() {
        try {
            List<Hub> hubs = ibmqClient.getNetworks();
            // Get Devices and their Queue-Status
            for (Hub hub: hubs) {
                String hubName = hub.getName();
                for (Group group: hub.getGroups().values()) {
                    String groupName = group.getName();
                    for (Project project: group.getProjects().values()) {
                        String projectName = project.getName();
                        for (Device device: project.getDevices().values()) {
                            String deviceName = device.getName();
                            // Get QueueStatus of device
                            QueueStatus queueStatus = ibmqClient.getQueueStatus(deviceName, hubName, groupName, projectName);
                            // Generate payload for event
                            EventPayload payload = new EventPayload();
                            payload.setEventType(EventType.QUEUE_SIZE);
                            // Add data to trigger payload
                            // payload.addEventPayloadProperties("apiToken", ibmqProperties.getApiToken());
                            payload.addEventPayloadProperties("device", deviceName);
                            // Add further data
                            payload.addAdditionalProperty("queueSize", queueStatus.getLengthQueue());
                            // Fire event by calling appropriate OpenWhisk-Triggers on some OpenWhisk-Provider
                            triggerService.emitEvent(payload);
                        }
                    }
                }
            }
            log.info("QueueSize-Polling-Iteration ended at {}", ZonedDateTime.now());
        } catch (Exception e) {
            log.error("Something went wrong accessing the IBMQ-API!", e);
        }
    }
}
