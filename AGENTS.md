# AGENTS.md

## Project Overview
HMS-PRO (Hospital Management System) là một hệ thống quản lý bệnh viện chuyên nghiệp được xây dựng trên kiến trúc Microservices. Hệ thống cung cấp các giải pháp toàn diện cho việc quản lý người dùng, hồ sơ bệnh nhân, lịch hẹn khám, dược phẩm, thanh toán và hệ thống thông báo thời gian thực.

## Tech Stack
- **Backend**: Java 21, Quarkus Framework (Migrated from Spring Boot).
- **Architecture**: Microservices với Quarkus RESTEasy Reactive, Hibernate Panache.
- **Microservices Infrastructure**:
    - **Service Discovery**: Quarkus gRPC / SmallRye Service Discovery.
    - **API Gateway**: Quarkus-based Gateway.
    - **Communication**: REST API, gRPC, Apache Kafka cho event-driven.
- **Database**: MySQL (Primary), Redis (Caching).
- **Security**: Quarkus Security với JWT và OIDC.
- **Frontend**: React.js / Next.js (hms-fe).
- **DevOps**: Docker, Docker Compose, Kubernetes.

## Project Structure
- `HMS-PRO/`: Thư mục gốc chứa toàn bộ giải pháp.
    - `UserMS/`: Quản lý định danh và xác thực người dùng.
    - `ProfileMS/`: Quản lý thông tin hồ sơ bệnh nhân và bác sĩ.
    - `Appointment/`: Xử lý luồng đặt lịch và quản lý ca khám.
    - `PharmacyMS/`: Quản lý kho thuốc, đơn thuốc và vật tư y tế.
    - `PaymentMS/`: Tích hợp các cổng thanh toán và quản lý hóa đơn.
    - `NotificationMS/`: Hệ thống gửi thông báo qua Email/SMS/Push.
    - `GatewayMS/`: Điểm cuối duy nhất cho Client tương tác với hệ thống.
    - `hms-common/`: Thư viện chia sẻ các DTO, Exception Handler và Utilities.
    - `hms-fe/`: Mã nguồn ứng dụng Frontend.
    - `ai_medical_chatbot/`: Module tích hợp AI hỗ trợ tư vấn y tế.

## Operational Resources (AI Context)
Mọi hành động của AI phải soi chiếu qua các tài nguyên này:
- **Workflows:** Tham khảo `.agent/workflows/` (`feature.md` cho tính năng mới, `debug.md` cho sửa lỗi, `improve.md` cho tối ưu hóa).
- **Coding Rules:** Tham khảo `.agent/rules/` (Tuân thủ nghiêm ngặt `code-quality.md` và `security.md`).
- **Special Skills:** Tham khảo `.agent/skills/` (Sử dụng các skill tương ứng như `api-design`, `brainstorming`, `database-design` khi cần).

## Conventions
- **API Naming**: Tuân thủ chuẩn RESTful API, sử dụng kebab-case cho URL.
- **Quarkus Patterns**: Ưu tiên sử dụng Dependency Injection của Quarkus (ArC), RESTEasy Reactive cho hiệu năng cao.
- **Error Handling**: Sử dụng ExceptionMapper trong Quarkus để xử lý lỗi tập trung.
- **Logging**: Sử dụng JBoss Logging (mặc định của Quarkus).
- **Commit Message**: Tuân thủ Conventional Commits (feat, fix, docs, style, refactor, test, chore).

## Commands
### Microservices (`UserMS/`, `ProfileMS/`, etc.)
- `./mvnw quarkus:dev` — Khởi chạy service ở chế độ Development với Live Coding.
- `./mvnw package` — Build ứng dụng (có thể thêm `-Dquarkus.package.type=native` để build native image).
- `./mvnw test` — Chạy các unit test và integration test.

### Infrastructure
- `docker-compose up -d` — Khởi chạy các dịch vụ bổ trợ (MySQL, Redis, Kafka).
