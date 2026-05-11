package com.hms.PaymentMS.service;

import com.hms.PaymentMS.clients.MomoClient;
import com.hms.PaymentMS.dto.MomoPaymentRequest;
import com.hms.PaymentMS.utils.MomoSecurity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class PaymentServiceImpl implements PaymentService {

    @ConfigProperty(name = "momo.partner-code")
    String partnerCode;

    @ConfigProperty(name = "momo.access-key")
    String accessKey;

    @ConfigProperty(name = "momo.secret-key")
    String secretKey;

    @ConfigProperty(name = "momo.ipn-url")
    String ipnUrl;

    @ConfigProperty(name = "momo.redirect-url")
    String redirectUrl;

    @Inject
    @RestClient
    MomoClient momoClient;

    @Override
    public String createMomoPayment(String orderId, Double amountDouble, String orderInfo) {
        // MoMo yêu cầu số tiền là Long (không thập phân) và String
        String amount = String.valueOf(amountDouble.longValue());
        String requestId = UUID.randomUUID().toString();
        String requestType = "captureWallet";
        String extraData = ""; // Có thể encode Base64 thông tin phụ nếu cần

        // 1. Tạo Raw Signature String (Đúng thứ tự a-z theo tài liệu MoMo)
        // format: accessKey=$accessKey&amount=$amount&extraData=$extraData&ipnUrl=$ipnUrl&orderId=$orderId&orderInfo=$orderInfo&partnerCode=$partnerCode&redirectUrl=$redirectUrl&requestId=$requestId&requestType=$requestType
        String rawSignature = "accessKey=" + accessKey
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&ipnUrl=" + ipnUrl
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + partnerCode
                + "&redirectUrl=" + redirectUrl
                + "&requestId=" + requestId
                + "&requestType=" + requestType;

        // 2. Tạo chữ ký
        String signature = MomoSecurity.signSHA256(rawSignature, secretKey);

        // 3. Tạo Request Body
        MomoPaymentRequest requestBody = MomoPaymentRequest.builder()
                .partnerCode(partnerCode)
                .requestId(requestId)
                .amount(amount)
                .orderId(orderId)
                .orderInfo(orderInfo)
                .redirectUrl(redirectUrl)
                .ipnUrl(ipnUrl)
                .requestType(requestType)
                .extraData(extraData)
                .lang("vi")
                .signature(signature)
                .build();

        // 4. Gửi Request sang MoMo qua RestClient
        try {
            Map<String, Object> response = momoClient.createPayment(requestBody);
            return response.get("payUrl").toString(); // Trả về link thanh toán
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi gọi MoMo API: " + e.getMessage());
        }
    }
}