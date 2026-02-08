package com.hms.ProfileMS.entity;


import com.hms.ProfileMS.dto.BloodGroup;
import com.hms.ProfileMS.dto.DoctorDTO;
import com.hms.ProfileMS.dto.PatientDTO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    @Column(unique = true)
    String email;
    LocalDate dob;
    Long profilePictureId;
    String phone;
    String address;
    @Column(unique = true)
    String licenseNo;
    String specialization;
    String department;
    Integer totalExp;

    public DoctorDTO toDTO() {
        return DoctorDTO.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .phone(this.phone)
                .dob(this.dob)
                .profilePictureId(profilePictureId)
                .address(this.address)
                .licenseNo(this.licenseNo)
                .specialization(this.specialization)
                .department(this.department)
                .totalExp(this.totalExp).build();
    }

}
