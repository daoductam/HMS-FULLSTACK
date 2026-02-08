package com.hms.NotificationMS.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendAppointmentConfirmation(String to, String patientName, String doctorName, String time) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            helper.setTo(to);
            helper.setSubject("Xác nhận Đặt lịch khám - Phòng Khám Thông Minh");

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

            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("Email sent to " + to);

        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            // Có thể throw exception để Kafka retry nếu cần
        }
    }
}