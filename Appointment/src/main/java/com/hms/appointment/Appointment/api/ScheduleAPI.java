package com.hms.appointment.Appointment.api;

import com.hms.appointment.Appointment.dto.*;
import com.hms.appointment.Appointment.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/appointment/schedule")
@CrossOrigin
@RequiredArgsConstructor
public class ScheduleAPI {
    private final ScheduleService scheduleService;

    @PostMapping("/create")
    public ResponseEntity<DoctorScheduleDTO> createSchedule(@RequestBody CreateScheduleRequest request) {
        return new ResponseEntity<>(scheduleService.createSchedule(request), HttpStatus.CREATED);
    }

    @PostMapping("/lock")
    public ResponseEntity<DoctorScheduleDTO> lockSchedule(@RequestBody LockScheduleRequest request) {
        return new ResponseEntity<>(scheduleService.lockSchedule(request), HttpStatus.OK);
    }

    @PutMapping("/unlock/{doctorId}")
    public ResponseEntity<DoctorScheduleDTO> unlockSchedule(
            @PathVariable Long doctorId,
            @RequestParam LocalDate scheduleDate) {
        return new ResponseEntity<>(scheduleService.unlockSchedule(doctorId, scheduleDate), HttpStatus.OK);
    }

    @GetMapping("/get/{doctorId}")
    public ResponseEntity<DoctorScheduleDTO> getSchedule(
            @PathVariable Long doctorId,
            @RequestParam LocalDate scheduleDate) {
        return new ResponseEntity<>(scheduleService.getSchedule(doctorId, scheduleDate), HttpStatus.OK);
    }

    @GetMapping("/getAll/{doctorId}")
    public ResponseEntity<List<DoctorScheduleDTO>> getSchedulesByDoctor(
            @PathVariable Long doctorId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        // Nếu không có startDate/endDate, lấy 30 ngày từ hôm nay
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = startDate.plusDays(30);
        }
        return new ResponseEntity<>(
                scheduleService.getSchedulesByDoctor(doctorId, startDate, endDate),
                HttpStatus.OK);
    }

    @GetMapping("/shifts")
    public ResponseEntity<List<ShiftDTO>> getAllShifts() {
        return new ResponseEntity<>(scheduleService.getAllShifts(), HttpStatus.OK);
    }

    @PostMapping("/initialize-shifts")
    public ResponseEntity<String> initializeShifts() {
        scheduleService.initializeDefaultShifts();
        return new ResponseEntity<>("Đã khởi tạo ca làm việc mặc định", HttpStatus.OK);
    }
}


