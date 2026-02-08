package com.hms.NotificationMS.controller;


import com.hms.NotificationMS.service.EmailService;
import com.hms.hms_common.event.AppointmentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final EmailService emailService;

    @KafkaListener(topics = "notification-appointment", groupId = "notification-group")
    public void handleAppointmentNotification(AppointmentEvent event) {
        System.out.println("Received Kafka Event: " + event);

        // Gọi service gửi mail
        emailService.sendAppointmentConfirmation(
                event.getPatientEmail(),
                event.getPatientName(),
                event.getDoctorName(),
                event.getAppointmentTime().toString()
        );
    }
}