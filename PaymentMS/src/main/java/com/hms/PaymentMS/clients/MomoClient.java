package com.hms.PaymentMS.clients;

import com.hms.PaymentMS.dto.MomoPaymentRequest;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.Map;

@RegisterRestClient(configKey = "momo-api")
public interface MomoClient {

    @POST
    @Path("/create")
    Map<String, Object> createPayment(MomoPaymentRequest request);
}
