package com.hms.PharmacyMS.entity;

import com.hms.PharmacyMS.dto.MedicineInventoryDTO;
import com.hms.PharmacyMS.dto.StockStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Getter
@Setter
public class MedicineInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;
    private String batchNo;
    private Integer quantity;
    private LocalDate expiryDate;
    private LocalDate addedTime;
    private Integer initialQuantity;
    @Enumerated(EnumType.STRING)
    private StockStatus status;

    public MedicineInventoryDTO toDTO() {
        return MedicineInventoryDTO.builder()
                .id(id)
                .medicineId(medicine != null ? medicine.getId() : null)
                .batchNo(batchNo)
                .quantity(quantity)
                .expiryDate(expiryDate)
                .addedTime(addedTime)
                .initialQuantity(initialQuantity)
                .status(status).build();
    }
}
