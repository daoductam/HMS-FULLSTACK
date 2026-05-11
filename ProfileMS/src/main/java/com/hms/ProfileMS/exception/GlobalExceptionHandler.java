package com.hms.ProfileMS.exception;

import com.hms.ProfileMS.dto.BaseResponse;
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
            return Response
                    .status(hmsException.getErrorCode().getStatusCode())
                    .entity(
                            BaseResponse.builder()
                                    .code(hmsException.getErrorCode().getCode())
                                    .message(hmsException.getErrorCode().getMessage())
                                    .build()
                    ).build();
        }

        if (exception instanceof ConstraintViolationException cve) {
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

        // Default uncategorized exception
        return Response.status(Response.Status.BAD_REQUEST).entity(
                BaseResponse.builder()
                        .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                        .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                        .build()
        ).build();
    }
}
