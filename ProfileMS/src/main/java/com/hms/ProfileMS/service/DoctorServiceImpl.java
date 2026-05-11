package com.hms.ProfileMS.service;

import com.hms.ProfileMS.dto.DoctorDTO;
import com.hms.ProfileMS.dto.DoctorDropdown;
import com.hms.ProfileMS.dto.PageResponse;
import com.hms.ProfileMS.entity.Doctor;
import com.hms.ProfileMS.exception.ErrorCode;
import com.hms.ProfileMS.exception.HmsException;
import com.hms.ProfileMS.repository.DoctorRepository;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "doctors-list")
    public Long addDoctor(DoctorDTO doctorDTO) {
        if (doctorDTO.getEmail() != null && doctorRepository.findByEmail(doctorDTO.getEmail()).isPresent()) {
            throw new HmsException(ErrorCode.DOCTOR_ALREADY_EXISTS);
        }
        if (doctorDTO.getLicenseNo() != null && doctorRepository.findByLicenseNo(doctorDTO.getLicenseNo()).isPresent()) {
            throw new HmsException(ErrorCode.DOCTOR_ALREADY_EXISTS);
        }
        Doctor doctor = doctorDTO.toEntity();
        doctorRepository.persist(doctor);
        return doctor.getId();
    }

    @Override
    @CacheResult(cacheName = "doctor-item")
    public DoctorDTO getDoctorById(Long id) {
        return doctorRepository.findByIdOptional(id)
                .orElseThrow(() -> new HmsException(ErrorCode.DOCTOR_NOT_FOUND)).toDTO();
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "doctors-list")
    @CacheInvalidateAll(cacheName = "doctor-item")
    public DoctorDTO updateDoctor(DoctorDTO doctorDTO) {
        doctorRepository.findByIdOptional(doctorDTO.getId())
                .orElseThrow(() -> new HmsException(ErrorCode.DOCTOR_NOT_FOUND));
        Doctor doctor = doctorDTO.toEntity();
        return doctorRepository.getEntityManager().merge(doctor).toDTO();
    }

    @Override
    public Boolean doctorExists(Long id) {
        return doctorRepository.findByIdOptional(id).isPresent();
    }

    @Override
    public List<DoctorDropdown> getDoctorDropdowns() {
        return doctorRepository.findAllDoctorDropdowns();
    }

    @Override
    @CacheResult(cacheName = "doctors-list")
    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.listAll().stream().map(Doctor::toDTO).toList();
    }

    @Override
    public PageResponse<DoctorDTO> getAllDoctorsPaginated(int page, int size) {
        PanacheQuery<Doctor> query = doctorRepository.findAll().page(page, size);
        
        List<DoctorDTO> doctorDTOs = query.list().stream()
                .map(Doctor::toDTO)
                .toList();
        
        long totalElements = query.count();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PageResponse.<DoctorDTO>builder()
                .content(doctorDTOs)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page == 0)
                .last(page >= totalPages - 1)
                .build();
    }

    @Override
    public List<DoctorDropdown> getDoctorsById(List<Long> ids) {
        return doctorRepository.findAllDoctorDropdownsByIds(ids);
    }
}
