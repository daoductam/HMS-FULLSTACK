#!/bin/bash

# HMS-PRO Multi-Service Launcher
# Usage: ./run-service.sh

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

while true; do
    echo "------------------------------------------"
    echo "   HMS-PRO: Launch Service in New Window"
    echo "------------------------------------------"
    for i in "${!services[@]}"; do
        printf "%2d) %s\n" "$((i+1))" "${services[$i]}"
    done
    echo " 0) Exit"
    echo "------------------------------------------"

    read -p "Enter choice [0-9]: " choice

    if [ "$choice" == "0" ] || [ -z "$choice" ]; then
        echo "Exiting..."
        break
    fi

    if [[ $choice -ge 1 && $choice -le ${#services[@]} ]]; then
        service=${services[$((choice-1))]}
        echo ">>> Launching $service in a new window..."
        
        if [ -d "$service" ]; then
            if [ "$service" == "hms-fe" ]; then
                start powershell -NoExit -Command "cd $service; npm start"
            else
                # Default to port 5005 for individual starts, you can manually change if needed
                start powershell -NoExit -Command "cd $service; ./mvnw quarkus:dev \"-Dquarkus.analytics.disabled=true\""
            fi
        else
            echo "Error: Directory $service not found!"
        fi
    else
        echo "Invalid selection."
    fi
    echo ""
done
