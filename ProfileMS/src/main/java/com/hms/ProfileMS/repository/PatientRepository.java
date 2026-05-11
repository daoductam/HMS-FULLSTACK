package com.hms.ProfileMS.repository;

import com.hms.ProfileMS.dto.DoctorDropdown;
import com.hms.ProfileMS.entity.Patient;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PatientRepository implements PanacheRepository<Patient> {
    
    public Optional<Patient> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<Patient> findByCCCD(String CCCD) {
        return find("CCCD", CCCD).firstResultOptional();
    }

    public List<DoctorDropdown> findAllPatientDropdownsByIds(List<Long> ids) {
        return find("id in ?1", ids).project(DoctorDropdown.class).list();
    }
}
