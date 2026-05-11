package com.hms.ProfileMS.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", 500),

    PATIENT_ALREADY_EXISTS(1002, "Patient đã tồn tại trong hệ thống", 409),
    PATIENT_NOT_FOUND(1012, "Patient not found", 404),
    DOCTOR_ALREADY_EXISTS(1002, "Doctor đã tồn tại trong hệ thống", 409),
    DOCTOR_NOT_FOUND(1012, "Doctor not found", 404),
    ACCESS_DENIED(1013, "Bạn không có quyền thực hiện hành động này", 403)
    ;
    private int code;
    private String message;
    private int statusCode;
}
