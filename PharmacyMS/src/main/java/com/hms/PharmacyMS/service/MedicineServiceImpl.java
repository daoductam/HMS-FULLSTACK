package com.hms.PharmacyMS.service;

import com.hms.PharmacyMS.dto.MedicineDTO;
import com.hms.PharmacyMS.entity.Medicine;
import com.hms.PharmacyMS.exception.ErrorCode;
import com.hms.PharmacyMS.exception.HmsException;
import com.hms.PharmacyMS.repository.MedicineRepository;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@Transactional
public class MedicineServiceImpl implements MedicineService {
    private final MedicineRepository medicineRepository;

    @Override
    @CacheInvalidateAll(cacheName = "medicines-list")
    public Long addMedicine(MedicineDTO medicineDTO) {
        Optional<Medicine> optional = medicineRepository
                .findByNameIgnoreCaseAndDosageIgnoreCase(medicineDTO.getName(), medicineDTO.getDosage());
        if (optional.isPresent()) {
            throw new HmsException(ErrorCode.MEDICINE_ALREADY_EXISTS);
        }

        medicineDTO.setStock(0);
        medicineDTO.setCreatedAt(LocalDateTime.now());
        Medicine medicine = medicineDTO.toEntity();
        medicineRepository.persist(medicine);
        return medicine.getId();
    }

    @Override
    @CacheResult(cacheName = "medicine-item")
    public MedicineDTO getMedicineById(Long id) {
        return medicineRepository.findByIdOptional(id)
                .orElseThrow(() -> new HmsException(ErrorCode.MEDICINE_NOT_FOUND)).toDTO();
    }

    @Override
    @CacheInvalidateAll(cacheName = "medicines-list")
    @CacheInvalidate(cacheName = "medicine-item")
    public void updateMedicine(MedicineDTO medicineDTO) {
        Medicine existingMedicine = medicineRepository.findByIdOptional(medicineDTO.getId())
                        .orElseThrow(() -> new HmsException(ErrorCode.MEDICINE_NOT_FOUND));
        
        if (!(medicineDTO.getName().equalsIgnoreCase(existingMedicine.getName())
                && medicineDTO.getDosage().equalsIgnoreCase(existingMedicine.getDosage()))) {
            Optional<Medicine> optional = medicineRepository
                    .findByNameIgnoreCaseAndDosageIgnoreCase(medicineDTO.getName(), medicineDTO.getDosage());
            if (optional.isPresent()) {
                throw new HmsException(ErrorCode.MEDICINE_ALREADY_EXISTS);
            }
        }
        
        existingMedicine.setName(medicineDTO.getName());
        existingMedicine.setDosage(medicineDTO.getDosage());
        existingMedicine.setCategory(medicineDTO.getCategory());
        existingMedicine.setType(medicineDTO.getType());
        existingMedicine.setManufacturer(medicineDTO.getManufacturer());
        existingMedicine.setUnitPrice(medicineDTO.getUnitPrice());
        existingMedicine.setCreatedAt(medicineDTO.getCreatedAt());
        // Panache manages entities, persist is for new ones, but for updates just being in a transaction is enough.
        // We call persist to be explicit or if it was detached.
        medicineRepository.persist(existingMedicine);
    }

    @Override
    @CacheResult(cacheName = "medicines-list")
    public List<MedicineDTO> getAllMedicines() {
        return medicineRepository.listAll().stream()
                .map(Medicine::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Integer getStockById(Long id) {
        return Math.toIntExact(medicineRepository.findStockById(id)
                .orElseThrow(() -> new HmsException(ErrorCode.MEDICINE_NOT_FOUND)));
    }

    @Override
    @CacheInvalidate(cacheName = "medicine-item")
    public Integer addStock(Long id, Integer quantity) {
        Medicine medicine = medicineRepository.findByIdOptional(id)
                .orElseThrow(() -> new HmsException(ErrorCode.MEDICINE_NOT_FOUND));
        medicine.setStock(medicine.getStock() != null ? medicine.getStock() + quantity : quantity);
        medicineRepository.persist(medicine);
        return medicine.getStock();
    }

    @Override
    @CacheInvalidate(cacheName = "medicine-item")
    public Integer removeStock(Long id, Integer quantity) {
        Medicine medicine = medicineRepository.findByIdOptional(id)
                .orElseThrow(() -> new HmsException(ErrorCode.MEDICINE_NOT_FOUND));
        medicine.setStock(medicine.getStock() != null ? medicine.getStock() - quantity : quantity);
        medicineRepository.persist(medicine);
        return medicine.getStock();
    }
}

