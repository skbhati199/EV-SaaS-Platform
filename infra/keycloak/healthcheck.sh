#!/bin/bash

# Wait for server to start
sleep 10

# Try admin endpoint - check if Keycloak admin console is accessible
response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/auth/ -H "Host: localhost" --max-time 10)

if [ "$response" = "200" ] || [ "$response" = "302" ]; then
  echo "Keycloak health check: OK"
  exit 0
else
  echo "Keycloak health check failed with status: $response"
  exit 1
fi