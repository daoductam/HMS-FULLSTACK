package com.hms.PharmacyMS.repository;

import com.hms.PharmacyMS.entity.Sale;
import com.hms.PharmacyMS.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
    List<SaleItem> findBySaleId(Long saleId);
    List<SaleItem> findByMedicineId(Long medicineId);
}
