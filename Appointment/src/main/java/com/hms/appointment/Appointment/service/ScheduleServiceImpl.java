package com.hms.appointment.Appointment.service;

import com.hms.appointment.Appointment.clients.ProfileClient;
import com.hms.appointment.Appointment.dto.*;
import com.hms.appointment.Appointment.entity.DoctorSchedule;
import com.hms.appointment.Appointment.entity.ScheduleShift;
import com.hms.appointment.Appointment.entity.Shift;
import com.hms.appointment.Appointment.exception.ErrorCode;
import com.hms.appointment.Appointment.exception.HmsException;
import com.hms.appointment.Appointment.repository.DoctorScheduleRepository;
import com.hms.appointment.Appointment.repository.ScheduleShiftRepository;
import com.hms.appointment.Appointment.repository.ShiftRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final DoctorScheduleRepository scheduleRepository;
    private final ScheduleShiftRepository scheduleShiftRepository;
    private final ShiftRepository shiftRepository;
    private final ProfileClient profileClient;

    @Override
    @Transactional
    public DoctorScheduleDTO createSchedule(CreateScheduleRequest request) {
        // Validate doctor
        Boolean doctorExists = profileClient.doctorExists(request.getDoctorId());
        if (doctorExists == null || !doctorExists) {
            throw new HmsException(ErrorCode.DOCTOR_NOT_FOUND);
        }

        // Check if schedule already exists
        if (scheduleRepository.findByDoctorIdAndScheduleDate(
                request.getDoctorId(), request.getScheduleDate()).isPresent()) {
            throw new HmsException(ErrorCode.SCHEDULE_ALREADY_EXISTS);
        }

        // Create schedule
        DoctorSchedule schedule = DoctorSchedule.builder()
                .doctorId(request.getDoctorId())
                .scheduleDate(request.getScheduleDate())
                .isLocked(false)
                .lockReason(null)
                .build();

        schedule = scheduleRepository.save(schedule);

        // Create schedule shifts
        for (CreateScheduleRequest.ShiftConfig shiftConfig : request.getShifts()) {
            Shift shift = shiftRepository.findById(shiftConfig.getShiftId())
                    .orElseThrow(() -> new HmsException(ErrorCode.SHIFT_NOT_FOUND));

            ScheduleShift scheduleShift = ScheduleShift.builder()
                    .schedule(schedule)
                    .shift(shift)
                    .maxSlots(shiftConfig.getMaxSlots())
                    .bookedSlots(0)
                    .build();

            scheduleShiftRepository.save(scheduleShift);
        }

        return toDTO(schedule);
    }

    @Override
    @Transactional
    public DoctorScheduleDTO lockSchedule(LockScheduleRequest request) {
        DoctorSchedule schedule = scheduleRepository
                .findByDoctorIdAndScheduleDate(request.getDoctorId(), request.getScheduleDate())
                .orElseThrow(() -> new HmsException(ErrorCode.SCHEDULE_NOT_FOUND));

        schedule.setIsLocked(true);
        schedule.setLockReason(request.getReason());
        schedule = scheduleRepository.save(schedule);

        return toDTO(schedule);
    }

    @Override
    @Transactional
    public DoctorScheduleDTO unlockSchedule(Long doctorId, LocalDate scheduleDate) {
        DoctorSchedule schedule = scheduleRepository
                .findByDoctorIdAndScheduleDate(doctorId, scheduleDate)
                .orElseThrow(() -> new HmsException(ErrorCode.SCHEDULE_NOT_FOUND));

        schedule.setIsLocked(false);
        schedule.setLockReason(null);
        schedule = scheduleRepository.save(schedule);

        return toDTO(schedule);
    }

    @Override
    public DoctorScheduleDTO getSchedule(Long doctorId, LocalDate scheduleDate) {
        DoctorSchedule schedule = scheduleRepository
                .findByDoctorIdAndScheduleDate(doctorId, scheduleDate)
                .orElseThrow(() -> new HmsException(ErrorCode.SCHEDULE_NOT_FOUND));

        return toDTO(schedule);
    }

    @Override
    public List<DoctorScheduleDTO> getSchedulesByDoctor(Long doctorId, LocalDate startDate, LocalDate endDate) {
        List<DoctorSchedule> schedules = scheduleRepository
                .findByDoctorIdAndScheduleDateBetween(doctorId, startDate, endDate);

        return schedules.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkSlotAvailability(Long doctorId, LocalDate scheduleDate, Long shiftId) {
        // Check if schedule is locked
        if (isScheduleLocked(doctorId, scheduleDate)) {
            return false;
        }

        ScheduleShift scheduleShift = scheduleShiftRepository
                .findByDoctorIdAndDateAndShiftId(doctorId, scheduleDate, shiftId)
                .orElse(null);

        if (scheduleShift == null) {
            return false; // No schedule for this shift
        }

        return scheduleShift.getBookedSlots() < scheduleShift.getMaxSlots();
    }

    @Override
    @Transactional
    public void incrementBookedSlots(Long doctorId, LocalDate scheduleDate, Long shiftId) {
        ScheduleShift scheduleShift = scheduleShiftRepository
                .findByDoctorIdAndDateAndShiftId(doctorId, scheduleDate, shiftId)
                .orElseThrow(() -> new HmsException(ErrorCode.SCHEDULE_NOT_FOUND));

        if (scheduleShift.getBookedSlots() >= scheduleShift.getMaxSlots()) {
            throw new HmsException(ErrorCode.NO_AVAILABLE_SLOTS);
        }

        scheduleShift.setBookedSlots(scheduleShift.getBookedSlots() + 1);
        scheduleShiftRepository.save(scheduleShift);
    }

    @Override
    @Transactional
    public void decrementBookedSlots(Long doctorId, LocalDate scheduleDate, Long shiftId) {
        ScheduleShift scheduleShift = scheduleShiftRepository
                .findByDoctorIdAndDateAndShiftId(doctorId, scheduleDate, shiftId)
                .orElse(null);

        if (scheduleShift != null && scheduleShift.getBookedSlots() > 0) {
            scheduleShift.setBookedSlots(scheduleShift.getBookedSlots() - 1);
            scheduleShiftRepository.save(scheduleShift);
        }
    }

    @Override
    public boolean isScheduleLocked(Long doctorId, LocalDate scheduleDate) {
        return scheduleRepository.findByDoctorIdAndScheduleDate(doctorId, scheduleDate)
                .map(DoctorSchedule::getIsLocked)
                .orElse(true); // Nếu không có lịch, coi như bị khóa
    }

    @Override
    public boolean isValidAppointmentTime(Long doctorId, LocalDate scheduleDate, int hour) {
        // Check if schedule exists and is not locked
        DoctorSchedule schedule = scheduleRepository
                .findByDoctorIdAndScheduleDate(doctorId, scheduleDate)
                .orElse(null);

        if (schedule == null) {
            System.out.println("❌ Schedule not found for doctorId: " + doctorId + ", date: " + scheduleDate);
            return false;
        }
        
        if (schedule.getIsLocked()) {
            System.out.println("❌ Schedule is locked for doctorId: " + doctorId + ", date: " + scheduleDate);
            return false;
        }

        // Check if hour falls within any shift (use JOIN FETCH to load shift)
        List<ScheduleShift> shifts = scheduleShiftRepository.findByScheduleIdWithShift(schedule.getId());
        
        if (shifts.isEmpty()) {
            System.out.println("❌ No shifts found for scheduleId: " + schedule.getId());
            return false;
        }
        
        System.out.println("🔍 Checking hour: " + hour + " against " + shifts.size() + " shifts");
        
        for (ScheduleShift scheduleShift : shifts) {
            Shift shift = scheduleShift.getShift();
            System.out.println("  - Shift: " + shift.getDisplayName() + " (" + shift.getStartHour() + "-" + shift.getEndHour() + ")");
            
            // Check if hour falls within shift range
            // Note: endHour is exclusive (e.g., 7-11 means 7, 8, 9, 10 are valid, but 11 is not)
            if (hour >= shift.getStartHour() && hour < shift.getEndHour()) {
                System.out.println("✅ Hour " + hour + " is valid for shift " + shift.getDisplayName());
                return true;
            }
        }

        System.out.println("❌ Hour " + hour + " does not fall within any shift");
        return false;
    }

    @Override
    public List<ShiftDTO> getAllShifts() {
        return shiftRepository.findAll().stream()
                .map(shift -> ShiftDTO.builder()
                        .id(shift.getId())
                        .name(shift.getName())
                        .displayName(shift.getDisplayName())
                        .startHour(shift.getStartHour())
                        .endHour(shift.getEndHour())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void initializeDefaultShifts() {
        // Chỉ tạo nếu chưa có
        if (shiftRepository.count() == 0) {
            Shift morningShift = Shift.builder()
                    .name("MORNING")
                    .displayName("Sáng")
                    .startHour(7)
                    .endHour(11)
                    .build();
            shiftRepository.save(morningShift);

            Shift afternoonShift = Shift.builder()
                    .name("AFTERNOON")
                    .displayName("Chiều")
                    .startHour(13)
                    .endHour(17)
                    .build();
            shiftRepository.save(afternoonShift);
        }
    }

    private DoctorScheduleDTO toDTO(DoctorSchedule schedule) {
        List<ScheduleShiftDTO> shiftDTOs = scheduleShiftRepository
                .findByScheduleIdWithShift(schedule.getId())
                .stream()
                .map(ss -> {
                    Shift shift = ss.getShift();
                    return ScheduleShiftDTO.builder()
                            .id(ss.getId())
                            .shiftId(shift.getId())
                            .shift(ShiftDTO.builder()
                                    .id(shift.getId())
                                    .name(shift.getName())
                                    .displayName(shift.getDisplayName())
                                    .startHour(shift.getStartHour())
                                    .endHour(shift.getEndHour())
                                    .build())
                            .maxSlots(ss.getMaxSlots())
                            .bookedSlots(ss.getBookedSlots())
                            .availableSlots(ss.getMaxSlots() - ss.getBookedSlots())
                            .build();
                })
                .collect(Collectors.toList());

        return DoctorScheduleDTO.builder()
                .id(schedule.getId())
                .doctorId(schedule.getDoctorId())
                .scheduleDate(schedule.getScheduleDate())
                .isLocked(schedule.getIsLocked())
                .lockReason(schedule.getLockReason())
                .shifts(shiftDTOs)
                .build();
    }
}


