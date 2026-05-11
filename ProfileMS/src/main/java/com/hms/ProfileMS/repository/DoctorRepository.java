package com.hms.ProfileMS.repository;

import com.hms.ProfileMS.dto.DoctorDropdown;
import com.hms.ProfileMS.entity.Doctor;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DoctorRepository implements PanacheRepository<Doctor> {
    
    public Optional<Doctor> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<Doctor> findByLicenseNo(String licenseNo) {
        return find("licenseNo", licenseNo).firstResultOptional();
    }

    public List<DoctorDropdown> findAllDoctorDropdowns() {
        return findAll().project(DoctorDropdown.class).list();
    }

    public List<DoctorDropdown> findAllDoctorDropdownsByIds(List<Long> ids) {
        return find("id in ?1", ids).project(DoctorDropdown.class).list();
    }
}
