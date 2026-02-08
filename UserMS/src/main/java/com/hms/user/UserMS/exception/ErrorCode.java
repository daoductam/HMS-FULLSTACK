package com.hms.user.UserMS.exception;

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
    USER_ALREADY_EXISTS(1022, "User đã tồn tại trong hệ thống", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS(1023, "Invalid credentials", HttpStatus.BAD_REQUEST),
    INVALID_USER_ROLE(1023, "Invalid user role", HttpStatus.BAD_REQUEST),
    ACCOUNT_PENDING_APPROVAL(1023, "Chưa xác nhận tài khoản", HttpStatus.BAD_REQUEST),
    ACCOUNT_LOCKED(1023, "Tài khoản đã bị khóa", HttpStatus.BAD_REQUEST),
    ;
    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
