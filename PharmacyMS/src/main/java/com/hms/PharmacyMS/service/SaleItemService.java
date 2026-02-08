package com.hms.PharmacyMS.service;

import com.hms.PharmacyMS.dto.SaleItemDTO;
import com.hms.PharmacyMS.entity.SaleItem;

import java.util.List;

public interface SaleItemService {
    Long createSaleItem(SaleItemDTO saleItemDTO);
    void createSaleItems(Long saleId, List<SaleItemDTO>  saleItemDTOS);
    void createMultipleSaleItem(Long saleId, Long medicineId,List<SaleItemDTO> saleItemDTOs);
    void updateSaleItem(SaleItemDTO saleItemDTO) ;
    List<SaleItemDTO> getSaleItemBySaleId(Long saleId);
    SaleItemDTO getSaleItem(Long id);

}
