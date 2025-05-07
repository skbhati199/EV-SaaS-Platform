#!/bin/bash

# Script to switch between Spring Cloud API Gateway and Nginx gateway
# Usage: ./switch-gateway.sh [nginx|spring]

# Function to print usage information
print_usage() {
  echo "Usage: ./switch-gateway.sh [nginx|spring]"
  echo "  nginx:  Use Nginx as the API Gateway"
  echo "  spring: Use Spring Cloud Gateway as the API Gateway"
}

# Function to switch to Nginx
use_nginx() {
  echo "Switching to Nginx as the API Gateway..."
  # Stop API Gateway if running
  docker compose stop api-gateway
  docker compose rm -f api-gateway
  
  # Start Nginx
  docker compose up -d nginx
  
  echo "Nginx gateway is now active. Accessible at http://localhost:8080"
}

# Function to switch to Spring Cloud Gateway
use_spring() {
  echo "Switching to Spring Cloud Gateway..."
  # Stop Nginx if running
  docker compose stop nginx
  docker compose rm -f nginx
  
  # Start API Gateway
  docker compose up -d api-gateway
  
  echo "Spring Cloud Gateway is now active. Accessible at http://localhost:8080"
}

# Main script logic
if [ $# -ne 1 ]; then
  print_usage
  exit 1
fi

case "$1" in
  nginx)
    use_nginx
    ;;
  spring)
    use_spring
    ;;
  *)
    echo "Invalid option: $1"
    print_usage
    exit 1
    ;;
esac

exit 0 