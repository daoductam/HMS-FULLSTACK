package com.hms.appointment.Appointment.config;

import com.hms.appointment.Appointment.service.ScheduleService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleInitializer {
    private final ScheduleService scheduleService;

    @PostConstruct
    public void init() {
        // Khởi tạo ca làm việc mặc định khi ứng dụng khởi động
        scheduleService.initializeDefaultShifts();
    }
}


