Write-Host "Starting HMS-PRO sequential build with local frontend compilation..."

# 1. Compile React frontend locally on the host machine (saves massive container memory)
Write-Host "`n[+] Compiling React Frontend locally on Windows host..." -ForegroundColor Yellow
$env:REACT_APP_LOCAL_BACKEND_URL="http://hms-pro.local"

Push-Location hms-fe
npm run build
Pop-Location

if ($LASTEXITCODE -ne 0) {
    Write-Error "Local frontend compilation failed. Please run 'npm install' inside 'hms-fe' directory and try again."
    exit 1
}

# 2. Build Docker images sequentially
docker build -t hms/hms-fe:latest -f hms-fe/Dockerfile ./hms-fe
if ($LASTEXITCODE -ne 0) { Write-Error "Build hms-fe Docker image failed"; exit 1 }

docker build -t hms/gateway-ms:latest -f GatewayMS/Dockerfile .
if ($LASTEXITCODE -ne 0) { Write-Error "Build gateway-ms failed"; exit 1 }

docker build -t hms/user-ms:latest -f UserMS/Dockerfile .
if ($LASTEXITCODE -ne 0) { Write-Error "Build user-ms failed"; exit 1 }

docker build -t hms/profile-ms:latest -f ProfileMS/Dockerfile .
if ($LASTEXITCODE -ne 0) { Write-Error "Build profile-ms failed"; exit 1 }

docker build -t hms/appointment-ms:latest -f Appointment/Dockerfile .
if ($LASTEXITCODE -ne 0) { Write-Error "Build appointment-ms failed"; exit 1 }

docker build -t hms/pharmacy-ms:latest -f PharmacyMS/Dockerfile .
if ($LASTEXITCODE -ne 0) { Write-Error "Build pharmacy-ms failed"; exit 1 }

docker build -t hms/payment-ms:latest -f PaymentMS/Dockerfile .
if ($LASTEXITCODE -ne 0) { Write-Error "Build payment-ms failed"; exit 1 }

docker build -t hms/notification-ms:latest -f NotificationMS/Dockerfile .
if ($LASTEXITCODE -ne 0) { Write-Error "Build notification-ms failed"; exit 1 }

docker build -t hms/media-ms:latest -f media/Dockerfile .
if ($LASTEXITCODE -ne 0) { Write-Error "Build media-ms failed"; exit 1 }

Write-Host "ALL DOCKER IMAGES BUILT SUCCESSFULLY IN MINIKUBE!"
