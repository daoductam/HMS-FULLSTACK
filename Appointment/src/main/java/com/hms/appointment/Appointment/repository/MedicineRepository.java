package com.hms.appointment.Appointment.repository;

import com.hms.appointment.Appointment.entity.Medicine;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class MedicineRepository implements PanacheRepository<Medicine> {

    public List<Medicine> findAllByPrescription_Id(Long prescriptionId) {
        return find("prescription.id", prescriptionId).list();
    }

    public List<Medicine> findAllByPrescription_IdIn(List<Long> prescriptionIds) {
        return find("prescription.id in ?1", prescriptionIds).list();
    }
}
