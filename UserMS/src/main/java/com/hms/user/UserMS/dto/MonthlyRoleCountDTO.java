package com.hms.user.UserMS.dto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MonthlyRoleCountDTO {
    private String month;
    private Long count;
}
