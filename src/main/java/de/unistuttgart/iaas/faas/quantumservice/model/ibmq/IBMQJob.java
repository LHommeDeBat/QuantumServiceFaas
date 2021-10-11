package de.unistuttgart.iaas.faas.quantumservice.model.ibmq;

import java.time.ZonedDateTime;
import java.util.Map;

import lombok.Data;

@Data
public class IBMQJob {

    private String id;
    private String kind;
    private Backend backend;
    private String status;
    private ZonedDateTime creationDate;
    private SummaryData summaryData;
    private Map<String, ZonedDateTime> timePerStep;
    private HubInfo hubInfo;
    private ZonedDateTime endDate;
    private Double cost;
    private String shareLevel;
    private String runMode;
}
