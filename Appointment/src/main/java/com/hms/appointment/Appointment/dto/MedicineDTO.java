package com.hms.appointment.Appointment.dto;

import com.hms.appointment.Appointment.entity.Medicine;
import com.hms.appointment.Appointment.entity.Prescription;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class MedicineDTO {
    private Long id;
    private String name;
    private Long medicineId;
    private String dosage;
    private String frequency;
    private Integer duration;
    private String route;
    private String type;
    private String instructions;
    private Long prescriptionId;

    public Medicine toEntity() {
        return Medicine.builder()
                .id(id)
                .name(name)
                .medicineId(medicineId)
                .dosage(dosage)
                .frequency(frequency)
                .duration(duration)
                .route(route)
                .type(type)
                .instructions(instructions)
                .prescription(new Prescription(getPrescriptionId())).build();
    }
}
