package com.hms.appointment.Appointment.service;

import com.hms.appointment.Appointment.dto.MedicineDTO;
import com.hms.appointment.Appointment.entity.Medicine;
import com.hms.appointment.Appointment.repository.MedicineRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicineServiceImpl implements MedicineService{
    private final MedicineRepository medicineRepository;

    @Override
    public Long saveMedicine(MedicineDTO request) {
        return medicineRepository.save(request.toEntity()).getId();
    }

    @Override
    public List<MedicineDTO> saveAllMedicines(List<MedicineDTO> requestList) {
        return  (medicineRepository.saveAll(
                requestList.stream().map(MedicineDTO::toEntity).toList()
        ))
                .stream().map(Medicine::toDTO).toList();
    }

    @Override
    public List<MedicineDTO> getAllMedicinesByPrescriptionId(Long prescriptionId) {
        return  (medicineRepository.findAllByPrescription_Id(
                prescriptionId
        ))
                .stream().map(Medicine::toDTO).toList();
    }

    @Override
    public List<MedicineDTO> getMedicinesByPrescriptionIds(List<Long> prescriptionIds) {
        return medicineRepository.findAllByPrescription_IdIn(prescriptionIds)
                .stream().map(Medicine::toDTO).toList();
    }
}
