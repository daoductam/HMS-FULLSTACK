package com.hms.PharmacyMS.repository;

import com.hms.PharmacyMS.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    Optional<Medicine> findByNameIgnoreCaseAndDosageIgnoreCase(String name, String dosage);
    Optional<Long> findStockById(Long id);
}
