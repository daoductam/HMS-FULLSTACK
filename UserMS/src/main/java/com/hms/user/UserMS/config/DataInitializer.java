package com.hms.user.UserMS.config;

import com.hms.user.UserMS.dto.Roles;
import com.hms.user.UserMS.dto.UserStatus;
import com.hms.user.UserMS.entity.User;
import com.hms.user.UserMS.repository.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@ApplicationScoped
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;

    @Transactional
    public void run(@Observes StartupEvent ev) {
        // Kiểm tra xem tài khoản admin đã tồn tại chưa
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            User admin = User.builder()
                    .name("System Administrator")
                    .email("admin@gmail.com")
                    .password(BcryptUtil.bcryptHash("admin123")) // Mật khẩu là admin123
                    .role(Roles.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            userRepository.persist(admin);
            System.out.println(">>> ĐÃ TẠO TÀI KHOẢN ADMIN THÀNH CÔNG: admin@gmail.com / admin123");
        }
    }
}