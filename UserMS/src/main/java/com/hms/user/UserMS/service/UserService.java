package com.hms.user.UserMS.service;

import com.hms.user.UserMS.clients.Profile;
import com.hms.user.UserMS.dto.RegistrationCountsDTO;
import com.hms.user.UserMS.dto.UserDTO;
import com.hms.user.UserMS.dto.UserStatus;
import com.hms.user.UserMS.exception.HmsException;

import java.util.List;

public interface UserService {
    void registerUser(UserDTO userDTO);
    UserDTO loginUser (UserDTO userDTO);
    UserDTO getUserById(Long id);
    UserDTO updateUser(UserDTO userDTO);
    UserDTO getUser(String email);
    Long getProfile(Long id);
    RegistrationCountsDTO getMonthlyRegistrationCounts();
    void updateUserStatus(Long userId, UserStatus newStatus) throws HmsException;
    List<UserDTO> getPendingDoctors(); // Lấy danh sách bác sĩ chờ duyệt
}
