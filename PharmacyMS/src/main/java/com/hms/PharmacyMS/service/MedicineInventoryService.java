package com.hms.PharmacyMS.service;

import com.hms.PharmacyMS.dto.MedicineInventoryDTO;
import com.hms.PharmacyMS.entity.MedicineInventory;

import java.util.List;

public interface MedicineInventoryService {
    List<MedicineInventoryDTO> getAllMedicines();
    MedicineInventoryDTO getMedicineById(Long id);
    MedicineInventoryDTO addMedicine(MedicineInventoryDTO medicine);
    MedicineInventoryDTO updateMedicine(MedicineInventoryDTO medicine);
    String sellStock(Long medicineId, Integer quantity);
    void deleteMedicine(Long id);
    void deleteExpiredMedicines();
}
