package com.hms.appointment.Appointment.exception;

import com.hms.appointment.Appointment.dto.BaseResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class HmsExceptionMapper implements ExceptionMapper<HmsException> {

    @Override
    public Response toResponse(HmsException exception) {
        return Response.status(exception.getErrorCode().getStatusCode())
                .entity(BaseResponse.builder()
                        .code(exception.getErrorCode().getCode())
                        .message(exception.getErrorCode().getMessage())
                        .build())
                .build();
    }
}
