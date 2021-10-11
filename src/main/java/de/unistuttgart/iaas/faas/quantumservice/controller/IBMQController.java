package de.unistuttgart.iaas.faas.quantumservice.controller;

import java.util.Set;

import de.unistuttgart.iaas.faas.quantumservice.service.IBMQService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "ibmq")
@RequiredArgsConstructor
public class IBMQController {

    private final IBMQService service;

    /**
     * This REST-Endpoint returns all available IBMQ-Devices.
     *
     * @return ibmqDevices
     */
    @Transactional
    @GetMapping("/devices")
    public ResponseEntity<Set<String>> getAvailableIbmqDevices() {
        return new ResponseEntity<>(service.getAvailableIbmqDevices(), HttpStatus.OK);
    }
}
