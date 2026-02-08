package com.hms.PaymentMS.service;

import com.google.gson.Gson;
import com.hms.PaymentMS.dto.MomoPaymentRequest;

import com.hms.PaymentMS.utils.MomoSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${momo.partner-code}")
    private String partnerCode;

    @Value("${momo.access-key}")
    private String accessKey;

    @Value("${momo.secret-key}")
    private String secretKey;

    @Value("${momo.endpoint}")
    private String momoEndpoint;

    @Value("${momo.ipn-url}")
    private String ipnUrl;

    @Value("${momo.redirect-url}")
    private String redirectUrl;

    @Override
    public String createMomoPayment(String orderId, Double amountDouble, String orderInfo) {
        RestTemplate restTemplate = new RestTemplate();

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

        // 4. Gửi Request sang MoMo
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MomoPaymentRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            // MoMo trả về JSON có chứa field "payUrl"
            String response = restTemplate.postForObject(momoEndpoint, entity, String.class);

            // Parse JSON để lấy payUrl (Dùng Gson hoặc Jackson)
            @SuppressWarnings("unchecked")
            Map<String, Object> map = new Gson().fromJson(response, Map.class);
            return map.get("payUrl").toString(); // Trả về link thanh toán
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi gọi MoMo API");
        }
    }
}