package de.unistuttgart.iaas.faas.quantumservice.model.entity.quantumapplication;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import de.unistuttgart.iaas.faas.quantumservice.model.entity.HasId;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventTrigger;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.scriptexecution.ScriptExecution;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.job.Job;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.openwhiskservice.OpenWhiskService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuantumApplication extends HasId {

    @Column(unique = true)
    private String name;

    @Lob
    private String code;

    private String dockerImage;
    private String notificationAddress;

    @ManyToOne
    private OpenWhiskService openWhiskService;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ScriptExecution> scriptExecutions = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Job> jobs = new HashSet<>();

    @ManyToMany(cascade = CascadeType.MERGE, mappedBy = "quantumApplications")
    private Set<EventTrigger> eventTriggers = new HashSet<>();

    public void setScriptExecutions(Set<ScriptExecution> scriptExecutions) {
        this.scriptExecutions.clear();
        if (scriptExecutions != null) {
            this.scriptExecutions.addAll(scriptExecutions);
        }
    }

    public void setJobs(Set<Job> jobs) {
        this.jobs.clear();
        if (jobs != null) {
            this.jobs.addAll(jobs);
        }
    }
}
