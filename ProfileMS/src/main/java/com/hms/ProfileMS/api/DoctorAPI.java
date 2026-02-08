package com.hms.ProfileMS.api;

import com.hms.ProfileMS.dto.DoctorDTO;
import com.hms.ProfileMS.dto.DoctorDropdown;
import com.hms.ProfileMS.dto.PageResponse;
import com.hms.ProfileMS.dto.PatientDTO;
import com.hms.ProfileMS.service.DoctorService;
import com.hms.ProfileMS.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profile/doctor")
    @CrossOrigin
@Validated
@RequiredArgsConstructor
public class DoctorAPI {

    private final DoctorService doctorService;

    @PostMapping("/add")
    public ResponseEntity<Long> addUser(@RequestBody DoctorDTO doctorDTO)  {
        return new ResponseEntity<>(doctorService.addDoctor(doctorDTO),
                HttpStatus.CREATED);

    }

    @GetMapping("/get/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Long id){
        return new ResponseEntity<>(doctorService.getDoctorById(id), HttpStatus.OK);

    }

    @GetMapping("/getProfileId/{id}")
    public ResponseEntity<Long> getProfileId(@PathVariable Long id){
        return new ResponseEntity<>(doctorService.getDoctorById(id).getProfilePictureId(), HttpStatus.OK);

    }

    @PutMapping("/update")
    public ResponseEntity<DoctorDTO> updateDoctor(@RequestBody DoctorDTO doctorDTO){
        return new ResponseEntity<>(doctorService.updateDoctor(doctorDTO), HttpStatus.OK);

    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> doctorExists(@PathVariable Long id){
        return new ResponseEntity<>(doctorService.doctorExists(id), HttpStatus.OK);

    }

    @GetMapping("/dropdowns")
    public ResponseEntity<List<DoctorDropdown>> getDoctorDropdowns() {
        return new ResponseEntity<>(doctorService.getDoctorDropdowns(),
                HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        return new ResponseEntity<>(doctorService.getAllDoctors(),
                HttpStatus.OK);
    }

    @GetMapping("/getAllPaginated")
    public ResponseEntity<PageResponse<DoctorDTO>> getAllDoctorsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(doctorService.getAllDoctorsPaginated(page, size),
                HttpStatus.OK);
    }

    @GetMapping("/getDoctorsById")
    public ResponseEntity<List<DoctorDropdown>> getDoctorsById(@RequestParam List<Long> ids) {
        return new ResponseEntity<>(doctorService.getDoctorsById(ids),
                HttpStatus.OK);
    }


}
