package de.unistuttgart.iaas.faas.quantumservice.controller;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This class represents the REST-Controller of the Root. It handles all incoming REST-Requests
 * for the Root.
 */
@RestController
public class RootController {

    /**
     * This method returns all available links to other REST-Controllers.
     *
     * @return representationModel
     */
    @GetMapping
    public ResponseEntity<RepresentationModel<?>> getRoot() {
        RepresentationModel<?> model = new RepresentationModel<>();
        model.add(WebMvcLinkBuilder.linkTo(methodOn(RootController.class).getRoot()).withSelfRel());
        model.add(WebMvcLinkBuilder.linkTo(methodOn(OpenWhiskServiceController.class).getOpenWhiskServices()).withRel("openwhisk-services"));
        model.add(WebMvcLinkBuilder.linkTo(methodOn(QuantumApplicationController.class).getQuantumApplications()).withRel("quantumApplications"));
        model.add(WebMvcLinkBuilder.linkTo(methodOn(EventTriggerController.class).getEventTriggers()).withRel("eventTriggers"));
        return new ResponseEntity<>(model, HttpStatus.OK);
    }
}
