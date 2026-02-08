package com.hms.PharmacyMS.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentLinkResponse {

    private String provider;   // momo / vnpay / chuyenkhoan
    private String orderId;    // orderId đã gắn timestamp: "45_169..."
    private BigDecimal amount;

    // tùy provider, một trong các trường dưới có giá trị:
    private String payUrl;     // Momo / VNPay redirect URL
    private String qrCodeUrl;  // VietQR image url/base64
    private String deeplink;   // nếu sau này dùng deeplink app
}
