# Set Execution Policy to allow running scripts in this session
$ErrorActionPreference = "Stop"

Write-Host "=============================================" -ForegroundColor Cyan
Write-Host "   HMS-PRO ULTRA-FAST K8S IMAGE BUILDER     " -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan

# 1. Build hms-common
Write-Host "`n[1/10] Building hms-common (Shared Library)..." -ForegroundColor Yellow
mvn -f hms-common/pom.xml clean install -DskipTests
if ($LASTEXITCODE -ne 0) { Write-Error "Build hms-common failed"; exit 1 }

# 2. Build all Java services on host machine using local maven cache
$services = @("GatewayMS", "UserMS", "ProfileMS", "Appointment", "PharmacyMS", "PaymentMS", "NotificationMS", "media")
$step = 2

foreach ($service in $services) {
    Write-Host "`n[$step/10] Compiling $service on host machine..." -ForegroundColor Yellow
    mvn -f "$service/pom.xml" clean package -DskipTests
    if ($LASTEXITCODE -ne 0) { Write-Error "Compilation of $service failed"; exit 1 }
    $step++
}

# 3. Configure PowerShell to use Minikube's Docker daemon
Write-Host "`n[+] Connecting to Minikube's Docker Daemon..." -ForegroundColor Yellow
minikube -p minikube docker-env | Invoke-Expression

# 4. Build hms-fe (React App)
Write-Host "`n[10/10] Building Docker Image: hms/hms-fe:latest..." -ForegroundColor Green
docker build -t hms/hms-fe:latest -f hms-fe/Dockerfile ./hms-fe
if ($LASTEXITCODE -ne 0) { Write-Error "Build hms/hms-fe image failed"; exit 1 }

# 5. Build Java Microservice Docker Images using single-stage Dockerfiles via stdin
$javaServices = @(
    @{ name = "gateway-ms"; dir = "GatewayMS"; port = 9000 },
    @{ name = "user-ms"; dir = "UserMS"; port = 8080 },
    @{ name = "profile-ms"; dir = "ProfileMS"; port = 9100 },
    @{ name = "appointment-ms"; dir = "Appointment"; port = 9200 },
    @{ name = "pharmacy-ms"; dir = "PharmacyMS"; port = 9300 },
    @{ name = "payment-ms"; dir = "PaymentMS"; port = 8086 },
    @{ name = "notification-ms"; dir = "NotificationMS"; port = 8085 },
    @{ name = "media-ms"; dir = "media"; port = 9400 }
)

foreach ($svc in $javaServices) {
    $svcName = $svc.name
    $svcDir = $svc.dir
    $svcPort = $svc.port
    
    Write-Host "`n[+] Building Docker Image: hms/$svcName:latest (Single Stage)..." -ForegroundColor Green
    
    $dockerfile = @"
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY $svcDir/target/quarkus-app/ /app/quarkus-app/
EXPOSE $svcPort
ENV JAVA_OPTS="-Xms128m -Xmx512m -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENTRYPOINT ["sh", "-c", "java `$JAVA_OPTS -jar /app/quarkus-app/quarkus-run.jar"]
"@
    
    $dockerfile | docker build -t "hms/$svcName:latest" -f - .
    if ($LASTEXITCODE -ne 0) { Write-Error "Build hms/$svcName image failed"; exit 1 }
}

Write-Host "`n=============================================" -ForegroundColor Cyan
Write-Host "   ALL IMAGES BUILT IN MINIKUBE SUCCESSFULLY! " -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan
