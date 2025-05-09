#!/bin/bash

# Deploy infrastructure components for EV SaaS Platform

echo "Creating namespaces..."
kubectl apply -f ../namespaces/namespaces.yaml

echo "Deploying PostgreSQL..."
kubectl apply -f ../infrastructure/postgres.yaml

echo "Deploying Kafka and Zookeeper..."
kubectl apply -f ../infrastructure/kafka.yaml

echo "Waiting for PostgreSQL to be ready..."
kubectl -n ev-infra wait --for=condition=ready pod -l app=postgres --timeout=120s

echo "Waiting for Kafka to be ready..."
kubectl -n ev-infra wait --for=condition=ready pod -l app=kafka --timeout=120s

echo "Waiting for Zookeeper to be ready..."
kubectl -n ev-infra wait --for=condition=ready pod -l app=zookeeper --timeout=120s

echo "Infrastructure components deployed successfully!"

# Check the status of deployed infrastructure components
echo "\nCurrent status of infrastructure pods:"
kubectl get pods -n ev-infra
