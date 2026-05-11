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
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final DoctorScheduleRepository scheduleRepository;
    private final ScheduleShiftRepository scheduleShiftRepository;
    private final ShiftRepository shiftRepository;
    
    @Inject
    @RestClient
    ProfileClient profileClient;

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

        scheduleRepository.persist(schedule);

        // Create schedule shifts
        for (CreateScheduleRequest.ShiftConfig shiftConfig : request.getShifts()) {
            Shift shift = shiftRepository.findByIdOptional(shiftConfig.getShiftId())
                    .orElseThrow(() -> new HmsException(ErrorCode.SHIFT_NOT_FOUND));

            ScheduleShift scheduleShift = ScheduleShift.builder()
                    .schedule(schedule)
                    .shift(shift)
                    .maxSlots(shiftConfig.getMaxSlots())
                    .bookedSlots(0)
                    .build();

            scheduleShiftRepository.persist(scheduleShift);
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
        scheduleRepository.persist(schedule);

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
        scheduleRepository.persist(schedule);

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
        if (isScheduleLocked(doctorId, scheduleDate)) {
            return false;
        }

        ScheduleShift scheduleShift = scheduleShiftRepository
                .findByDoctorIdAndDateAndShiftId(doctorId, scheduleDate, shiftId)
                .orElse(null);

        if (scheduleShift == null) {
            return false;
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
        scheduleShiftRepository.persist(scheduleShift);
    }

    @Override
    @Transactional
    public void decrementBookedSlots(Long doctorId, LocalDate scheduleDate, Long shiftId) {
        ScheduleShift scheduleShift = scheduleShiftRepository
                .findByDoctorIdAndDateAndShiftId(doctorId, scheduleDate, shiftId)
                .orElse(null);

        if (scheduleShift != null && scheduleShift.getBookedSlots() > 0) {
            scheduleShift.setBookedSlots(scheduleShift.getBookedSlots() - 1);
            scheduleShiftRepository.persist(scheduleShift);
        }
    }

    @Override
    public boolean isScheduleLocked(Long doctorId, LocalDate scheduleDate) {
        return scheduleRepository.findByDoctorIdAndScheduleDate(doctorId, scheduleDate)
                .map(DoctorSchedule::getIsLocked)
                .orElse(true);
    }

    @Override
    public boolean isValidAppointmentTime(Long doctorId, LocalDate scheduleDate, int hour) {
        DoctorSchedule schedule = scheduleRepository
                .findByDoctorIdAndScheduleDate(doctorId, scheduleDate)
                .orElse(null);

        if (schedule == null || schedule.getIsLocked()) {
            return false;
        }

        List<ScheduleShift> shifts = scheduleShiftRepository.findByScheduleIdWithShift(schedule.getId());
        
        for (ScheduleShift scheduleShift : shifts) {
            Shift shift = scheduleShift.getShift();
            if (hour >= shift.getStartHour() && hour < shift.getEndHour()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<ShiftDTO> getAllShifts() {
        return shiftRepository.listAll().stream()
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
        if (shiftRepository.count() == 0) {
            Shift morningShift = Shift.builder()
                    .name("MORNING")
                    .displayName("Sáng")
                    .startHour(7)
                    .endHour(11)
                    .build();
            shiftRepository.persist(morningShift);

            Shift afternoonShift = Shift.builder()
                    .name("AFTERNOON")
                    .displayName("Chiều")
                    .startHour(13)
                    .endHour(17)
                    .build();
            shiftRepository.persist(afternoonShift);
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



