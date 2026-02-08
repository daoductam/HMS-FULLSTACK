package com.hms.appointment.Appointment.entity;


import com.hms.appointment.Appointment.dto.AppointmentDTO;
import com.hms.appointment.Appointment.dto.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long patientId;
    Long doctorId;
    LocalDateTime appointmentTime;
    Status status;
    String reason;
    String notes;

    public Appointment(Long appointmentId) {
        this.id = appointmentId;
    }

    public AppointmentDTO toDTO() {
        return AppointmentDTO.builder()
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
