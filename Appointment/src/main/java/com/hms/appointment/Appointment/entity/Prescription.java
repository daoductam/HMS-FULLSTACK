package com.hms.appointment.Appointment.entity;

import com.hms.appointment.Appointment.dto.PrescriptionDTO;
import com.hms.appointment.Appointment.dto.PrescriptionDetails;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long patientId;
    private Long doctorId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
    private LocalDate prescriptionDate;
    private String notes;

    public Prescription(Long prescriptionId) {
        this.id = prescriptionId;
    }

    public PrescriptionDTO toDTO() {
        return PrescriptionDTO.builder()
                .id(id)
                .patientId(patientId)
                .doctorId(doctorId)
                .appointmentId(appointment.getId())
                .prescriptionDate(prescriptionDate)
                .notes(notes)
                .medicines(null)
                .build();
    }

    public PrescriptionDetails toDetails() {
        return PrescriptionDetails.builder()
                .id(id)
                .patientId(patientId)
                .doctorId(doctorId)
                .doctorName(null)
                .patientName(null)
                .appointmentId(appointment.getId())
                .prescriptionDate(prescriptionDate)
                .notes(notes)
                .medicines(null)
                .build();
    }
}
