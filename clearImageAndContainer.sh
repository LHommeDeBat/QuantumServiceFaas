echo "Build QuantumService"
mvn clean install -DskipTests
echo "Removing quantum service container..."
docker container rm -f quantumservice
echo "Removing quantum service image..."
docker image rm -f quantumservice

echo "Removing db container..."
docker container rm -f quantumservicedb

echo "Removing mq container..."
docker container rm -f quantumservicemq
