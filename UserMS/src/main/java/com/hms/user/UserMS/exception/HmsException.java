package com.hms.user.UserMS.exception;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HmsException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private ErrorCode errorCode;

    public HmsException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
