package com.hms.appointment.Appointment.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class DoctorScheduleDTO {
    Long id;
    Long doctorId;
    LocalDate scheduleDate;
    Boolean isLocked;
    String lockReason;
    List<ScheduleShiftDTO> shifts;
}


