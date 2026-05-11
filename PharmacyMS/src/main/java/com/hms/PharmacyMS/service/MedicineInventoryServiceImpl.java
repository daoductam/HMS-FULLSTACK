package com.hms.PharmacyMS.service;

import com.hms.PharmacyMS.dto.MedicineInventoryDTO;
import com.hms.PharmacyMS.dto.StockStatus;
import com.hms.PharmacyMS.entity.MedicineInventory;
import com.hms.PharmacyMS.exception.ErrorCode;
import com.hms.PharmacyMS.exception.HmsException;
import com.hms.PharmacyMS.repository.MedicineInventoryRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
@Transactional
public class MedicineInventoryServiceImpl implements MedicineInventoryService {
    private final MedicineInventoryRepository medicineInventoryRepository;
    private final MedicineService medicineService;

    @Override
    public List<MedicineInventoryDTO> getAllMedicines() {
        return medicineInventoryRepository.listAll().stream()
                .map(MedicineInventory::toDTO)
                .toList();
    }

    @Override
    public MedicineInventoryDTO getMedicineById(Long id) {
        return medicineInventoryRepository.findByIdOptional(id)
                .orElseThrow(() -> new HmsException(ErrorCode.INVENTORY_NOT_FOUND)).toDTO();
    }

    @Override
    public MedicineInventoryDTO addMedicine(MedicineInventoryDTO medicine) {
        medicine.setAddedTime(LocalDate.now());
        medicineService.addStock(medicine.getMedicineId(), medicine.getQuantity());
        medicine.setInitialQuantity(medicine.getQuantity());
        medicine.setStatus(StockStatus.ACTIVE);
        MedicineInventory entity = medicine.toEntity();
        medicineInventoryRepository.persist(entity);
        return entity.toDTO();
    }

    @Override
    public MedicineInventoryDTO updateMedicine(MedicineInventoryDTO medicine) {
        MedicineInventory existingInventory = medicineInventoryRepository.findByIdOptional(medicine.getId())
                        .orElseThrow(() -> new HmsException(ErrorCode.INVENTORY_NOT_FOUND));

        existingInventory.setBatchNo(medicine.getBatchNo());
        if (existingInventory.getInitialQuantity() < medicine.getQuantity()) {
            medicineService.addStock(medicine.getMedicineId(),
                    medicine.getQuantity() - existingInventory.getInitialQuantity());
        } else if (existingInventory.getInitialQuantity() > medicine.getQuantity()) {
            medicineService.removeStock(medicine.getMedicineId(),
                    existingInventory.getInitialQuantity() - medicine.getQuantity());
        }
        existingInventory.setQuantity(medicine.getQuantity());
        existingInventory.setInitialQuantity(medicine.getQuantity());
        existingInventory.setExpiryDate(medicine.getExpiryDate());
        medicineInventoryRepository.persist(existingInventory);
        return existingInventory.toDTO();
    }

    @Override
    public String sellStock(Long medicineId, Integer quantity) {
        List<MedicineInventory> inventories = medicineInventoryRepository
                .findByMedicineIdAndExpiryDateAfterAndQuantityGreaterThanAndStatusOrderByExpiryDateAsc(medicineId,
                        LocalDate.now(), 0, StockStatus.ACTIVE);
        if (inventories.isEmpty()) {
            throw new HmsException(ErrorCode.OUT_OF_STOCK);
        }

        StringBuilder batchDetails = new StringBuilder();
        int remainingQuantity = quantity;
        for (MedicineInventory inventory : inventories) {
            if (remainingQuantity <= 0) {
                break;
            }
            int availableQuantity = inventory.getQuantity();
            if (availableQuantity <= remainingQuantity) {
                batchDetails.append(String.format("Batch %s: %d units\n", inventory.getBatchNo(), availableQuantity));
                remainingQuantity -= availableQuantity;
                inventory.setQuantity(0);
                inventory.setStatus(StockStatus.EXPIRED);
            } else {
                batchDetails.append(String.format("Batch %s: %d units\n", inventory.getBatchNo(), remainingQuantity));
                inventory.setQuantity(availableQuantity - remainingQuantity);
                remainingQuantity = 0;
            }
            medicineInventoryRepository.persist(inventory);
        }
        
        if (remainingQuantity > 0) {
            throw new HmsException(ErrorCode.INSUFFICIENT_STOCK);
        }
        medicineService.removeStock(medicineId, quantity);
        return batchDetails.toString();
    }

    public void markExpired(List<MedicineInventory> inventories) {
        for (MedicineInventory inventory : inventories) {
            inventory.setStatus(StockStatus.EXPIRED);
            medicineInventoryRepository.persist(inventory);
        }
    }

    @Override
    public void deleteMedicine(Long id) {
        medicineInventoryRepository.deleteById(id);
    }

    @Override
    @Scheduled(cron = "30 43 14 * * ?")
    public void deleteExpiredMedicines() {
        System.out.println("Scheduled task running");
        List<MedicineInventory> expiredMedicines =
                medicineInventoryRepository.findByExpiryDateBefore(LocalDate.now());
        for (MedicineInventory medicine : expiredMedicines) {
            medicineService.removeStock(medicine.getMedicine().getId(),
                    medicine.getQuantity());
        }
        this.markExpired(expiredMedicines);
    }

    @Scheduled(cron = "0 30 14 * * ?")
    public void print() {
        System.out.println("Scheduled task running");
    }
}

