package com.hms.appointment.Appointment.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ShiftDTO {
    Long id;
    String name; // "MORNING", "AFTERNOON"
    String displayName; // "Sáng", "Chiều"
    Integer startHour;
    Integer endHour;
}


