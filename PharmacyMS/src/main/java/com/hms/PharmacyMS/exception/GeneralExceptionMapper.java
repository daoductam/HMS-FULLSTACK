package com.hms.PharmacyMS.exception;

import com.hms.PharmacyMS.dto.BaseResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GeneralExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        exception.printStackTrace();
        BaseResponse apiResponse = new BaseResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(apiResponse)
                .build();
    }
}
