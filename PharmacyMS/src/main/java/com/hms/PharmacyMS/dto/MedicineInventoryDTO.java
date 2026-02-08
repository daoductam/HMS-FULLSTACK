package com.hms.PharmacyMS.dto;

import com.hms.PharmacyMS.entity.Medicine;
import com.hms.PharmacyMS.entity.MedicineInventory;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class MedicineInventoryDTO {
    private Long id;
    private Long medicineId;
    private String batchNo;
    private Integer quantity;
    private LocalDate expiryDate;
    private LocalDate addedTime;
    private Integer initialQuantity;
    private StockStatus status;

    public MedicineInventory toEntity() {
        return MedicineInventory.builder()
                .id(id)
                .medicine(new Medicine(medicineId))
                .batchNo(batchNo)
                .quantity(quantity)
                .expiryDate(expiryDate)
                .addedTime(addedTime)
                .initialQuantity(initialQuantity)
                .status(status).build();
    }
}
