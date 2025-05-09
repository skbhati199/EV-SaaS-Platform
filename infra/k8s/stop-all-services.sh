#!/bin/bash

# Script to completely remove all Kubernetes resources for EV SaaS Platform
# This script will delete all resources in the relevant namespaces

set -e

echo "===== Removing All EV SaaS Platform Resources ====="

# Function to check if namespace exists
namespace_exists() {
  kubectl get namespace $1 &> /dev/null
  return $?
}

# Function to remove all resources in a namespace
remove_resources() {
  local namespace=$1
  echo "Removing all resources in namespace: $namespace"
  
  # List of resource types to remove
  RESOURCE_TYPES=("deployments" "services" "pods" "configmaps" "secrets" "statefulsets" "daemonsets" "jobs" "cronjobs" "ingresses" "pvc")
  
  # Delete each resource type
  for RESOURCE in "${RESOURCE_TYPES[@]}"; do
    echo "Removing $RESOURCE in namespace $namespace"
    kubectl delete $RESOURCE --all -n $namespace --ignore-not-found=true
  done
  
  echo "Waiting for resources to be removed in namespace: $namespace"
  # Wait for pods to terminate (with timeout)
  timeout=90
  while [ $timeout -gt 0 ] && [ "$(kubectl get pods -n $namespace 2>/dev/null | grep -v \"NAME\" | grep -v \"No resources found\" | wc -l)" -gt 0 ]; do
    echo "Waiting for pods to terminate... ($timeout seconds remaining)"
    sleep 5
    timeout=$((timeout-5))
  done
  
  if [ $timeout -le 0 ]; then
    echo "Warning: Some pods in $namespace may still be terminating"
    echo "Attempting to force delete remaining pods"
    kubectl delete pods --all --force --grace-period=0 -n $namespace --ignore-not-found=true
  else
    echo "All resources in namespace $namespace have been removed"
  fi
}

# Remove resources in each namespace
NAMESPACES=("ev-saas" "ev-infra" "ingress-local")

for NS in "${NAMESPACES[@]}"; do
  if namespace_exists $NS; then
    remove_resources $NS
  else
    echo "Namespace $NS does not exist, skipping"
  fi
done

echo "\n===== Verification ====="
for NS in "${NAMESPACES[@]}"; do
  if namespace_exists $NS; then
    echo "Checking namespace: $NS"
    echo "Pods:"
    kubectl get pods -n $NS
    echo "Services:"
    kubectl get services -n $NS
    echo "Deployments:"
    kubectl get deployments -n $NS
  fi
done

echo "\n===== All EV SaaS Platform resources have been removed ====="
echo "To restart services, run: ./deploy-local-environment.sh"