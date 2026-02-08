package com.hms.appointment.Appointment.service;

import com.hms.appointment.Appointment.dto.ApRecordDTO;
import com.hms.appointment.Appointment.dto.RecordDetails;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ApRecordService {
    Long createApRecord(ApRecordDTO request);
    void  updateApRecord(ApRecordDTO request);
    ApRecordDTO getApRecordByAppointmentId(Long appointmentId);
    ApRecordDTO getApRecordDetailsByAppointmentId(Long appointmentId);
    ApRecordDTO getApRecordById(Long recordId);
    List<RecordDetails> getRecordsByPatientId(Long patientId);
    Boolean isRecordExists(Long appointmentId);
}
