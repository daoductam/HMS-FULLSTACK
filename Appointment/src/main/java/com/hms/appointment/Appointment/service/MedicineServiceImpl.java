package com.hms.appointment.Appointment.service;

import com.hms.appointment.Appointment.dto.MedicineDTO;
import com.hms.appointment.Appointment.entity.Medicine;
import com.hms.appointment.Appointment.repository.MedicineRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
@Transactional
public class MedicineServiceImpl implements MedicineService {
    private final MedicineRepository medicineRepository;

    @Override
    public Long saveMedicine(MedicineDTO request) {
        Medicine medicine = request.toEntity();
        medicineRepository.persist(medicine);
        return medicine.getId();
    }

    @Override
    public List<MedicineDTO> saveAllMedicines(List<MedicineDTO> requestList) {
        List<Medicine> medicines = requestList.stream().map(MedicineDTO::toEntity).toList();
        medicineRepository.persist(medicines);
        return medicines.stream().map(Medicine::toDTO).toList();
    }

    @Override
    public List<MedicineDTO> getAllMedicinesByPrescriptionId(Long prescriptionId) {
        return medicineRepository.findAllByPrescription_Id(prescriptionId)
                .stream().map(Medicine::toDTO).toList();
    }

    @Override
    public List<MedicineDTO> getMedicinesByPrescriptionIds(List<Long> prescriptionIds) {
        return medicineRepository.findAllByPrescription_IdIn(prescriptionIds)
                .stream().map(Medicine::toDTO).toList();
    }
}

