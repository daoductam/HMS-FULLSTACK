package com.hms.PharmacyMS.clients;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "payment-api")
public interface PaymentClient {
    
    @POST
    @Path("/payment/create-momo")
    @Consumes(MediaType.TEXT_PLAIN)
    String createMomoPayment(
            @QueryParam("orderId") String orderId,
            @QueryParam("amount") Double amount
    );
}
