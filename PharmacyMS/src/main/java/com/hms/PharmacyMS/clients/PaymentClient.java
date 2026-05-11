package com.hms.PharmacyMS.clients;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "payment-api")
public interface PaymentClient {
    
    @POST
    @Path("/payment/create-momo")
    String createMomoPayment(
            @QueryParam("orderId") String orderId,
            @QueryParam("amount") Double amount
    );
}
