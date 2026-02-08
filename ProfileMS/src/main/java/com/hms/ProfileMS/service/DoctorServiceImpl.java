package com.hms.ProfileMS.service;

import com.hms.ProfileMS.dto.DoctorDTO;
import com.hms.ProfileMS.dto.DoctorDropdown;
import com.hms.ProfileMS.dto.PageResponse;
import com.hms.ProfileMS.entity.Doctor;
import com.hms.ProfileMS.entity.Patient;
import com.hms.ProfileMS.exception.ErrorCode;
import com.hms.ProfileMS.exception.HmsException;
import com.hms.ProfileMS.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;


    @Override
    @CacheEvict(value = "doctors_list", allEntries = true) // Xóa cache list khi thêm
    public Long addDoctor(DoctorDTO doctorDTO) {
        if (doctorDTO.getEmail() != null && doctorRepository.findByEmail(doctorDTO.getEmail()).isPresent()) {
            throw new HmsException(ErrorCode.DOCTOR_ALREADY_EXISTS);
        }
        if (doctorDTO.getLicenseNo() != null && doctorRepository.findByLicenseNo(doctorDTO.getLicenseNo()).isPresent()) {
            throw new HmsException(ErrorCode.DOCTOR_ALREADY_EXISTS);
        }
        return doctorRepository.save(doctorDTO.toEntity()).getId();
    }

    @Override
    @Cacheable(value = "doctor_item", key = "#id")
    public DoctorDTO getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new HmsException(ErrorCode.DOCTOR_NOT_FOUND)).toDTO();
    }

    @Override
    @CacheEvict(value = {"doctors_list"}, allEntries = true)
    @CachePut(value = "doctor_item", key = "#doctorDTO.id") // Xóa cache cũ để lần sau lấy mới
    public DoctorDTO updateDoctor(DoctorDTO doctorDTO) {
        doctorRepository.findById(doctorDTO.getId())
                .orElseThrow(() -> new HmsException(ErrorCode.DOCTOR_NOT_FOUND));
        return doctorRepository.save(doctorDTO.toEntity()).toDTO();
    }

    @Override
    public Boolean doctorExists(Long id) {
        return doctorRepository.existsById(id);
    }

    @Override
    public List<DoctorDropdown> getDoctorDropdowns() {
        return doctorRepository.findAllDoctorDropdowns();
    }

    @Override
    @Cacheable(value = "doctors_list")
    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll().stream().map(Doctor::toDTO).toList();
    }

    @Override
    public PageResponse<DoctorDTO> getAllDoctorsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Doctor> doctorPage = doctorRepository.findAll(pageable);
        
        List<DoctorDTO> doctorDTOs = doctorPage.getContent().stream()
                .map(Doctor::toDTO)
                .toList();
        
        return PageResponse.<DoctorDTO>builder()
                .content(doctorDTOs)
                .page(doctorPage.getNumber())
                .size(doctorPage.getSize())
                .totalElements(doctorPage.getTotalElements())
                .totalPages(doctorPage.getTotalPages())
                .first(doctorPage.isFirst())
                .last(doctorPage.isLast())
                .build();
    }

    @Override
    public List<DoctorDropdown> getDoctorsById(List<Long> ids) {
        return doctorRepository.findAllDoctorDropdownsByIds(ids);
    }


}
