package com.hms.NotificationMS.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class EmailService {

    @Inject
    Mailer mailer;

    public void sendAppointmentConfirmation(String to, String patientName, String doctorName, String time) {
        try {
            // Nội dung HTML chuyên nghiệp
            String htmlContent = String.format(
                    "<h1>Xin chào %s,</h1>" +
                            "<p>Lịch hẹn của bạn tại <b>Phòng Khám Thông Minh</b> đã được xác nhận thành công.</p>" +
                            "<h3>Chi tiết lịch hẹn:</h3>" +
                            "<ul>" +
                            "<li><b>Bác sĩ:</b> %s</li>" +
                            "<li><b>Thời gian:</b> %s</li>" +
                            "</ul>" +
                            "<p>Vui lòng đến trước 15 phút để làm thủ tục.</p>" +
                            "<br/>" +
                            "<p>Trân trọng,<br/>Đội ngũ HMS</p>",
                    patientName, doctorName, time
            );

            mailer.send(Mail.withHtml(to, "Xác nhận Đặt lịch khám - Phòng Khám Thông Minh", htmlContent));
            log.info("Email sent to {}", to);

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            // Có thể throw exception để Kafka retry nếu cần
        }
    }
}