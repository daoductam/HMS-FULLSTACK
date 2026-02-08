package com.hms.appointment.Appointment.dto;

import com.hms.appointment.Appointment.entity.ApRecord;
import com.hms.appointment.Appointment.entity.Appointment;
import com.hms.appointment.Appointment.util.StringListConverter;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ApRecordDTO {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long appointmentId;
    private List<String> symptoms;
    private String diagnosis;
    private List<String> tests;
    private String notes;
    private String referral;
    private PrescriptionDTO prescription;
    private LocalDate followUpDate;
    private LocalDateTime createdAt;

    public ApRecord toEntity() {
        return ApRecord.builder()
                .id(id)
                .patientId(patientId)
                .doctorId(doctorId)
                .appointment(new Appointment(appointmentId))
                .symptoms(StringListConverter.convertListToString(symptoms))
                .diagnosis(diagnosis)
                .tests(StringListConverter.convertListToString(tests))
                .notes(notes)
                .referral(referral)
                .followUpDate(followUpDate)
                .createdAt(createdAt)
                .build();
    }
}
