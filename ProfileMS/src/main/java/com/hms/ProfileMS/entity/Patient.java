package com.hms.ProfileMS.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.hms.ProfileMS.dto.BloodGroup;
import com.hms.ProfileMS.dto.PatientDTO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    @Column(unique = true)
    String email;
//    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dob;
    Long profilePictureId;
    String phone;
    String address;
    @Column(unique = true)
    String CCCD;
    BloodGroup bloodGroup;
    String allergies;
    String chronicDisease;

    public PatientDTO toDTO() {
        return PatientDTO.builder()
                .id(this.id)
                .name(this.name)
                .dob(this.dob)
                .profilePictureId(profilePictureId)
                .email(this.email)
                .phone(this.phone)
                .address(this.address)
                .CCCD(this.CCCD)
                .bloodGroup(this.bloodGroup)
                .allergies(this.allergies)
                .chronicDisease(this.chronicDisease)
                .build();
    }

}
