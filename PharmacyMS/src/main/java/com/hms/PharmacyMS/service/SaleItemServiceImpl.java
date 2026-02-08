package com.hms.PharmacyMS.service;

import com.hms.PharmacyMS.dto.SaleItemDTO;
import com.hms.PharmacyMS.entity.SaleItem;
import com.hms.PharmacyMS.exception.ErrorCode;
import com.hms.PharmacyMS.exception.HmsException;
import com.hms.PharmacyMS.repository.SaleItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleItemServiceImpl implements SaleItemService {

    private final SaleItemRepository saleItemRepository;
    private final MedicineInventoryService medicineInventoryService;
    @Override
    public Long createSaleItem(SaleItemDTO saleItemDTO) {
        return saleItemRepository.save(saleItemDTO.toEntity()).getId();
    }

    @Override
    public void createSaleItems(Long saleId, List<SaleItemDTO> saleItemDTOS) {
        
        saleItemDTOS.stream().map((x) -> {
            x.setSaleId(saleId);
            return x.toEntity();
        }).forEach(saleItemRepository::save);
    }

    @Override
    public void createMultipleSaleItem(Long saleId, Long medicineId, List<SaleItemDTO> saleItemDTOs) {
        saleItemDTOs.stream().map((x) -> {
            x.setSaleId(saleId);
            x.setMedicineId(medicineId);
            return x.toEntity();
        }).forEach(saleItemRepository::save);
    }

    @Override
    public void updateSaleItem(SaleItemDTO saleItemDTO) {
        SaleItem existingSaleItem =
                saleItemRepository.findById(saleItemDTO.getId())
                        .orElseThrow(() -> new HmsException(ErrorCode.SALE_ITEM_NOT_FOUND));
        existingSaleItem.setQuantity(saleItemDTO.getQuantity());
        existingSaleItem.setUnitPrice(saleItemDTO.getUnitPrice());
        saleItemRepository.save(existingSaleItem);
    }

    @Override
    public List<SaleItemDTO> getSaleItemBySaleId(Long saleId) {
        return saleItemRepository.findBySaleId(saleId).stream()
                .map(SaleItem::toDTO)
                .toList();
    }

    @Override
    public SaleItemDTO getSaleItem(Long id) {
        return saleItemRepository.findById(id)
                .map(SaleItem::toDTO)
                .orElseThrow(() -> new HmsException(ErrorCode.SALE_ITEM_NOT_FOUND));
    }
}
