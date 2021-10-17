# QuantumService

The QuantumService enables users to register and configure their quantum applications to be automatically executed by specific events. For that, users can register any OpenWhisk-powered FaaS-service as a so called "Provider", where the quantum applications can be deployed and automatically executed by the system. The system currently only works with IBM Quantums offering of quantum computers, meaning all quantum applications need to be python-functions that are written with the SDK "Qiskit".

## Usage Instructions

The QuantumService can be run by using Docker. For that, a docker-compose-template.yml has been prepared. To run the application the following steps can be performed:

1. Copy the docker-compose-template.yml and rename the copy to docker-compose.yml
2. Within the file, replace all missing parts that are marked by "!!!...!!!" (add own IBM Quantum API-Token and a volume for the MySQL database)
3. For future-proofing, the QuantumService can connect to a IBM MQ queue using JMS and receive events from other event sources. This queue is currently not used since all events are gathered by polling the IBM Quantum's REST endpoints. The connection to mq is therefore disabled by default. That means that the mqseries-service can be removed from the docker-compose.yml for the time being.
4. Run "docker-compose up -d". That will start the QuantumService with the default configuration in a docker container using the Spring-Profile "docker" (for individual configuration check application.yml and application-docker.yml and change default values or add appropriate environment variables to docker-compose)
5. To interact with the system use SwaggerUI by accessing "{host}:{port}/swagger-ui/" in the browser or use [QuantumServiceUI](https://github.com/LHommeDeBat/QuantumServiceUI)
