FROM openjdk:11
ARG JAR_FILE=target/quantum-service-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} quantum-service.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=docker","-jar","/quantum-service.jar"]