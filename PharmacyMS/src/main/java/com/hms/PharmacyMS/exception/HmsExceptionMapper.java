package com.hms.PharmacyMS.exception;

import com.hms.PharmacyMS.dto.BaseResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class HmsExceptionMapper implements ExceptionMapper<HmsException> {

    @Override
    public Response toResponse(HmsException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        BaseResponse apiResponse = new BaseResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return Response.status(errorCode.getStatusCode())
                .entity(apiResponse)
                .build();
    }
}
