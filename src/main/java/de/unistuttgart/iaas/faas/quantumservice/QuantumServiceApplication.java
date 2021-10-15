package de.unistuttgart.iaas.faas.quantumservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * This class contains the entrypoint for the spring boot application.
 * It starts a embedded tomcat and deploys the application to listen to the configured port.
 */
@EnableScheduling
@SpringBootApplication
public class QuantumServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuantumServiceApplication.class, args);
    }
}
