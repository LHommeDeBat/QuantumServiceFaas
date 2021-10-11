package de.unistuttgart.iaas.faas.quantumservice.model.ibmq;

import lombok.Data;

@Data
public class HubInfo {

    private Hub hub;
    private Group group;
    private Project project;
}
