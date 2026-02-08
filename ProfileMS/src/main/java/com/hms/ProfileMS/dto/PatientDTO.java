package com.hms.ProfileMS.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hms.ProfileMS.entity.Patient;
import jakarta.persistence.Column;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PatientDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    Long id;
    String name;
    String email;
//    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dob;
    Long profilePictureId;

    String phone;
    String address;
    String CCCD;
    BloodGroup bloodGroup;
    String allergies;
    String chronicDisease;


    public Patient toEntity() {
        return Patient.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .phone(this.phone)
                .dob(this.dob)
                .profilePictureId(profilePictureId)
                .address(this.address)
                .CCCD(this.CCCD)
                .bloodGroup(this.bloodGroup)
                .allergies(this.allergies)
                .chronicDisease(this.chronicDisease).build();
    }
}
