package com.hms.user.UserMS.jwt;

import com.hms.user.UserMS.dto.UserDTO;
import com.hms.user.UserMS.exception.HmsException;
import com.hms.user.UserMS.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class MyUserDetailsService {

    private final UserService userService;

    public CustomerUserDetails loadUserByEmail(String email) {
        try {
            UserDTO dto = userService.getUser(email);
            return CustomerUserDetails.builder()
                    .id(dto.getId())
                    .email(dto.getEmail())
                    .username(dto.getEmail())
                    .password(dto.getPassword())
                    .role(dto.getRole())
                    .profileId(dto.getProfileId())
                    .name(dto.getName())
                    .build();
        } catch (HmsException e) {
            e.printStackTrace();
        }
        return null;
    }
}

