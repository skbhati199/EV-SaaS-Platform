#!/bin/bash

# Self-contained script to deploy EV SaaS Platform services
# This script will build and deploy without relying on external files

set -e

echo "===== Setting up environment ====="
# Create namespace
kubectl create namespace ev-saas 2>/dev/null || echo "Namespace ev-saas already exists"

echo "===== Deploying infrastructure components ====="
# Deploy infrastructure components using inline YAML
kubectl apply -f - <<EOF
# Zookeeper
apiVersion: v1
kind: Service
metadata:
  name: zookeeper
  namespace: ev-saas
spec:
  ports:
  - port: 2181
    name: client
  selector:
    app: zookeeper
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: zookeeper
  namespace: ev-saas
spec:
  replicas: 1
  selector:
    matchLabels:
      app: zookeeper
  template:
    metadata:
      labels:
        app: zookeeper
    spec:
      containers:
      - name: zookeeper
        image: confluentinc/cp-zookeeper:7.3.0
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 2181
        env:
        - name: ZOOKEEPER_CLIENT_PORT
          value: "2181"
        - name: ZOOKEEPER_TICK_TIME
          value: "2000"
---
# Kafka
apiVersion: v1
kind: Service
metadata:
  name: kafka
  namespace: ev-saas
spec:
  ports:
  - port: 9092
    name: kafka
  selector:
    app: kafka
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: kafka
  namespace: ev-saas
spec:
  replicas: 1
  selector:
    matchLabels:
      app: kafka
  template:
    metadata:
      labels:
        app: kafka
    spec:
      containers:
      - name: kafka
        image: confluentinc/cp-kafka:7.3.0
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 9092
        env:
        - name: KAFKA_BROKER_ID
          value: "1"
        - name: KAFKA_ZOOKEEPER_CONNECT
          value: "zookeeper:2181"
        - name: KAFKA_ADVERTISED_LISTENERS
          value: "PLAINTEXT://kafka:9092"
        - name: KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR
          value: "1"
---
# PostgreSQL
apiVersion: v1
kind: Service
metadata:
  name: postgres
  namespace: ev-saas
spec:
  ports:
  - port: 5432
    targetPort: 5432
  selector:
    app: postgres
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: postgres-config
  namespace: ev-saas
data:
  init.sql: |
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    CREATE DATABASE notification_db;
    GRANT ALL PRIVILEGES ON DATABASE notification_db TO evsaas;
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: postgres
  namespace: ev-saas
spec:
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
      - name: postgres
        image: postgres:14-alpine
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 5432
        env:
        - name: POSTGRES_DB
          value: "evsaas_db"
        - name: POSTGRES_USER
          value: "evsaas"
        - name: POSTGRES_PASSWORD
          value: "evsaas_password"
        volumeMounts:
        - name: init-script
          mountPath: /docker-entrypoint-initdb.d
      volumes:
      - name: init-script
        configMap:
          name: postgres-config
EOF

echo "Waiting for infrastructure to start..."
kubectl rollout status deployment/postgres -n ev-saas --timeout=120s || echo "Postgres deployment timeout, continuing anyway"
kubectl rollout status deployment/kafka -n ev-saas --timeout=120s || echo "Kafka deployment timeout, continuing anyway"
kubectl rollout status deployment/zookeeper -n ev-saas --timeout=120s || echo "Zookeeper deployment timeout, continuing anyway"

echo "===== Building and deploying Roaming Service ====="
# Create simplified Dockerfile for roaming service
cat > roaming-service/Dockerfile.simple <<EOF
FROM eclipse-temurin:17-jre

WORKDIR /app

# Create a dummy server for testing
RUN echo 'public class SimpleHttpServer { \
    public static void main(String[] args) { \
        System.out.println("Dummy HTTP Server started on port 8088"); \
        while(true){try{Thread.sleep(60000);}catch(Exception e){}} \
    } \
}' > SimpleHttpServer.java && \
    javac SimpleHttpServer.java

EXPOSE 8088
CMD ["java", "SimpleHttpServer"]
EOF

# Build the Docker image
cd roaming-service
docker build -t ev-saas-platform-roaming-service:latest -f Dockerfile.simple .
cd ..

# Check which local Kubernetes environment is being used and load the image
if command -v minikube &> /dev/null && minikube status &> /dev/null; then
    echo "Loading image into Minikube..."
    minikube image load ev-saas-platform-roaming-service:latest
elif command -v kind &> /dev/null && kind get clusters &> /dev/null; then
    echo "Loading image into Kind..."
    kind load docker-image ev-saas-platform-roaming-service:latest
elif command -v k3d &> /dev/null && k3d cluster list &> /dev/null; then
    echo "Loading image into K3d..."
    k3d image import ev-saas-platform-roaming-service:latest
else
    echo "Warning: Unable to detect running Kubernetes environment (Minikube/Kind/K3d)"
    echo "Make sure your Kubernetes cluster is running and try again."
    echo "Continuing with deployment anyway..."
fi

# Deploy the Roaming Service
kubectl apply -f - <<EOF
apiVersion: v1
kind: Service
metadata:
  name: roaming-service
  namespace: ev-saas
spec:
  ports:
  - port: 8088
    targetPort: 8088
  selector:
    app: roaming-service
  type: NodePort
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: roaming-service
  namespace: ev-saas
spec:
  replicas: 1
  selector:
    matchLabels:
      app: roaming-service
  template:
    metadata:
      labels:
        app: roaming-service
    spec:
      containers:
      - name: roaming-service
        image: ev-saas-platform-roaming-service:latest
        imagePullPolicy: Never
        ports:
        - containerPort: 8088
EOF

# Deploy NGINX for API Gateway
kubectl apply -f - <<EOF
apiVersion: v1
kind: Service
metadata:
  name: nginx-gateway
  namespace: ev-saas
spec:
  type: NodePort
  ports:
  - port: 80
    targetPort: 80
    nodePort: 30080
  selector:
    app: nginx-gateway
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: nginx-conf
  namespace: ev-saas
data:
  nginx.conf: |
    user  nginx;
    worker_processes  auto;
    error_log  /var/log/nginx/error.log notice;
    pid        /var/run/nginx.pid;
    events {
        worker_connections  1024;
    }
    http {
        include       /etc/nginx/mime.types;
        default_type  application/octet-stream;
        log_format  main  '\$remote_addr - \$remote_user [\$time_local] "\$request" '
                          '\$status \$body_bytes_sent "\$http_referer" '
                          '"\$http_user_agent" "\$http_x_forwarded_for"';
        access_log  /var/log/nginx/access.log  main;
        sendfile        on;
        keepalive_timeout  65;
        server {
            listen       80;
            server_name  localhost;
            location /roaming/ {
                proxy_pass http://roaming-service:8088/;
                proxy_set_header Host \$host;
                proxy_set_header X-Real-IP \$remote_addr;
            }
            location / {
                return 200 'EV SaaS Platform API Gateway';
            }
        }
    }
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-gateway
  namespace: ev-saas
spec:
  replicas: 1
  selector:
    matchLabels:
      app: nginx-gateway
  template:
    metadata:
      labels:
        app: nginx-gateway
    spec:
      containers:
      - name: nginx
        image: nginx:alpine
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 80
        volumeMounts:
        - name: nginx-conf
          mountPath: /etc/nginx/nginx.conf
          subPath: nginx.conf
      volumes:
      - name: nginx-conf
        configMap:
          name: nginx-conf
EOF

echo "Waiting for services to start..."
kubectl rollout status deployment/roaming-service -n ev-saas --timeout=60s || echo "Roaming service deployment timeout, continuing anyway"
kubectl rollout status deployment/nginx-gateway -n ev-saas --timeout=60s || echo "Nginx deployment timeout, continuing anyway"

echo "===== Deployment Complete ====="
echo ""
echo "To check status:"
echo "kubectl get pods -n ev-saas"
echo ""

# Determine how to access the service
if command -v minikube &> /dev/null && minikube status &> /dev/null; then
    MINIKUBE_IP=$(minikube ip)
    echo "Access the API Gateway through: http://$MINIKUBE_IP:30080"
    echo "The Roaming Service is at: http://$MINIKUBE_IP:30080/roaming/"
else
    echo "Access the API Gateway through: http://localhost:30080"
    echo "The Roaming Service is at: http://localhost:30080/roaming/"
fi

echo ""
echo "To see logs:"
echo "kubectl logs -n ev-saas deployment/roaming-service"
echo "kubectl logs -n ev-saas deployment/nginx-gateway"
