# HMS-PRO Multi-Service Launcher (PowerShell)

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

while ($true) {
    Write-Host "`n------------------------------------------" -ForegroundColor Cyan
    Write-Host "   HMS-PRO: Launch Service in New Window" -ForegroundColor Cyan
    Write-Host "------------------------------------------" -ForegroundColor Cyan

    for ($i = 0; $i -lt $services.Count; $i++) {
        Write-Host ("{0,2}) {1}" -f ($i + 1), $services[$i])
    }
    Write-Host " 0) Exit"
    Write-Host "------------------------------------------" -ForegroundColor Cyan

    $choice = Read-Host "Enter choice [0-9]"

    if ($choice -eq "0" -or [string]::IsNullOrWhiteSpace($choice)) {
        Write-Host "Exiting..."
        break
    }

    if ($choice -ge 1 -and $choice -le $services.Count) {
        $service = $services[$choice - 1]
        Write-Host ">>> Launching $service in a new window..." -ForegroundColor Green
        
        if (Test-Path $service) {
            if ($service -eq "hms-fe") {
                Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd $service; npm start"
            } else {
                Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd $service; ./mvnw quarkus:dev '-Dquarkus.analytics.disabled=true'"
            }
        } else {
            Write-Host "Error: Directory $service not found!" -ForegroundColor Red
        }
    } else {
        Write-Host "Invalid selection." -ForegroundColor Yellow
    }
}
