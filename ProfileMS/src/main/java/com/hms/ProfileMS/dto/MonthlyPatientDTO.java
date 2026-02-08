package com.hms.ProfileMS.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class MonthlyPatientDTO {
    private String month;
    private Long count;
}
