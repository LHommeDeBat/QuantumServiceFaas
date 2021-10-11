package de.unistuttgart.iaas.faas.quantumservice.model.entity.job;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface JobRepository extends CrudRepository<Job, UUID> {

    Optional<Job> findById(UUID id);
    Page<Job> findAll(Pageable pageable);
    Page<Job> findByStatusIn(Set<JobStatus> statusFilter, Pageable pageable);
    Set<Job> findByQuantumApplicationName(String actionName);
    Set<Job> findByStatusNotIn(Set<JobStatus> statuses);

    default Set<Job> findRunningJobs() {
        return findByStatusNotIn(new HashSet<>(Arrays.asList(JobStatus.COMPLETED, JobStatus.FAILED)));
    }

    default Page<Job> findAll(Set<JobStatus> statusFilter, Pageable pageable) {
        if (Objects.isNull(statusFilter) || statusFilter.isEmpty()) {
            return findAll(pageable);
        }
        return findByStatusIn(statusFilter, pageable);
    }
}
