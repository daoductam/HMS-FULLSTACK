package com.hms.PharmacyMS.api;

import com.hms.PharmacyMS.dto.MedicineDTO;
import com.hms.PharmacyMS.dto.MedicineInventoryDTO;
import com.hms.PharmacyMS.dto.ResponseDTO;
import com.hms.PharmacyMS.service.MedicineInventoryService;
import com.hms.PharmacyMS.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/pharmacy/inventory")
@RequiredArgsConstructor
public class MedicineInventoryAPI {
    private final MedicineInventoryService medicineInventoryService;

    @PostMapping("/add")
    public ResponseEntity<MedicineInventoryDTO> addMedicine(@RequestBody MedicineInventoryDTO medicineDTO) {
        return  new ResponseEntity<>(
                medicineInventoryService.addMedicine(medicineDTO), HttpStatus.CREATED
        );
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<MedicineInventoryDTO> getMedicineById(@PathVariable Long id) {
        return  new ResponseEntity<>(
                medicineInventoryService.getMedicineById(id), HttpStatus.OK
        );
    }

    @PutMapping("/update")
    public ResponseEntity<MedicineInventoryDTO> updateMedicine(@RequestBody MedicineInventoryDTO medicineDTO) {

        return new ResponseEntity<>(medicineInventoryService.updateMedicine(medicineDTO),HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<MedicineInventoryDTO>> getAllMedicines() {
        return  new ResponseEntity<>(
                medicineInventoryService.getAllMedicines(), HttpStatus.OK
        );
    }
}
