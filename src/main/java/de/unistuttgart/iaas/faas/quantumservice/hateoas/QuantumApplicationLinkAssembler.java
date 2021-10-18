package de.unistuttgart.iaas.faas.quantumservice.hateoas;

import de.unistuttgart.iaas.faas.quantumservice.controller.EventTriggerController;
import de.unistuttgart.iaas.faas.quantumservice.controller.QuantumApplicationController;
import de.unistuttgart.iaas.faas.quantumservice.controller.ScriptExecutionController;
import de.unistuttgart.iaas.faas.quantumservice.controller.JobController;
import de.unistuttgart.iaas.faas.quantumservice.controller.OpenWhiskServiceController;
import de.unistuttgart.iaas.faas.quantumservice.model.dto.QuantumApplicationDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This class is responsible for adding hateoas-links to QuantumApplication DTOs
 */
@Component
public class QuantumApplicationLinkAssembler extends GenericLinkAssembler<QuantumApplicationDto> {
    @Override
    public void addLinks(EntityModel<QuantumApplicationDto> resource) {
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(QuantumApplicationController.class).getQuantumApplication(getName(resource))).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(OpenWhiskServiceController.class).getOpenWhiskService(getOpenWhiskServiceName(resource))).withRel("openWhiskService"));
        resource.add(WebMvcLinkBuilder.linkTo((methodOn(ScriptExecutionController.class).getScriptExecutionsByAction(getName(resource)))).withRel("activations"));
        resource.add(WebMvcLinkBuilder.linkTo((methodOn(JobController.class).getJobsByQuantumApplication(getName(resource)))).withRel("jobs"));
        resource.add(WebMvcLinkBuilder.linkTo((methodOn(EventTriggerController.class).getEventTriggersByQuantumApplication(getName(resource)))).withRel("eventTriggers"));
    }

    private String getOpenWhiskServiceName(EntityModel<QuantumApplicationDto> resource) {
        return resource.getContent().getOpenWhiskService().getName();
    }

    private String getName(EntityModel<QuantumApplicationDto> resource) {
        return resource.getContent().getName();
    }
}
