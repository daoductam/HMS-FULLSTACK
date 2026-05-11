package com.hms.PharmacyMS.service;

import com.hms.PharmacyMS.dto.SaleDTO;
import com.hms.PharmacyMS.dto.SaleItemDTO;
import com.hms.PharmacyMS.dto.SaleRequest;
import com.hms.PharmacyMS.entity.Sale;
import com.hms.PharmacyMS.exception.ErrorCode;
import com.hms.PharmacyMS.exception.HmsException;
import com.hms.PharmacyMS.repository.SaleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
@Transactional
public class SaleServiceImpl implements SaleService {
    private final SaleRepository saleRepository;
    private final SaleItemService saleItemService;
    private final MedicineInventoryService medicineInventoryService;

    @Override
    public Long createSale(SaleRequest dto) {
        if (dto.getPrescriptionId() != null && saleRepository.existsByPrescriptionId(dto.getPrescriptionId())) {
            throw new HmsException(ErrorCode.SALE_ALREADY_EXISTS);
        }
        for (SaleItemDTO saleItem : dto.getSaleItems()) {
            saleItem.setBatchNo(
                    medicineInventoryService.sellStock(saleItem.getMedicineId(),
                            saleItem.getQuantity()));
        }
        
        // Xác định status dựa trên paymentMethod
        String status = "DIRECT".equalsIgnoreCase(dto.getPaymentMethod()) ? "PAID" : "PENDING";
        
        Sale sale = Sale.builder()
                .id(null)
                .prescriptionId(dto.getPrescriptionId())
                .buyerName(dto.getBuyerName())
                .buyerContact(dto.getBuyerContact())
                .saleDate(LocalDateTime.now())
                .totalAmount(dto.getTotalAmount())
                .status(status)
                .build();
        
        saleRepository.persist(sale);
        saleItemService.createSaleItems(sale.getId(), dto.getSaleItems());
        return sale.getId();
    }

    @Override
    public void updateSale(SaleDTO dto) {
        Sale sale = saleRepository.findByIdOptional(dto.getId())
                        .orElseThrow(() -> new HmsException(ErrorCode.SALE_NOT_FOUND));
        sale.setSaleDate(dto.getSaleDate());
        sale.setTotalAmount(dto.getTotalAmount());
        saleRepository.persist(sale);
    }

    @Override
    public SaleDTO getSale(Long id) {
        return saleRepository.findByIdOptional(id)
                .orElseThrow(() -> new HmsException(ErrorCode.SALE_NOT_FOUND)).toDTO();
    }

    @Override
    public SaleDTO getSaleByPrescriptionId(Long prescriptionId) {
        return saleRepository.findByPrescriptionId(prescriptionId)
                .orElseThrow(() -> new HmsException(ErrorCode.SALE_NOT_FOUND)).toDTO();
    }

    @Override
    public List<SaleDTO> getAllSales() {
        return saleRepository.listAll().stream().map(Sale::toDTO).toList();
    }
}

