package de.unistuttgart.iaas.faas.quantumservice.events;

import java.util.Set;

import de.unistuttgart.iaas.faas.quantumservice.api.IBMQClient;
import de.unistuttgart.iaas.faas.quantumservice.api.OpenWhiskClient;
import de.unistuttgart.iaas.faas.quantumservice.messaging.JobStatusChangedSender;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.job.Job;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.job.JobRepository;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.job.JobStatus;
import de.unistuttgart.iaas.faas.quantumservice.model.ibmq.IBMQJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is responsible for polling the IBMQ-REST-API and gather Job data.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JobChecker {

    private final IBMQClient ibmqClient;
    private final OpenWhiskClient openWhiskClient;
    private final JobRepository jobRepository;
    private final JobStatusChangedSender jobStatusChangedSender;

    /**
     * This scheduled method is repeatedly executed in process that is running in the background.
     */
    @Transactional
    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    public void checkRunningJobs() {
        // Retrieve all running jobs from the database
        Set<Job> runningJobs = jobRepository.findRunningJobs();
        log.info("Checking " + runningJobs.size() + " running jobs...");
        // For each running job
        for (Job runningJob : runningJobs) {
            // Poll data from IBMQ
            IBMQJob ibmqJob = ibmqClient.getJob("ibm-q", "open", "main", runningJob.getIbmqId());
            // Update Status + StatusDetails and other data
            runningJob.setStatus(JobStatus.valueOf(ibmqJob.getStatus()));
            runningJob.setStatusDetails(ibmqJob.getTimePerStep());
            runningJob.setCreationDate(ibmqJob.getCreationDate());

            // If execution completed
            if (ibmqJob.getStatus().equals("COMPLETED")) {
                // Retrieve Job-Result from IBMQ and add it with other information to existing job
                runningJob.setEndDate(ibmqJob.getEndDate());
                runningJob.setResult(ibmqClient.getJobResult("ibm-q", "open", "main", runningJob.getIbmqId()));
                runningJob.setSuccess(ibmqJob.getSummaryData().getSuccess());
            }

            // Send job status changed event
            jobStatusChangedSender.sendJobStatusReachedEvent(runningJob);

            // Update job in database
            jobRepository.save(runningJob);
        }
    }
}
