#!/bin/bash

# Script to verify connectivity between services in the EV SaaS Platform
# This script helps check if services can communicate with each other

set -e

NAMESPACE="ev-saas"

echo "Checking for running pods in namespace $NAMESPACE..."
PODS=$(kubectl get pods -n $NAMESPACE -o jsonpath='{.items[*].metadata.name}')

if [ -z "$PODS" ]; then
  echo "No pods found in namespace $NAMESPACE. Make sure services are deployed."
  exit 1
fi

# Select the first running pod to use for tests
for POD in $PODS; do
  STATUS=$(kubectl get pod $POD -n $NAMESPACE -o jsonpath='{.status.phase}')
  if [ "$STATUS" == "Running" ]; then
    TEST_POD=$POD
    break
  fi
done

if [ -z "$TEST_POD" ]; then
  echo "No running pods found in namespace $NAMESPACE."
  exit 1
fi

echo "Using pod $TEST_POD for connectivity tests..."

# List of services to check
SERVICES=("auth-service" "user-service" "station-service" \
          "smart-charging-service" "notification-service" "roaming-service" \
          "scheduler-service" "billing-service" "eureka-service" \
          "postgres-service" "redis-service")

echo "\nVerifying DNS resolution for services:"
for SERVICE in "${SERVICES[@]}"; do
  echo -n "Checking $SERVICE: "
  if kubectl exec -it $TEST_POD -n $NAMESPACE -- nslookup $SERVICE > /dev/null 2>&1; then
    echo "✅ Resolved"
  else
    echo "❌ Failed to resolve"
  fi
done

echo "\nVerifying HTTP connectivity to services with health endpoints:"
HTTP_SERVICES=("auth-service:8080" "user-service:8080" \
               "station-service:8080" "smart-charging-service:8080" "notification-service:8080" \
               "roaming-service:8080" "scheduler-service:8080" "billing-service:8080" \
               "eureka-service:8761")

for SERVICE in "${HTTP_SERVICES[@]}"; do
  echo -n "Checking $SERVICE/actuator/health: "
  if kubectl exec -it $TEST_POD -n $NAMESPACE -- curl -s -o /dev/null -w "%{http_code}" http://$SERVICE/actuator/health 2>/dev/null | grep -q "200"; then
    echo "✅ Healthy (200 OK)"
  else
    echo "❌ Failed or unhealthy"
  fi
done

echo "\nVerifying Eureka service discovery:"
echo -n "Checking if services are registered with Eureka: "
EUREKA_APPS=$(kubectl exec -it $TEST_POD -n $NAMESPACE -- curl -s http://eureka-service:8761/eureka/apps 2>/dev/null)
if [ -n "$EUREKA_APPS" ] && echo "$EUREKA_APPS" | grep -q "<application>"; then
  echo "✅ Services registered"
  echo "\nRegistered services:"
  echo "$EUREKA_APPS" | grep -o "<application>.*</application>" | grep -o "<name>.*</name>" | sed 's/<name>//g' | sed 's/<\/name>//g' | sort | uniq
else
  echo "❌ No services registered or Eureka unavailable"
fi

echo "\nVerifying Nginx Ingress Controller:"
echo -n "Checking if Nginx Ingress Controller is running: "
NGINX_PODS=$(kubectl get pods -n ingress-nginx -l app=nginx-ingress -o jsonpath='{.items[*].metadata.name}' 2>/dev/null)
if [ -n "$NGINX_PODS" ]; then
  echo "✅ Running"
  echo "Nginx Ingress Controller pods:"
  kubectl get pods -n ingress-nginx -l app=nginx-ingress
else
  echo "❌ Not running or not found"
fi

echo "\nConnectivity verification completed."