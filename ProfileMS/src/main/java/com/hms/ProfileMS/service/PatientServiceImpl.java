package com.hms.ProfileMS.service;

import com.hms.ProfileMS.dto.DoctorDropdown;
import com.hms.ProfileMS.dto.PageResponse;
import com.hms.ProfileMS.dto.PatientDTO;
import com.hms.ProfileMS.entity.Patient;
import com.hms.ProfileMS.exception.ErrorCode;
import com.hms.ProfileMS.exception.HmsException;
import com.hms.ProfileMS.repository.PatientRepository;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "patients-list")
    public Long addPatient(PatientDTO patientDTO) {
        if (patientDTO.getEmail() != null && patientRepository.findByEmail(patientDTO.getEmail()).isPresent()) {
            throw new HmsException(ErrorCode.PATIENT_ALREADY_EXISTS);
        }
        if (patientDTO.getCCCD() != null && patientRepository.findByCCCD(patientDTO.getCCCD()).isPresent()) {
            throw new HmsException(ErrorCode.PATIENT_ALREADY_EXISTS);
        }
        Patient patient = patientDTO.toEntity();
        patientRepository.persist(patient);
        return patient.getId();
    }

    @Override
    @CacheResult(cacheName = "patient-item")
    public PatientDTO getPatientById(Long id) {
        return patientRepository.findByIdOptional(id)
                .orElseThrow(() -> new HmsException(ErrorCode.PATIENT_NOT_FOUND)).toDTO();
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "patient-item")
    @CacheInvalidateAll(cacheName = "patients-list")
    public PatientDTO updatePatient(PatientDTO patientDTO) {
        patientRepository.findByIdOptional(patientDTO.getId())
                .orElseThrow(() -> new HmsException(ErrorCode.PATIENT_NOT_FOUND));
        Patient patient = patientDTO.toEntity();
        return patientRepository.getEntityManager().merge(patient).toDTO();
    }

    @Override
    public Boolean patientExists(Long id) {
        return patientRepository.findByIdOptional(id).isPresent();
    }

    @Override
    @CacheResult(cacheName = "patients-list")
    public List<PatientDTO> getAllPatients() {
        return patientRepository.listAll().stream().map(Patient::toDTO).toList();
    }

    @Override
    public PageResponse<PatientDTO> getAllPatientsPaginated(int page, int size) {
        PanacheQuery<Patient> query = patientRepository.findAll().page(page, size);
        
        List<PatientDTO> patientDTOs = query.list().stream()
                .map(Patient::toDTO)
                .collect(Collectors.toList());
        
        long totalElements = query.count();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PageResponse.<PatientDTO>builder()
                .content(patientDTOs)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page == 0)
                .last(page >= totalPages - 1)
                .build();
    }

    @Override
    public List<DoctorDropdown> getPatientsById(List<Long> ids) {
        return patientRepository.findAllPatientDropdownsByIds(ids);
    }

    @Override
    public List<PatientDTO> findAllByIds(List<Long> ids) {
        return patientRepository.find("id in ?1", ids).list()
                .stream()
                .map(Patient::toDTO)
                .collect(Collectors.toList());
    }
}
