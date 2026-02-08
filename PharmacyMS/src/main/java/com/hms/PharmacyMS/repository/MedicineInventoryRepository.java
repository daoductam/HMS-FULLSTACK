package com.hms.PharmacyMS.repository;

import com.hms.PharmacyMS.dto.StockStatus;
import com.hms.PharmacyMS.entity.Medicine;
import com.hms.PharmacyMS.entity.MedicineInventory;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MedicineInventoryRepository extends JpaRepository<MedicineInventory, Long> {
    List<MedicineInventory> findByExpiryDateBefore(LocalDate date);

    List<MedicineInventory> findByMedicineIdAndExpiryDateAfterAndQuantityGreaterThanAndStatusOrderByExpiryDateAsc(
            Long medicineId,
            LocalDate date, Integer quantity, StockStatus status
    );
}
