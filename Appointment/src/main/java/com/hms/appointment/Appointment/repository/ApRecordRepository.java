package com.hms.appointment.Appointment.repository;

import com.hms.appointment.Appointment.entity.ApRecord;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ApRecordRepository implements PanacheRepository<ApRecord> {
    
    public Optional<ApRecord> findByAppointment_Id(Long appointmentId) {
        return find("appointment.id", appointmentId).firstResultOptional();
    }

    public List<ApRecord> findByPatientId(Long patientId) {
        return find("patientId", patientId).list();
    }

    public Boolean existsByAppointment_Id(Long appointmentId) {
        return count("appointment.id", appointmentId) > 0;
    }
}
