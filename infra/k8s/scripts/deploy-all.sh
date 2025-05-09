#!/bin/bash

# EV SaaS Platform - Complete Deployment Script
# This script deploys all components of the EV SaaS Platform

set -e

BASE_DIR="$(cd "$(dirname "$0")/.." && pwd)"
echo "Using base directory: $BASE_DIR"

# Color definitions
BLUE="\033[1;34m"
GREEN="\033[1;32m"
YELLOW="\033[1;33m"
RED="\033[1;31m"
NC="\033[0m" # No Color

echo -e "${BLUE}===================================================${NC}"
echo -e "${BLUE}    EV SaaS Platform Deployment - Starting${NC}"
echo -e "${BLUE}===================================================${NC}"

# Step 1: Create namespaces
echo -e "\n${GREEN}1. Creating namespaces...${NC}"
kubectl apply -f "$BASE_DIR/namespaces/namespaces.yaml"

# Step 2: Deploy infrastructure components
echo -e "\n${GREEN}2. Deploying infrastructure components...${NC}"
kubectl apply -f "$BASE_DIR/infrastructure/postgres.yaml"
kubectl apply -f "$BASE_DIR/infrastructure/kafka.yaml"

echo -e "${YELLOW}   Waiting for infrastructure components to become ready...${NC}"
kubectl -n ev-infra wait --for=condition=ready pod -l app=postgres --timeout=120s || echo "Warning: Timeout waiting for PostgreSQL"
kubectl -n ev-infra wait --for=condition=ready pod -l app=kafka --timeout=120s || echo "Warning: Timeout waiting for Kafka"
kubectl -n ev-infra wait --for=condition=ready pod -l app=zookeeper --timeout=120s || echo "Warning: Timeout waiting for Zookeeper"

# Step 3: Deploy microservices
echo -e "\n${GREEN}3. Deploying microservices...${NC}"

# Core services first - based on the memory we know auth, notification, and roaming were working
echo -e "${YELLOW}   Deploying core services...${NC}"
kubectl apply -f "$BASE_DIR/services/auth-service.yaml"
kubectl apply -f "$BASE_DIR/services/notification-service.yaml"
kubectl apply -f "$BASE_DIR/services/roaming-service.yaml"

echo -e "${YELLOW}   Waiting for core services to become ready...${NC}"
kubectl -n ev-saas wait --for=condition=ready pod -l app=auth-service --timeout=60s || echo "Warning: Timeout waiting for auth-service"
kubectl -n ev-saas wait --for=condition=ready pod -l app=notification-service --timeout=60s || echo "Warning: Timeout waiting for notification-service"
kubectl -n ev-saas wait --for=condition=ready pod -l app=roaming-service --timeout=60s || echo "Warning: Timeout waiting for roaming-service"

# Deploy remaining services
echo -e "${YELLOW}   Deploying remaining services...${NC}"
kubectl apply -f "$BASE_DIR/services/billing-service.yaml"
kubectl apply -f "$BASE_DIR/services/station-service.yaml"
kubectl apply -f "$BASE_DIR/services/scheduler-service.yaml"
kubectl apply -f "$BASE_DIR/services/smart-charging-service.yaml"
kubectl apply -f "$BASE_DIR/services/user-service.yaml"
kubectl apply -f "$BASE_DIR/services/eureka-server.yaml"

# Step 4: Deploy utility services
echo -e "\n${GREEN}4. Deploying utility services...${NC}"
kubectl apply -f "$BASE_DIR/utils/swagger-ui.yaml"

# Step 5: Deploy ingress configurations
echo -e "\n${GREEN}5. Deploying API Gateway and Service Proxy...${NC}"
kubectl apply -f "$BASE_DIR/ingress/nginx-gateway.yaml"
kubectl apply -f "$BASE_DIR/ingress/service-proxy.yaml"

echo -e "${YELLOW}   Waiting for API Gateway to become ready...${NC}"
kubectl -n ingress-local wait --for=condition=ready pod -l app=nginx-gateway --timeout=60s || echo "Warning: Timeout waiting for API Gateway"

# Step 6: Final checks
echo -e "\n${GREEN}6. Checking deployment status...${NC}"
echo -e "${YELLOW}   Infrastructure components:${NC}"
kubectl get pods -n ev-infra

echo -e "\n${YELLOW}   Microservices:${NC}"
kubectl get pods -n ev-saas

echo -e "\n${YELLOW}   API Gateway:${NC}"
kubectl get pods -n ingress-local

# Step 7: Display access information
echo -e "\n${GREEN}7. Deployment complete!${NC}"
echo -e "${BLUE}===================================================${NC}"
echo -e "${BLUE}    EV SaaS Platform services are now available:${NC}"
echo -e "${BLUE}===================================================${NC}"
echo -e "\n${GREEN}API Gateway:${NC} http://localhost:30080"
echo -e "${GREEN}Service Proxy:${NC} http://localhost:30090"
echo -e "\n${YELLOW}Service Endpoints:${NC}"
echo -e "- Auth Service: http://localhost:30080/auth/"
echo -e "- Billing Service: http://localhost:30080/billing/"
echo -e "- Notification Service: http://localhost:30080/notifications/"
echo -e "- Roaming Service: http://localhost:30080/roaming/"
echo -e "- Station Service: http://localhost:30080/stations/"
echo -e "- Scheduler Service: http://localhost:30080/scheduler/"
echo -e "- Smart Charging Service: http://localhost:30080/smart-charging/"
echo -e "- User Service: http://localhost:30080/users/"
echo -e "- Eureka Server: http://localhost:30080/eureka/"
echo -e "- Swagger UI: http://localhost:30080/swagger/"

echo -e "\n${YELLOW}For Cloudflare tunnel deployment:${NC}"
echo -e "Run: ${GREEN}cd $BASE_DIR/../cloudflare && ./deploy-cloudflare-tunnel.sh${NC}"

echo -e "\n${BLUE}===================================================${NC}"

# Make script executable
chmod +x "$0"
