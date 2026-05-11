package com.hms.PharmacyMS.repository;

import com.hms.PharmacyMS.entity.Sale;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class SaleRepository implements PanacheRepository<Sale> {
    public boolean existsByPrescriptionId(Long prescriptionId) {
        return count("prescriptionId", prescriptionId) > 0;
    }

    public Optional<Sale> findByPrescriptionId(Long prescriptionId) {
        return find("prescriptionId", prescriptionId).firstResultOptional();
    }
}

