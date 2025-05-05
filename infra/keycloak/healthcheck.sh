#!/bin/bash
set -e

# Wait for server to start
sleep 10

# Try admin endpoint - check if Keycloak admin console is accessible
http_code=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/health)

if [ "$http_code" = "200" ]; then
  echo "Keycloak health check: OK"
  exit 0
else
  echo "Keycloak health check failed with status: $http_code"
  exit 1
fi