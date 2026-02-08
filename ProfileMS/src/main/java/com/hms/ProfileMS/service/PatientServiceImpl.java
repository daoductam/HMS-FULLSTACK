package com.hms.ProfileMS.service;

import com.hms.ProfileMS.dto.DoctorDropdown;
import com.hms.ProfileMS.dto.PageResponse;
import com.hms.ProfileMS.dto.PatientDTO;
import com.hms.ProfileMS.entity.Patient;
import com.hms.ProfileMS.exception.ErrorCode;
import com.hms.ProfileMS.exception.HmsException;
import com.hms.ProfileMS.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    @CacheEvict(value = "patients_list", allEntries = true)
    public Long addPatient(PatientDTO patientDTO) {
        if (patientDTO.getEmail() != null && patientRepository.findByEmail(patientDTO.getEmail()).isPresent()) {
            throw new HmsException(ErrorCode.PATIENT_ALREADY_EXISTS);
        }
        if (patientDTO.getCCCD() != null && patientRepository.findByCCCD(patientDTO.getCCCD()).isPresent()) {
            throw new HmsException(ErrorCode.PATIENT_ALREADY_EXISTS);
        }
        return patientRepository.save(patientDTO.toEntity()).getId();
    }

    @Override
    @Cacheable(value = "patient_item", key = "#id")
    public PatientDTO getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new HmsException(ErrorCode.PATIENT_NOT_FOUND)).toDTO();
    }

    @Override
    @CachePut(value = "patient_item", key = "#patientDTO.id")
    @CacheEvict(value = "patients_list", allEntries = true)
    public PatientDTO updatePatient(PatientDTO patientDTO) {
        patientRepository.findById(patientDTO.getId())
                .orElseThrow(() -> new HmsException(ErrorCode.PATIENT_NOT_FOUND));
        return patientRepository.save(patientDTO.toEntity()).toDTO();
    }

    @Override
    public Boolean patientExists(Long id) {
        return patientRepository.existsById(id);
    }

    @Override
    @Cacheable(value = "patients_list")
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream().map(Patient::toDTO).toList();
    }

    @Override
    public PageResponse<PatientDTO> getAllPatientsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Patient> patientPage = patientRepository.findAll(pageable);
        
        List<PatientDTO> patientDTOs = patientPage.getContent().stream()
                .map(Patient::toDTO)
                .collect(Collectors.toList());
        
        return PageResponse.<PatientDTO>builder()
                .content(patientDTOs)
                .page(patientPage.getNumber())
                .size(patientPage.getSize())
                .totalElements(patientPage.getTotalElements())
                .totalPages(patientPage.getTotalPages())
                .first(patientPage.isFirst())
                .last(patientPage.isLast())
                .build();
    }

    @Override
    public List<DoctorDropdown> getPatientsById(List<Long> ids) {
        return patientRepository.findAllPatientDropdownsByIds(ids);
    }

    @Override
    public List<PatientDTO> findAllByIds(List<Long> ids) {
        return patientRepository.findAllById(ids)
                .stream()
                .map(Patient::toDTO)
                .collect(Collectors.toList());
    }
}
