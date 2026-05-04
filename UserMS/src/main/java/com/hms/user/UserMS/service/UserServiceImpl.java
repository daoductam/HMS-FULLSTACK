package com.hms.user.UserMS.service;

import com.hms.user.UserMS.clients.ProfileClient;
import com.hms.user.UserMS.dto.*;
import com.hms.user.UserMS.entity.User;
import com.hms.user.UserMS.exception.ErrorCode;
import com.hms.user.UserMS.exception.HmsException;
import com.hms.user.UserMS.repository.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProfileClient profileClient;

    public UserServiceImpl(UserRepository userRepository, @RestClient ProfileClient profileClient) {
        this.userRepository = userRepository;
        this.profileClient = profileClient;
    }

    @Override
    public void registerUser(UserDTO userDTO) throws HmsException {
        Optional<User> opt = userRepository.findByEmail(userDTO.getEmail());
        if (opt.isPresent()) {
            throw new HmsException(ErrorCode.USER_ALREADY_EXISTS);
        }
        
        // Use BcryptUtil for Quarkus
        userDTO.setPassword(BcryptUtil.bcryptHash(userDTO.getPassword()));

        if (userDTO.getRole().equals(Roles.DOCTOR)) {
            userDTO.setStatus(UserStatus.PENDING);
        } else {
            userDTO.setStatus(UserStatus.ACTIVE);
        }

        Long profileId = null;
        if (userDTO.getRole().equals(Roles.DOCTOR)) {
            profileId = profileClient.addDoctor(userDTO);
        } else if (userDTO.getRole().equals(Roles.PATIENT)) {
            profileId = profileClient.addPatient(userDTO);
        }
        userDTO.setProfileId(profileId);
        userRepository.persist(userDTO.toEntity());
    }

    @Override
    public UserDTO loginUser(UserDTO userDTO) throws HmsException {
        User user = userRepository.findByEmail(userDTO.getEmail())
                .orElseThrow(() -> new HmsException(ErrorCode.EMAIL_NOT_FOUND));
        
        if (!BcryptUtil.matches(userDTO.getPassword(), user.getPassword())) {
            throw new HmsException(ErrorCode.INVALID_CREDENTIALS);
        }
        
        log.info("status: {}", user.getStatus());
        if (UserStatus.PENDING.equals(user.getStatus())) {
            throw new HmsException(ErrorCode.ACCOUNT_PENDING_APPROVAL);
        }

        if (UserStatus.LOCKED.equals(user.getStatus()) || UserStatus.REJECTED.equals(user.getStatus())) {
            throw new HmsException(ErrorCode.ACCOUNT_LOCKED);
        }
        return user.toDTO();
    }

    @Override
    public UserDTO getUserById(Long id) throws HmsException {
        return userRepository.findByIdOptional(id)
                .orElseThrow(() -> new HmsException(ErrorCode.USER_NOT_FOUND)).toDTO();
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
        User user = userRepository.findByIdOptional(id).orElseThrow(() -> new HmsException(ErrorCode.USER_NOT_FOUND));
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
        User user = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new HmsException(ErrorCode.USER_NOT_FOUND));

        user.setStatus(newStatus);
        // (Tùy chọn) Gửi email thông báo cho bác sĩ tại đây:
        // notificationClient.sendApprovalEmail(user.getEmail());
        userRepository.persist(user);
    }

    @Override
    public List<UserDTO> getPendingDoctors() {
        return userRepository.findByRoleAndStatus(Roles.DOCTOR, UserStatus.PENDING)
                .stream().map(User::toDTO).toList();
    }
}

