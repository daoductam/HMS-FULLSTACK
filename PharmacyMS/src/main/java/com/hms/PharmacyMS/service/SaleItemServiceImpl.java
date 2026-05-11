package com.hms.PharmacyMS.service;

import com.hms.PharmacyMS.dto.SaleItemDTO;
import com.hms.PharmacyMS.entity.SaleItem;
import com.hms.PharmacyMS.exception.ErrorCode;
import com.hms.PharmacyMS.exception.HmsException;
import com.hms.PharmacyMS.repository.SaleItemRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
@Transactional
public class SaleItemServiceImpl implements SaleItemService {

    private final SaleItemRepository saleItemRepository;

    @Override
    public Long createSaleItem(SaleItemDTO saleItemDTO) {
        SaleItem entity = saleItemDTO.toEntity();
        saleItemRepository.persist(entity);
        return entity.getId();
    }

    @Override
    public void createSaleItems(Long saleId, List<SaleItemDTO> saleItemDTOS) {
        saleItemDTOS.stream().map(x -> {
            x.setSaleId(saleId);
            return x.toEntity();
        }).forEach(saleItemRepository::persist);
    }

    @Override
    public void createMultipleSaleItem(Long saleId, Long medicineId, List<SaleItemDTO> saleItemDTOs) {
        saleItemDTOs.stream().map(x -> {
            x.setSaleId(saleId);
            x.setMedicineId(medicineId);
            return x.toEntity();
        }).forEach(saleItemRepository::persist);
    }

    @Override
    public void updateSaleItem(SaleItemDTO saleItemDTO) {
        SaleItem existingSaleItem = saleItemRepository.findByIdOptional(saleItemDTO.getId())
                        .orElseThrow(() -> new HmsException(ErrorCode.SALE_ITEM_NOT_FOUND));
        existingSaleItem.setQuantity(saleItemDTO.getQuantity());
        existingSaleItem.setUnitPrice(saleItemDTO.getUnitPrice());
        saleItemRepository.persist(existingSaleItem);
    }

    @Override
    public List<SaleItemDTO> getSaleItemBySaleId(Long saleId) {
        return saleItemRepository.findBySaleId(saleId).stream()
                .map(SaleItem::toDTO)
                .toList();
    }

    @Override
    public SaleItemDTO getSaleItem(Long id) {
        return saleItemRepository.findByIdOptional(id)
                .map(SaleItem::toDTO)
                .orElseThrow(() -> new HmsException(ErrorCode.SALE_ITEM_NOT_FOUND));
    }
}

