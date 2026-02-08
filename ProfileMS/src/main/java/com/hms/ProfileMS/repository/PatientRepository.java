package com.hms.ProfileMS.repository;

import com.hms.ProfileMS.dto.DoctorDropdown;
import com.hms.ProfileMS.dto.MonthlyPatientDTO;
import com.hms.ProfileMS.entity.Doctor;
import com.hms.ProfileMS.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByCCCD(String CCCD);
    @Query("SELECT d.id AS id, d.name AS name FROM Patient d WHERE d.id in ?1")
    List<DoctorDropdown> findAllPatientDropdownsByIds(List<Long> ids);


}
