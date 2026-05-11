package com.hms.PharmacyMS.repository;

import com.hms.PharmacyMS.entity.SaleItem;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class SaleItemRepository implements PanacheRepository<SaleItem> {
    public List<SaleItem> findBySaleId(Long saleId) {
        return list("sale.id", saleId);
    }

    public List<SaleItem> findByMedicineId(Long medicineId) {
        return list("medicine.id", medicineId);
    }
}

