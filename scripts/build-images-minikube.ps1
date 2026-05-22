param (
    [switch]$SkipCompile
)

if (-not $SkipCompile) {
    Write-Host "==========================================" -ForegroundColor Cyan
    Write-Host "STEP 1: Compiling Java microservices on host..." -ForegroundColor Cyan
    Write-Host "==========================================" -ForegroundColor Cyan

    # 1. hms-common
    Write-Host "`n[+] Compiling hms-common..." -ForegroundColor Green
    mvn -f hms-common/pom.xml clean install -DskipTests
    if ($LASTEXITCODE -ne 0) { Write-Error "Compilation of hms-common failed"; exit 1 }

    $javaServices = @(
        "GatewayMS",
        "UserMS",
        "ProfileMS",
        "Appointment",
        "PharmacyMS",
        "PaymentMS",
        "NotificationMS",
        "media"
    )

    foreach ($service in $javaServices) {
        Write-Host "`n[+] Compiling $service..." -ForegroundColor Green
        mvn -f "$service/pom.xml" clean package -DskipTests
        if ($LASTEXITCODE -ne 0) { Write-Error "Compilation of $service failed"; exit 1 }
    }
} else {
    Write-Host "==========================================" -ForegroundColor Cyan
    Write-Host "STEP 1: Skipping Java compilation (using existing host-built JARs)..." -ForegroundColor Cyan
    Write-Host "==========================================" -ForegroundColor Cyan
}

Write-Host "`n==========================================" -ForegroundColor Cyan
Write-Host "STEP 2: Configuring Docker environment to use Minikube's Docker daemon..." -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
minikube -p minikube docker-env | Invoke-Expression
$env:DOCKER_BUILDKIT="1"

Write-Host "`n[+] Verification: Current Docker info (should point to minikube):"
docker info | Select-String "Name:"

Write-Host "`n==========================================" -ForegroundColor Cyan
Write-Host "STEP 3: Building Docker images..." -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

Write-Host "`n[+] Building hms/hms-fe:latest..." -ForegroundColor Green
docker build -t hms/hms-fe:latest -f hms-fe/Dockerfile ./hms-fe
if ($LASTEXITCODE -ne 0) { Write-Error "Build hms-fe failed"; exit 1 }

Write-Host "`n[+] Building hms/gateway-ms:latest..." -ForegroundColor Green
docker build -t hms/gateway-ms:latest -f GatewayMS/Dockerfile .
if ($LASTEXITCODE -ne 0) { Write-Error "Build gateway-ms failed"; exit 1 }

Write-Host "`n[+] Building hms/user-ms:latest..." -ForegroundColor Green
docker build -t hms/user-ms:latest -f UserMS/Dockerfile .
if ($LASTEXITCODE -ne 0) { Write-Error "Build user-ms failed"; exit 1 }

Write-Host "`n[+] Building hms/profile-ms:latest..." -ForegroundColor Green
docker build -t hms/profile-ms:latest -f ProfileMS/Dockerfile .
if ($LASTEXITCODE -ne 0) { Write-Error "Build profile-ms failed"; exit 1 }

Write-Host "`n[+] Building hms/appointment-ms:latest..." -ForegroundColor Green
docker build -t hms/appointment-ms:latest -f Appointment/Dockerfile .
if ($LASTEXITCODE -ne 0) { Write-Error "Build appointment-ms failed"; exit 1 }

Write-Host "`n[+] Building hms/pharmacy-ms:latest..." -ForegroundColor Green
docker build -t hms/pharmacy-ms:latest -f PharmacyMS/Dockerfile .
if ($LASTEXITCODE -ne 0) { Write-Error "Build pharmacy-ms failed"; exit 1 }

Write-Host "`n[+] Building hms/payment-ms:latest..." -ForegroundColor Green
docker build -t hms/payment-ms:latest -f PaymentMS/Dockerfile .
if ($LASTEXITCODE -ne 0) { Write-Error "Build payment-ms failed"; exit 1 }

Write-Host "`n[+] Building hms/notification-ms:latest..." -ForegroundColor Green
docker build -t hms/notification-ms:latest -f NotificationMS/Dockerfile .
if ($LASTEXITCODE -ne 0) { Write-Error "Build notification-ms failed"; exit 1 }

Write-Host "`n[+] Building hms/media-ms:latest..." -ForegroundColor Green
docker build -t hms/media-ms:latest -f media/Dockerfile .
if ($LASTEXITCODE -ne 0) { Write-Error "Build media-ms failed"; exit 1 }

Write-Host "`n[+] ALL IMAGES BUILT SUCCESSFULLY IN MINIKUBE DOCKER DAEMON!" -ForegroundColor Cyan
