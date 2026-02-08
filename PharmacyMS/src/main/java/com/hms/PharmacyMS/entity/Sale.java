package com.hms.PharmacyMS.entity;

import com.hms.PharmacyMS.dto.SaleDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Getter
@Setter
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long prescriptionId;
    private String buyerName;
    private String buyerContact;
    private LocalDateTime saleDate;
    private Double totalAmount;
    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    private String status;

    public Sale(Long id) {
        this.id = id;
    }

    public SaleDTO toDTO() {
        return SaleDTO.builder()
                .id(id)
                .prescriptionId(prescriptionId)
                .buyerName(buyerName)
                .buyerContact(buyerContact)
                .saleDate(saleDate)
                .status(status)
                .totalAmount(totalAmount).build();
    }
}
