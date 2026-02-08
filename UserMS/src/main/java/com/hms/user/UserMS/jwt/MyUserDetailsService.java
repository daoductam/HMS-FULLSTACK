package com.hms.user.UserMS.jwt;

import com.hms.user.UserMS.dto.UserDTO;
import com.hms.user.UserMS.exception.HmsException;
import com.hms.user.UserMS.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final UserService userService;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
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
                    .authorities(null).build();
        } catch (HmsException e) {
            e.printStackTrace();
        }

        return null;
    }
}
