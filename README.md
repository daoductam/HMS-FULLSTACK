# HMS-PRO: Hospital Management System

HMS-PRO là một hệ thống quản lý bệnh viện chuyên nghiệp và toàn diện được xây dựng trên kiến trúc **Microservices** hiện đại, sử dụng framework **Quarkus** (Java).

## 🚀 Tổng quan dự án

Hệ thống cung cấp các giải pháp quản lý y tế từ đầu đến cuối, bao gồm quản lý hồ sơ bệnh nhân, lịch hẹn bác sĩ, kho dược phẩm và quy trình thanh toán an toàn.

## 🛠️ Công nghệ sử dụng

- **Backend**: Java 21, Quarkus Framework.
- **Frontend**: React.js / Next.js.
- **Database**: MySQL (Primary), Redis (Caching).
- **Giao tiếp**: REST API, gRPC, Apache Kafka (Event-driven).
- **Bảo mật**: JWT & OIDC (Quarkus Security).
- **Infrastructure**: Docker, Docker Compose, Kubernetes.

## 🏗️ Kiến trúc Microservices

Hệ thống được chia thành các dịch vụ chuyên biệt:

- **GatewayMS**: Điểm cuối duy nhất cho Client, xử lý định tuyến và xác thực ban đầu.
- **UserMS**: Quản lý định danh người dùng, đăng ký và xác thực (JWT).
- **ProfileMS**: Quản lý thông tin chi tiết hồ sơ bệnh nhân và bác sĩ.
- **Appointment**: Xử lý logic đặt lịch khám và quản lý ca làm việc của bác sĩ.
- **PharmacyMS**: Quản lý kho thuốc, vật tư y tế và đơn thuốc.
- **PaymentMS**: Tích hợp các cổng thanh toán (ví dụ: Momo) và quản lý hóa đơn.
- **NotificationMS**: Gửi thông báo thời gian thực qua Email/SMS/Push dựa trên sự kiện từ Kafka.
- **ai_medical_chatbot**: Module AI hỗ trợ tư vấn y tế ban đầu.
- **hms-common**: Thư viện dùng chung chứa các DTO, Exception và Utilities.

## 🚦 Hướng dẫn khởi chạy

### Yêu cầu hệ thống

- Java 21 trở lên
- Node.js & npm
- Docker & Docker Compose
- Maven

### Các bước cài đặt

1. **Clone repository**:
   ```bash
   git clone https://github.com/daoductam/HMS-FULLSTACK.git
   cd HMS-PRO
   ```

### 💻 Lựa chọn 1: Khởi chạy bằng Docker Compose & Script Local (Development)

1. **Khởi động hạ tầng**:
   Sử dụng Docker Compose để chạy MySQL, Redis, và Kafka:
   ```bash
   docker-compose up -d
   ```

2. **Chạy các Service**:
   Sử dụng các script hỗ trợ để khởi chạy microservices và frontend:
   - **Windows (PowerShell)**: `.\run-service.ps1`
   - **Linux/macOS**: `./run-service.sh`

3. **Truy cập ứng dụng**:
   - **Frontend**: `http://localhost:3000`
   - **API Gateway**: `http://localhost:9000`

---

### ☸️ Lựa chọn 2: Triển khai bằng Kubernetes (K8s / Production Mock)

Hệ thống cung cấp đầy đủ manifest cấu hình Kubernetes dưới thư mục `k8s/` để triển khai thông qua **Kustomize**.

#### 1. Yêu cầu môi trường K8s
- [Minikube](https://minikube.sigs.k8s.io/docs/start/) đã được cài đặt và kích hoạt Ingress addon.
- Tài nguyên khuyến nghị cho Minikube: Tối thiểu **6GB RAM** và **4 CPUs**.

#### 2. Các bước khởi chạy trên Minikube (Windows PowerShell)

a. **Khởi động Minikube & Bật Ingress**:
```powershell
minikube start --driver=docker --memory=6144 --cpus=4
minikube addons enable ingress
```

b. **Biên dịch Frontend**:
```powershell
Push-Location hms-fe
$env:REACT_APP_LOCAL_BACKEND_URL="http://hms-pro.local"
cmd /c npm run build
Pop-Location
```

c. **Build Docker images trực tiếp trong Minikube Daemon**:
Sử dụng script được cấu hình sẵn để chuyển đổi Docker CLI trỏ vào daemon của Minikube và đóng gói các services:
```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\build-images-minikube.ps1
```

d. **Deploy các tài nguyên lên K8s**:
Sử dụng Kustomize (được tích hợp sẵn trong `kubectl` với cờ `-k`) để apply toàn bộ cấu hình:
```powershell
kubectl apply -k k8s/
```

e. **Cấu hình truy cập tên miền cục bộ**:
1. Mở một terminal mới và chạy:
   ```powershell
   minikube tunnel
   ```
2. Thêm dòng sau vào file hosts của hệ điều hành (`C:\Windows\System32\drivers\etc\hosts` trên Windows):
   ```text
   127.0.0.1 hms-pro.local
   ```

3. **Truy cập ứng dụng**:
   Mở trình duyệt và truy cập `http://hms-pro.local` (Ingress sẽ tự động định tuyến `/` tới frontend và các API endpoint `/user`, `/profile`, `/appointment`... tới API Gateway).


## 🔐 Bảo mật

Tất cả các yêu cầu đến microservices nội bộ đều được đi qua **GatewayMS**. Việc xác thực được thực hiện qua JWT. Giao tiếp giữa các service nội bộ được bảo vệ bằng header bí mật (`X-Secret-Key`).

## 📜 Quy ước phát triển

- **API Naming**: RESTful API sử dụng `kebab-case`.
- **Commit Message**: Tuân thủ chuẩn [Conventional Commits](https://www.conventionalcommits.org/).
- **Quarkus Patterns**: Ưu tiên sử dụng Dependency Injection (ArC) và RESTEasy Reactive.

---
Phát triển bởi [daoductam](https://github.com/daoductam)
