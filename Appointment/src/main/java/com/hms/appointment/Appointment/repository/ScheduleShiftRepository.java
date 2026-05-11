package com.hms.appointment.Appointment.repository;

import com.hms.appointment.Appointment.entity.ScheduleShift;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ScheduleShiftRepository implements PanacheRepository<ScheduleShift> {

    public Optional<ScheduleShift> findByScheduleIdAndShiftId(Long scheduleId, Long shiftId) {
        return find("schedule.id = ?1 AND shift.id = ?2", scheduleId, shiftId).firstResultOptional();
    }
    
    public List<ScheduleShift> findByScheduleId(Long scheduleId) {
        return find("schedule.id", scheduleId).list();
    }
    
    public List<ScheduleShift> findByScheduleIdWithShift(Long scheduleId) {
        return find("from ScheduleShift ss join fetch ss.shift where ss.schedule.id = ?1", scheduleId).list();
    }
    
    public Optional<ScheduleShift> findByDoctorIdAndDateAndShiftId(Long doctorId, LocalDate scheduleDate, Long shiftId) {
        return find("from ScheduleShift ss join ss.schedule s where s.doctorId = ?1 and s.scheduleDate = ?2 and ss.shift.id = ?3", 
                doctorId, scheduleDate, shiftId).firstResultOptional();
    }
}
