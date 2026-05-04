package com.hms.user.UserMS.jwt;

import com.hms.user.UserMS.dto.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerUserDetails {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Roles role;
    private String name;
    private Long profileId;
}

