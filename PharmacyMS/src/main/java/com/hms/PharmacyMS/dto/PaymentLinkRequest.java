package com.hms.PharmacyMS.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentLinkRequest {

    @NotBlank
    private String orderId;      // ví dụ: "45" (service sẽ thêm timestamp phía sau)

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotBlank
    private String orderInfo;

    @NotBlank
    private String providerName; // "momo" | "vnpay" | "chuyenkhoan"

    // có thể lấy từ request nếu client không gửi
    private String ipAddr;
}
