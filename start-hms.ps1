# HMS-PRO Start Script
# Requirements: Docker Desktop and Java 21

Write-Host "Starting Infrastructure (Docker Compose)..." -ForegroundColor Cyan
docker-compose up -d

Write-Host "Waiting for infrastructure (10s)..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

$services = @(
    "UserMS",
    "ProfileMS",
    "Appointment",
    "PharmacyMS",
    "media",
    "NotificationMS",
    "PaymentMS",
    "GatewayMS",
    "hms-fe"
)

$debugPort = 5005

Write-Host "Starting Microservices in separate windows..." -ForegroundColor Cyan

foreach ($service in $services) {
    Write-Host "-> Starting $service..." -ForegroundColor Green
    if ($service -eq "hms-fe") {
        Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd $service; npm start"
    } else {
        Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd $service; ./mvnw quarkus:dev '-Ddebug.port=$debugPort' '-Dquarkus.analytics.disabled=true'"
        $debugPort++
    }
    Start-Sleep -Seconds 2
}

Write-Host "All services are starting!" -ForegroundColor Magenta
Write-Host "Tip: Press 's' to restart or 'r' to re-run tests in service windows." -ForegroundColor White
