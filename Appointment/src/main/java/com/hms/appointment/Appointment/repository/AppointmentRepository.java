package com.hms.appointment.Appointment.repository;

import com.hms.appointment.Appointment.dto.AppointmentDetails;
import com.hms.appointment.Appointment.dto.MonthlyVisitDTO;
import com.hms.appointment.Appointment.dto.ReasonCountDTO;
import com.hms.appointment.Appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT new com.hms.appointment.Appointment.dto.AppointmentDetails(" +
            "a.id, a.patientId, null, null, null, " +
            "a.doctorId, null, a.appointmentTime, a.status, a.reason, a.notes) " +
            "FROM Appointment a WHERE a.patientId = :patientId")
    List<AppointmentDetails> findAllByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT new com.hms.appointment.Appointment.dto.AppointmentDetails(" +
            "a.id, a.patientId, null, null, null, " +
            "a.doctorId, null, a.appointmentTime, a.status, a.reason, a.notes) " +
            "FROM Appointment a WHERE a.doctorId = :doctorId")
    List<AppointmentDetails> findAllByDoctorId(@Param("doctorId") Long doctorId);

    // Đếm số lượt khám theo tháng của bệnh nhân
    @Query("SELECT new com.hms.appointment.Appointment.dto.MonthlyVisitDTO(" +
            "CAST(FUNCTION('MONTHNAME', a.appointmentTime) AS string), COUNT(a)) " +
            "FROM Appointment a " +
            "WHERE a.patientId = ?1 AND YEAR(a.appointmentTime) = YEAR(CURRENT_DATE) " +
            "GROUP BY FUNCTION('MONTH', a.appointmentTime), " +
            "CAST(FUNCTION('MONTHNAME', a.appointmentTime) AS string) " +
            "ORDER BY FUNCTION('MONTH', a.appointmentTime)")
    List<MonthlyVisitDTO> countCurrentYearVisitsByPatient(Long patientId);

    // Đếm số lượt khám theo tháng của bác sĩ
    @Query("SELECT new com.hms.appointment.Appointment.dto.MonthlyVisitDTO(" +
            "CAST(FUNCTION('MONTHNAME', a.appointmentTime) AS string), COUNT(a)) " +
            "FROM Appointment a " +
            "WHERE a.doctorId = ?1 AND YEAR(a.appointmentTime) = YEAR(CURRENT_DATE) " +
            "GROUP BY FUNCTION('MONTH', a.appointmentTime), " +
            "CAST(FUNCTION('MONTHNAME', a.appointmentTime) AS string) " +
            "ORDER BY FUNCTION('MONTH', a.appointmentTime)")
    List<MonthlyVisitDTO> countCurrentYearVisitsByDoctor(Long doctorId);

    @Query("SELECT new com.hms.appointment.Appointment.dto.MonthlyVisitDTO(" +
            "CAST(FUNCTION('MONTHNAME', a.appointmentTime) AS string), COUNT(DISTINCT a.patientId)) " +
            "FROM Appointment a " +
            "WHERE a.doctorId = ?1 AND YEAR(a.appointmentTime) = YEAR(CURRENT_DATE) " +
            "GROUP BY FUNCTION('MONTH', a.appointmentTime), " +
            "CAST(FUNCTION('MONTHNAME', a.appointmentTime) AS string) " +
            "ORDER BY FUNCTION('MONTH', a.appointmentTime)")
    List<MonthlyVisitDTO> countCurrentYearPatientsByDoctor(Long doctorId);

    // Đếm số lượt khám theo tháng (toàn hệ thống)
    @Query("SELECT new com.hms.appointment.Appointment.dto.MonthlyVisitDTO(" +
            "CAST(FUNCTION('MONTHNAME', a.appointmentTime) AS string), COUNT(a)) " +
            "FROM Appointment a " +
            "WHERE YEAR(a.appointmentTime) = YEAR(CURRENT_DATE) " +
            "GROUP BY FUNCTION('MONTH', a.appointmentTime), " +
            "CAST(FUNCTION('MONTHNAME', a.appointmentTime) AS string) " +
            "ORDER BY FUNCTION('MONTH', a.appointmentTime)")
    List<MonthlyVisitDTO> countCurrentYearVisits();

    // Đếm lý do khám theo bệnh nhân
    @Query("SELECT new com.hms.appointment.Appointment.dto.ReasonCountDTO(" +
            "a.reason, COUNT(a)) " +
            "FROM Appointment a WHERE a.patientId = ?1 GROUP BY a.reason")
    List<ReasonCountDTO> countReasonsByPatientId(Long patientId);

    // Đếm lý do khám theo bác sĩ
    @Query("SELECT new com.hms.appointment.Appointment.dto.ReasonCountDTO(" +
            "a.reason, COUNT(a)) " +
            "FROM Appointment a WHERE a.doctorId = ?1 GROUP BY a.reason")
    List<ReasonCountDTO> countReasonsByDoctorId(Long doctorId);

    // Đếm lý do khám toàn hệ thống
    @Query("SELECT new com.hms.appointment.Appointment.dto.ReasonCountDTO(" +
            "a.reason, COUNT(a)) FROM Appointment a GROUP BY a.reason")
    List<ReasonCountDTO> countReasons();

    List<Appointment> findByAppointmentTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("SELECT DISTINCT a.patientId FROM Appointment a WHERE a.doctorId = :doctorId")
    List<Long> getAllPatientIdsByDoctorId(@Param("doctorId") Long doctorId);

}
