# EV SaaS Platform Kubernetes Deployment Guide

## Overview

This guide provides instructions for deploying the EV SaaS Platform microservices on Kubernetes. The deployment has been updated to fix service connectivity issues by ensuring proper environment variable configuration and direct service references.

## Key Changes Made

1. **ConfigMap Updates**:
   - Added direct service URL references (e.g., `AUTH_SERVICE_URL`, `USER_SERVICE_URL`, etc.)
   - Added Kafka bootstrap server configuration
   - Added Scheduler service host configuration

2. **Service Deployment Fixes**:
   - Updated all services to use direct service URLs instead of environment variable interpolation
   - Set `imagePullPolicy: Never` for local development
   - Fixed service naming inconsistencies (e.g., smart-charging vs smart-charging-service)
   - Added proper inter-service communication environment variables

3. **Combined Configuration**:
   - Created a single deployment file (`all-services-fixed.yaml`) that includes all services with the fixes applied

## Deployment Instructions

### Prerequisites

- Kubernetes cluster running (minikube, kind, or cloud provider)
- kubectl configured to connect to your cluster
- Docker images for all services built and available

### Deployment Steps

1. **Create the namespace**:
   ```bash
   kubectl apply -f 00-namespace.yaml
   ```

2. **Deploy ConfigMap with updated environment variables**:
   ```bash
   kubectl apply -f 01-configmap.yaml
   ```

3. **Deploy Secrets**:
   ```bash
   kubectl apply -f 02-secrets.yaml
   ```

4. **Deploy Database and Redis**:
   ```bash
   kubectl apply -f 03-postgres.yaml
   kubectl apply -f 04-redis.yaml
   ```

5. **Deploy Eureka Service Discovery**:
   ```bash
   kubectl apply -f eureka-server.yaml
   ```

6. **Deploy all services using the fixed configuration**:
   ```bash
   kubectl apply -f all-services-fixed.yaml
   ```

   Alternatively, you can deploy services individually:
   ```bash
   kubectl apply -f api-gateway-fixed.yaml
   kubectl apply -f auth-service-fixed.yaml
   kubectl apply -f service-deployments-fixed.yaml
   ```

## Verification

1. **Check if all pods are running**:
   ```bash
   kubectl get pods -n ev-saas
   ```

2. **Verify service connectivity**:
   ```bash
   ./verify-service-connectivity.sh
   ```

3. **Access services through Nginx Ingress**:
   ```bash
   # Get the Nginx Ingress Controller external IP/port
   kubectl get svc -n ingress-nginx
   
   # Access services using the Ingress Controller IP and the service path
   # Example: curl http://<ingress-ip>/auth/actuator/health
   ```

3. **Check logs for any connectivity issues**:
   ```bash
   kubectl logs <pod-name> -n ev-saas
   ```

## Troubleshooting

1. **Service Discovery Issues**:
   - Ensure Eureka server is running: `kubectl get pods -n ev-saas | grep eureka`
   - Check Eureka logs: `kubectl logs <eureka-pod-name> -n ev-saas`
   - Verify services are registering with Eureka: Access Eureka dashboard through port-forwarding

2. **Nginx Ingress Controller Issues**:
   - Verify Nginx Ingress Controller is running: `kubectl get pods -n ingress-nginx`
   - Check Nginx Ingress Controller logs: `kubectl logs <nginx-pod-name> -n ingress-nginx`
   - Verify Ingress resources: `kubectl get ingress -n ev-saas`
   - Check Ingress Controller service: `kubectl get svc -n ingress-nginx`

3. **Database Connection Issues**:
   - Verify PostgreSQL is running: `kubectl get pods -n ev-saas | grep postgres`
   - Check database logs: `kubectl logs <postgres-pod-name> -n ev-saas`
   - Ensure database credentials in secrets match what services are using

4. **Inter-service Communication**:
   - Verify services can resolve each other: `kubectl exec -it <pod-name> -n ev-saas -- nslookup <service-name>`
   - Check if services are using the correct URLs from ConfigMap

## Next Steps

1. Set up monitoring and logging solutions
2. Configure horizontal pod autoscaling
3. Implement CI/CD pipeline for automated deployments