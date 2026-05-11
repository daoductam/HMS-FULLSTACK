package com.hms.appointment.Appointment.repository;

import com.hms.appointment.Appointment.dto.AppointmentDetails;
import com.hms.appointment.Appointment.dto.MonthlyVisitDTO;
import com.hms.appointment.Appointment.dto.ReasonCountDTO;
import com.hms.appointment.Appointment.entity.Appointment;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AppointmentRepository implements PanacheRepository<Appointment> {

    public List<Appointment> findAllByPatientId(Long patientId) {
        return find("patientId", patientId).list();
    }

    public List<Appointment> findAllByDoctorId(Long doctorId) {
        return find("doctorId", doctorId).list();
    }

    // Đếm số lượt khám theo tháng của bệnh nhân
    public List<MonthlyVisitDTO> countCurrentYearVisitsByPatient(Long patientId) {
        return getEntityManager().createQuery(
                "SELECT new com.hms.appointment.Appointment.dto.MonthlyVisitDTO(" +
                "CAST(FUNCTION('MONTHNAME', a.appointmentTime) AS string), COUNT(a)) " +
                "FROM Appointment a " +
                "WHERE a.patientId = ?1 AND YEAR(a.appointmentTime) = YEAR(CURRENT_DATE) " +
                "GROUP BY FUNCTION('MONTH', a.appointmentTime), " +
                "CAST(FUNCTION('MONTHNAME', a.appointmentTime) AS string) " +
                "ORDER BY FUNCTION('MONTH', a.appointmentTime)", MonthlyVisitDTO.class)
                .setParameter(1, patientId)
                .getResultList();
    }

    // Đếm số lượt khám theo tháng của bác sĩ
    public List<MonthlyVisitDTO> countCurrentYearVisitsByDoctor(Long doctorId) {
        return getEntityManager().createQuery(
                "SELECT new com.hms.appointment.Appointment.dto.MonthlyVisitDTO(" +
                "CAST(FUNCTION('MONTHNAME', a.appointmentTime) AS string), COUNT(a)) " +
                "FROM Appointment a " +
                "WHERE a.doctorId = ?1 AND YEAR(a.appointmentTime) = YEAR(CURRENT_DATE) " +
                "GROUP BY FUNCTION('MONTH', a.appointmentTime), " +
                "CAST(FUNCTION('MONTHNAME', a.appointmentTime) AS string) " +
                "ORDER BY FUNCTION('MONTH', a.appointmentTime)", MonthlyVisitDTO.class)
                .setParameter(1, doctorId)
                .getResultList();
    }

    public List<MonthlyVisitDTO> countCurrentYearPatientsByDoctor(Long doctorId) {
        return getEntityManager().createQuery(
                "SELECT new com.hms.appointment.Appointment.dto.MonthlyVisitDTO(" +
                "CAST(FUNCTION('MONTHNAME', a.appointmentTime) AS string), COUNT(DISTINCT a.patientId)) " +
                "FROM Appointment a " +
                "WHERE a.doctorId = ?1 AND YEAR(a.appointmentTime) = YEAR(CURRENT_DATE) " +
                "GROUP BY FUNCTION('MONTH', a.appointmentTime), " +
                "CAST(FUNCTION('MONTHNAME', a.appointmentTime) AS string) " +
                "ORDER BY FUNCTION('MONTH', a.appointmentTime)", MonthlyVisitDTO.class)
                .setParameter(1, doctorId)
                .getResultList();
    }

    // Đếm số lượt khám theo tháng (toàn hệ thống)
    public List<MonthlyVisitDTO> countCurrentYearVisits() {
        return getEntityManager().createQuery(
                "SELECT new com.hms.appointment.Appointment.dto.MonthlyVisitDTO(" +
                "CAST(FUNCTION('MONTHNAME', a.appointmentTime) AS string), COUNT(a)) " +
                "FROM Appointment a " +
                "WHERE YEAR(a.appointmentTime) = YEAR(CURRENT_DATE) " +
                "GROUP BY FUNCTION('MONTH', a.appointmentTime), " +
                "CAST(FUNCTION('MONTHNAME', a.appointmentTime) AS string) " +
                "ORDER BY FUNCTION('MONTH', a.appointmentTime)", MonthlyVisitDTO.class)
                .getResultList();
    }

    // Đếm lý do khám theo bệnh nhân
    public List<ReasonCountDTO> countReasonsByPatientId(Long patientId) {
        return getEntityManager().createQuery("SELECT new com.hms.appointment.Appointment.dto.ReasonCountDTO(a.reason, COUNT(a)) FROM Appointment a WHERE a.patientId = ?1 GROUP BY a.reason", ReasonCountDTO.class)
                .setParameter(1, patientId)
                .getResultList();
    }

    // Đếm lý do khám theo bác sĩ
    public List<ReasonCountDTO> countReasonsByDoctorId(Long doctorId) {
        return getEntityManager().createQuery("SELECT new com.hms.appointment.Appointment.dto.ReasonCountDTO(a.reason, COUNT(a)) FROM Appointment a WHERE a.doctorId = ?1 GROUP BY a.reason", ReasonCountDTO.class)
                .setParameter(1, doctorId)
                .getResultList();
    }

    // Đếm lý do khám toàn hệ thống
    public List<ReasonCountDTO> countReasons() {
        return getEntityManager().createQuery("SELECT new com.hms.appointment.Appointment.dto.ReasonCountDTO(a.reason, COUNT(a)) FROM Appointment a GROUP BY a.reason", ReasonCountDTO.class)
                .getResultList();
    }

    public List<Appointment> findByAppointmentTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return find("appointmentTime >= ?1 AND appointmentTime <= ?2", startOfDay, endOfDay).list();
    }

    public List<Long> getAllPatientIdsByDoctorId(Long doctorId) {
        return getEntityManager().createQuery("SELECT DISTINCT a.patientId FROM Appointment a WHERE a.doctorId = :doctorId", Long.class)
                .setParameter("doctorId", doctorId)
                .getResultList();
    }

    public Optional<Appointment> findByIdOptional(Long id) {
        return find("id", id).firstResultOptional();
    }
}

