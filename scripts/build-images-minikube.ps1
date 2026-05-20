Write-Host "Configuring Docker environment to use Minikube's Docker daemon..." -ForegroundColor Yellow
minikube -p minikube docker-env | Invoke-Expression

Write-Host "`n[+] Verification: Current Docker info (should point to minikube):"
docker info | Select-String "Name:"

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
