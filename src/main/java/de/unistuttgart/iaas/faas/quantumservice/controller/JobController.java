package de.unistuttgart.iaas.faas.quantumservice.controller;

import java.util.Set;
import java.util.UUID;

import de.unistuttgart.iaas.faas.quantumservice.hateoas.JobLinkAssembler;
import de.unistuttgart.iaas.faas.quantumservice.model.dto.JobDto;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.job.JobStatus;
import de.unistuttgart.iaas.faas.quantumservice.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class represents the REST-Controller of the Jobs. It handles all incoming REST-Requests
 * for the Jobs.
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService service;
    private final JobLinkAssembler linkAssembler;

    /**
     * This REST-Endpoint returns all existing Jobs in a paginated manner. Optional parameters allow further filtering
     * of the result.
     *
     * @param statusFilter
     * @param pageable
     * @return jobs
     */
    @Transactional
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<JobDto>>> getJobs(@RequestParam(required = false) Set<JobStatus> statusFilter, Pageable pageable) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findAll(statusFilter, pageable), JobDto.class), HttpStatus.OK);
    }

    /**
     * This method returns a specific job that has a specific id.
     *
     * @param id ID of the job
     * @return job
     */
    @Transactional
    @GetMapping(value = "/{id}")
    public ResponseEntity<EntityModel<JobDto>> getJob(@PathVariable UUID id) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findById(id), JobDto.class), HttpStatus.OK);
    }

    /**
     * This method returns all jobs of a quantum application.
     *
     * @param name Name of the QuantumApplication
     * @return jobs
     */
    @Transactional
    @GetMapping(value = "/quantum-application/{name}")
    public ResponseEntity<CollectionModel<EntityModel<JobDto>>> getJobsByQuantumApplication(@PathVariable String name) {
        return new ResponseEntity<>(linkAssembler.toModel(service.findByQuantumApplication(name), JobDto.class), HttpStatus.OK);
    }
}
