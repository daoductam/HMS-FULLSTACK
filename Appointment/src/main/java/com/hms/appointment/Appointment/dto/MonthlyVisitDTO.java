package com.hms.appointment.Appointment.dto;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class MonthlyVisitDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String month;
    private Long count;
}
