import os
import mysql.connector
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import requests
from dotenv import load_dotenv
from fastapi.middleware.cors import CORSMiddleware
from datetime import datetime
import logging
import json
import re
from typing import Optional, List, Dict, Any

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

load_dotenv()

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Groq API Configuration
GROQ_API_KEY = os.environ.get("GROQ_API_KEY")
GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions"
GROQ_MODEL = os.environ.get("GROQ_MODEL", "llama-3.3-70b-versatile")

if not GROQ_API_KEY:
    logger.error("GROQ_API_KEY is not set in environment variables!")
    raise ValueError("GROQ_API_KEY is not set in the .env file")

logger.info(f"Using Groq API with model: {GROQ_MODEL}")

# Cấu hình Proxy (nếu cần fake IP)
PROXY_URL = os.environ.get("PROXY_URL")
USE_PROXY = os.environ.get("USE_PROXY", "false").lower() == "true"

proxies = None
if USE_PROXY and PROXY_URL:
    proxies = {
        "http": PROXY_URL,
        "https": PROXY_URL
    }
    logger.info(f"Using proxy: {PROXY_URL}")

# Cấu hình Database
DB_CONFIG = {
    "host": os.environ.get("DB_HOST", "localhost"),
    "user": os.environ.get("DB_USER", "root"),
    "password": os.environ.get("DB_PASSWORD", "TaM123456789@"),
    "port": int(os.environ.get("DB_PORT", 3306))
}

class ChatRequest(BaseModel):
    message: str
    user_id: str
    role: str = "PATIENT"

# ==================== DATABASE QUERY FUNCTIONS ====================

def get_db_connection():
    """Tạo kết nối database"""
    return mysql.connector.connect(**DB_CONFIG)

def resolve_profile_id(cursor, user_id: str, role: str) -> Optional[int]:
    """Tìm Profile ID từ User ID"""
    try:
        cursor.execute("SELECT email FROM userdb.user WHERE id = %s", (user_id,))
        user_record = cursor.fetchone()
        
        if not user_record:
            return None

        email = user_record['email']
        
        if role.upper() == "PATIENT":
            cursor.execute("SELECT id FROM profiledb.patient WHERE email = %s", (email,))
        elif role.upper() == "DOCTOR":
            cursor.execute("SELECT id FROM profiledb.doctor WHERE email = %s", (email,))
        else:
            return None

        profile_record = cursor.fetchone()
        return profile_record['id'] if profile_record else None
    except Exception as e:
        logger.error(f"Error resolving profile ID: {e}")
        return None

def query_patient_info(cursor, patient_id: int) -> Dict[str, Any]:
    """Lấy thông tin bệnh nhân"""
    try:
        cursor.execute("""
            SELECT id, name, email, dob, phone, address, CCCD, blood_group, allergies, chronic_disease
            FROM profiledb.patient WHERE id = %s
        """, (patient_id,))
        result = cursor.fetchone()
        return result if result else {}
    except Exception as e:
        logger.error(f"Error querying patient info: {e}")
        return {}

def query_doctor_info(cursor, doctor_id: int) -> Dict[str, Any]:
    """Lấy thông tin bác sĩ"""
    try:
        cursor.execute("""
            SELECT id, name, email, phone, address, license_no, specialization, department, total_exp
            FROM profiledb.doctor WHERE id = %s
        """, (doctor_id,))
        result = cursor.fetchone()
        return result if result else {}
    except Exception as e:
        logger.error(f"Error querying doctor info: {e}")
        return {}

def query_appointments(cursor, patient_id: Optional[int] = None, doctor_id: Optional[int] = None, 
                      status: Optional[str] = None, limit: int = 10) -> List[Dict[str, Any]]:
    """Lấy danh sách lịch hẹn"""
    try:
        query = """
            SELECT a.id, a.patient_id, a.doctor_id, a.appointment_time, a.status, a.reason, a.notes,
                   p.name as patient_name, d.name as doctor_name
            FROM appointmentdb.appointment a
            LEFT JOIN profiledb.patient p ON a.patient_id = p.id
            LEFT JOIN profiledb.doctor d ON a.doctor_id = d.id
            WHERE 1=1
        """
        params = []
        
        if patient_id:
            query += " AND a.patient_id = %s"
            params.append(patient_id)
        if doctor_id:
            query += " AND a.doctor_id = %s"
            params.append(doctor_id)
        if status:
            query += " AND a.status = %s"
            params.append(status)
        
        query += " ORDER BY a.appointment_time DESC LIMIT %s"
        params.append(limit)
        
        cursor.execute(query, params)
        return cursor.fetchall()
    except Exception as e:
        logger.error(f"Error querying appointments: {e}")
        return []

def query_prescriptions(cursor, patient_id: Optional[int] = None, doctor_id: Optional[int] = None,
                       limit: int = 10) -> List[Dict[str, Any]]:
    """Lấy danh sách đơn thuốc"""
    try:
        query = """
            SELECT pr.id, pr.patient_id, pr.doctor_id, pr.appointment_id, pr.prescription_date, pr.notes,
                   p.name as patient_name, d.name as doctor_name
            FROM appointmentdb.prescription pr
            LEFT JOIN profiledb.patient p ON pr.patient_id = p.id
            LEFT JOIN profiledb.doctor d ON pr.doctor_id = d.id
            WHERE 1=1
        """
        params = []
        
        if patient_id:
            query += " AND pr.patient_id = %s"
            params.append(patient_id)
        if doctor_id:
            query += " AND pr.doctor_id = %s"
            params.append(doctor_id)
        
        query += " ORDER BY pr.prescription_date DESC LIMIT %s"
        params.append(limit)
        
        cursor.execute(query, params)
        prescriptions = cursor.fetchall()
        
        # Lấy danh sách thuốc cho mỗi đơn
        for pres in prescriptions:
            cursor.execute("""
                SELECT id, name, dosage, frequency, duration, route, type, instructions
                FROM appointmentdb.medicine WHERE prescription_id = %s
            """, (pres['id'],))
            pres['medicines'] = cursor.fetchall()
        
        return prescriptions
    except Exception as e:
        logger.error(f"Error querying prescriptions: {e}")
        return []

def query_ap_records(cursor, patient_id: Optional[int] = None, doctor_id: Optional[int] = None,
                    limit: int = 10) -> List[Dict[str, Any]]:
    """Lấy danh sách báo cáo khám bệnh"""
    try:
        query = """
            SELECT ar.id, ar.patient_id, ar.doctor_id, ar.appointment_id, ar.symptoms, ar.diagnosis,
                   ar.tests, ar.notes, ar.referral, ar.follow_up_date, ar.created_at,
                   p.name as patient_name, d.name as doctor_name
            FROM appointmentdb.ap_record ar
            LEFT JOIN profiledb.patient p ON ar.patient_id = p.id
            LEFT JOIN profiledb.doctor d ON ar.doctor_id = d.id
            WHERE 1=1
        """
        params = []
        
        if patient_id:
            query += " AND ar.patient_id = %s"
            params.append(patient_id)
        if doctor_id:
            query += " AND ar.doctor_id = %s"
            params.append(doctor_id)
        
        query += " ORDER BY ar.created_at DESC LIMIT %s"
        params.append(limit)
        
        cursor.execute(query, params)
        return cursor.fetchall()
    except Exception as e:
        logger.error(f"Error querying ap_records: {e}")
        return []

def query_doctor_schedules(cursor, doctor_id: int, start_date: Optional[str] = None,
                          end_date: Optional[str] = None) -> List[Dict[str, Any]]:
    """Lấy lịch làm việc của bác sĩ"""
    try:
        query = """
            SELECT ds.id, ds.doctor_id, ds.schedule_date, ds.is_locked, ds.lock_reason,
                   ss.id as shift_id, ss.max_slots, ss.booked_slots,
                   s.name as shift_name, s.display_name as shift_display, s.start_hour, s.end_hour
            FROM appointmentdb.doctor_schedules ds
            LEFT JOIN appointmentdb.schedule_shifts ss ON ds.id = ss.schedule_id
            LEFT JOIN appointmentdb.shifts s ON ss.shift_id = s.id
            WHERE ds.doctor_id = %s
        """
        params = [doctor_id]
        
        if start_date:
            query += " AND ds.schedule_date >= %s"
            params.append(start_date)
        if end_date:
            query += " AND ds.schedule_date <= %s"
            params.append(end_date)
        
        query += " ORDER BY ds.schedule_date ASC, s.start_hour ASC"
        
        cursor.execute(query, params)
        return cursor.fetchall()
    except Exception as e:
        logger.error(f"Error querying doctor schedules: {e}")
        return []

def query_medicine_inventory(cursor, low_stock: bool = False, limit: int = 20) -> List[Dict[str, Any]]:
    """Lấy thông tin kho thuốc"""
    try:
        query = """
            SELECT mi.id, mi.medicine_id, mi.batch_no, mi.quantity, mi.expiry_date, mi.status,
                   m.name as medicine_name, m.dosage, m.unit_price, m.stock
            FROM pharmacydb.medicine_inventory mi
            LEFT JOIN pharmacydb.medicine m ON mi.medicine_id = m.id
            WHERE 1=1
        """
        params = []
        
        if low_stock:
            query += " AND (m.stock < 50 OR mi.quantity < 50)"
        
        query += " ORDER BY mi.expiry_date ASC LIMIT %s"
        params.append(limit)
        
        cursor.execute(query, params)
        return cursor.fetchall()
    except Exception as e:
        logger.error(f"Error querying medicine inventory: {e}")
        return []

def query_sales(cursor, start_date: Optional[str] = None, end_date: Optional[str] = None,
               status: Optional[str] = None, limit: int = 20) -> List[Dict[str, Any]]:
    """Lấy danh sách bán hàng"""
    try:
        query = """
            SELECT s.id, s.prescription_id, s.buyer_name, s.buyer_contact, s.sale_date,
                   s.total_amount, s.status
            FROM pharmacydb.sale s
            WHERE 1=1
        """
        params = []
        
        if start_date:
            query += " AND DATE(s.sale_date) >= %s"
            params.append(start_date)
        if end_date:
            query += " AND DATE(s.sale_date) <= %s"
            params.append(end_date)
        if status:
            query += " AND s.status = %s"
            params.append(status)
        
        query += " ORDER BY s.sale_date DESC LIMIT %s"
        params.append(limit)
        
        cursor.execute(query, params)
        return cursor.fetchall()
    except Exception as e:
        logger.error(f"Error querying sales: {e}")
        return []

def query_statistics(cursor) -> Dict[str, Any]:
    """Lấy thống kê tổng quan hệ thống"""
    try:
        stats = {}
        
        # Tổng số user
        cursor.execute("SELECT COUNT(*) as cnt FROM userdb.user")
        stats['total_users'] = cursor.fetchone()['cnt']
        
        # Tổng số bệnh nhân
        cursor.execute("SELECT COUNT(*) as cnt FROM profiledb.patient")
        stats['total_patients'] = cursor.fetchone()['cnt']
        
        # Tổng số bác sĩ
        cursor.execute("SELECT COUNT(*) as cnt FROM profiledb.doctor")
        stats['total_doctors'] = cursor.fetchone()['cnt']
        
        # Lịch hẹn hôm nay
        cursor.execute("SELECT COUNT(*) as cnt FROM appointmentdb.appointment WHERE DATE(appointment_time) = CURDATE()")
        stats['today_appointments'] = cursor.fetchone()['cnt']
        
        # Lịch hẹn sắp tới (7 ngày)
        cursor.execute("SELECT COUNT(*) as cnt FROM appointmentdb.appointment WHERE appointment_time >= CURDATE() AND appointment_time <= DATE_ADD(CURDATE(), INTERVAL 7 DAY)")
        stats['upcoming_appointments'] = cursor.fetchone()['cnt']
        
        # Thuốc sắp hết
        cursor.execute("SELECT COUNT(*) as cnt FROM pharmacydb.medicine WHERE stock < 50")
        stats['low_stock_medicines'] = cursor.fetchone()['cnt']
        
        # Doanh thu hôm nay
        cursor.execute("SELECT COALESCE(SUM(total_amount), 0) as total FROM pharmacydb.sale WHERE DATE(sale_date) = CURDATE() AND status = 'PAID'")
        revenue_result = cursor.fetchone()
        stats['today_revenue'] = float(revenue_result['total']) if revenue_result and revenue_result['total'] else 0.0
        
        return stats
    except Exception as e:
        logger.error(f"Error querying statistics: {e}")
        return {}

# ==================== INTENT ANALYSIS & FUNCTION CALLING ====================

def analyze_intent(message: str, role: str) -> Dict[str, Any]:
    """Phân tích intent từ câu hỏi để quyết định gọi function nào"""
    message_lower = message.lower()
    intent = {
        "function": None,
        "arguments": {}
    }
    
    # Keywords mapping
    if any(word in message_lower for word in ["lịch hẹn", "cuộc hẹn", "appointment", "đặt lịch", "lịch khám"]):
        intent["function"] = "query_appointments"
        if "đã hủy" in message_lower or "cancelled" in message_lower:
            intent["arguments"]["status"] = "CANCELLED"
        elif "hoàn thành" in message_lower or "completed" in message_lower:
            intent["arguments"]["status"] = "COMPLETED"
        elif "đã đặt" in message_lower or "scheduled" in message_lower:
            intent["arguments"]["status"] = "SCHEDULED"
    
    elif any(word in message_lower for word in ["đơn thuốc", "prescription", "thuốc đã kê"]):
        intent["function"] = "query_prescriptions"
    
    elif any(word in message_lower for word in ["báo cáo", "kết quả khám", "ap_record", "record", "hồ sơ khám"]):
        intent["function"] = "query_ap_records"
    
    elif any(word in message_lower for word in ["thông tin bệnh nhân", "hồ sơ bệnh nhân", "patient info"]):
        intent["function"] = "query_patient_info"
    
    elif any(word in message_lower for word in ["thông tin bác sĩ", "hồ sơ bác sĩ", "doctor info"]):
        intent["function"] = "query_doctor_info"
    
    elif any(word in message_lower for word in ["lịch làm việc", "schedule", "ca làm việc"]):
        intent["function"] = "query_doctor_schedules"
    
    elif any(word in message_lower for word in ["kho thuốc", "inventory", "tồn kho", "thuốc sắp hết"]):
        intent["function"] = "query_medicine_inventory"
        if "sắp hết" in message_lower or "low stock" in message_lower:
            intent["arguments"]["low_stock"] = True
    
    elif any(word in message_lower for word in ["bán hàng", "sales", "doanh thu", "revenue"]):
        intent["function"] = "query_sales"
    
    elif any(word in message_lower for word in ["thống kê", "statistics", "báo cáo tổng", "tổng quan"]):
        intent["function"] = "query_statistics"
    
    # Extract IDs from message
    id_match = re.search(r'\b(\d+)\b', message)
    if id_match:
        if "bệnh nhân" in message_lower or "patient" in message_lower:
            intent["arguments"]["patient_id"] = int(id_match.group(1))
        elif "bác sĩ" in message_lower or "doctor" in message_lower:
            intent["arguments"]["doctor_id"] = int(id_match.group(1))
    
    return intent

def execute_function_by_intent(intent: Dict[str, Any], user_id: str, role: str) -> Any:
    """Thực thi function dựa trên intent"""
    if not intent["function"]:
        return None
    
    conn = get_db_connection()
    cursor = conn.cursor(dictionary=True)
    
    try:
        profile_id = resolve_profile_id(cursor, user_id, role)
        args = intent["arguments"].copy()
        
        if intent["function"] == "query_patient_info":
            patient_id = args.get("patient_id") or profile_id
            if patient_id:
                return query_patient_info(cursor, patient_id)
        
        elif intent["function"] == "query_doctor_info":
            doctor_id = args.get("doctor_id") or profile_id
            if doctor_id:
                return query_doctor_info(cursor, doctor_id)
        
        elif intent["function"] == "query_appointments":
            if role.upper() == "PATIENT":
                args["patient_id"] = args.get("patient_id") or profile_id
            elif role.upper() == "DOCTOR":
                args["doctor_id"] = args.get("doctor_id") or profile_id
            return query_appointments(cursor, args.get("patient_id"), args.get("doctor_id"), 
                                     args.get("status"), args.get("limit", 10))
        
        elif intent["function"] == "query_prescriptions":
            if role.upper() == "PATIENT":
                args["patient_id"] = args.get("patient_id") or profile_id
            elif role.upper() == "DOCTOR":
                args["doctor_id"] = args.get("doctor_id") or profile_id
            return query_prescriptions(cursor, args.get("patient_id"), args.get("doctor_id"), 
                                     args.get("limit", 10))
        
        elif intent["function"] == "query_ap_records":
            if role.upper() == "PATIENT":
                args["patient_id"] = args.get("patient_id") or profile_id
            elif role.upper() == "DOCTOR":
                args["doctor_id"] = args.get("doctor_id") or profile_id
            return query_ap_records(cursor, args.get("patient_id"), args.get("doctor_id"), 
                                  args.get("limit", 10))
        
        elif intent["function"] == "query_doctor_schedules":
            doctor_id = args.get("doctor_id") or profile_id
            if doctor_id:
                return query_doctor_schedules(cursor, doctor_id, args.get("start_date"), args.get("end_date"))
        
        elif intent["function"] == "query_medicine_inventory":
            return query_medicine_inventory(cursor, args.get("low_stock", False), args.get("limit", 20))
        
        elif intent["function"] == "query_sales":
            return query_sales(cursor, args.get("start_date"), args.get("end_date"), 
                             args.get("status"), args.get("limit", 20))
        
        elif intent["function"] == "query_statistics":
            return query_statistics(cursor)
        
        return None
    
    finally:
        cursor.close()
        conn.close()

def format_data_for_ai(data: Any) -> str:
    """Format dữ liệu để đưa vào prompt cho AI"""
    if not data:
        return "Không có dữ liệu."
    
    if isinstance(data, dict):
        if not data:
            return "Không có dữ liệu."
        return json.dumps(data, default=str, ensure_ascii=False, indent=2)
    
    if isinstance(data, list):
        if not data:
            return "Không có dữ liệu."
        return json.dumps(data, default=str, ensure_ascii=False, indent=2)
    
    return str(data)

# ==================== GROQ API CALL ====================

def call_groq_api(system_instruction: str, user_message: str) -> str:
    """Gọi Groq API để lấy phản hồi từ AI"""
    headers = {
        "Authorization": f"Bearer {GROQ_API_KEY}",
        "Content-Type": "application/json"
    }
    
    payload = {
        "model": GROQ_MODEL,
        "messages": [
            {"role": "system", "content": system_instruction},
            {"role": "user", "content": user_message}
        ],
        "temperature": 0.3,
        "max_tokens": 2048
    }
    
    logger.info(f"Calling Groq API with model: {GROQ_MODEL}")
    
    response = requests.post(
        GROQ_API_URL,
        json=payload,
        headers=headers,
        proxies=proxies,
        timeout=60,
        verify=True
    )
    
    logger.info(f"Groq API response status: {response.status_code}")
    
    if response.status_code == 200:
        result = response.json()
        return result["choices"][0]["message"]["content"]
    else:
        error_detail = response.text
        logger.error(f"Groq API error: {response.status_code} - {error_detail}")
        
        if response.status_code == 401:
            raise HTTPException(status_code=500, detail="API key không hợp lệ. Vui lòng kiểm tra lại GROQ_API_KEY")
        elif response.status_code == 403:
            raise HTTPException(status_code=500, detail="Access denied. Có thể do IP bị chặn. Vui lòng sử dụng proxy.")
        else:
            raise HTTPException(status_code=500, detail=f"Lỗi từ Groq API: {response.status_code} - {error_detail}")

# ==================== CHAT ENDPOINT ====================

@app.post("/chat")
@app.post("/chatbot/chat")
async def chat_endpoint(request: ChatRequest):
    try:
        current_time = datetime.now().strftime("%H:%M %d/%m/%Y")
        
        # Phân tích intent từ câu hỏi
        intent = analyze_intent(request.message, request.role)
        logger.info(f"Detected intent: {intent}")
        
        # Thực thi function nếu có
        db_data = None
        if intent["function"]:
            db_data = execute_function_by_intent(intent, request.user_id, request.role)
            logger.info(f"Function {intent['function']} returned data: {len(str(db_data))} chars")
        
        # System prompt dựa trên role
        role_instructions = {
            "PATIENT": "Bạn là trợ lý y tế thân thiện của phòng khám. Hỗ trợ bệnh nhân tra cứu thông tin lịch hẹn, đơn thuốc, báo cáo khám bệnh. Trả lời bằng tiếng Việt, thân thiện và dễ hiểu.",
            "DOCTOR": "Bạn là trợ lý ảo hỗ trợ Bác sĩ. Giúp bác sĩ tra cứu lịch làm việc, thông tin bệnh nhân, đơn thuốc đã kê. Trả lời chuyên nghiệp, ngắn gọn bằng tiếng Việt.",
            "ADMIN": "Bạn là trợ lý quản trị viên. Hỗ trợ tra cứu thống kê hệ thống, quản lý kho thuốc, doanh thu. Trả lời tập trung vào số liệu và phân tích bằng tiếng Việt."
        }
        
        system_instruction = (
            f"{role_instructions.get(request.role.upper(), role_instructions['PATIENT'])} "
            f"Bây giờ là: {current_time}. "
        )
        
        # Thêm dữ liệu từ database vào user message nếu có
        user_message = request.message
        if db_data:
            formatted_data = format_data_for_ai(db_data)
            user_message = (
                f"{request.message}\n\n"
                f"Dưới đây là dữ liệu thực tế từ hệ thống:\n"
                f"--- DỮ LIỆU HỆ THỐNG ---\n{formatted_data}\n--- HẾT DỮ LIỆU ---\n"
                "Hãy trả lời câu hỏi của người dùng dựa trên dữ liệu trên. "
                "Nếu dữ liệu trống hoặc 'Không có dữ liệu', hãy thông báo cho người dùng biết."
            )
        
        # Gọi Groq API
        ai_response = call_groq_api(system_instruction, user_message)
        
        return {"response": ai_response}
    
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error in chat endpoint: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Lỗi không xác định: {str(e)}")

@app.get("/health")
@app.get("/chatbot/health")
async def health_check():
    return {"status": "ok", "provider": "groq", "model": GROQ_MODEL}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
