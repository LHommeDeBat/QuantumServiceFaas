package de.unistuttgart.iaas.faas.quantumservice.hateoas;

import de.unistuttgart.iaas.faas.quantumservice.controller.QuantumApplicationController;
import de.unistuttgart.iaas.faas.quantumservice.controller.ScriptExecutionController;
import de.unistuttgart.iaas.faas.quantumservice.controller.OpenWhiskServiceController;
import de.unistuttgart.iaas.faas.quantumservice.controller.EventTriggerController;
import de.unistuttgart.iaas.faas.quantumservice.model.dto.OpenWhiskServiceDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This class is responsible for adding hateoas-links to Provider DTOs
 */
@Component
public class OpenWhiskServiceLinkAssembler extends GenericLinkAssembler<OpenWhiskServiceDto> {
    @Override
    public void addLinks(EntityModel<OpenWhiskServiceDto> resource) {
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(OpenWhiskServiceController.class).getOpenWhiskService(getName(resource))).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo((methodOn(QuantumApplicationController.class).getQuantumApplicationsByProvider(getName(resource)))).withRel("quantumApplications"));
        resource.add(WebMvcLinkBuilder.linkTo((methodOn(EventTriggerController.class).getEventTriggersByProvider(getName(resource)))).withRel("eventTriggers"));
        resource.add(WebMvcLinkBuilder.linkTo((methodOn(ScriptExecutionController.class).getScriptExecutionsByProvider(getName(resource)))).withRel("scriptExecutions"));
    }

    private String getName(EntityModel<OpenWhiskServiceDto> resource) {
        return resource.getContent().getName();
    }
}
