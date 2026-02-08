package com.hms.appointment.Appointment.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_ALREADY_EXISTS(1001, "Email đã tồn tại trong hệ thống", HttpStatus.CONFLICT),

    PHONE_ALREADY_EXISTS(1002, "Phone đã tồn tại trong hệ thống", HttpStatus.CONFLICT),
    EMAIL_NOT_FOUND(1003, "Email không tồn tại trong hệ thống", HttpStatus.NOT_FOUND),
    PHONE_NOT_FOUND(1004, "Phone không tồn tại trong hệ thống", HttpStatus.NOT_FOUND),
    USER_ALREADY_VERIFIED(1005, "User đã được xá thực", HttpStatus.CONFLICT),
    INVALID_OTP(1006, "OTP is invalid", HttpStatus.BAD_REQUEST),
    EXPIRED_OTP(1007, "Mã xác thực đã hết hạn.", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND(1008, "User không tồn tại", HttpStatus.NOT_FOUND),
    FILE_TOO_LARGE(1009, "File vượt quá dung lượng 5MB", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE(1010, "Chỉ cho phép định dạng ảnh JPEG, PNG, JPG", HttpStatus.BAD_REQUEST),
    UPLOAD_FAILED(1011, "Upload Minio thất bại", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(1012, "Order not found", HttpStatus.NOT_FOUND),
    STATUS_NOT_PENDING(1013, "Order đã thanh toán hoặc đã bị hủy", HttpStatus.BAD_REQUEST),
    PAYMENT_FAILED(1014, "Thanh toán thất bại", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED(1015, "Access denied", HttpStatus.UNAUTHORIZED),
    POINT_CONFIG_NOT_FOUND(1015, "Không tìm thấy cấu hình point", HttpStatus.CONFLICT),
    DUPLICATED_IDEMPOTENT_KEY(1016, "Trùng idempotent key.", HttpStatus.CONFLICT),
    TOO_MANY_REQUEST(1017, "Thực hiện quá nhiều request.", HttpStatus.BAD_REQUEST),
    FAILED_TO_SEND_POINT_NOTIFICATION(1018,"Failed to send changed Point Notification", HttpStatus.BAD_REQUEST),
    FAILED_TO_GENERATE_HMAC(1019, "Failed to generate HMAC", HttpStatus.BAD_REQUEST),
    INVALID_SIGNATURE(1020,"Invalid signature", HttpStatus.UNAUTHORIZED),
    ERROR_HANDLING_WEBHOOK(1021, "Error handling webhook", HttpStatus.BAD_REQUEST),
    USER_ALREADY_EXISTS(1022, "User đã tồn tại trong hệ thống", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS(1023, "Invalid credentials", HttpStatus.BAD_REQUEST),
    PATIENT_ALREADY_EXISTS(1002, "Patient đã tồn tại trong hệ thống", HttpStatus.CONFLICT),
    PATIENT_NOT_FOUND(1012, "Patient not found", HttpStatus.NOT_FOUND),
    DOCTOR_ALREADY_EXISTS(1002, "Doctor đã tồn tại trong hệ thống", HttpStatus.CONFLICT),
    DOCTOR_NOT_FOUND(1012, "Doctor not found", HttpStatus.NOT_FOUND),
    APPOINTMENT_NOT_FOUND(1013,"Không tìm thấy cuộc hẹn", HttpStatus.NOT_FOUND),
    APPOINTMENT_ALREADY_CANCELLED(1014,"Cuộc hẹn đã bị hủy", HttpStatus.NOT_FOUND),
    APPOINTMENT_RECORD_NOT_FOUND(1043,"Không tìm thấy bản ghi cuộc hẹn", HttpStatus.NOT_FOUND),
    PRESCRIPTION_NOT_FOUND(1043,"Không tìm thấy đơn thuôc", HttpStatus.NOT_FOUND),
    SCHEDULE_NOT_FOUND(1044,"Không tìm thấy lịch làm việc", HttpStatus.NOT_FOUND),
    SCHEDULE_ALREADY_EXISTS(1045,"Lịch làm việc đã tồn tại", HttpStatus.CONFLICT),
    SCHEDULE_LOCKED(1046,"Lịch làm việc đã bị khóa (bác sĩ nghỉ phép)", HttpStatus.BAD_REQUEST),
    SHIFT_NOT_FOUND(1047,"Không tìm thấy ca làm việc", HttpStatus.NOT_FOUND),
    NO_AVAILABLE_SLOTS(1048,"Ca làm việc đã hết slot", HttpStatus.BAD_REQUEST),
    INVALID_APPOINTMENT_TIME(1049,"Thời gian đặt lịch không hợp lệ (không thuộc ca làm việc)", HttpStatus.BAD_REQUEST),

    ;
    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
