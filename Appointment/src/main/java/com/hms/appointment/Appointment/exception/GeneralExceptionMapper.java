package com.hms.appointment.Appointment.exception;

import com.hms.appointment.Appointment.dto.BaseResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Provider
@Slf4j
public class GeneralExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        log.error("Uncaught exception: ", exception);

        if (exception instanceof jakarta.ws.rs.WebApplicationException webEx) {
            return webEx.getResponse();
        }
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(BaseResponse.builder()
                        .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                        .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage() + ": " + exception.getMessage())
                        .build())
                .build();
    }
}
