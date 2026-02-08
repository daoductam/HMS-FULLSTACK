package com.hms.appointment.Appointment.dto;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ReasonCountDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String reason;
    private Long count;
}
