#!/bin/bash

# Script to deploy a local test environment for EV SaaS Platform
# This script builds all services and deploys them with infrastructure components

set -e

echo "===== Building all microservices ====="
# ./infra/k8s/build-all-services.sh

echo "===== Deploying infrastructure components ====="
kubectl apply -f infrastructure-components.yaml

echo "Waiting for infrastructure components to start..."
kubectl wait --for=condition=available --timeout=120s deployment/postgres -n ev-infra
kubectl wait --for=condition=available --timeout=120s deployment/kafka -n ev-infra
kubectl wait --for=condition=available --timeout=120s deployment/zookeeper -n ev-infra

echo "===== Deploying simplified services ====="
kubectl apply -f simplified-deployment.yaml

echo "===== Deploying NGINX gateway ====="
kubectl apply -f nginx-local.yaml

echo "===== Deployment completed ====="
echo ""
echo "To access your services:"
echo ""

if command -v minikube &> /dev/null; then
  MINIKUBE_IP=$(minikube ip)
  echo "Minikube IP: $MINIKUBE_IP"
  echo "API Gateway: http://$MINIKUBE_IP:30080"
else
  echo "API Gateway: http://localhost:30080"
fi

echo ""
echo "To check pod status:"
echo "kubectl get pods -n ev-saas"
echo "kubectl get pods -n ev-infra"
echo "kubectl get pods -n ingress-local"
