package com.hms.PharmacyMS.exception;

import com.hms.PharmacyMS.dto.BaseResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.stream.Collectors;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        BaseResponse apiResponse = new BaseResponse();
        apiResponse.setCode(400);
        apiResponse.setMessage(message);

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(apiResponse)
                .build();
    }
}
