package com.hms.user.UserMS.exception;

import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", Response.Status.INTERNAL_SERVER_ERROR),
    EMAIL_ALREADY_EXISTS(1001, "Email đã tồn tại trong hệ thống", Response.Status.CONFLICT),
    PHONE_ALREADY_EXISTS(1002, "Phone đã tồn tại trong hệ thống", Response.Status.CONFLICT),
    EMAIL_NOT_FOUND(1003, "Email không tồn tại trong hệ thống", Response.Status.NOT_FOUND),
    PHONE_NOT_FOUND(1004, "Phone không tồn tại trong hệ thống", Response.Status.NOT_FOUND),
    USER_ALREADY_VERIFIED(1005, "User đã được xá thực", Response.Status.CONFLICT),
    INVALID_OTP(1006, "OTP is invalid", Response.Status.BAD_REQUEST),
    EXPIRED_OTP(1007, "Mã xác thực đã hết hạn.", Response.Status.UNAUTHORIZED),
    USER_NOT_FOUND(1008, "User không tồn tại", Response.Status.NOT_FOUND),
    USER_ALREADY_EXISTS(1022, "User đã tồn tại trong hệ thống", Response.Status.CONFLICT),
    INVALID_CREDENTIALS(1023, "Invalid credentials", Response.Status.BAD_REQUEST),
    INVALID_USER_ROLE(1023, "Invalid user role", Response.Status.BAD_REQUEST),
    ACCOUNT_PENDING_APPROVAL(1023, "Chưa xác nhận tài khoản", Response.Status.BAD_REQUEST),
    ACCOUNT_LOCKED(1023, "Tài khoản đã bị khóa", Response.Status.BAD_REQUEST),
    ;
    private int code;
    private String message;
    private Response.Status statusCode;
}

