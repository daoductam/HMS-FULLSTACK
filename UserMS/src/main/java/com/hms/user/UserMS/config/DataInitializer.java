package com.hms.user.UserMS.config;

import com.hms.user.UserMS.dto.Roles;
import com.hms.user.UserMS.dto.UserStatus;
import com.hms.user.UserMS.entity.User;
import com.hms.user.UserMS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra xem tài khoản admin đã tồn tại chưa
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            User admin = User.builder()
                    .name("System Administrator")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("admin123")) // Mật khẩu là admin123
                    .role(Roles.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            userRepository.save(admin);
            System.out.println(">>> ĐÃ TẠO TÀI KHOẢN ADMIN THÀNH CÔNG: admin@gmail.com / admin123");
        }
    }
}