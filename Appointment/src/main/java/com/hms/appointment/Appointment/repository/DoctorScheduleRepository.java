package com.hms.appointment.Appointment.repository;

import com.hms.appointment.Appointment.entity.DoctorSchedule;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DoctorScheduleRepository implements PanacheRepository<DoctorSchedule> {

    public Optional<DoctorSchedule> findByDoctorIdAndScheduleDate(Long doctorId, LocalDate scheduleDate) {
        return find("doctorId = ?1 AND scheduleDate = ?2", doctorId, scheduleDate).firstResultOptional();
    }
    
    public List<DoctorSchedule> findByDoctorIdAndScheduleDateBetween(Long doctorId, LocalDate startDate, LocalDate endDate) {
        return find("doctorId = ?1 AND scheduleDate >= ?2 AND scheduleDate <= ?3", doctorId, startDate, endDate).list();
    }
    
    public List<DoctorSchedule> findAvailableSchedules(Long doctorId, LocalDate startDate, LocalDate endDate) {
        return find("doctorId = ?1 AND scheduleDate >= ?2 AND scheduleDate <= ?3 AND isLocked = false", doctorId, startDate, endDate).list();
    }
    
    public List<DoctorSchedule> findByDoctorId(Long doctorId) {
        return find("doctorId", doctorId).list();
    }
}
