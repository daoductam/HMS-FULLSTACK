package com.hms.appointment.Appointment.service;

import com.hms.appointment.Appointment.clients.ProfileClient;
import com.hms.appointment.Appointment.dto.*;
import com.hms.appointment.Appointment.entity.Prescription;
import com.hms.appointment.Appointment.exception.ErrorCode;
import com.hms.appointment.Appointment.exception.HmsException;
import com.hms.appointment.Appointment.repository.PrescriptionRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PrescriptionServiceImpl implements PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final MedicineService medicineService;
    
    @Inject
    @RestClient
    ProfileClient profileClient;

    @Override
    public Long savePrescription(PrescriptionDTO request) {
        request.setPrescriptionDate(LocalDate.now());
        Prescription prescription = request.toEntity();
        prescriptionRepository.persist(prescription);
        
        Long prescriptionId = prescription.getId();
        request.getMedicines().forEach(medicine -> {
            medicine.setPrescriptionId(prescriptionId);
        });
        medicineService.saveAllMedicines(request.getMedicines());
        return prescriptionId;
    }

    @Override
    public PrescriptionDTO getPrescriptionByAppointmentId(Long appointmentId) {
        PrescriptionDTO prescriptionDTO = prescriptionRepository.findByAppointment_Id(appointmentId)
                        .orElseThrow(() -> new HmsException(ErrorCode.PRESCRIPTION_NOT_FOUND)).toDTO();
        prescriptionDTO.setMedicines(medicineService.getAllMedicinesByPrescriptionId(prescriptionDTO.getId()));
        return prescriptionDTO;
    }

    @Override
    public PrescriptionDTO getPrescriptionById(Long prescriptionId) {
        PrescriptionDTO dto = prescriptionRepository.findByIdOptional(prescriptionId)
                .orElseThrow(() -> new HmsException(ErrorCode.PRESCRIPTION_NOT_FOUND)).toDTO();
        dto.setMedicines(medicineService.getAllMedicinesByPrescriptionId(dto.getId()));
        return dto;
    }

    @Override
    public List<PrescriptionDetails> getPrescriptionByPatientId(Long patientId) {
        List<Prescription> prescriptions = prescriptionRepository.findAllByPatientId(patientId);

        List<PrescriptionDetails> prescriptionDetails = prescriptions.stream()
                .map(Prescription::toDetails)
                .toList();

        prescriptionDetails.forEach(details -> {
            details.setMedicines(medicineService.getAllMedicinesByPrescriptionId(details.getId()));
        });

        List<Long> doctorIds = prescriptionDetails.stream()
                .map(PrescriptionDetails::getDoctorId)
                .distinct().toList();
        
        if (!doctorIds.isEmpty()) {
            List<DoctorName> doctorNames = profileClient.getDoctorsById(doctorIds);
            Map<Long, String> doctorMap = doctorNames.stream()
                    .collect(Collectors.toMap(DoctorName::getId, DoctorName::getName));
            
            prescriptionDetails.forEach(details -> {
                String doctorName = doctorMap.get(details.getDoctorId());
                details.setDoctorName(doctorName != null ? doctorName : "Unknown Doctor");
            });
        }
        
        return prescriptionDetails;
    }

    @Override
    public List<PrescriptionDetails> getPrescriptions() {
        List<Prescription> prescriptions = prescriptionRepository.listAll();
        
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
        
        if (!doctorIds.isEmpty()) {
            List<DoctorName> doctorNames = profileClient.getDoctorsById(doctorIds);
            Map<Long, String> doctorMap = doctorNames.stream()
                    .collect(Collectors.toMap(DoctorName::getId, DoctorName::getName));
            
            prescriptionDetails.forEach(details -> {
                String doctorName = doctorMap.get(details.getDoctorId());
                details.setDoctorName(doctorName != null ? doctorName : "Unknown Doctor");
            });
        }
        
        if (!patientIds.isEmpty()) {
            List<DoctorName> patientNames = profileClient.getPatientsById(patientIds);
            Map<Long, String> patientMap = patientNames.stream()
                    .collect(Collectors.toMap(DoctorName::getId, DoctorName::getName));
            
            prescriptionDetails.forEach(details -> {
                String patientName = patientMap.get(details.getPatientId());
                details.setPatientName(patientName != null ? patientName : "Unknown Patient");
            });
        }
        
        return prescriptionDetails;
    }

    @Override
    public List<MedicineDTO> getMedicineByPatientId(Long patientId) {
        List<Long> pIds = prescriptionRepository.findAllPreIdsByPatient(patientId);
        return medicineService.getMedicinesByPrescriptionIds(pIds);
    }
}

