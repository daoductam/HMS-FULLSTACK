package com.hms.ProfileMS.exception;

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

    PATIENT_ALREADY_EXISTS(1002, "Patient đã tồn tại trong hệ thống", HttpStatus.CONFLICT),
    PATIENT_NOT_FOUND(1012, "Patient not found", HttpStatus.NOT_FOUND),
    DOCTOR_ALREADY_EXISTS(1002, "Doctor đã tồn tại trong hệ thống", HttpStatus.CONFLICT),
    DOCTOR_NOT_FOUND(1012, "Doctor not found", HttpStatus.NOT_FOUND),
    ACCESS_DENIED(1013, "Bạn không có quyền thực hiện hành động này", HttpStatus.FORBIDDEN)
    ;
    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
