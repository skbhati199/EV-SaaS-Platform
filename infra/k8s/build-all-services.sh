#!/bin/bash

# Script to build Docker images for all EV SaaS Platform services and load them into local Kubernetes
# Usage: ./build-all-services.sh [kubernetes-env]

set -e

# Detect Kubernetes environment if not provided
K8S_ENV=${1:-auto}
if [ "$K8S_ENV" == "auto" ]; then
  if command -v minikube &> /dev/null; then
    K8S_ENV="minikube"
  elif command -v kind &> /dev/null; then
    K8S_ENV="kind"
  else
    K8S_ENV="none"
  fi
fi

echo "Kubernetes environment: $K8S_ENV"

# List of all microservices to build
SERVICES=(
  "auth-service"
  "billing-service"
  "notification-service"
  "station-service"
  "roaming-service"
  "scheduler-service"
  "smart-charging"
  "user-service"
)

# Function to build and load a service
build_service() {
  local service=$1
  local image_name="ev-saas-platform-${service}"
  local image_tag="latest"

  echo "========================================="
  echo "Building Docker image for ${service}..."
  echo "========================================="

  # Move to service directory
  if [ ! -d "${service}" ]; then
    echo "Error: ${service} directory not found!"
    return 1
  fi

  cd "${service}"

  # Ensure Maven wrapper is executable
  if [ -f "mvnw" ]; then
    chmod +x mvnw
  fi

  # Creating standard Dockerfile if it doesn't exist
  if [ ! -f "Dockerfile" ]; then
    echo "No Dockerfile found for ${service}, creating a standard one..."
    cat > Dockerfile << 'EOF'
FROM eclipse-temurin:17-jdk as builder
WORKDIR /app

# Copy Maven wrapper files first
COPY .mvn/ .mvn/
COPY mvnw mvnw.cmd ./
RUN chmod +x mvnw

# Copy pom.xml separately to leverage Docker cache for dependencies
COPY pom.xml ./
RUN ./mvnw dependency:go-offline

# Now copy source code
COPY src/ ./src/

# Build the application
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]
EOF
  fi

  # Build the Docker image
  cd /Users/sonukumar/IdeaProjects/"EV SaaS Platform"/${image_name}/
  docker build -t "${image_name}:${image_tag}" .

  echo "Docker image built: ${image_name}:${image_tag}"

  # Load image into Kubernetes environment
  case "$K8S_ENV" in
    minikube)
      echo "Loading image into Minikube..."
      minikube image load "${image_name}:${image_tag}"
      ;;
    kind)
      echo "Loading image into Kind..."
      kind load docker-image "${image_name}:${image_tag}"
      ;;
    none)
      echo "No Kubernetes environment detected. Image is available locally."
      ;;
  esac

  # Return to root directory
  cd ..

  echo "Completed building ${service}"
  echo ""
}

# Build all services
for service in "${SERVICES[@]}"; do
  build_service "$service" || {
    echo "Failed to build ${service}. Continuing with others..."
    continue
  }
done

echo "========================================="
echo "All services built successfully!"
echo "========================================="
echo "Images are now available for use in Kubernetes with imagePullPolicy: Never"
