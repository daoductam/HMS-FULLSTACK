package com.hms.PharmacyMS.dto;

import com.hms.PharmacyMS.entity.Medicine;
import com.hms.PharmacyMS.entity.MedicineCategory;
import com.hms.PharmacyMS.entity.MedicineType;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class MedicineDTO  implements Serializable {
    private static final long serialVersionUID = 1L; // 3. Thêm serialVersionUID (Good practice)
    private Long id;
    private String name;
    private String dosage;
    private MedicineCategory category;
    private MedicineType type;
    private String manufacturer;
    private Integer unitPrice;
    private Integer stock;
    private LocalDateTime createdAt;

    public Medicine toEntity() {
        return Medicine.builder()
                .id(id)
                .name(name)
                .dosage(dosage)
                .category(category)
                .type(type)
                .manufacturer(manufacturer)
                .unitPrice(unitPrice)
                .stock(stock)
                .createdAt(createdAt).build();
    }
}
