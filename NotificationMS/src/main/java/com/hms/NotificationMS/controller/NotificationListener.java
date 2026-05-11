package com.hms.NotificationMS.controller;

import com.hms.NotificationMS.service.EmailService;
import com.hms.hms_common.event.AppointmentEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
@Slf4j
public class NotificationListener {

    @Inject
    EmailService emailService;

    @Incoming("appointment-in")
    public void handleAppointmentNotification(AppointmentEvent event) {
        log.info("Received Kafka Event: {}", event);

        // Gọi service gửi mail
        emailService.sendAppointmentConfirmation(
                event.getPatientEmail(),
                event.getPatientName(),
                event.getDoctorName(),
                event.getAppointmentTime().toString()
        );
    }
}