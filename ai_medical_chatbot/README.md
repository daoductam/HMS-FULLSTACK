# AI Medical Chatbot - Hệ Thống Trợ Lý Y Tế Thông Minh

Chatbot y tế sử dụng **Groq API** với khả năng truy vấn dữ liệu từ database để trả lời câu hỏi của người dùng.

## ✨ Tính Năng

### 🔍 Truy Vấn Dữ Liệu Tự Động
Chatbot có thể tự động truy vấn và trả lời về:
- 📅 **Lịch hẹn**: Xem lịch hẹn sắp tới, đã hoàn thành, đã hủy
- 💊 **Đơn thuốc**: Tra cứu đơn thuốc đã kê, danh sách thuốc
- 📋 **Báo cáo khám bệnh**: Xem triệu chứng, chẩn đoán, xét nghiệm
- 👤 **Thông tin bệnh nhân**: Hồ sơ, nhóm máu, dị ứng, bệnh mãn tính
- 👨‍⚕️ **Thông tin bác sĩ**: Chuyên khoa, phòng ban, kinh nghiệm
- 📆 **Lịch làm việc bác sĩ**: Ca làm việc, số slot còn trống
- 💉 **Kho thuốc**: Tồn kho, thuốc sắp hết, hạn sử dụng
- 💰 **Bán hàng & Doanh thu**: Thống kê bán hàng, doanh thu
- 📊 **Thống kê hệ thống**: Tổng quan hệ thống, số liệu tổng hợp

### 🎯 Hỗ Trợ Theo Role
- **PATIENT**: Tra cứu lịch hẹn, đơn thuốc, báo cáo khám bệnh của chính mình
- **DOCTOR**: Xem lịch làm việc, thông tin bệnh nhân, đơn thuốc đã kê
- **ADMIN**: Thống kê hệ thống, quản lý kho thuốc, doanh thu

## 🚀 Cài Đặt

### 1. Cài đặt Dependencies

```bash
pip install fastapi uvicorn mysql-connector-python python-dotenv requests

# Nếu dùng SOCKS5 proxy, cài thêm:
pip install requests[socks]
```

### 2. Cấu Hình Environment Variables

Tạo file `.env` trong thư mục `ai_medical_chatbot`:

```env
# Groq API Key (Bắt buộc)
# Lấy từ: https://console.groq.com/keys
GROQ_API_KEY=your_groq_api_key_here

# Model Groq (tùy chọn, mặc định: llama-3.3-70b-versatile)
# Có thể dùng: llama-3.3-70b-versatile, mixtral-8x7b-32768, gemma-7b-it
GROQ_MODEL=llama-3.3-70b-versatile

# Cấu hình Proxy (tùy chọn - nếu bị chặn IP)
USE_PROXY=false
PROXY_URL=http://proxy.example.com:8080
# Hoặc dùng SOCKS5:
# PROXY_URL=socks5://127.0.0.1:1080
```

### 3. Cấu Hình Database

Đảm bảo chatbot có quyền truy cập vào các database:
- `userdb` - Thông tin user
- `profiledb` - Thông tin bệnh nhân và bác sĩ
- `appointmentdb` - Lịch hẹn, đơn thuốc, báo cáo khám
- `pharmacydb` - Kho thuốc, bán hàng

Cập nhật thông tin kết nối trong `app.py` nếu cần:
```python
DB_CONFIG = {
    "host": "localhost",
    "user": "root",
    "password": "your_password",
    "port": 3306
}
```

## 🏃 Chạy Ứng Dụng

```bash
python app.py
```

Hoặc:

```bash
uvicorn app:app --reload --port 8000
```

API sẽ chạy tại: `http://localhost:8000`

## 📡 API Endpoints

### POST `/chat`
Gửi tin nhắn đến chatbot

**Request Body:**
```json
{
  "message": "Cho tôi xem lịch hẹn sắp tới",
  "user_id": "503",
  "role": "PATIENT"
}
```

**Response:**
```json
{
  "response": "Dựa trên dữ liệu hệ thống, bạn có 2 lịch hẹn sắp tới..."
}
```

### GET `/health`
Kiểm tra trạng thái service

**Response:**
```json
{
  "status": "ok",
  "provider": "groq",
  "model": "llama-3.3-70b-versatile"
}
```

## 💬 Ví Dụ Câu Hỏi

### Cho Bệnh Nhân (PATIENT):
- "Cho tôi xem lịch hẹn sắp tới"
- "Tôi có đơn thuốc nào không?"
- "Xem báo cáo khám bệnh gần nhất"
- "Thông tin hồ sơ của tôi"

### Cho Bác Sĩ (DOCTOR):
- "Lịch làm việc của tôi tuần này"
- "Bệnh nhân nào có lịch hẹn hôm nay?"
- "Đơn thuốc tôi đã kê gần đây"
- "Thông tin bệnh nhân ID 101"

### Cho Admin (ADMIN):
- "Thống kê tổng quan hệ thống"
- "Thuốc nào sắp hết trong kho?"
- "Doanh thu hôm nay"
- "Số lượng lịch hẹn hôm nay"

## 🔧 Xử Lý Lỗi

### Lỗi "Access denied" từ Groq API:
1. **Sử dụng Proxy**:
   ```env
   USE_PROXY=true
   PROXY_URL=http://your-proxy-server:port
   ```

2. **Kiểm tra API Key**: Đảm bảo `GROQ_API_KEY` đúng và còn hiệu lực

3. **Kiểm tra Quota**: Xem quota trên https://console.groq.com/

### Lỗi Kết Nối Database:
- Kiểm tra thông tin kết nối trong `DB_CONFIG`
- Đảm bảo MySQL đang chạy
- Kiểm tra quyền truy cập database

## 🎯 Cách Hoạt Động

1. **Phân Tích Intent**: Chatbot phân tích câu hỏi để xác định cần truy vấn dữ liệu gì
2. **Truy Vấn Database**: Tự động gọi function phù hợp để lấy dữ liệu từ database
3. **Xử Lý Dữ Liệu**: Format dữ liệu và đưa vào context cho AI
4. **Trả Lời**: AI sử dụng dữ liệu thực tế để trả lời câu hỏi của người dùng

## 📝 Lưu Ý

- Chatbot tự động map `user_id` sang `profile_id` dựa trên email
- Dữ liệu được truy vấn real-time từ database
- Hỗ trợ đa ngôn ngữ (chủ yếu tiếng Việt)
- Có logging chi tiết để debug
- Error handling đầy đủ

## 🔐 Bảo Mật

- API key được lưu trong file `.env` (không commit vào git)
- Database connection chỉ truy cập với quyền cần thiết
- User chỉ có thể xem dữ liệu của chính mình (dựa trên role)

## 📚 Tài Liệu Tham Khảo

- Groq API: https://console.groq.com/docs
- FastAPI: https://fastapi.tiangolo.com/
- MySQL Connector: https://dev.mysql.com/doc/connector-python/en/
