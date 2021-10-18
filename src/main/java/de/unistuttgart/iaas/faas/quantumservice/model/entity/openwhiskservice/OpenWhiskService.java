package de.unistuttgart.iaas.faas.quantumservice.model.entity.openwhiskservice;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import de.unistuttgart.iaas.faas.quantumservice.model.entity.HasId;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.scriptexecution.ScriptExecution;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.job.Job;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventTrigger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "faas_provider")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OpenWhiskService extends HasId {

    @Column(unique = true)
    private String name;

    private String basicCredentials;
    private String baseUrl;
    private String namespace;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventTrigger> eventTriggers = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuantumApplication> quantumApplications = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ScriptExecution> scriptExecutions = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Job> jobs = new HashSet<>();

    public void setQuantumApplications(Set<QuantumApplication> quantumApplications) {
        this.quantumApplications.clear();
        if (quantumApplications != null) {
            this.quantumApplications.addAll(quantumApplications);
        }
    }

    public void setEventTriggers(Set<EventTrigger> eventTriggers) {
        this.eventTriggers.clear();
        if (eventTriggers != null) {
            this.eventTriggers.addAll(eventTriggers);
        }
    }

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
