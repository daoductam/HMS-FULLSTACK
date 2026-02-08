package com.hms.appointment.Appointment.dto;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PatientDTO {
    Long id;
    String name;
    String email;
//    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dob;
    String phone;
    String address;
    String CCCD;
    BloodGroup bloodGroup;
    String allergies;
    String chronicDisease;


}
