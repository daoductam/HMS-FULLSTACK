package com.hms.PaymentMS.service;

public interface PaymentService {
    String createMomoPayment(String orderId, Double amountDouble, String orderInfo);

}
