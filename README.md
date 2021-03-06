# QuantumService

The QuantumService enables users to register and configure their quantum applications to be automatically executed by specific events. For that, users can register any OpenWhisk-powered FaaS-service as a so called "OpenWhiskService", where the quantum applications can be deployed and automatically executed by the system. The system currently only works with IBM Quantums offering of quantum computers, meaning all quantum applications need to be python-functions that are written with the SDK "Qiskit".

## Usage Instructions

The QuantumService can be run by using Docker. For that, a docker-compose-template.yml has been prepared. To run the application the following steps can be performed:

1. Copy the docker-compose-template.yml and rename the copy to docker-compose.yml
2. Within the file, replace all missing parts that are marked by "!!!...!!!" (add own IBM Quantum API-Token and a volume for the MySQL database)
3. For future-proofing, the QuantumService can connect to a IBM MQ queue using JMS and receive events from other event sources. This queue is currently not used since all events are gathered by polling the IBM Quantum's REST endpoints. The connection to mq is therefore disabled by default. That means that the mqseries-service can be removed from the docker-compose.yml for the time being.
4. Run "docker-compose up -d". That will start the QuantumService with the default configuration in a docker container using the Spring-Profile "docker" (for individual configuration check application.yml and application-docker.yml and change default values or add appropriate environment variables to docker-compose)
5. To interact with the system use SwaggerUI by accessing "{host}:{port}/swagger-ui/" (example: http://localhost:8000/swagger-ui/) in the browser or use [QuantumServiceUI](https://github.com/LHommeDeBat/QuantumServiceFaasUI)

## Usage of OpenWhiskServices


The QuantumService needs you to register a FaaS-Service that is based on OpenWhisk as a so called "OpenWhiskService". For that, you can either make an [IBM Cloud](https://cloud.ibm.com/login) account and use IBMs Cloud Functions as a OpenWhiskService. Alternatively you can run your own [OpenWhisk-Server](https://openwhisk.apache.org/documentation.html#openwhisk_deployment) and use it as a OpenWhiskService. 

To create/register a new OpenWhiskService perform a POST-Request to:

{{YOUR-HOST}}/openwhisk-services 

(example: http://localhost:8000/openwhisk-services) 

with body

``` json
{
    "name": "MyUniqueOpenWhiskServiceName",
    "basicCredentials": "username:password",
    "baseUrl": "https://eu-gb.functions.cloud.ibm.com/api/v1",
    "namespace": "MyUniqueNamespace"
}
```

with:

- name: Unique name of your OpenWhiskService
- basicCredentials: username and password of your OpenWhisk/CloudFunctions-Namespace seperated by a ":"
- baseUrl: Base-URL for your OpenWhisk-Server/CloudFunctions
- namespace: Your unique namespace within your OpenWhisk-Server/CloudFunctions

After you have created your OpenWhiskService you can start adding QuantumApplications or EventTriggers which will automatically create appropriate Actions, Triggers and Rules within your custom OpenWhiskService (OpenWhisk-Server-/CloudFunctions-Namespace)

## Registration/Deployment/Execution of quantum applications

### Structure of quantum applications

Quantum applications of the systems need to be structured properly to work together with the system. The requirements are the following:

- all quantum applications need to be written as functions that are called **main** that take one single parameter as input
- all quantum applications need to return a object with the field **jobId** that contains the Job-ID generated by IBM Quantum during execution of a quantum circuit
- all quantum applications need to close the IBM Quantum session using Qiskit's **IBMQ.disable_account()**
- depending on the events the quantum application is registered for, it needs to use different fields of the input parameter

Example quantum application that fulfils all requirements:

``` python
from qiskit import IBMQ, transpile
from qiskit.circuit.random import random_circuit

def main(params):
    try:
        openWhiskService = IBMQ.enable_account(params['apiToken'])
        backend = openWhiskService.get_backend(params['device'])
        qx = random_circuit(num_qubits=5, depth=4)
        transpiled = transpile(qx, backend=backend)
        job = backend.run(transpiled)
    finally:    
        IBMQ.disable_account()
    
    return {
        "jobId": job.job_id()
    }
```    

#### Input Parameters

The following input parameters are always available

- **apiToken** (String) (example: **params['apiToken']**) is the API-Token that **always** has to be used to connect with IBM Quantum and execute circuits
- **device** (String) (example: **params['device']**) is the unique name of the quantum computer that will be used for execution of the circuit. It is always available but can be hardcoded by used. (If hardcoding the device, make sure it is actually available for the account - don't use premium device for free acount)

The following input parameters are additionally available if quantum application is executed by a ExecutionResultEventTrigger

- **result** (JSON-String) (example: **params['result']**) is the result of some other quantum application containing all metadata. To use you have to [parse](https://www.w3schools.com/python/python_json.asp) the JSON-String first

Example Result:

``` json
{
  "date": "2021-10-16T16:40:17.838303",
  "backend_version": "0.9.0",
  "metadata": {
    "max_memory_mb": 386975,
    "mpi_rank": 0,
    "max_gpu_memory_mb": 0,
    "num_mpi_processes": 1,
    "omp_enabled": true,
    "parallel_experiments": 1,
    "time_taken": 0.00028477200000000003
  },
  "qobj_id": "9960f0ca-0e64-4d68-ba62-725b2d3cff9a",
  "job_id": "616b006ebfa0ecf2e8b29e0e",
  "success": true,
  "backend_name": "ibmq_qasm_simulator",
  "header": {
    "backend_version": "0.1.547",
    "backend_name": "ibmq_qasm_simulator"
  },
  "results": [
    {
      "seed_simulator": 227590426,
      "metadata": {
        "fusion": {
          "enabled": false
        },
        "measure_sampling": true,
        "method": "stabilizer",
        "num_qubits": 0,
        "parallel_shots": 1,
        "active_input_qubits": [],
        "remapped_qubits": false,
        "noise": "ideal",
        "parallel_state_update": 16,
        "device": "CPU",
        "num_clbits": 0,
        "input_qubit_map": []
      },
      "data": {},
      "success": true,
      "header": {
        "metadata": {},
        "qubit_labels": [
          [
            "q",
            0
          ],
          [
            "q",
            1
          ],
          [
            "q",
            2
          ],
          [
            "q",
            3
          ],
          [
            "q",
            4
          ]
        ],
        "global_phase": 0,
        "n_qubits": 5,
        "name": "circuit-0",
        "qreg_sizes": [
          [
            "q",
            5
          ]
        ],
        "memory_slots": 0,
        "clbit_labels": [],
        "creg_sizes": []
      },
      "shots": 1024,
      "time_taken": 0.00007358600000000001,
      "status": "DONE"
    }
  ],
  "time_taken": 0.00028477200000000003,
  "status": "COMPLETED"
}
```

### Registration and Deployment of quantum applications

To register and deploy a quantum application a Multipart-POST-Request needs to be performed to: 

{{YOUR-HOST}}/quantum-applications 

(example: http://localhost:8000/quantum-applications) 

with parts:

- **file** Python-File that follows structure requirements
- **name** Unique name of your quantum application
- **dockerImage** (optional) Name of a docker image that contains the necessary runtime to execute your quantum application. By default a python runtime with pre-installed qiskit is used
- **openWhiskServiceName** Name of an existung OpenWhiskService that should be used for deployment of the quantum-application as an Action

The quantum applicaton will be automatically deployed as an action to the submitted OpenWhiskService (your OpenWhisk-Server-/CloudFunctions-Namespace).

## Event-Feeds and Event-Triggers

### Supported Feeds

The QuantumService uses the IBM Quantum API to provide two kinds of event feeds that deliver following events:

1. QueueSize-Event: Contains size of the queues of some available quantum computer
2. ExecutionResult-Event: Contains result of some executed quantum application

### Event-Triggers

Event triggers are fired if some event occurs. They can be configured to only fire if a event meets specific requirements. To create event triggers a post requests needs to be performed to: 

{{YOUR-HOST}}/event-triggers?openWhiskServiceName={{yourOpenWhiskServiceName}} 

(example: http://localhost:8000/event-triggers?openWhiskServiceName=NameOfMyOpenWhiskService) 

with body

``` json
{
   "name":"MyQueueSizeEventTrigger",
   "eventType":"QUEUE_SIZE",
   "sizeThreshold":55,
   "trackedDevices":[
      "simulator_stabilizer",
      "ibmq_bogota",
      "ibmq_qasm_simulator"
   ],
   "triggerDelay":60
}
```

for event triggers that are triggered by ExecutionResult-Events or

``` json
{
   "name":"MyExecutionResultEventTrigger",
   "eventType":"EXECUTION_RESULT",
   "executedApplicationName":"SomeRegisteredApplicationName"
}
```

for event triggers that are triggered by QueueSize-Events with:

- **name**: Unique name of the trigger
- **eventType**: Either **QUEUE_SIZE** or **EXECUTION-RESULT**
- **sizeThreshold** (QueueSize-Events only): Maximum amount of jobs in queue of some tracked quantum computer
- **trackedDevices** (QueueSize-Events only): Names of the quantum computers that should be tracked and used for execution
- **triggerDelay** (QueueSize-Events only - optional): Time in minutes (integer) that the trigger will be disabled after being trigged. Leave null if trigger should be fired only once (will be deleted automatically after being fired)
- **executedApplicationName** (ExecutionResult-Events only): The unique name of a quantum application whose results should fire the trigger

### (De)-Register Quantum Applications to Event-Feeds

To register a quantum application to a feed, the user has to link it with a event trigger (QueueSizeEventTrigger or ExecutionResultEventTrigger).
For that a POST-Request needs to be performed to:

{{YOUR-HOST}}/event-triggers/{{MyTriggerName}}/quantum-applications/{{MyQuantumApplicationName}} 

(example: http://localhost:8000/event-triggers/MyQueueSizeEventTrigger/quantum-applications/MyQuantumApplication)

To deregister quantum application from a feed, the user has to unlink it from a event trigger (QueueSizeEventTrigger or ExecutionResultEventTrigger).
For that a DELETE-Request needs to be performed to:

{{YOUR-HOST}}/event-triggers/{{MyTriggerName}}/quantum-applications/{{MyQuantumApplicationName}} 

(example: http://localhost:8000/event-triggers/MyQueueSizeEventTrigger/quantum-applications/MyQuantumApplication) 
