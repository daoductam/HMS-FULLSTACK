package com.hms.appointment.Appointment.service;

import com.hms.appointment.Appointment.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    DoctorScheduleDTO createSchedule(CreateScheduleRequest request);
    DoctorScheduleDTO lockSchedule(LockScheduleRequest request);
    DoctorScheduleDTO unlockSchedule(Long doctorId, LocalDate scheduleDate);
    DoctorScheduleDTO getSchedule(Long doctorId, LocalDate scheduleDate);
    List<DoctorScheduleDTO> getSchedulesByDoctor(Long doctorId, LocalDate startDate, LocalDate endDate);
    boolean checkSlotAvailability(Long doctorId, LocalDate scheduleDate, Long shiftId);
    void incrementBookedSlots(Long doctorId, LocalDate scheduleDate, Long shiftId);
    void decrementBookedSlots(Long doctorId, LocalDate scheduleDate, Long shiftId);
    boolean isScheduleLocked(Long doctorId, LocalDate scheduleDate);
    boolean isValidAppointmentTime(Long doctorId, LocalDate scheduleDate, int hour);
    List<ShiftDTO> getAllShifts();
    void initializeDefaultShifts(); // Khởi tạo ca làm việc mặc định (Sáng, Chiều)
}


