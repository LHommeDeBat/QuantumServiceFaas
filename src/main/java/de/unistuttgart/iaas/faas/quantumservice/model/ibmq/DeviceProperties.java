package de.unistuttgart.iaas.faas.quantumservice.model.ibmq;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({ "backend_name", "backend_version", "last_update_date", "gates", "general", "qubits" })
public class DeviceProperties {
    @JsonProperty("backend_name")
    private String backendName;

    @JsonProperty("backend_version")
    private String backendVersion;

    @JsonProperty("last_update_date")
    private ZonedDateTime lastUpdate;

    private List<Gate> gates  = new ArrayList<>();
    private List<Object> general = new ArrayList<>();
    private List<List<GateParameters>> qubits = new ArrayList<>();
}
