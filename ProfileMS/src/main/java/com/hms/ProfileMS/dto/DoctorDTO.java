package com.hms.ProfileMS.dto;


import com.hms.ProfileMS.entity.Doctor;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    Long id;
    String name;
    String email;
    LocalDate dob;
    Long profilePictureId;

    String phone;
    String address;
    String licenseNo;
    String specialization;
    String department;
    Integer totalExp;

    public Doctor toEntity() {
        return Doctor.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .dob(this.dob)
                .profilePictureId(profilePictureId)
                .phone(this.phone)
                .address(this.address)
                .licenseNo(this.licenseNo)
                .specialization(this.specialization)
                .department(this.department)
                .totalExp(this.totalExp).build();
    }

}
