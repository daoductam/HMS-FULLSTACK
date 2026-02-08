package com.hms.appointment.Appointment.dto;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class LockScheduleRequest {
    Long doctorId;
    LocalDate scheduleDate;
    String reason; // Lý do khóa (nghỉ phép, bận việc, ...)
}


