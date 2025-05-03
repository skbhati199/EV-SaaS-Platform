#!/bin/sh

# Choose between Spring Boot app and SimpleHttpServer based on SERVER_MODE environment variable
if [ "$SERVER_MODE" = "simple" ]; then
  echo "Starting SimpleHttpServer..."
  java com.ev.roamingservice.SimpleHttpServer
else
  echo "Starting Spring Boot application..."
  java -Djava.security.egd=file:/dev/./urandom -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -jar app.jar
fi 