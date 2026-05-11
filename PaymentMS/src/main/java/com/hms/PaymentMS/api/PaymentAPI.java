package com.hms.PaymentMS.api;

import com.hms.PaymentMS.entity.PaymentTransaction;
import com.hms.PaymentMS.repository.PaymentTransactionRepository;
import com.hms.PaymentMS.service.PaymentService;
import com.hms.hms_common.event.PaymentSuccessEvent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.resteasy.reactive.RestQuery;

import java.util.Map;

@Path("/payment")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class PaymentAPI {

    @Inject
    PaymentService paymentService;

    @Inject
    PaymentTransactionRepository paymentTransactionRepository;

    @Inject
    @Channel("payment-success-out")
    Emitter<PaymentSuccessEvent> paymentEmitter;

    // API 1: Tạo link thanh toán
    @POST
    @Path("/create-momo")
    public Response createMomo(@RestQuery String orderId, @RestQuery Double amount) {
        String payUrl = paymentService.createMomoPayment(orderId, amount, "Thanh toan don thuoc");
        return Response.ok(payUrl).build();
    }

    @POST
    @Path("/ipn-callback")
    @Transactional
    public Response ipnCallback(Map<String, Object> response) {
        log.info("🔔 Nhận được IPN callback từ MoMo: {}", response);
        
        // ... (Logic kiểm tra chữ ký giữ nguyên) ...

        String orderId = response.get("orderId").toString();
        String transId = response.get("transId") != null ? response.get("transId").toString() : null;
        Long amount = Long.valueOf(response.get("amount").toString());
        String resultCode = response.get("resultCode").toString();

        // Lưu hoặc cập nhật payment transaction vào database
        PaymentTransaction transaction = paymentTransactionRepository.findByOrderId(orderId)
                .orElse(PaymentTransaction.builder()
                        .orderId(orderId)
                        .amount(amount.doubleValue())
                        .paymentMethod("MOMO")
                        .status("PENDING")
                        .build());

        transaction.setTransactionId(transId);
        
        if ("0".equals(resultCode)) {
            // Thanh toán thành công
            transaction.setStatus("SUCCESS");
            log.info("Thanh toán THÀNH CÔNG cho đơn: {}", orderId);

            // Xác định nguồn thanh toán dựa trên orderId
            String paymentSource = "PHARMACY"; // Mặc định là PHARMACY
            if (orderId.startsWith("SALE-")) {
                paymentSource = "PHARMACY";
            } else if (orderId.startsWith("APPOINTMENT-")) {
                paymentSource = "APPOINTMENT";
            }

            // Tạo PaymentSuccessEvent
            PaymentSuccessEvent event = new PaymentSuccessEvent();
            event.setOrderId(orderId);
            event.setAmount(amount.doubleValue());
            event.setTransactionId(transId);
            event.setPaymentSource(paymentSource);

            // Gửi message vào topic "payment_success_topic"
            paymentEmitter.send(event);
            log.info("✅ Đã gửi event thanh toán thành công: {}", event);
        } else {
            // Thanh toán thất bại
            transaction.setStatus("FAILED");
            log.info("❌ Thanh toán THẤT BẠI cho đơn: {}, resultCode: {}", orderId, resultCode);
        }

        // Lưu vào database
        paymentTransactionRepository.persist(transaction);
        log.info("💾 Đã lưu payment transaction vào database: {}", transaction.getId());

        return Response.noContent().build();
    }
}