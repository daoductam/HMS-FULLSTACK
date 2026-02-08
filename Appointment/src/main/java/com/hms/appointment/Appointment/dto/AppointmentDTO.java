package com.hms.appointment.Appointment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hms.appointment.Appointment.entity.Appointment;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AppointmentDTO {
    Long id;
    Long patientId;
    Long doctorId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime appointmentTime;
    Status status;
    String reason;
    String notes;

    public Appointment toEntity() {
        return Appointment.builder()
                .id(this.id)
                .patientId(this.patientId)
                .doctorId(this.doctorId)
                .appointmentTime(appointmentTime)
                .status(status)
                .reason(reason)
                .notes(this.notes)
                .build();
    }
}
