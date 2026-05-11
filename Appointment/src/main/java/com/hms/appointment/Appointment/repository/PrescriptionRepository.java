package com.hms.appointment.Appointment.repository;

import com.hms.appointment.Appointment.entity.Prescription;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PrescriptionRepository implements PanacheRepository<Prescription> {

    public Optional<Prescription> findByAppointment_Id(Long appointmentId) {
        return find("appointment.id", appointmentId).firstResultOptional();
    }

    public List<Prescription> findAllByPatientId(Long patientId) {
        return find("patientId", patientId).list();
    }

    public List<Long> findAllPreIdsByPatient(Long patientId) {
        return getEntityManager().createQuery("SELECT p.id FROM Prescription p WHERE p.patientId = :patientId", Long.class)
                .setParameter("patientId", patientId)
                .getResultList();
    }
}
