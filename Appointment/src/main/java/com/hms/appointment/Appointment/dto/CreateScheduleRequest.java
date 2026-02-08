package com.hms.appointment.Appointment.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CreateScheduleRequest {
    Long doctorId;
    LocalDate scheduleDate;
    List<ShiftConfig> shifts; // Danh sách ca làm việc với maxSlots
    
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class ShiftConfig {
        Long shiftId;
        Integer maxSlots; // Số slot tối đa cho ca này
    }
}


