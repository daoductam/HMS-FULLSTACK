package com.hms.user.UserMS.dto;

import com.hms.user.UserMS.entity.User;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
    Long id;
    @NotBlank(message = "Name is mandatory")
    String name;
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    String email;
    @NotBlank(message = "Password is mandatory")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Mật khẩu phải có ít nhất 8 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt"
    )
    String password;
    Roles role;
    Long profileId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    UserStatus status;
    public User toEntity() {
        return User.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .password(this.password)
                .role(this.role)
                .profileId(this.profileId)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .status(status)
                .build();
    }
}
