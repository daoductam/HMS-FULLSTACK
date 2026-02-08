package com.hms.ProfileMS.service;

import com.hms.ProfileMS.dto.DoctorDTO;
import com.hms.ProfileMS.dto.DoctorDropdown;
import com.hms.ProfileMS.dto.PageResponse;
import com.hms.ProfileMS.dto.PatientDTO;

import java.util.List;


public interface DoctorService {
    Long addDoctor(DoctorDTO doctorDTO) ;
    DoctorDTO getDoctorById(Long id) ;

    DoctorDTO updateDoctor(DoctorDTO doctorDTO);

    Boolean doctorExists(Long id);

    List<DoctorDropdown> getDoctorDropdowns();
    List<DoctorDTO> getAllDoctors();
    
    PageResponse<DoctorDTO> getAllDoctorsPaginated(int page, int size);

    List<DoctorDropdown> getDoctorsById(List<Long> ids);
}
