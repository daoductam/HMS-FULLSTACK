package com.hms.appointment.Appointment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AppointmentDetails {
    Long id;
    Long patientId;
    String patientName;
    String patientEmail;
    String patientPhone;
    Long doctorId;
    String doctorName;
    LocalDateTime appointmentTime;
    Status status;
    String reason;
    String notes;

}
