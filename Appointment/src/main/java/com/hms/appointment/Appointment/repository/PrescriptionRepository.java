package com.hms.appointment.Appointment.repository;

import com.hms.appointment.Appointment.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Optional<Prescription> findByAppointment_Id(Long appointmentId);
    List<Prescription> findAllByPatientId(Long patientId);
    @Query("Select p.id from Prescription p where p.patientId=?1")
    List<Long> findAllPreIdsByPatient(Long patientId);
}
