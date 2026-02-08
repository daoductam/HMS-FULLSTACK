package com.hms.appointment.Appointment.service;

import com.hms.appointment.Appointment.dto.MedicineDTO;

import java.util.List;

public interface MedicineService {
    Long saveMedicine(MedicineDTO request) ;
    List<MedicineDTO> saveAllMedicines(List<MedicineDTO> requestList);
    List<MedicineDTO> getAllMedicinesByPrescriptionId(Long prescriptionId);
    List<MedicineDTO> getMedicinesByPrescriptionIds(List<Long> prescriptionIds);
}
