package de.unistuttgart.iaas.faas.quantumservice.model.ibmq;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class GateParameters {
    private ZonedDateTime date;
    private String name;
    private String unit;
    private BigDecimal value;
}
