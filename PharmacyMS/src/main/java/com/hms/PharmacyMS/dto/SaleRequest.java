package com.hms.PharmacyMS.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SaleRequest {
    private Long prescriptionId;
    private String buyerName;
    private String buyerContact;
    private Double totalAmount;
    private List<SaleItemDTO> saleItems;
    private String paymentMethod; // "DIRECT" hoặc "MOMO"

}
