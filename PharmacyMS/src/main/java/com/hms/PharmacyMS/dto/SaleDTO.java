package com.hms.PharmacyMS.dto;


import com.hms.PharmacyMS.entity.Sale;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SaleDTO {
    private Long id;
    private Long prescriptionId;
    private String buyerName;
    private String buyerContact;
    private LocalDateTime saleDate;
    private Double totalAmount;
    private String status;

    public Sale toEntity() {
        return Sale.builder()
                .id(id)
                .prescriptionId(prescriptionId)
                .buyerName(buyerName)
                .buyerContact(buyerContact)
                .saleDate(saleDate)
                .status(status)
                .totalAmount(totalAmount).build();
    }

}
