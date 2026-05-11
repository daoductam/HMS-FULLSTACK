package com.hms.PaymentMS.api;

import com.hms.PaymentMS.entity.PaymentTransaction;
import com.hms.PaymentMS.repository.PaymentTransactionRepository;
import com.hms.hms_common.event.PaymentSuccessEvent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.HashMap;
import java.util.Map;

/**
 * Test API để simulate MoMo callback cho testing
 * CHỈ DÙNG CHO MÔI TRƯỜNG TEST/DEVELOPMENT
 */
@Path("/payment/test")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class PaymentTestAPI {

    @Inject
    @Channel("payment-success-out")
    Emitter<PaymentSuccessEvent> paymentEmitter;
    
    @Inject
    PaymentTransactionRepository paymentTransactionRepository;

    /**
     * Simulate MoMo callback thành công
     * POST /payment/test/simulate-callback
     * Body: { "orderId": "SALE-123", "amount": 100000 }
     */
    @POST
    @Path("/simulate-callback")
    @Transactional
    public Response simulateCallback(Map<String, Object> request) {
        String orderId = request.get("orderId").toString();
        Long amount = Long.valueOf(request.get("amount").toString());
        String transId = "TEST-TRANS-" + System.currentTimeMillis();

        log.info("🧪 TEST: Simulating payment success for order: {}", orderId);

        // Xác định nguồn thanh toán
        String paymentSource = "PHARMACY";
        if (orderId.startsWith("SALE-")) {
            paymentSource = "PHARMACY";
        } else if (orderId.startsWith("APPOINTMENT-")) {
            paymentSource = "APPOINTMENT";
        }

        // Lưu payment transaction vào database
        PaymentTransaction transaction = paymentTransactionRepository.findByOrderId(orderId)
                .orElse(PaymentTransaction.builder()
                        .orderId(orderId)
                        .amount(amount.doubleValue())
                        .paymentMethod("MOMO")
                        .status("PENDING")
                        .build());

        transaction.setTransactionId(transId);
        transaction.setStatus("SUCCESS");
        paymentTransactionRepository.persist(transaction);
        log.info("💾 TEST: Đã lưu payment transaction vào database: {}", transaction.getId());

        // Tạo PaymentSuccessEvent
        PaymentSuccessEvent event = new PaymentSuccessEvent();
        event.setOrderId(orderId);
        event.setAmount(amount.doubleValue());
        event.setTransactionId(transId);
        event.setPaymentSource(paymentSource);

        // Gửi message vào topic "payment_success_topic"
        paymentEmitter.send(event);
        log.info("✅ TEST: Đã gửi event thanh toán thành công: {}", event);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Payment callback simulated successfully");
        response.put("orderId", orderId);
        response.put("transactionId", transId);

        return Response.ok(response).build();
    }
}
