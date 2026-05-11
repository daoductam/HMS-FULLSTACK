#!/bin/bash

# HMS-PRO Start Script for Git Bash / MINGW64
# Requirements: Docker Desktop and Java 21

echo "Starting Infrastructure (Docker Compose)..."
docker-compose up -d

echo "Waiting for infrastructure (10s)..."
sleep 10

services=(
    "UserMS"
    "ProfileMS"
    "Appointment"
    "PharmacyMS"
    "media"
    "NotificationMS"
    "PaymentMS"
    "GatewayMS"
    "hms-fe"
)

# Start debug port from 5005
debug_port=5005

echo "Starting Microservices in separate windows..."

for service in "${services[@]}"; do
    if [ -d "$service" ]; then
        echo "-> Starting $service..."
        if [ "$service" == "hms-fe" ]; then
            # Start Frontend using npm
            start powershell -NoExit -Command "cd $service; npm start"
        else
            # Start Microservices using mvnw with unique debug port and disabled analytics
            start powershell -NoExit -Command "cd $service; ./mvnw quarkus:dev \"-Ddebug.port=$debug_port\" \"-Dquarkus.analytics.disabled=true\""
            ((debug_port++))
        fi
        sleep 2
    else
        echo "Warning: Directory $service not found, skipping."
    fi
done

echo "All services are starting!"
echo "Tip: Press 's' to restart or 'r' to re-run tests in service windows."
