package com.hms.appointment.Appointment.repository;

import com.hms.appointment.Appointment.entity.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {
    Optional<DoctorSchedule> findByDoctorIdAndScheduleDate(Long doctorId, LocalDate scheduleDate);
    
    List<DoctorSchedule> findByDoctorIdAndScheduleDateBetween(
            Long doctorId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT ds FROM DoctorSchedule ds WHERE ds.doctorId = :doctorId " +
           "AND ds.scheduleDate >= :startDate AND ds.scheduleDate <= :endDate " +
           "AND ds.isLocked = false")
    List<DoctorSchedule> findAvailableSchedules(
            @Param("doctorId") Long doctorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    List<DoctorSchedule> findByDoctorId(Long doctorId);
}


