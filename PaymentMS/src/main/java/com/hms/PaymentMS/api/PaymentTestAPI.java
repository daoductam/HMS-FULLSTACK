package com.hms.PaymentMS.api;

import com.hms.PaymentMS.entity.PaymentTransaction;
import com.hms.PaymentMS.repository.PaymentTransactionRepository;
import com.hms.hms_common.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Test API để simulate MoMo callback cho testing
 * CHỈ DÙNG CHO MÔI TRƯỜNG TEST/DEVELOPMENT
 */
@RestController
@RequestMapping("/payment/test")
@RequiredArgsConstructor
public class PaymentTestAPI {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    private final PaymentTransactionRepository paymentTransactionRepository;

    /**
     * Simulate MoMo callback thành công
     * POST /payment/test/simulate-callback
     * Body: { "orderId": "SALE-123", "amount": 100000 }
     */
    @PostMapping("/simulate-callback")
    public ResponseEntity<Map<String, String>> simulateCallback(@RequestBody Map<String, Object> request) {
        String orderId = request.get("orderId").toString();
        Long amount = Long.valueOf(request.get("amount").toString());
        String transId = "TEST-TRANS-" + System.currentTimeMillis();

        System.out.println("🧪 TEST: Simulating payment success for order: " + orderId);

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
        paymentTransactionRepository.save(transaction);
        System.out.println("💾 TEST: Đã lưu payment transaction vào database: " + transaction.getId());

        // Tạo PaymentSuccessEvent
        PaymentSuccessEvent event = new PaymentSuccessEvent();
        event.setOrderId(orderId);
        event.setAmount(amount.doubleValue());
        event.setTransactionId(transId);
        event.setPaymentSource(paymentSource);

        // Gửi message vào topic "payment_success_topic"
        kafkaTemplate.send("payment_success_topic", event);
        System.out.println("✅ TEST: Đã gửi event thanh toán thành công: " + event);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Payment callback simulated successfully");
        response.put("orderId", orderId);
        response.put("transactionId", transId);

        return ResponseEntity.ok(response);
    }
}

