package de.unistuttgart.iaas.faas.quantumservice.model.entity.job;

import java.time.ZonedDateTime;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobStatusDetails {

    private ZonedDateTime statusReached;
    private boolean statusEventSent;
}
