package de.unistuttgart.iaas.faas.quantumservice.api;

import java.util.Objects;

import de.unistuttgart.iaas.faas.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.provider.Provider;
import de.unistuttgart.iaas.faas.quantumservice.model.entity.eventtrigger.EventTrigger;
import de.unistuttgart.iaas.faas.quantumservice.model.exception.OpenWhiskException;
import de.unistuttgart.iaas.faas.quantumservice.model.openwhisk.Annotation;
import de.unistuttgart.iaas.faas.quantumservice.model.openwhisk.Exec;
import de.unistuttgart.iaas.faas.quantumservice.model.openwhisk.ActivationResult;
import de.unistuttgart.iaas.faas.quantumservice.model.openwhisk.OpenWhiskAction;
import de.unistuttgart.iaas.faas.quantumservice.model.openwhisk.OpenWhiskActivation;
import de.unistuttgart.iaas.faas.quantumservice.model.openwhisk.OpenWhiskRule;
import de.unistuttgart.iaas.faas.quantumservice.model.openwhisk.OpenWhiskTrigger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * This class contains methods that communicate with an OpenWhisk-Provider.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OpenWhiskClient {

    private final RestTemplate restTemplate;

    public void deployActionToFaas(QuantumApplication quantumApplication) {
        OpenWhiskAction openWhiskAction = createOpenWhiskAction(quantumApplication);
        String url = quantumApplication.getProvider().getBaseUrl() + "/namespaces/" + quantumApplication.getProvider().getNamespace() + "/actions/" + quantumApplication.getName() + "?overwrite=true";
        HttpEntity<OpenWhiskAction> entity = new HttpEntity<>(openWhiskAction, generateHeaders(quantumApplication.getProvider()));
        restTemplate.exchange(url, HttpMethod.PUT, entity, Object.class);
    }

    public ActivationResult invokeAction(QuantumApplication quantumApplication, Object functionParameters) {
        String url = quantumApplication.getProvider().getBaseUrl() + "/namespaces/" + quantumApplication.getProvider().getNamespace() + "/actions/" + quantumApplication.getName();
        HttpEntity<Object> entity = new HttpEntity<>(functionParameters, generateHeaders(quantumApplication.getProvider()));
        return restTemplate.exchange(url, HttpMethod.POST, entity, ActivationResult.class).getBody();
    }

    public void removeActionFromFaas(QuantumApplication quantumApplication) {
        String url = quantumApplication.getProvider().getBaseUrl() + "/namespaces/" + quantumApplication.getProvider().getNamespace() + "/actions/" + quantumApplication.getName();
        HttpEntity<Object> entity = new HttpEntity<>(generateHeaders(quantumApplication.getProvider()));
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Object.class);
    }

    public void deployTriggerToFaas(EventTrigger eventTrigger) {
        OpenWhiskTrigger openWhiskTrigger = createOpenWhiskTrigger(eventTrigger);
        String url = eventTrigger.getProvider().getBaseUrl() + "/namespaces/" + eventTrigger.getProvider().getNamespace() + "/triggers/" + eventTrigger.getName() + "?overwrite=true";
        HttpEntity<OpenWhiskTrigger> entity = new HttpEntity<>(openWhiskTrigger, generateHeaders(eventTrigger.getProvider()));
        restTemplate.exchange(url, HttpMethod.PUT, entity, Object.class);
    }

    public void removeTriggerFromFaas(EventTrigger eventTrigger) {
        String url = eventTrigger.getProvider().getBaseUrl() + "/namespaces/" + eventTrigger.getProvider().getNamespace() + "/triggers/" + eventTrigger.getName();
        HttpEntity<Object> entity = new HttpEntity<>(generateHeaders(eventTrigger.getProvider()));
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Object.class);
    }

    public ActivationResult fireTrigger(EventTrigger eventTrigger, Object functionParameters) {
        HttpEntity<Object> entity = new HttpEntity<>(functionParameters, generateHeaders(eventTrigger.getProvider()));
        ResponseEntity<ActivationResult> response = restTemplate.postForEntity(eventTrigger.getProvider().getBaseUrl() + "/namespaces/" + eventTrigger.getProvider().getNamespace() + "/triggers/" + eventTrigger.getName(), entity, ActivationResult.class);
        if (Objects.isNull(response.getBody())) {
            throw new OpenWhiskException("Trigger '" + eventTrigger.getName() + "' did not return any activation ID. Maybe there are no active rules for given trigger!");
        }
        return  response.getBody();
    }

    public void deployRuleToFaas(EventTrigger eventTrigger, QuantumApplication quantumApplication) {
        OpenWhiskRule openWhiskRule = createOpenWhiskRule(eventTrigger, quantumApplication);
        // Create or Update the Rule
        String url = quantumApplication.getProvider().getBaseUrl() + "/namespaces/" + quantumApplication.getProvider().getNamespace() + "/rules/" + eventTrigger.getName() + "-" + quantumApplication.getName();
        HttpEntity<OpenWhiskRule> entity = new HttpEntity<>(openWhiskRule, generateHeaders(quantumApplication.getProvider()));
        restTemplate.exchange(url + "?overwrite=true", HttpMethod.PUT, entity, Object.class);
    }

    public void removeRuleFromFaas(EventTrigger eventTrigger, QuantumApplication quantumApplication) {
        String url = quantumApplication.getProvider().getBaseUrl() + "/namespaces/" + quantumApplication.getProvider().getNamespace() + "/rules/" + eventTrigger.getName() + "-" + quantumApplication.getName();
        HttpEntity<Object> entity = new HttpEntity<>(generateHeaders(quantumApplication.getProvider()));
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Object.class);
    }

    /* public void toggleRuleStatus(Rule rule) {
        ToggleRuleStatusRequest toggleRequest = new ToggleRuleStatusRequest(rule.isActive() ? "active" : "inactive");
        String url = rule.getProvider().getBaseUrl() + "/namespaces/" + rule.getProvider().getNamespace() + "/rules/" + rule.getName();
        HttpEntity<ToggleRuleStatusRequest> entity = new HttpEntity<>(toggleRequest, generateHeaders(rule.getProvider()));
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    } */

    public OpenWhiskActivation getActivation(Provider provider, String activationId) {
        String url = provider.getBaseUrl() + "/namespaces/" + provider.getNamespace() + "/activations/" + activationId;
        HttpEntity<Object> entity = new HttpEntity<>(generateHeaders(provider));
        try {
            return restTemplate.exchange(url, HttpMethod.GET, entity, OpenWhiskActivation.class).getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw new RuntimeException("Something went wrong accessing an OpenWhisk-Activation with ID: " + activationId + "!");
        }
    }

    /**
     * This method generates headers needed for accessing the Endpoints of a OpenWhisk-Provider.
     *
     * @param provider Used provider
     * @return headers
     */
    private HttpHeaders generateHeaders(Provider provider) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Basic " + provider.getBasicCredentials());
        return headers;
    }

    /**
     * This method generates a OpenWhisk-Action from a QuantumApplication.
     *
     * @param quantumApplication Used QuantumApplication
     * @return openWhiskAction
     */
    private OpenWhiskAction createOpenWhiskAction(QuantumApplication quantumApplication) {
        OpenWhiskAction openWhiskAction = new OpenWhiskAction();
        openWhiskAction.setNamespace(quantumApplication.getProvider().getNamespace());
        openWhiskAction.setName(quantumApplication.getName());
        openWhiskAction.setVersion("1.0");
        openWhiskAction.setPublish(false);

        Exec exec = new Exec();
        exec.setCode(quantumApplication.getCode());
        exec.setKind("blackbox");
        exec.setImage(quantumApplication.getDockerImage());
        openWhiskAction.setExec(exec);

        openWhiskAction.addAnnotation(new Annotation("exec", "blackbox"));

        return openWhiskAction;
    }

    /**
     * This method creates an OpenWhisk-Trigger from an EventTrigger.
     *
     * @param eventTrigger
     * @return openWhiskTrigger
     */
    private OpenWhiskTrigger createOpenWhiskTrigger(EventTrigger eventTrigger) {
        OpenWhiskTrigger openWhiskTrigger = new OpenWhiskTrigger();
        openWhiskTrigger.setNamespace(eventTrigger.getProvider().getNamespace());
        openWhiskTrigger.setName(eventTrigger.getName());
        openWhiskTrigger.setVersion("1.0");
        openWhiskTrigger.setPublish(false);

        return openWhiskTrigger;
    }

    /**
     * This method creates a OpenWhisk-Rule from the link between a EventTrigger and QuantumApplication.
     *
     * @param eventTrigger Used EventTrigger
     * @param quantumApplication Used QuantumApplication
     * @return openWhiskRule
     */
    private OpenWhiskRule createOpenWhiskRule(EventTrigger eventTrigger, QuantumApplication quantumApplication) {
        OpenWhiskRule openWhiskRule = new OpenWhiskRule();
        openWhiskRule.setName(eventTrigger.getName() + "-" + quantumApplication.getName());
        openWhiskRule.setVersion("1.0");
        openWhiskRule.setPublish(false);
        // TODO: Change this to a string or enum (active/inactive) instead of (true/false)
        openWhiskRule.setStatus(true);
        openWhiskRule.setAction("/" + quantumApplication.getProvider().getNamespace() + "/" + quantumApplication.getName());
        openWhiskRule.setTrigger("/" + quantumApplication.getProvider().getNamespace() + "/" + eventTrigger.getName());

        return openWhiskRule;
    }
}
