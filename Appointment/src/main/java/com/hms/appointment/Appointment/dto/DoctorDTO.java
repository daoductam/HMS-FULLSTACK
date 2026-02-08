package com.hms.appointment.Appointment.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorDTO {
    Long id;
    String name;
    String email;
    LocalDate dob;
    String phone;
    String address;
    String licenseNo;
    String specialization;
    String department;
    Integer totalExp;



}
