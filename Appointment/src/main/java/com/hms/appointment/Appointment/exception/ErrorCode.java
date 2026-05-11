package com.hms.appointment.Appointment.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", 500),
    EMAIL_ALREADY_EXISTS(1001, "Email đã tồn tại trong hệ thống", 409),
    PHONE_ALREADY_EXISTS(1002, "Phone đã tồn tại trong hệ thống", 409),
    EMAIL_NOT_FOUND(1003, "Email không tồn tại trong hệ thống", 404),
    PHONE_NOT_FOUND(1004, "Phone không tồn tại trong hệ thống", 404),
    USER_ALREADY_VERIFIED(1005, "User đã được xác thực", 409),
    INVALID_OTP(1006, "OTP is invalid", 400),
    EXPIRED_OTP(1007, "Mã xác thực đã hết hạn.", 401),
    USER_NOT_FOUND(1008, "User không tồn tại", 404),
    FILE_TOO_LARGE(1009, "File vượt quá dung lượng 5MB", 400),
    INVALID_IMAGE(1010, "Chỉ cho phép định dạng ảnh JPEG, PNG, JPG", 400),
    UPLOAD_FAILED(1011, "Upload Minio thất bại", 400),
    ORDER_NOT_FOUND(1012, "Order not found", 404),
    STATUS_NOT_PENDING(1013, "Order đã thanh toán hoặc đã bị hủy", 400),
    PAYMENT_FAILED(1014, "Thanh toán thất bại", 400),
    ACCESS_DENIED(1015, "Access denied", 401),
    POINT_CONFIG_NOT_FOUND(1015, "Không tìm thấy cấu hình point", 409),
    DUPLICATED_IDEMPOTENT_KEY(1016, "Trùng idempotent key.", 409),
    TOO_MANY_REQUEST(1017, "Thực hiện quá nhiều request.", 400),
    FAILED_TO_SEND_POINT_NOTIFICATION(1018,"Failed to send changed Point Notification", 400),
    FAILED_TO_GENERATE_HMAC(1019, "Failed to generate HMAC", 400),
    INVALID_SIGNATURE(1020,"Invalid signature", 401),
    ERROR_HANDLING_WEBHOOK(1021, "Error handling webhook", 400),
    USER_ALREADY_EXISTS(1022, "User đã tồn tại trong hệ thống", 409),
    INVALID_CREDENTIALS(1023, "Invalid credentials", 400),
    PATIENT_ALREADY_EXISTS(1002, "Patient đã tồn tại trong hệ thống", 409),
    PATIENT_NOT_FOUND(1012, "Patient not found", 404),
    DOCTOR_ALREADY_EXISTS(1002, "Doctor đã tồn tại trong hệ thống", 409),
    DOCTOR_NOT_FOUND(1012, "Doctor not found", 404),
    APPOINTMENT_NOT_FOUND(1013,"Không tìm thấy cuộc hẹn", 404),
    APPOINTMENT_ALREADY_CANCELLED(1014,"Cuộc hẹn đã bị hủy", 404),
    APPOINTMENT_RECORD_NOT_FOUND(1043,"Không tìm thấy bản ghi cuộc hẹn", 404),
    PRESCRIPTION_NOT_FOUND(1043,"Không tìm thấy đơn thuốc", 404),
    SCHEDULE_NOT_FOUND(1044,"Không tìm thấy lịch làm việc", 404),
    SCHEDULE_ALREADY_EXISTS(1045,"Lịch làm việc đã tồn tại", 409),
    SCHEDULE_LOCKED(1046,"Lịch làm việc đã bị khóa (bác sĩ nghỉ phép)", 400),
    SHIFT_NOT_FOUND(1047,"Không tìm thấy ca làm việc", 404),
    NO_AVAILABLE_SLOTS(1048,"Ca làm việc đã hết slot", 400),
    INVALID_APPOINTMENT_TIME(1049,"Thời gian đặt lịch không hợp lệ (không thuộc ca làm việc)", 400);

    private int code;
    private String message;
    private int statusCode;
}

