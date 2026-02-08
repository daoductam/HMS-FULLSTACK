package com.hms.appointment.Appointment.repository;

import com.hms.appointment.Appointment.entity.ScheduleShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleShiftRepository extends JpaRepository<ScheduleShift, Long> {
    Optional<ScheduleShift> findByScheduleIdAndShiftId(Long scheduleId, Long shiftId);
    
    List<ScheduleShift> findByScheduleId(Long scheduleId);
    
    @Query("SELECT ss FROM ScheduleShift ss " +
           "JOIN FETCH ss.shift " +
           "WHERE ss.schedule.id = :scheduleId")
    List<ScheduleShift> findByScheduleIdWithShift(@Param("scheduleId") Long scheduleId);
    
    @Query("SELECT ss FROM ScheduleShift ss " +
           "JOIN ss.schedule s " +
           "WHERE s.doctorId = :doctorId AND s.scheduleDate = :scheduleDate " +
           "AND ss.shift.id = :shiftId")
    Optional<ScheduleShift> findByDoctorIdAndDateAndShiftId(
            @Param("doctorId") Long doctorId,
            @Param("scheduleDate") java.time.LocalDate scheduleDate,
            @Param("shiftId") Long shiftId);
}


