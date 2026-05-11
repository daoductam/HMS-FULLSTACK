package com.hms.PharmacyMS.listener;

import com.hms.PharmacyMS.entity.Sale;
import com.hms.PharmacyMS.repository.SaleRepository;
import com.hms.hms_common.event.PaymentSuccessEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
@RequiredArgsConstructor
public class PaymentListener {

    private final SaleRepository saleRepository;

    @Incoming("payment-success")
    @Transactional
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        System.out.println("PharmacyMS nhận được sự kiện thanh toán: " + event);

        try {
            // Kiểm tra xem event có phải trả cho Pharmacy không
            if ("PHARMACY".equals(event.getPaymentSource())) {

                // Lấy SaleID từ orderId (format: "SALE-123")
                String orderId = event.getOrderId();
                Long saleId;
                
                if (orderId != null && orderId.startsWith("SALE-")) {
                    saleId = Long.parseLong(orderId.substring(5));
                } else if (orderId != null) {
                    // Nếu không có prefix, thử parse trực tiếp
                    saleId = Long.parseLong(orderId);
                } else {
                    System.err.println("❌ OrderID is null");
                    return;
                }

                // Cập nhật Database
                Sale sale = saleRepository.findByIdOptional(saleId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + saleId));

                if (!"PAID".equals(sale.getStatus())) {
                    sale.setStatus("PAID");
                    saleRepository.persist(sale);
                    System.out.println("✅ Đã cập nhật trạng thái PAID cho đơn hàng: " + saleId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi xử lý message Kafka: " + e.getMessage());
        }
    }
}