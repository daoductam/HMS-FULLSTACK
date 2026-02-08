package com.hms.appointment.Appointment.entity;

import com.hms.appointment.Appointment.dto.MedicineDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long medicineId;
    private String dosage;
    private String frequency;
    private Integer duration;
    private String route; // oral intravenous
    private String type; // tablet, syryp
    private String instructions;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    public MedicineDTO toDTO() {
        return MedicineDTO.builder()
                .id(id)
                .name(name)
                .medicineId(medicineId)
                .dosage(dosage)
                .frequency(frequency)
                .duration(duration)
                .route(route)
                .type(type)
                .instructions(instructions)
                .prescriptionId(prescription.getId())
                .build();
    }



}
