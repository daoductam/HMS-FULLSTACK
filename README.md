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

2. **Khởi động hạ tầng**:
   Sử dụng Docker Compose để chạy MySQL, Redis, và Kafka:
   ```bash
   docker-compose up -d
   ```

3. **Chạy các Service**:
   Sử dụng các script hỗ trợ để khởi chạy microservices và frontend:
   - **Windows (PowerShell)**: `.\run-service.ps1`
   - **Linux/macOS**: `./run-service.sh`

4. **Truy cập ứng dụng**:
   - **Frontend**: `http://localhost:3000`
   - **API Gateway**: `http://localhost:9000`

## 🔐 Bảo mật

Tất cả các yêu cầu đến microservices nội bộ đều được đi qua **GatewayMS**. Việc xác thực được thực hiện qua JWT. Giao tiếp giữa các service nội bộ được bảo vệ bằng header bí mật (`X-Secret-Key`).

## 📜 Quy ước phát triển

- **API Naming**: RESTful API sử dụng `kebab-case`.
- **Commit Message**: Tuân thủ chuẩn [Conventional Commits](https://www.conventionalcommits.org/).
- **Quarkus Patterns**: Ưu tiên sử dụng Dependency Injection (ArC) và RESTEasy Reactive.

---
Phát triển bởi [daoductam](https://github.com/daoductam)
