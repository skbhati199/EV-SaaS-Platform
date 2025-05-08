#!/bin/bash

# Make sure we're using the local profile
echo "Starting station-service in local mode..."
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Exit with the same status code as the mvn command
exit $? 