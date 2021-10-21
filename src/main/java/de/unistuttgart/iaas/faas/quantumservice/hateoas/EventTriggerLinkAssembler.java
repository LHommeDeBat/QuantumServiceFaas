package de.unistuttgart.iaas.faas.quantumservice.hateoas;

import de.unistuttgart.iaas.faas.quantumservice.controller.EventTriggerController;
import de.unistuttgart.iaas.faas.quantumservice.controller.OpenWhiskServiceController;
import de.unistuttgart.iaas.faas.quantumservice.model.dto.eventtrigger.EventTriggerDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This class is responsible for adding hateoas-links to EventTrigger DTOs
 */
@Component
public class EventTriggerLinkAssembler extends GenericLinkAssembler<EventTriggerDto> {
    @Override
    public void addLinks(EntityModel<EventTriggerDto> resource) {
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(EventTriggerController.class).getEventTrigger(getName(resource))).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(OpenWhiskServiceController.class).getOpenWhiskService(getOpenWhiskServiceName(resource))).withRel("openWhiskService"));
    }

    private String getOpenWhiskServiceName(EntityModel<EventTriggerDto> resource) {
        return resource.getContent().getOpenWhiskService().getName();
    }

    private String getName(EntityModel<EventTriggerDto> resource) {
        return resource.getContent().getName();
    }
}
