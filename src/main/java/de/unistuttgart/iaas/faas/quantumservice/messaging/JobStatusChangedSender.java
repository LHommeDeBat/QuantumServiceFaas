package de.unistuttgart.iaas.faas.quantumservice.messaging;

import java.util.Objects;

import javax.jms.TextMessage;

import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventPayload;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventType;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.job.Job;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.job.JobStatus;
import de.unistuttgart.iaas.faas.quantumservice.service.EventTriggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobStatusChangedSender {

    private final JmsTemplate jmsTemplate;
    private final EventTriggerService eventTriggerService;

    /**
     * This method uses JMS to send the status-reached events of jobs to the defined Reply-To-Address (destination).
     *
     * @param job Job that is checked for status changes
     */
    public void sendJobStatusReachedEvent(Job job) {
        for (JobStatus status : JobStatus.values()) {
            if (job.getStatusDetails().get(status) != null && !job.getStatusDetails().get(status).isStatusEventSent()) {
                if (status == JobStatus.COMPLETED && Objects.isNull(job.getQuantumApplication().getNotificationAddress())) {
                    emitExecutionResultEvent(job);
                }

                if (!Objects.isNull(job.getQuantumApplication().getNotificationAddress())) {
                    createAndSendExecutionStatusChangedNotification(job, status);
                }
                job.getStatusDetails().get(status).setStatusEventSent(true);
            }
        }
    }

    /**
     * This method sends a status-changed notification to given notification address
     * @param job currently executing IBMQ-Job
     * @param status name of reached status
     */
    private void createAndSendExecutionStatusChangedNotification(Job job, JobStatus status) {
        jmsTemplate.send(job.getQuantumApplication().getNotificationAddress(),
                session -> {
                    TextMessage message = session.createTextMessage();
                    message.setJMSReplyTo(session.createQueue("QC.EVENT.QUEUE"));

                    JSONObject notificationObject = new JSONObject();
                    notificationObject.put("executedApplication", job.getQuantumApplication().getName());
                    notificationObject.put("status", status.toString());
                    notificationObject.put("statusReached", job.getStatusDetails().get(status).getStatusReached().toString());
                    notificationObject.put("device", job.getDevice());
                    if (status == JobStatus.COMPLETED) {
                        notificationObject.put("executionSuccessful", job.getSuccess());
                        if (job.getSuccess()) {
                            notificationObject.put("executionResult", job.getResult());
                        }
                    }

                    message.setText(notificationObject.toString());
                    return message;
                });
        log.info("Job-Status={} was reached for application={} and notification was sent to destination={}!", status, job.getQuantumApplication().getName(), job.getQuantumApplication().getNotificationAddress());
    }

    private void emitExecutionResultEvent(Job job) {
        EventPayload eventPayload = new EventPayload();
        eventPayload.setEventType(EventType.EXECUTION_RESULT);
        eventPayload.addAdditionalProperty("quantumApplicationName", job.getQuantumApplication().getName());
        eventPayload.addEventPayloadProperties("device", job.getDevice());
        eventPayload.addEventPayloadProperties("result", job.getResult().toString());
        eventTriggerService.emitEvent(eventPayload);
    }
}
