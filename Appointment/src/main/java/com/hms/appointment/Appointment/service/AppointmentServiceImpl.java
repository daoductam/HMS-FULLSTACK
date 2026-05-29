package com.hms.appointment.Appointment.service;

import com.hms.appointment.Appointment.clients.ProfileClient;
import com.hms.appointment.Appointment.dto.*;
import com.hms.appointment.Appointment.entity.Appointment;
import com.hms.appointment.Appointment.exception.ErrorCode;
import com.hms.appointment.Appointment.exception.HmsException;
import com.hms.appointment.Appointment.repository.AppointmentRepository;
import com.hms.appointment.Appointment.repository.ShiftRepository;
import com.hms.hms_common.event.AppointmentEvent;
import com.hms.hms_common.lock.DistributedLock;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    
    @Inject
    @RestClient
    ProfileClient profileClient;

    @Inject
    @Channel("notification-appointment")
    Emitter<Object> appointmentEmitter;

    private final ScheduleService scheduleService;
    private final ShiftRepository shiftRepository;

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "stats-dashboard")
    @CacheInvalidateAll(cacheName = "stats-reasons")
    @DistributedLock(keyExpression = "appointmentDTO.doctorId")
    public Long scheduleAppointment(AppointmentDTO appointmentDTO) {
        // 1. Validate Doctor
        Boolean doctorExists = profileClient.doctorExists(appointmentDTO.getDoctorId());
        if (doctorExists == null || !doctorExists) {
            throw new HmsException(ErrorCode.DOCTOR_NOT_FOUND);
        }
        DoctorDTO doctorInfo = profileClient.getDoctorById(appointmentDTO.getDoctorId());

        // 2. Validate Patient
        Boolean patientExists = profileClient.patientExists(appointmentDTO.getPatientId());
        if (patientExists == null || !patientExists) {
            throw new HmsException(ErrorCode.PATIENT_NOT_FOUND);
        }
        PatientDTO patientInfo = profileClient.getPatientById(appointmentDTO.getPatientId());

        // 3. Validate Schedule và Slot
        LocalDate scheduleDate = appointmentDTO.getAppointmentTime().toLocalDate();
        int appointmentHour = appointmentDTO.getAppointmentTime().getHour();
        
        if (scheduleService.isScheduleLocked(appointmentDTO.getDoctorId(), scheduleDate)) {
            throw new HmsException(ErrorCode.SCHEDULE_LOCKED);
        }
        
        if (!scheduleService.isValidAppointmentTime(appointmentDTO.getDoctorId(), scheduleDate, appointmentHour)) {
            throw new HmsException(ErrorCode.INVALID_APPOINTMENT_TIME);
        }
        
        Long shiftId = findShiftIdByHour(appointmentHour);
        if (shiftId == null) {
            throw new HmsException(ErrorCode.INVALID_APPOINTMENT_TIME);
        }
        
        if (!scheduleService.checkSlotAvailability(appointmentDTO.getDoctorId(), scheduleDate, shiftId)) {
            throw new HmsException(ErrorCode.NO_AVAILABLE_SLOTS);
        }

        // 4. Save to DB
        appointmentDTO.setStatus(Status.SCHEDULED);
        Appointment appointment = appointmentDTO.toEntity();
        appointmentRepository.persist(appointment);
        
        // 5. Tăng số slot đã đặt
        scheduleService.incrementBookedSlots(appointmentDTO.getDoctorId(), scheduleDate, shiftId);

        // 6. Send Event to Kafka
        try {
            AppointmentEvent event = AppointmentEvent.builder()
                    .appointmentId(appointment.getId())
                    .patientEmail(patientInfo.getEmail())
                    .patientName(patientInfo.getName())
                    .doctorName(doctorInfo.getName())
                    .appointmentTime(String.valueOf(appointment.getAppointmentTime()))
                    .status(appointment.getStatus().toString())
                    .build();

            appointmentEmitter.send(event);
        } catch (Exception e) {
            System.err.println("Error sending Kafka event: " + e.getMessage());
        }

        return appointment.getId();
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "stats-dashboard")
    @CacheInvalidateAll(cacheName = "stats-reasons")
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findByIdOptional(appointmentId)
                .orElseThrow(() -> new HmsException(ErrorCode.APPOINTMENT_NOT_FOUND));
        
        if (appointment.getStatus().equals(Status.CANCELLED)) {
            throw new HmsException(ErrorCode.APPOINTMENT_ALREADY_CANCELLED);
        }
        
        if (appointment.getStatus().equals(Status.SCHEDULED)) {
            LocalDate scheduleDate = appointment.getAppointmentTime().toLocalDate();
            int appointmentHour = appointment.getAppointmentTime().getHour();
            Long shiftId = findShiftIdByHour(appointmentHour);
            
            if (shiftId != null) {
                try {
                    scheduleService.decrementBookedSlots(appointment.getDoctorId(), scheduleDate, shiftId);
                } catch (Exception e) {
                    System.err.println("Error decrementing booked slots: " + e.getMessage());
                }
            }
        }
        
        appointment.setStatus(Status.CANCELLED);
        appointmentRepository.persist(appointment);
    }
    
    private Long findShiftIdByHour(int hour) {
        return shiftRepository.listAll().stream()
                .filter(shift -> hour >= shift.getStartHour() && hour < shift.getEndHour())
                .map(com.hms.appointment.Appointment.entity.Shift::getId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void completeAppointment(Long appointmentId) {
        // Implementation if needed
    }

    @Override
    public void rescheduleAppointment(Long appointmentId, String newDateTime) {
        // Implementation if needed
    }

    @Override
    public AppointmentDTO getAppointmentDetails(Long appointmentId) {
        return appointmentRepository.findByIdOptional(appointmentId)
                .orElseThrow(() -> new HmsException(ErrorCode.APPOINTMENT_NOT_FOUND)).toDTO();
    }

    @Override
    public AppointmentDetails getAppointmentDetailsWithName(Long appointmentId) {
        Appointment appointment = appointmentRepository.findByIdOptional(appointmentId)
                .orElseThrow(() -> new HmsException(ErrorCode.APPOINTMENT_NOT_FOUND));
        
        DoctorDTO doctorDTO = profileClient.getDoctorById(appointment.getDoctorId());
        PatientDTO patientDTO = profileClient.getPatientById(appointment.getPatientId());
        
        return AppointmentDetails.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .patientName(patientDTO.getName())
                .doctorId(appointment.getDoctorId())
                .doctorName(doctorDTO.getName())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus())
                .reason(appointment.getReason())
                .notes(appointment.getNotes())
                .patientEmail(patientDTO.getEmail())
                .patientPhone(patientDTO.getPhone()).build();
    }

    @Override
    public List<AppointmentDetails> getAllAppointmentDetailsByPatientId(Long patientId) {
        return appointmentRepository.findAllByPatientId(patientId).stream()
                .map(appointment -> {
                    AppointmentDetails details = appointment.toDetails();
                    try {
                        DoctorDTO doctorDTO = profileClient.getDoctorById(appointment.getDoctorId());
                        details.setDoctorName(doctorDTO.getName());
                    } catch (Exception e) {
                        details.setDoctorName("Unknown Doctor");
                    }
                    return details;
                }).toList();
    }

    @Override
    public List<AppointmentDetails> getAllAppointmentDetailsByDoctorId(Long doctorId) {
        return appointmentRepository.findAllByDoctorId(doctorId).stream()
                .map(appointment -> {
                    AppointmentDetails details = appointment.toDetails();
                    try {
                        PatientDTO patientDTO = profileClient.getPatientById(appointment.getPatientId());
                        details.setPatientName(patientDTO.getName());
                        details.setPatientEmail(patientDTO.getEmail());
                        details.setPatientPhone(patientDTO.getPhone());
                    } catch (Exception e) {
                        details.setPatientName("Unknown Patient");
                    }
                    return details;
                }).toList();
    }

    @Override
    @CacheResult(cacheName = "stats-dashboard")
    public List<MonthlyVisitDTO> getAppointmentCountByPatient(Long patientId) {
        return appointmentRepository.countCurrentYearVisitsByPatient(patientId);
    }

    @Override
    @CacheResult(cacheName = "stats-dashboard")
    public List<MonthlyVisitDTO> getAppointmentCountByDoctor(Long doctorId) {
        return appointmentRepository.countCurrentYearVisitsByDoctor(doctorId);
    }

    @Override
    @CacheResult(cacheName = "stats-dashboard")
    public List<MonthlyVisitDTO> getPatientCountByDoctor(Long doctorId) {
        return appointmentRepository.countCurrentYearPatientsByDoctor(doctorId);
    }

    @Override
    @CacheResult(cacheName = "stats-dashboard")
    public List<MonthlyVisitDTO> getAppointmentCount() {
        return appointmentRepository.countCurrentYearVisits();
    }

    @Override
    @CacheResult(cacheName = "stats-reasons")
    public List<ReasonCountDTO> getReasonCountByPatient(Long patientId) {
        return appointmentRepository.countReasonsByPatientId(patientId);
    }

    @Override
    @CacheResult(cacheName = "stats-reasons")
    public List<ReasonCountDTO> getReasonCountByDoctor(Long doctorId) {
        return appointmentRepository.countReasonsByDoctorId(doctorId);
    }

    @Override
    @CacheResult(cacheName = "stats-reasons")
    public List<ReasonCountDTO> getReasonCount() {
        return appointmentRepository.countReasons();
    }

    @Override
    public List<AppointmentDetails> getTodaysAppointment() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        
        return appointmentRepository.findByAppointmentTimeBetween(startOfDay, endOfDay)
                .stream().map(appointment -> {
                    DoctorDTO doctorDTO = profileClient.getDoctorById(appointment.getDoctorId());
                    PatientDTO patientDTO = profileClient.getPatientById(appointment.getPatientId());
                    return AppointmentDetails.builder()
                            .id(appointment.getId())
                            .patientId(appointment.getPatientId())
                            .patientName(patientDTO.getName())
                            .patientEmail(patientDTO.getEmail())
                            .patientPhone(patientDTO.getPhone())
                            .doctorId(appointment.getDoctorId())
                            .doctorName(doctorDTO.getName())
                            .appointmentTime(appointment.getAppointmentTime())
                            .status(appointment.getStatus())
                            .reason(appointment.getReason())
                            .notes(appointment.getNotes()).build();
                }).toList();
    }
}

