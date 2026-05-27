# Hướng Dẫn Khởi Động Dự Án Sau Khi Bật Máy (Startup Guide)

Tài liệu này hướng dẫn nhanh các bước để khởi động lại toàn bộ dự án HMS-PRO sau khi bạn bật hoặc khởi động lại máy tính (Windows).

---

## ☸️ TRƯỜNG HỢP 1: Chạy trên Kubernetes (Minikube) - Mặc định

Nếu bạn triển khai hệ thống thông qua Minikube, hãy thực hiện theo các bước sau:

### Bước 1: Khởi động Minikube
Mở **PowerShell** (không cần quyền Admin) và chạy lệnh:
```powershell
minikube start --driver=docker --memory=8192 --cpus=4
```
> [!NOTE]  
> Lệnh này sẽ bật lại cụm container Minikube cũ có sẵn trong Docker Desktop của bạn. K8s sẽ tự động khởi tạo lại tất cả các Pod/Service mà bạn đã deploy trước đó mà không làm mất dữ liệu.

### Bước 2: Bật Minikube Tunnel (Để định tuyến cổng 80)
Mở một **cửa sổ PowerShell mới** và chạy:
```powershell
minikube tunnel
```
> [!IMPORTANT]  
> Giữ cửa sổ terminal này chạy ngầm suốt quá trình sử dụng ứng dụng để chuyển tiếp cổng Ingress ra cổng 80 của Windows.

### Bước 3: Kiểm tra trạng thái các Pod
Trong một terminal mới, chạy lệnh sau để kiểm tra xem tất cả các dịch vụ đã sẵn sàng (`Running 1/1`) chưa:
```powershell
kubectl get pods -n hms-pro
```

---

## 💻 TRƯỜNG HỢP 2: Khởi chạy nhanh bằng Docker Compose (Development Local)

Nếu bạn chỉ muốn chạy nhanh hạ tầng cơ bản và các dịch vụ trực tiếp trên máy host:

1. **Khởi động các Database & Middleware (MySQL, Redis, Kafka):**
   Mở terminal tại thư mục gốc dự án và chạy:
   ```bash
   docker-compose up -d
   ```
2. **Khởi chạy các service backend và frontend:**
   * **Windows (PowerShell)**: 
     ```powershell
     .\run-service.ps1
     ```
   * **Linux/macOS (Terminal)**: 
     ```bash
     ./run-service.sh
     ```

---

## 🌐 Hướng Dẫn Cách Truy Cập Hệ Thống Mượt Mà nhất

### 1. Truy cập mượt nhất (Không bị lag) - Khuyên dùng khi code/test trên máy tính
Để tránh bị độ trễ mạng do đi qua server trung gian, hãy truy cập trực tiếp bằng tên miền offline nội bộ:
* Mở trình duyệt và truy cập: **`http://hms-pro.local`**

*(Đảm bảo file `C:\Windows\System32\drivers\etc\hosts` của Windows đã được thêm dòng: `127.0.0.1 hms-pro.local`)*

### 2. Chia sẻ cho điện thoại / thiết bị khác (Mạng Wi-Fi chung - Nhanh & mượt)
Nếu muốn test giao diện trên điện thoại hoặc máy tính bảng kết nối cùng mạng Wi-Fi:
1. Mở PowerShell và gõ: `ipconfig`
2. Tìm đến phần **Wireless LAN adapter Wi-Fi** và copy dòng **IPv4 Address** (Ví dụ: `192.168.1.15`).
3. Dùng điện thoại truy cập địa chỉ: **`http://192.168.1.15`**

### 3. Chia sẻ ra Internet (Localtunnel - Dễ bị lag do đi qua server nước ngoài)
Sử dụng nếu thiết bị test ngoài mạng Wi-Fi của bạn (sử dụng 3G/4G):
1. Mở một terminal mới và chạy:
   ```powershell
   cmd /c npx -y localtunnel --port 80
   ```
2. Sao chép địa chỉ URL được cấp (dạng `https://xxxx.loca.lt`) và truy cập.
