#!/bin/bash

# Script to apply notification service Kubernetes configurations

set -e

echo "Applying Kafka and Zookeeper configurations..."
kubectl apply -f 07-kafka-zookeeper.yaml

echo "Updating PostgreSQL for notification database..."
kubectl apply -f 09-postgres-notification-update.yaml

# Update the postgres StatefulSet to mount the additional init script
echo "Patching PostgreSQL StatefulSet to include notification database init script..."
kubectl patch statefulset postgres -n ev-saas --type=json -p='[
  {
    "op": "add",
    "path": "/spec/template/spec/volumes/1/configMap/items",
    "value": [
      {"key": "init-timescaledb.sh", "path": "init-timescaledb.sh"},
      {"key": "create-notification-db.sh", "path": "create-notification-db.sh"}
    ]
  }
]'

echo "Applying notification service configuration..."
kubectl apply -f 08-notification-service-update.yaml

echo "Restarting PostgreSQL to apply new init scripts..."
kubectl rollout restart statefulset postgres -n ev-saas

echo "Waiting for PostgreSQL to restart..."
kubectl rollout status statefulset postgres -n ev-saas

echo "Configuration complete. Checking status..."
kubectl get pods -n ev-saas -l app=notification-service
kubectl get pods -n ev-saas -l app=kafka
kubectl get pods -n ev-saas -l app=zookeeper

echo ""
echo "To verify the notification service is working, use:"
echo "kubectl logs <notification-pod-name> -n ev-saas"
