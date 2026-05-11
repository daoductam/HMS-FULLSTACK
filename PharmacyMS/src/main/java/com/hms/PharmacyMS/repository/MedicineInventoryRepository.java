package com.hms.PharmacyMS.repository;

import com.hms.PharmacyMS.dto.StockStatus;
import com.hms.PharmacyMS.entity.MedicineInventory;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class MedicineInventoryRepository implements PanacheRepository<MedicineInventory> {
    public List<MedicineInventory> findByExpiryDateBefore(LocalDate date) {
        return list("expiryDate < ?1", date);
    }

    public List<MedicineInventory> findByMedicineIdAndExpiryDateAfterAndQuantityGreaterThanAndStatusOrderByExpiryDateAsc(
            Long medicineId,
            LocalDate date, Integer quantity, StockStatus status
    ) {
        return list("medicine.id = ?1 AND expiryDate > ?2 AND quantity > ?3 AND status = ?4 ORDER BY expiryDate ASC",
                medicineId, date, quantity, status);
    }
}

