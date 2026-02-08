package com.hms.appointment.Appointment.service;

import com.hms.appointment.Appointment.dto.MedicineDTO;
import com.hms.appointment.Appointment.dto.PrescriptionDTO;
import com.hms.appointment.Appointment.dto.PrescriptionDetails;

import java.util.List;

public interface PrescriptionService {
    Long savePrescription(PrescriptionDTO request);
    PrescriptionDTO getPrescriptionByAppointmentId(Long appointmentId);
    PrescriptionDTO getPrescriptionById(Long prescriptionId);
    List<PrescriptionDetails> getPrescriptionByPatientId(Long patientId);
    List<PrescriptionDetails> getPrescriptions();
    List<MedicineDTO> getMedicineByPatientId(Long patientId);
}
