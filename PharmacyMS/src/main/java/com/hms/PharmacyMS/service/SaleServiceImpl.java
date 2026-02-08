package com.hms.PharmacyMS.service;

import com.hms.PharmacyMS.dto.SaleDTO;
import com.hms.PharmacyMS.dto.SaleItemDTO;
import com.hms.PharmacyMS.dto.SaleRequest;
import com.hms.PharmacyMS.entity.Sale;
import com.hms.PharmacyMS.exception.ErrorCode;
import com.hms.PharmacyMS.exception.HmsException;
import com.hms.PharmacyMS.repository.SaleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService{
    private final SaleRepository saleRepository;
    private final SaleItemService saleItemService;
    private final MedicineInventoryService medicineInventoryService;

    @Override
    @Transactional
    public Long createSale(SaleRequest dto) {
        if (dto.getPrescriptionId()!=null && saleRepository.existsByPrescriptionId(dto.getPrescriptionId())) {
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
        sale = saleRepository.save(sale);
        saleItemService.createSaleItems(sale.getId(), dto.getSaleItems());
        return sale.getId();
    }

    @Override
    public void updateSale(SaleDTO dto) {
        Sale sale =
                saleRepository.findById(dto.getId())
                        .orElseThrow(() -> new HmsException(ErrorCode.SALE_NOT_FOUND));
        sale.setSaleDate(dto.getSaleDate());
        sale.setTotalAmount(dto.getTotalAmount());
        saleRepository.save(sale);
    }

    @Override
    public SaleDTO getSale(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new HmsException(ErrorCode.SALE_NOT_FOUND)).toDTO();
    }

    @Override
    public SaleDTO getSaleByPrescriptionId(Long prescriptionId) {
        return saleRepository.findByPrescriptionId(prescriptionId)
                .orElseThrow(() -> new HmsException(ErrorCode.SALE_NOT_FOUND)).toDTO();

    }

    @Override
    public List<SaleDTO> getAllSales() {
        return saleRepository.findAll().stream().map(Sale::toDTO).toList();
    }
}
