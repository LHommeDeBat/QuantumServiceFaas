package de.unistuttgart.iaas.faas.quantumservice.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.unistuttgart.iaas.faas.quantumservice.api.IBMQClient;
import de.unistuttgart.iaas.faas.quantumservice.model.ibmq.Device;
import de.unistuttgart.iaas.faas.quantumservice.model.ibmq.Group;
import de.unistuttgart.iaas.faas.quantumservice.model.ibmq.Hub;
import de.unistuttgart.iaas.faas.quantumservice.model.ibmq.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * This is a Service-Class performing operations on the IBMQ-API.
 */
@Service
@RequiredArgsConstructor
public class IBMQService {

    private final IBMQClient ibmqClient;

    /**
     * This method uses the IBMQClient to retrieve all available devices.
     *
     * @return availableDevices
     */
    public Set<String> getAvailableIbmqDevices() {
        Set<String> availableDevices = new HashSet<>();
        List<Hub> hubs = ibmqClient.getNetworks();

        // Collect all devices from all projects, groups and hubs
        for (Hub hub: hubs) {
            for (Group group: hub.getGroups().values()) {
                for (Project project: group.getProjects().values()) {
                    for (Device device: project.getDevices().values()) {
                        availableDevices.add(device.getName());
                    }
                }
            }
        }

        return availableDevices;
    }
}
