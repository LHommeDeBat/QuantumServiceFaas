package de.unistuttgart.iaas.faas.quantumservice.model.entity.job;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import de.unistuttgart.iaas.faas.quantumservice.model.entity.HasId;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.quantumapplication.QuantumApplication;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Job extends HasId {

    private String ibmqId;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @ElementCollection
    private Map<JobStatus, JobStatusDetails> statusDetails = new HashMap<>();

    @Lob
    @Convert(converter= JobResultConverter.class)
    private JSONObject result;

    private String device;

    @Lob
    @Column
    private String inputParams;
    private ZonedDateTime creationDate;
    private ZonedDateTime endDate;

    private Boolean success;

    @ManyToOne(fetch = FetchType.LAZY)
    private QuantumApplication quantumApplication;

    public void setStatusDetails(Map<String, ZonedDateTime> timePerStep) {
        for (String statusString : timePerStep.keySet()) {
            if (statusDetails.get(JobStatus.valueOf(statusString)) == null) {
                statusDetails.put(JobStatus.valueOf(statusString), new JobStatusDetails(timePerStep.get(statusString), false));
            }
        }
    }
}
