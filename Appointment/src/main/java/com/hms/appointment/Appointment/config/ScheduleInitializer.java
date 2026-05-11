package com.hms.appointment.Appointment.config;

import com.hms.appointment.Appointment.service.ScheduleService;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ScheduleInitializer {
    
    @Inject
    ScheduleService scheduleService;

    @Startup
    public void init() {
        // Khởi tạo ca làm việc mặc định khi ứng dụng khởi động
        scheduleService.initializeDefaultShifts();
    }
}



