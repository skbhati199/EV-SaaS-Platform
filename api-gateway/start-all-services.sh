#!/bin/bash

echo "Starting EV SaaS Platform Services..."

# Start Eureka Server (Discovery Service)
echo "Starting Eureka Server..."
cd ../eureka-server
./mvnw spring-boot:run > eureka.log 2>&1 &
EUREKA_PID=$!
echo "Eureka Server started with PID: $EUREKA_PID"

# Wait for Eureka to start
echo "Waiting for Eureka to start..."
sleep 20

# Start Auth Service
echo "Starting Auth Service..."
cd ../auth-service
./mvnw spring-boot:run > auth.log 2>&1 &
AUTH_PID=$!
echo "Auth Service started with PID: $AUTH_PID"

# Start User Service
echo "Starting User Service..."
cd ../user-service
./mvnw spring-boot:run > user.log 2>&1 &
USER_PID=$!
echo "User Service started with PID: $USER_PID"

# Start Station Service
echo "Starting Station Service..."
cd ../station-service
./mvnw spring-boot:run > station.log 2>&1 &
STATION_PID=$!
echo "Station Service started with PID: $STATION_PID"

# Start Billing Service
echo "Starting Billing Service..."
cd ../billing-service
./mvnw spring-boot:run > billing.log 2>&1 &
BILLING_PID=$!
echo "Billing Service started with PID: $BILLING_PID"

# Start Smart Charging Service
echo "Starting Smart Charging Service..."
cd ../smart-charging
./mvnw spring-boot:run > smart-charging.log 2>&1 &
SMART_PID=$!
echo "Smart Charging Service started with PID: $SMART_PID"

# Start Notification Service
echo "Starting Notification Service..."
cd ../notification-service
./mvnw spring-boot:run > notification.log 2>&1 &
NOTIFICATION_PID=$!
echo "Notification Service started with PID: $NOTIFICATION_PID"

# Start Roaming Service
echo "Starting Roaming Service..."
cd ../roaming-service
./mvnw spring-boot:run > roaming.log 2>&1 &
ROAMING_PID=$!
echo "Roaming Service started with PID: $ROAMING_PID"

# Wait for services to register with Eureka
echo "Waiting for services to register with Eureka..."
sleep 30

# Finally, start the API Gateway
echo "Starting API Gateway..."
cd ../api-gateway
./mvnw spring-boot:run > gateway.log 2>&1 &
GATEWAY_PID=$!
echo "API Gateway started with PID: $GATEWAY_PID"

echo "All services have been started."
echo "Swagger UI is available at: http://localhost:8080/swagger-ui.html"
echo ""
echo "To stop all services, run: kill $EUREKA_PID $AUTH_PID $USER_PID $STATION_PID $BILLING_PID $SMART_PID $NOTIFICATION_PID $ROAMING_PID $GATEWAY_PID"
echo "Or simply run: pkill -f 'spring-boot:run'" 