package com.hms.PharmacyMS.repository;

import com.hms.PharmacyMS.entity.Medicine;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class MedicineRepository implements PanacheRepository<Medicine> {
    public Optional<Medicine> findByNameIgnoreCaseAndDosageIgnoreCase(String name, String dosage) {
        return find("LOWER(name) = LOWER(?1) AND LOWER(dosage) = LOWER(?2)", name, dosage).firstResultOptional();
    }

    public Optional<Long> findStockById(Long id) {
        return find("SELECT m.stock FROM Medicine m WHERE m.id = ?1", id).project(Long.class).firstResultOptional();
    }
}

