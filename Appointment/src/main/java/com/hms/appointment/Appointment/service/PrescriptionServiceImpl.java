package com.hms.appointment.Appointment.service;

import com.hms.appointment.Appointment.clients.ProfileClient;
import com.hms.appointment.Appointment.dto.*;
import com.hms.appointment.Appointment.entity.Prescription;
import com.hms.appointment.Appointment.exception.ErrorCode;
import com.hms.appointment.Appointment.exception.HmsException;
import com.hms.appointment.Appointment.repository.PrescriptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PrescriptionServiceImpl implements PrescriptionService{
    private final PrescriptionRepository prescriptionRepository;
    private final MedicineService medicineService;
    private final ProfileClient profileClient;

    @Override
    public Long savePrescription(PrescriptionDTO request) {
        request.setPrescriptionDate(LocalDate.now());
        Long prescriptionId =
                prescriptionRepository.save(request.toEntity()).getId();
        request.getMedicines().forEach(medicine -> {
            medicine.setPrescriptionId(prescriptionId);
        });
        medicineService.saveAllMedicines(request.getMedicines());
        return prescriptionId;
    }

    @Override
    public PrescriptionDTO getPrescriptionByAppointmentId(Long appointmentId) {
        PrescriptionDTO prescriptionDTO =
                prescriptionRepository.findByAppointment_Id(appointmentId)
                        .orElseThrow(() -> new HmsException(ErrorCode.PRESCRIPTION_NOT_FOUND)).toDTO();
        prescriptionDTO.setMedicines(medicineService.getAllMedicinesByPrescriptionId(prescriptionDTO.getId()));
        return prescriptionDTO;
    }

    @Override
    public PrescriptionDTO getPrescriptionById(Long prescriptionId) {
        PrescriptionDTO dto = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new HmsException(ErrorCode.PRESCRIPTION_NOT_FOUND)).toDTO();
        dto.setMedicines(medicineService.getAllMedicinesByPrescriptionId(dto.getId()));
        return dto;
    }

    @Override
    public List<PrescriptionDetails> getPrescriptionByPatientId(Long patientId) {
        List<Prescription> prescriptions=
                prescriptionRepository.findAllByPatientId(patientId);

        List<PrescriptionDetails> prescriptionDetails = prescriptions.stream()
                .map(Prescription::toDetails)
                .toList();

        prescriptionDetails.forEach(details -> {
            details.setMedicines(medicineService.getAllMedicinesByPrescriptionId(details.getId()));
        });

        List<Long> doctorIds = prescriptionDetails.stream()
                .map(PrescriptionDetails::getDoctorId)
                .distinct().toList();
        List<DoctorName> doctorNames = profileClient.getDoctorsById(doctorIds);
        Map<Long, String> doctorMap = doctorNames.stream()
                .collect(Collectors.toMap(DoctorName::getId, DoctorName::getName));
        prescriptionDetails.forEach(details -> {
            String doctorName =  doctorMap.get(details.getDoctorId());
            if (doctorName != null) {
                details.setDoctorName(doctorName);
            } else {
                details.setDoctorName("Unknown Doctor");
            }
        });
        return prescriptionDetails;
    }

    @Override
    public List<PrescriptionDetails> getPrescriptions() {
        List<Prescription> prescriptions=
                prescriptionRepository.findAll();
        List<PrescriptionDetails> prescriptionDetails = prescriptions.stream()
                .map(Prescription::toDetails)
                .toList();
        prescriptionDetails.forEach(details -> {
            details.setMedicines(medicineService.getAllMedicinesByPrescriptionId(details.getId()));
        });
        List<Long> doctorIds = prescriptionDetails.stream()
                .map(PrescriptionDetails::getDoctorId)
                .distinct().toList();
        List<Long> patientIds = prescriptionDetails.stream()
                .map(PrescriptionDetails::getPatientId)
                .distinct().toList();
        List<DoctorName> doctorNames = profileClient.getDoctorsById(doctorIds);
        List<DoctorName> patientNames = profileClient.getPatientsById(patientIds);
        Map<Long, String> doctorMap = doctorNames.stream()
                .collect(Collectors.toMap(DoctorName::getId, DoctorName::getName));
        Map<Long, String> patientMap = patientNames.stream()
                .collect(Collectors.toMap(DoctorName::getId, DoctorName::getName));
        prescriptionDetails.forEach(details -> {
            String doctorName =  doctorMap.get(details.getDoctorId());
            String patientName =  patientMap.get(details.getPatientId());
            if (doctorName != null) {
                details.setDoctorName(doctorName);
            } else {
                details.setDoctorName("Unknown Doctor");
            }
            if (patientName != null) {
                details.setPatientName(patientName);
            } else {
                details.setPatientName("Unknown Patient");
            }
        });
        return prescriptionDetails;
    }

    @Override
    public List<MedicineDTO> getMedicineByPatientId(Long patientId) {
        List<Long> pIds = prescriptionRepository.findAllPreIdsByPatient(patientId);
        return medicineService.getMedicinesByPrescriptionIds(pIds);
    }
}
