#!/bin/bash

# Script to apply Kubernetes configurations for EV SaaS Platform
# This script applies the configurations in the correct order

set -e

echo "Creating namespace..."
kubectl apply -f 00-namespace.yaml

echo "Applying ConfigMap with environment variables..."
kubectl apply -f 01-configmap.yaml

echo "Applying Secrets..."
kubectl apply -f 02-secrets.yaml

echo "Deploying PostgreSQL database..."
kubectl apply -f 03-postgres.yaml

echo "Deploying Redis..."
kubectl apply -f 04-redis.yaml

echo "Deploying Eureka Service Discovery..."
kubectl apply -f eureka-server.yaml

echo "Waiting for infrastructure services to start..."
sleep 30

echo "Deploying all microservices with fixed configurations..."
kubectl apply -f all-services-fixed.yaml

echo "Deploying Nginx Ingress Controller and routes..."
kubectl apply -f nginx.yaml

echo "Deployment completed. Checking pod status..."
kubectl get pods -n ev-saas
echo "Checking Nginx Ingress Controller status..."
kubectl get pods -n ingress-nginx

echo ""
echo "To verify service connectivity, run:"
echo "./verify-service-connectivity.sh"

echo ""
echo "To check logs for a specific service, run:"
echo "kubectl logs <pod-name> -n ev-saas"

echo ""
echo "For more information, refer to DEPLOYMENT-GUIDE.md"