package com.hms.appointment.Appointment.entity;

import com.hms.appointment.Appointment.dto.ApRecordDTO;
import com.hms.appointment.Appointment.dto.RecordDetails;
import com.hms.appointment.Appointment.util.StringListConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class ApRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long patientId;
    private Long doctorId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
    private String symptoms;
    private String diagnosis;
    private String tests;
    private String notes;
    private String referral;
    private LocalDate followUpDate;
    private LocalDateTime createdAt;

    public ApRecordDTO toDTO() {
        return ApRecordDTO.builder()
                .id(id)
                .patientId(patientId)
                .doctorId(doctorId)
                .appointmentId(appointment.getId())
                .symptoms(StringListConverter.convertStringToList(symptoms))
                .tests(StringListConverter.convertStringToList(tests))
                .diagnosis(diagnosis)
                .notes(notes)
                .referral(referral)
                .prescription(null)
                .followUpDate(followUpDate)
                .createdAt(createdAt)
                .build();
    }

    public RecordDetails toRecordDetails() {
        return RecordDetails.builder()
                .id(id)
                .patientId(patientId)
                .doctorId(doctorId)
                .doctorName(null)
                .appointmentId(appointment.getId())
                .symptoms(StringListConverter.convertStringToList(symptoms))
                .diagnosis(diagnosis)
                .tests(StringListConverter.convertStringToList(tests))
                .notes(notes)
                .referral(referral)
                .followUpDate(followUpDate)
                .createdAt(createdAt)
                .build();
    }
}
