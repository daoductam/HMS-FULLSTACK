package com.hms.appointment.Appointment.service;

import com.hms.appointment.Appointment.clients.ProfileClient;
import com.hms.appointment.Appointment.dto.ApRecordDTO;
import com.hms.appointment.Appointment.dto.DoctorName;
import com.hms.appointment.Appointment.dto.RecordDetails;
import com.hms.appointment.Appointment.entity.ApRecord;
import com.hms.appointment.Appointment.exception.ErrorCode;
import com.hms.appointment.Appointment.exception.HmsException;
import com.hms.appointment.Appointment.repository.ApRecordRepository;
import com.hms.appointment.Appointment.util.StringListConverter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ApRecordServiceImpl implements ApRecordService{

    private final ApRecordRepository apRecordRepository;
    private final PrescriptionService prescriptionService;
    private final ProfileClient profileClient;

    @Override
    public Long createApRecord(ApRecordDTO request) {
        Optional<ApRecord>existingRecord =
                apRecordRepository.findByAppointment_Id(request.getAppointmentId());
        if (existingRecord.isPresent()) {
            throw new HmsException(ErrorCode.APPOINTMENT_RECORD_NOT_FOUND);
        }
        request.setCreatedAt(LocalDateTime.now());
        Long id = apRecordRepository.save(request.toEntity()).getId();
        if (request.getPrescription() != null) {
            request.getPrescription().setAppointmentId(request.getAppointmentId());
            prescriptionService.savePrescription(request.getPrescription());
        }
        return id;
    }

    @Override
    public void updateApRecord(ApRecordDTO request) {
        ApRecord existing =
                apRecordRepository.findById(request.getId())
                        .orElseThrow(() -> new HmsException(ErrorCode.APPOINTMENT_RECORD_NOT_FOUND));
        existing.setNotes(request.getNotes());
        existing.setDiagnosis(request.getDiagnosis());
        existing.setFollowUpDate(request.getFollowUpDate());
        existing.setSymptoms(StringListConverter.convertListToString(request.getSymptoms()));
        existing.setTests(StringListConverter.convertListToString(request.getTests()));
        existing.setReferral(request.getReferral());

        apRecordRepository.save(existing);
    }

    @Override
    public ApRecordDTO getApRecordByAppointmentId(Long appointmentId) {
        return apRecordRepository.findByAppointment_Id(appointmentId)
                .orElseThrow(() -> new HmsException(ErrorCode.APPOINTMENT_RECORD_NOT_FOUND)).toDTO();
    }

    @Override
    public ApRecordDTO getApRecordDetailsByAppointmentId(Long appointmentId) {
        ApRecordDTO record =  apRecordRepository.findByAppointment_Id(appointmentId)
                .orElseThrow(() -> new HmsException(ErrorCode.APPOINTMENT_RECORD_NOT_FOUND)).toDTO();
        record.setPrescription(prescriptionService.getPrescriptionByAppointmentId(appointmentId));
        return record;
    }

    @Override
    public ApRecordDTO getApRecordById(Long recordId) {
        return apRecordRepository.findById(recordId)
                .orElseThrow(() -> new HmsException(ErrorCode.APPOINTMENT_RECORD_NOT_FOUND)).toDTO();

    }

    @Override
    public List<RecordDetails> getRecordsByPatientId(Long patientId) {
        List<ApRecord> records = apRecordRepository.findByPatientId(patientId);
        List<RecordDetails> recordDetails = records.stream()
                .map(ApRecord::toRecordDetails)
                .toList();
        List<Long> doctorIds = recordDetails.stream()
                .map(RecordDetails::getDoctorId)
                .distinct().toList();
        List<DoctorName> doctors = profileClient.getDoctorsById(doctorIds);
        Map<Long, String> doctorMap = doctors.stream()
                .collect(Collectors.toMap(DoctorName::getId, DoctorName::getName));
        recordDetails.forEach(record -> {
            String doctorName = doctorMap.get(record.getDoctorId());
            if (doctorName != null) {
                record.setDoctorName(doctorName);
            } else {
                record.setDoctorName("Unknown Doctor");
            }
        });
        return recordDetails;
    }

    @Override
    public Boolean isRecordExists(Long appointmentId) {
        return apRecordRepository.existsByAppointment_Id(appointmentId);
    }
}
