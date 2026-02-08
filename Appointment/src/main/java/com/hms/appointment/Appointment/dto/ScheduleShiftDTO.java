package com.hms.appointment.Appointment.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ScheduleShiftDTO {
    Long id;
    Long shiftId;
    ShiftDTO shift;
    Integer maxSlots;
    Integer bookedSlots;
    Integer availableSlots; // maxSlots - bookedSlots
}


