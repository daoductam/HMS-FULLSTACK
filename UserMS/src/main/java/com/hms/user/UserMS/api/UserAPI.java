package com.hms.user.UserMS.api;

import com.hms.user.UserMS.clients.Profile;
import com.hms.user.UserMS.dto.*;
import com.hms.user.UserMS.exception.ErrorCode;
import com.hms.user.UserMS.exception.HmsException;
import com.hms.user.UserMS.jwt.JwtUtil;
import com.hms.user.UserMS.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Validated
@CrossOrigin
@RequiredArgsConstructor
public class UserAPI {

    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerUser(@RequestBody @Valid UserDTO userDTO) throws HmsException {
        userService.registerUser(userDTO);
        return new ResponseEntity<>(new ResponseDTO("Account created"), HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginDTO loginDTO) throws HmsException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(),
                            loginDTO.getPassword())
            );

        } catch (AuthenticationException e) {
            throw new HmsException(ErrorCode.INVALID_CREDENTIALS);
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(loginDTO.getEmail());
        userDTO.setPassword(loginDTO.getPassword());

        // 2. Gọi hàm loginUser trong Service
        // Hàm này sẽ thực hiện:
        // - Kiểm tra email tồn tại
        // - Kiểm tra mật khẩu (passwordEncoder.matches)
        // - Kiểm tra trạng thái (PENDING, LOCKED...) -> Ném lỗi nếu vi phạm
        userService.loginUser(userDTO);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getEmail());
        final  String jwt = jwtUtil.generateToken(userDetails);
        return new ResponseEntity<>(jwt, HttpStatus.OK);

    }

    @GetMapping("/getProfile/{id}")
    public ResponseEntity<Long> getProfile(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getProfile(id), HttpStatus.OK);
    }

    @GetMapping("/getRegistrationCounts")
    public ResponseEntity<RegistrationCountsDTO> getMonthlyRegistrationCounts() {
        return new ResponseEntity<>(userService.getMonthlyRegistrationCounts(), HttpStatus.OK);
    }

    @PutMapping("/admin/approve/{id}")
     @PreAuthorize("hasRole('ADMIN')") // Nhớ thêm bảo mật
    public ResponseEntity<String> approveDoctor(@PathVariable Long id) {
        userService.updateUserStatus(id, UserStatus.ACTIVE);
        return new ResponseEntity<>("Doctor approved successfully", HttpStatus.OK);
    }

    @PutMapping("/admin/reject/{id}")
    public ResponseEntity<String> rejectDoctor(@PathVariable Long id) {
        userService.updateUserStatus(id, UserStatus.REJECTED);
        return new ResponseEntity<>("Doctor rejected", HttpStatus.OK);
    }

    @GetMapping("/getPendingDoctors")
    public ResponseEntity<List<UserDTO>> getPendingDoctors() {
        return new ResponseEntity<>(userService.getPendingDoctors(), HttpStatus.OK);
    }
}
