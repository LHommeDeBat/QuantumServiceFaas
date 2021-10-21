package de.unistuttgart.iaas.faas.quantumservice.api;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import de.unistuttgart.iaas.faas.quantumservice.configuration.IBMQProperties;
import de.unistuttgart.iaas.faas.quantumservice.model.ibmq.Hub;
import de.unistuttgart.iaas.faas.quantumservice.model.ibmq.IBMQJob;
import de.unistuttgart.iaas.faas.quantumservice.model.ibmq.JobDownloadUrl;
import de.unistuttgart.iaas.faas.quantumservice.model.ibmq.QueueStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * This class contains methods that communicate with the IBMQ-API
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class IBMQClient {

    private final IBMQProperties ibmqProperties;
    private final RestTemplate restTemplate;

    /**
     * This method returns all available IBM Quantum Hubs
     *
     * @return hubs list of available hubs
     */
    public List<Hub> getNetworks() {
        return Arrays.asList(restTemplate.getForEntity(addTokenToUri("/Network"), Hub[].class).getBody());
    }

    /**
     * This method returns the status of a queue of some IBM Quantum device.
     *
     * @return queueStatus status of a queue of some device
     */
    public QueueStatus getQueueStatus(String device, String hub, String group, String project) {
        String path = "/Network/" + hub + "/Groups/" + group + "/Projects/" + project + "/devices/" + device + "/queue/status";
        return restTemplate.getForEntity(addTokenToUri(path), QueueStatus.class).getBody();
    }

    /**
     * This method returns an IBM Quantum Job.
     *
     * @return ibmqJob IBM Quantum Job
     */
    public IBMQJob getJob(String hub, String group, String project, String jobId) {
        String path = "/Network/" + hub + "/Groups/" + group + "/Projects/" + project + "/Jobs/" + jobId + "/v/1";
        return restTemplate.getForEntity(addTokenToUri(path), IBMQJob.class).getBody();
    }

    /**
     * This method returns the job result of a completed IBM Quantum job.
     *
     * @return jobResult Job-Result as a JSONObject
     */
    public JSONObject getJobResult(String hub, String group, String project, String jobId) {
        String path = "/Network/" + hub + "/Groups/" + group + "/Projects/" + project + "/Jobs/" + jobId + "/resultDownloadUrl";
        JobDownloadUrl downloadUrl = restTemplate.getForEntity(addTokenToUri(path), JobDownloadUrl.class).getBody();
        try {
            return new JSONObject(IOUtils.toString(new URL(downloadUrl.getUrl()), Charset.forName("UTF-8")));
        } catch (IOException exception) {
            return new JSONObject().put("url", downloadUrl.getUrl());
        }
    }

    /**
     * This method adds the accessToken to the list of query parameters.
     *
     * @return uriWithToken URI containing token
     */
    private String addTokenToUri(String path) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ibmqProperties.getApiHost() + path)
                .queryParam("access_token", ibmqProperties.getAccessToken());
        return builder.toUriString();
    }
}
