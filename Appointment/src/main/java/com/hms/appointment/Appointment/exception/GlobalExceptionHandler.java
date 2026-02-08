package com.hms.appointment.Appointment.exception;

import com.hms.appointment.Appointment.dto.BaseResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllException(Exception exception) {
        return ResponseEntity.badRequest().body(
                BaseResponse.builder()
                        .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                        .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(HmsException.class)
    public ResponseEntity<Object> handleBusinessException(HmsException exception) {
        return ResponseEntity
                .status(exception.getErrorCode().getStatusCode())
                .body(
                        BaseResponse.builder()
                                .code(exception.getErrorCode().getCode())
                                .message(exception.getErrorCode().getMessage())
                                .build()
                );
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<Object> handleValidationException(Exception e) {
        String errorMsg;
        if (e instanceof  MethodArgumentNotValidException manv) {
            errorMsg = manv.getBindingResult().getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage).collect(Collectors.joining(","));

        } else {
            ConstraintViolationException cve = (ConstraintViolationException) e;
            errorMsg = cve.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(","));

        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(
                        BaseResponse.builder()
                                .message(errorMsg)
                                .build()
                );

    }
}
