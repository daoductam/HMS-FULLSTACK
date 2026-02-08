package com.hms.appointment.Appointment.dto;


import com.hms.appointment.Appointment.entity.Appointment;
import com.hms.appointment.Appointment.entity.Prescription;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PrescriptionDTO {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long appointmentId;
    private LocalDate prescriptionDate;
    private String notes;
    private List<MedicineDTO> medicines;

    public Prescription toEntity() {
        return Prescription.builder()
                .id(id)
                .patientId(patientId)
                .doctorId(doctorId)
                .appointment(new Appointment(appointmentId))
                .prescriptionDate(prescriptionDate)
                .notes(notes)
                .build();
    }
}
