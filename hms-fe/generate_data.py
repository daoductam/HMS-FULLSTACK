import requests
import random
from faker import Faker
import time

# CẤU HÌNH
API_URL = "http://localhost:9000/user/register" # Thay port Gateway của bạn
TOTAL_DOCTORS = 100
TOTAL_PATIENTS = 400

fake = Faker()

def generate_user(role_id):
    # Role: 1 = DOCTOR, 0 = PATIENT
    prefix = "dr" if role_id == 1 else "pat"
    
    payload = {
        "name": fake.name(),
        # Tạo email unique dựa trên timestamp để tránh trùng lặp
        "email": f"{prefix}_{fake.user_name()}_{int(time.time()*1000)}@gmail.com",
        "password": "TaM123456789@", # Password mặc định
        "role": role_id,
        "status": "ACTIVE",
        "mobileNumber": f"09{random.randint(10000000, 99999999)}"
    }
    return payload

def run_batch(role_id, count, label):
    print(f"--- Bắt đầu tạo {count} {label} ---")
    success = 0
    errors = 0
    
    for i in range(count):
        data = generate_user(role_id)
        try:
            response = requests.post(API_URL, json=data, timeout=5)
            if response.status_code in [200, 201]:
                print(f"[{i+1}/{count}] Success: {data['email']}")
                success += 1
            else:
                print(f"[{i+1}/{count}] FAILED: {response.text}")
                errors += 1
        except Exception as e:
            print(f"[{i+1}/{count}] ERROR: {str(e)}")
            errors += 1
            
    print(f"--- Hoàn thành {label}: {success} Thành công, {errors} Thất bại ---")

if __name__ == "__main__":
    # 1. Tạo Doctors
    run_batch(1, TOTAL_DOCTORS, "DOCTOR")
    
    # 2. Tạo Patients
    run_batch(0, TOTAL_PATIENTS, "PATIENT")