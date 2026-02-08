package com.hms.PharmacyMS.entity;

import com.hms.PharmacyMS.dto.MedicineDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Getter
@Setter
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String dosage;
    private MedicineCategory category;
    private MedicineType type;
    private String manufacturer;
    private Integer unitPrice;
    private Integer stock;
    private LocalDateTime createdAt;

    public Medicine(Long medicineId) {
        this.id = medicineId;
    }

    public MedicineDTO toDTO() {
        return MedicineDTO.builder()
                .id(id)
                .name(name)
                .dosage(dosage)
                .category(category)
                .type(type)
                .manufacturer(manufacturer)
                .unitPrice(unitPrice)
                .stock(stock)
                .createdAt(createdAt).build();
}}
