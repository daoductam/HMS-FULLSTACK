package com.hms.PharmacyMS.api;


import com.hms.PharmacyMS.dto.MedicineDTO;
import com.hms.PharmacyMS.dto.ResponseDTO;
import com.hms.PharmacyMS.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/pharmacy/medicines")
@RequiredArgsConstructor
public class MedicineAPI {
    private final MedicineService medicineService;

    @PostMapping("/add")
    public ResponseEntity<Long> addMedicine(@RequestBody MedicineDTO medicineDTO) {
        return  new ResponseEntity<>(
                medicineService.addMedicine(medicineDTO), HttpStatus.CREATED
        );
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<MedicineDTO> getMedicineById(@PathVariable Long id) {
        return  new ResponseEntity<>(
                medicineService.getMedicineById(id), HttpStatus.OK
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDTO> updateMedicine(@RequestBody MedicineDTO medicineDTO) {
        medicineService.updateMedicine(medicineDTO);
        return new ResponseEntity<>(new ResponseDTO("Medicine Updated"),HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<MedicineDTO>> getAllMedicines() {
        return  new ResponseEntity<>(
                medicineService.getAllMedicines(), HttpStatus.OK
        );
    }
}
