package com.hms.PharmacyMS.clients;

import com.hms.PharmacyMS.config.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PaymentMS", configuration = FeignClientInterceptor.class)
public interface PaymentClient {
    
    @PostMapping("/payment/create-momo")
    String createMomoPayment(
            @RequestParam("orderId") String orderId,
            @RequestParam("amount") Double amount
    );
}

