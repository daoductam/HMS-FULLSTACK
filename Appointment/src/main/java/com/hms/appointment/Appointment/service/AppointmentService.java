package com.hms.appointment.Appointment.service;

import com.hms.appointment.Appointment.dto.*;

import java.util.List;

public interface AppointmentService {
    Long scheduleAppointment(AppointmentDTO appointmentDTO);
    void cancelAppointment(Long appointmentId);
    void completeAppointment(Long appointmentId);
    void rescheduleAppointment(Long appointmentId, String newDateTime);
    AppointmentDTO getAppointmentDetails(Long appointmentId);
    AppointmentDetails getAppointmentDetailsWithName(Long appointmentId);
    List<AppointmentDetails> getAllAppointmentDetailsByPatientId(Long patientId);
    List<AppointmentDetails> getAllAppointmentDetailsByDoctorId(Long doctorId);
    List<MonthlyVisitDTO> getAppointmentCountByPatient(Long patientId);
    List<MonthlyVisitDTO> getAppointmentCountByDoctor(Long doctorId);
    List<MonthlyVisitDTO> getAppointmentCount();

    List<ReasonCountDTO> getReasonCountByPatient(Long patientId);
    List<ReasonCountDTO> getReasonCountByDoctor(Long doctorId);
    List<MonthlyVisitDTO> getPatientCountByDoctor(Long doctorId);
    List<ReasonCountDTO> getReasonCount();

    List<AppointmentDetails> getTodaysAppointment();

//    List<PatientDTO> getAllPatientsByDoctorId(Long doctorId);


}
