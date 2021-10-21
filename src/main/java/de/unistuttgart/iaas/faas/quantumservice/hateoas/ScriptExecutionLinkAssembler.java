package de.unistuttgart.iaas.faas.quantumservice.hateoas;

import java.util.UUID;

import de.unistuttgart.iaas.faas.quantumservice.controller.QuantumApplicationController;
import de.unistuttgart.iaas.faas.quantumservice.controller.ScriptExecutionController;
import de.unistuttgart.iaas.faas.quantumservice.controller.OpenWhiskServiceController;
import de.unistuttgart.iaas.faas.quantumservice.model.dto.ScriptExecutionDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This class is responsible for adding hateoas-links to ScriptExecution DTOs
 */
@Component
public class ScriptExecutionLinkAssembler extends GenericLinkAssembler<ScriptExecutionDto> {
    @Override
    public void addLinks(EntityModel<ScriptExecutionDto> resource) {
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(ScriptExecutionController.class).getScriptExecution(getId(resource))).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(OpenWhiskServiceController.class).getOpenWhiskService(getOpenWhiskServiceName(resource))).withRel("openWhiskService"));
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(QuantumApplicationController.class).getQuantumApplication(getQuantumApplicationName(resource))).withRel("quantumApplication"));
    }

    private String getOpenWhiskServiceName(EntityModel<ScriptExecutionDto> resource) {
        return resource.getContent().getOpenWhiskServiceDto().getName();
    }

    private String getQuantumApplicationName(EntityModel<ScriptExecutionDto> resource) {
        return resource.getContent().getQuantumApplication().getName();
    }

    private UUID getId(EntityModel<ScriptExecutionDto> resource) {
        return resource.getContent().getId();
    }
}
