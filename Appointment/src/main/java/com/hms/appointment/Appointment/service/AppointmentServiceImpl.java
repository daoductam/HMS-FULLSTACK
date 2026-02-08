package com.hms.appointment.Appointment.service;

import com.hms.appointment.Appointment.clients.ProfileClient;
import com.hms.appointment.Appointment.dto.*;
import com.hms.appointment.Appointment.entity.Appointment;
import com.hms.appointment.Appointment.exception.ErrorCode;
import com.hms.appointment.Appointment.exception.HmsException;
import com.hms.appointment.Appointment.repository.AppointmentRepository;
import com.hms.appointment.Appointment.repository.ShiftRepository;
import com.hms.hms_common.event.AppointmentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService{
    private final AppointmentRepository appointmentRepository;
    private final ApiService apiService;
    private final ProfileClient profileClient;
    private final KafkaTemplate<String, Object> kafkaTemplate; // <--- Inject Kafka
    private final ScheduleService scheduleService;
    private final ShiftRepository shiftRepository;

    @Override
    @CacheEvict(value = {"stats_dashboard", "stats_reasons"}, allEntries = true) // Khi đặt lịch mới, xóa cache thống kê
    public Long scheduleAppointment(AppointmentDTO appointmentDTO) {
        // 1. Validate Doctor
        Boolean doctorExists = profileClient.doctorExists(appointmentDTO.getDoctorId());
        if (doctorExists == null || !doctorExists) {
            throw new HmsException(ErrorCode.DOCTOR_NOT_FOUND);
        }
        DoctorDTO doctorInfo = profileClient.getDoctorById(appointmentDTO.getDoctorId()); // Lấy thông tin bác sĩ để gửi mail

        // 2. Validate Patient
        Boolean patientExists = profileClient.patientExists(appointmentDTO.getPatientId());
        if (patientExists == null || !patientExists) {
            throw new HmsException(ErrorCode.PATIENT_NOT_FOUND);
        }
        PatientDTO patientInfo = profileClient.getPatientById(appointmentDTO.getPatientId()); // Lấy thông tin bệnh nhân

        // 3. Validate Schedule và Slot
        LocalDate scheduleDate = appointmentDTO.getAppointmentTime().toLocalDate();
        int appointmentHour = appointmentDTO.getAppointmentTime().getHour();
        
        System.out.println("📅 Validating appointment:");
        System.out.println("   DoctorId: " + appointmentDTO.getDoctorId());
        System.out.println("   Date: " + scheduleDate);
        System.out.println("   Hour: " + appointmentHour);
        System.out.println("   Full DateTime: " + appointmentDTO.getAppointmentTime());
        
        // 3.1. Kiểm tra lịch có bị khóa không
        if (scheduleService.isScheduleLocked(appointmentDTO.getDoctorId(), scheduleDate)) {
            System.out.println("❌ Schedule is locked");
            throw new HmsException(ErrorCode.SCHEDULE_LOCKED);
        }
        
        // 3.2. Kiểm tra thời gian đặt lịch có hợp lệ (thuộc ca làm việc) không
        if (!scheduleService.isValidAppointmentTime(appointmentDTO.getDoctorId(), scheduleDate, appointmentHour)) {
            System.out.println("❌ Invalid appointment time - not within any shift");
            throw new HmsException(ErrorCode.INVALID_APPOINTMENT_TIME);
        }
        
        // 3.3. Tìm shift phù hợp với giờ đặt lịch
        Long shiftId = findShiftIdByHour(appointmentHour);
        if (shiftId == null) {
            throw new HmsException(ErrorCode.INVALID_APPOINTMENT_TIME);
        }
        
        // 3.4. Kiểm tra slot còn trống không
        if (!scheduleService.checkSlotAvailability(appointmentDTO.getDoctorId(), scheduleDate, shiftId)) {
            throw new HmsException(ErrorCode.NO_AVAILABLE_SLOTS);
        }

        // 4. Save to DB
        appointmentDTO.setStatus(Status.SCHEDULED);
        Appointment savedAppointment = appointmentRepository.save(appointmentDTO.toEntity());
        
        // 5. Tăng số slot đã đặt
        try {
            scheduleService.incrementBookedSlots(appointmentDTO.getDoctorId(), scheduleDate, shiftId);
        } catch (Exception e) {
            // Nếu tăng slot lỗi, rollback appointment
            appointmentRepository.delete(savedAppointment);
            throw e;
        }

        // 4. Send Event to Kafka (Logic mới)
        try {
            AppointmentEvent event = AppointmentEvent.builder()
                    .appointmentId(savedAppointment.getId())
                    .patientEmail(patientInfo.getEmail()) // Cần đảm bảo PatientDTO có field email
                    .patientName(patientInfo.getName())
                    .doctorName(doctorInfo.getName())
                    .appointmentTime(String.valueOf(savedAppointment.getAppointmentTime()))
                    .status(savedAppointment.getStatus().toString())
                    .build();

            // Gửi message vào topic "notification-appointment"
//            kafkaTemplate.send("notification-appointment", event);

        } catch (Exception e) {
            // Log error nhưng KHÔNG throw exception để rollback transaction đặt lịch
            // (vì việc gửi mail lỗi không nên làm hủy lịch hẹn đã lưu thành công)
            System.err.println("Error sending Kafka event: " + e.getMessage());
        }

        return savedAppointment.getId();
    }

    @Override
    @CacheEvict(value = {"stats_dashboard", "stats_reasons"}, allEntries = true) // Khi hủy lịch, xóa cache thống kê
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new HmsException(ErrorCode.APPOINTMENT_NOT_FOUND));
        if (appointment.getStatus().equals(Status.CANCELLED)) {
            throw new HmsException(ErrorCode.APPOINTMENT_ALREADY_CANCELLED);
        }
        
        // Giảm số slot đã đặt nếu appointment đã được scheduled
        if (appointment.getStatus().equals(Status.SCHEDULED)) {
            LocalDate scheduleDate = appointment.getAppointmentTime().toLocalDate();
            int appointmentHour = appointment.getAppointmentTime().getHour();
            Long shiftId = findShiftIdByHour(appointmentHour);
            
            if (shiftId != null) {
                try {
                    scheduleService.decrementBookedSlots(appointment.getDoctorId(), scheduleDate, shiftId);
                } catch (Exception e) {
                    // Log error nhưng không throw để vẫn có thể hủy appointment
                    System.err.println("Error decrementing booked slots: " + e.getMessage());
                }
            }
        }
        
        appointment.setStatus(Status.CANCELLED);
        appointmentRepository.save(appointment);
    }
    
    // Helper method để tìm shiftId từ giờ đặt lịch
    private Long findShiftIdByHour(int hour) {
        return shiftRepository.findAll().stream()
                .filter(shift -> hour >= shift.getStartHour() && hour < shift.getEndHour())
                .map(com.hms.appointment.Appointment.entity.Shift::getId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void completeAppointment(Long appointmentId) {
//        Appointment appointment = appointmentRepository.findById(appointmentId)
//                .orElseThrow(() -> new HmsException(ErrorCode.APPOINTMENT_NOT_FOUND));
//        if (appointment.getStatus().equals(Status.CANCELLED)) {
//            throw new HmsException(ErrorCode.APPOINTMENT_ALREADY_CANCELLED);
//        }
//        appointment.setStatus(Status.CANCELLED);
//        appointmentRepository.save(appointment);
    }

    @Override
    public void rescheduleAppointment(Long appointmentId, String newDateTime) {

    }

    @Override
    public AppointmentDTO getAppointmentDetails(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new HmsException(ErrorCode.APPOINTMENT_NOT_FOUND)).toDTO();
    }

    @Override
    public AppointmentDetails getAppointmentDetailsWithName(Long appointmentId) {
        AppointmentDTO appointmentDTO = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new HmsException(ErrorCode.APPOINTMENT_NOT_FOUND)).toDTO();
        DoctorDTO doctorDTO = profileClient.getDoctorById(appointmentDTO.getDoctorId());
        PatientDTO patientDTO = profileClient.getPatientById(appointmentDTO.getPatientId());
        return AppointmentDetails.builder()
                .id(appointmentDTO.getId())
                .patientId(appointmentDTO.getPatientId())
                .patientName(patientDTO.getName())
                .doctorId(appointmentDTO.getDoctorId())
                .doctorName(doctorDTO.getName())
                .appointmentTime(appointmentDTO.getAppointmentTime())
                .status(appointmentDTO.getStatus())
                .reason(appointmentDTO.getReason())
                .notes(appointmentDTO.getNotes())
                .patientEmail(patientDTO.getEmail())
                .patientPhone(patientDTO.getPhone()).build();
    }

    @Override
    public List<AppointmentDetails> getAllAppointmentDetailsByPatientId(Long patientId) {
        return appointmentRepository.findAllByPatientId(patientId).stream()
                .map(appointment -> {
                    DoctorDTO doctorDTO =
                            profileClient.getDoctorById(appointment.getDoctorId());
                    appointment.setDoctorName(doctorDTO.getName());
                    return appointment;
                }).toList();
    }

    @Override
    public List<AppointmentDetails> getAllAppointmentDetailsByDoctorId(Long doctorId) {
        return appointmentRepository.findAllByDoctorId(doctorId).stream()
                .map(appointment -> {
                    PatientDTO patientDTO =
                            profileClient.getPatientById(appointment.getPatientId());
                    appointment.setPatientName(patientDTO.getName());
                    appointment.setPatientEmail(patientDTO.getEmail());
                    appointment.setPatientPhone(patientDTO.getPhone());
                    return appointment;
                }).toList();
    }

    @Override
    @Cacheable(value = "stats_dashboard", key = "'visit_count_patient_' + #patientId")
    public List<MonthlyVisitDTO> getAppointmentCountByPatient(Long patientId) {
        return appointmentRepository.countCurrentYearVisitsByPatient(patientId);
    }

    @Override
    @Cacheable(value = "stats_dashboard", key = "'visit_count_doctor_' + #doctorId")
    public List<MonthlyVisitDTO> getAppointmentCountByDoctor(Long doctorId) {
        return appointmentRepository.countCurrentYearVisitsByDoctor(doctorId);
    }

    @Override
    @Cacheable(value = "stats_dashboard", key = "'patient_count_doctor_' + #doctorId")
    public List<MonthlyVisitDTO> getPatientCountByDoctor(Long doctorId) {
        return appointmentRepository.countCurrentYearPatientsByDoctor(doctorId);
    }

    @Override
    @Cacheable(value = "stats_dashboard", key = "'total_visit_count'")
    public List<MonthlyVisitDTO> getAppointmentCount() {
        return appointmentRepository.countCurrentYearVisits();
    }

    @Override
    @Cacheable(value = "stats_reasons", key = "'reasons_patient_' + #patientId")
    public List<ReasonCountDTO> getReasonCountByPatient(Long patientId) {
        return appointmentRepository.countReasonsByPatientId(patientId);
    }

    @Override
    @Cacheable(value = "stats_reasons", key = "'reasons_doctor_' + #doctorId")
    public List<ReasonCountDTO> getReasonCountByDoctor(Long doctorId) {
        return appointmentRepository.countReasonsByDoctorId(doctorId);
    }

    @Override
    @Cacheable(value = "stats_reasons", key = "'total_reasons'")
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

//    @Override
//    public List<PatientDTO> getAllPatientsByDoctorId(Long doctorId) {
////        List<Long> patientIds = appointmentRepository.getAllPatientIdsByDoctorId(doctorId);
//
//    }
}
