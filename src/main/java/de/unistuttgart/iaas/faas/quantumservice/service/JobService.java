package de.unistuttgart.iaas.faas.quantumservice.service;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import de.unistuttgart.iaas.faas.quantumservice.model.entity.job.Job;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.job.JobRepository;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.job.JobStatus;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * This Service-Class implements functions that operate on Job objects.
 */
@Service
@AllArgsConstructor
public class JobService {

    private final JobRepository repository;

    /**
     * This method returns all jobs in a paginated manner with the option to use optional filters.
     *
     * @param statusFilter
     * @param pageable
     * @return jobs
     */
    public Page<Job> findAll(Set<JobStatus> statusFilter, Pageable pageable) {
        return repository.findAll(statusFilter, pageable);
    }

    /**
     * This method returns a specific job using a unique ID.
     *
     * @param id Unique ID of the job
     * @return job
     */
    public Job findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Job does not exist!"));
    }

    /**
     * This method returns all jobs that belong to a specific namespace of a specific quantum-application.
     *
     * @param quantumApplicationName Name of the quantum-application
     * @return quantumApplicationJobs
     */
    public Set<Job> findByQuantumApplication(String quantumApplicationName) {
        return repository.findByQuantumApplicationName(quantumApplicationName);
    }
}
