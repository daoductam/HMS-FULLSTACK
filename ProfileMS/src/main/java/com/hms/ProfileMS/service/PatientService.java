package com.hms.ProfileMS.service;

import com.hms.ProfileMS.dto.DoctorDropdown;
import com.hms.ProfileMS.dto.PageResponse;
import com.hms.ProfileMS.dto.PatientDTO;

import java.util.List;


public interface PatientService {
    Long addPatient(PatientDTO patientDTO) ;
    PatientDTO getPatientById(Long id) ;

    PatientDTO updatePatient(PatientDTO patientDTO);

    Boolean patientExists(Long id);

    List<PatientDTO> getAllPatients();
    
    PageResponse<PatientDTO> getAllPatientsPaginated(int page, int size);

    List<DoctorDropdown> getPatientsById(List<Long> ids);

    List<PatientDTO> findAllByIds(List<Long> ids);

}

