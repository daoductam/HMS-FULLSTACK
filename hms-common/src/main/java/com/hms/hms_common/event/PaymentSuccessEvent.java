package com.hms.hms_common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSuccessEvent {
    private String orderId;       // ID đơn hàng/lịch hẹn
    private Double amount;
    private String transactionId; // Mã giao dịch Momo
    private String paymentSource; // "PHARMACY" hoặc "APPOINTMENT"
}