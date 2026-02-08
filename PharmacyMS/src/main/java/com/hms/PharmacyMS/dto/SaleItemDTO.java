package com.hms.PharmacyMS.dto;

import com.hms.PharmacyMS.entity.Medicine;
import com.hms.PharmacyMS.entity.Sale;
import com.hms.PharmacyMS.entity.SaleItem;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SaleItemDTO {
    private Long id;
    private Long saleId;
    private Long medicineId;
    private String batchNo;
    private Integer quantity;
    private Double unitPrice;

    public SaleItem toEntity() {
        return SaleItem.builder()
                .id(id)
                .sale(new Sale(saleId))
                .medicine(new Medicine(medicineId))
                .batchNo(batchNo)
                .quantity(quantity)
                .unitPrice(unitPrice).build();
    }
}
