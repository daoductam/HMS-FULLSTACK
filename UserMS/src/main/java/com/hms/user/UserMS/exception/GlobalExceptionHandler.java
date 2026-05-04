package com.hms.user.UserMS.exception;

import com.hms.user.UserMS.dto.BaseResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.stream.Collectors;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof HmsException hmsException) {
            return handleBusinessException(hmsException);
        }
        if (exception instanceof ConstraintViolationException constraintViolationException) {
            return handleValidationException(constraintViolationException);
        }
        return handleAllException(exception);
    }

    private Response handleAllException(Exception exception) {
        return Response.status(Response.Status.BAD_REQUEST).entity(
                BaseResponse.builder()
                        .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                        .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                        .build()
        ).build();
    }

    private Response handleBusinessException(HmsException exception) {
        return Response
                .status(exception.getErrorCode().getStatusCode())
                .entity(
                        BaseResponse.builder()
                                .code(exception.getErrorCode().getCode())
                                .message(exception.getErrorCode().getMessage())
                                .build()
                ).build();
    }

    private Response handleValidationException(ConstraintViolationException cve) {
        String errorMsg = cve.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(","));

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(
                        BaseResponse.builder()
                                .message(errorMsg)
                                .build()
                ).build();
    }
}

