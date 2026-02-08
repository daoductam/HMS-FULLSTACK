package com.hms.PharmacyMS.entity;

import com.hms.PharmacyMS.dto.SaleItemDTO;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Getter
@Setter
public class SaleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;
    private String batchNo;
    private Integer quantity;
    private Double unitPrice;

    public SaleItemDTO toDTO() {
        return SaleItemDTO.builder()
                .id(id)
                .saleId(sale != null ? sale.getId() : null)
                .medicineId(medicine.getId())
                .batchNo(batchNo)
                .quantity(quantity)
                .unitPrice(unitPrice).build();
    }

}
