package com.hms.user.UserMS.service;

import com.hms.user.UserMS.clients.Profile;
import com.hms.user.UserMS.clients.ProfileClient;
import com.hms.user.UserMS.dto.*;
import com.hms.user.UserMS.entity.User;
import com.hms.user.UserMS.exception.ErrorCode;
import com.hms.user.UserMS.exception.HmsException;
import com.hms.user.UserMS.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service("userService")
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileClient profileClient;

    @Override
    public void registerUser(UserDTO userDTO) throws HmsException {
        Optional<User> opt = userRepository.findByEmail(userDTO.getEmail());
        if (opt.isPresent()) {
            throw new HmsException(ErrorCode.USER_ALREADY_EXISTS);
        }
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        if (userDTO.getRole().equals(Roles.DOCTOR)) {
            // Nếu là Bác sĩ -> Trạng thái CHỜ DUYỆT
            userDTO.setStatus(UserStatus.PENDING);
        } else {
            // Nếu là Bệnh nhân -> Trạng thái HOẠT ĐỘNG luôn
            userDTO.setStatus(UserStatus.ACTIVE);
        }

        Long profileId = null;
        if (userDTO.getRole().equals(Roles.DOCTOR)) {
            profileId = profileClient.addDoctor(userDTO);
        } else if (userDTO.getRole().equals(Roles.PATIENT)) {
            profileId = profileClient.addPatient(userDTO);
        }
        System.out.println(profileId);
        userDTO.setProfileId(profileId);
        userRepository.save(userDTO.toEntity());
    }

    @Override
    public UserDTO loginUser(UserDTO userDTO) throws HmsException {
        User user = userRepository.findByEmail(userDTO.getEmail())
                .orElseThrow(()->new HmsException(ErrorCode.EMAIL_NOT_FOUND));
        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new HmsException(ErrorCode.INVALID_CREDENTIALS);
        }
        System.out.println("status "+user.getStatus());
        log.info("status: {}", user.getStatus());
        if (UserStatus.PENDING.equals(user.getStatus())) {
            // Lưu ý: Bạn cần chắc chắn đã thêm ACCOUNT_PENDING_APPROVAL vào Enum ErrorCode
            // Nếu chưa có, tạm thời dùng throw new HmsException("Account is pending approval");
            throw new HmsException(ErrorCode.ACCOUNT_PENDING_APPROVAL);
        }

        if (UserStatus.LOCKED.equals(user.getStatus()) || UserStatus.REJECTED.equals(user.getStatus())) {
            throw new HmsException(ErrorCode.ACCOUNT_LOCKED);
        }
//        user.setPassword(null);
        return user.toDTO();
    }

    @Override
    public UserDTO getUserById(Long id) throws HmsException {
        return userRepository.findById(id)
                .orElseThrow(() -> new  HmsException(ErrorCode.USER_NOT_FOUND)).toDTO();
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        return null;
    }

    @Override
    public UserDTO getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new HmsException(ErrorCode.EMAIL_NOT_FOUND)).toDTO();
    }

    @Override
    public Long getProfile(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new HmsException(ErrorCode.USER_NOT_FOUND));
        System.out.println("Checking profile for user: " + user.getEmail() + " | Role: " + user.getRole());
        if (user.getRole().equals(Roles.DOCTOR)) {
            return profileClient.getDoctor(user.getProfileId());
        } else if (user.getRole().equals(Roles.PATIENT)) {
            return profileClient.getPatient(user.getProfileId());
        }
        throw new HmsException(ErrorCode.INVALID_USER_ROLE);
    }

    @Override
    public RegistrationCountsDTO getMonthlyRegistrationCounts() {
        List<MonthlyRoleCountDTO> doctorCounts =
                userRepository.countRegistrationsByRoleGroupedByMonth(Roles.DOCTOR);
        List<MonthlyRoleCountDTO> patientCounts =
                userRepository.countRegistrationsByRoleGroupedByMonth(Roles.PATIENT);
        return new RegistrationCountsDTO(doctorCounts, patientCounts);
    }

    @Override
    public void updateUserStatus(Long userId, UserStatus newStatus) throws HmsException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HmsException(ErrorCode.USER_NOT_FOUND));

        user.setStatus(newStatus);
        userRepository.save(user);

        // (Tùy chọn) Gửi email thông báo cho bác sĩ tại đây:
        // notificationClient.sendApprovalEmail(user.getEmail());
    }

    @Override
    public List<UserDTO> getPendingDoctors() {
        // Bạn cần viết thêm method trong UserRepository: findByRoleAndStatus(Roles role, UserStatus status)
        return userRepository.findByRoleAndStatus(Roles.DOCTOR, UserStatus.PENDING)
                .stream().map(User::toDTO).toList();
    }
}
