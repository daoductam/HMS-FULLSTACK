package com.hms.PharmacyMS.service;

import com.hms.PharmacyMS.dto.SaleDTO;
import com.hms.PharmacyMS.dto.SaleRequest;

import java.util.List;

public interface SaleService {
    Long createSale(SaleRequest dto);
    void updateSale(SaleDTO dto);
    SaleDTO getSale(Long id);
    SaleDTO getSaleByPrescriptionId(Long prescriptionId)    ;
    List<SaleDTO> getAllSales();
}
