package io.volunteerapp.volunteer_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import io.volunteerapp.volunteer_app.Util.RestResponse;
import io.volunteerapp.volunteer_app.Util.SecurityUtil;
import io.volunteerapp.volunteer_app.model.User;
import io.volunteerapp.volunteer_app.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityUtil securityUtil;

    @PostMapping("/register")
    public ResponseEntity<RestResponse<UserDTO>> register(@Valid @RequestBody RegisterRequest request) {

        // Kiểm tra email đã tồn tại
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            RestResponse<UserDTO> response = new RestResponse<>();
            response.setStatus(400);
            response.setError("Bad Request");
            response.setMessage("Email đã được sử dụng");
            return ResponseEntity.badRequest().body(response);
        }

        // Tạo user mới
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Mã hóa password
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole("USER"); // Mặc định là USER
        user.setStatus("ACTIVE");
        user.setUpdatedAt(Instant.now());

        // Lưu vào database
        User savedUser = userRepository.save(user);

        // Trả về DTO (không trả password)
        UserDTO userDTO = new UserDTO(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getRole(),
                savedUser.getStatus());

        RestResponse<UserDTO> response = new RestResponse<>();
        response.setStatus(201);
        response.setMessage("Đăng ký thành công");
        response.setData(userDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<RestResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {

        // Tìm user theo email
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            RestResponse<LoginResponse> response = new RestResponse<>();
            response.setStatus(401);
            response.setError("Unauthorized");
            response.setMessage("Email hoặc mật khẩu không đúng");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        User user = userOptional.get();

        // Kiểm tra password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            RestResponse<LoginResponse> response = new RestResponse<>();
            response.setStatus(401);
            response.setError("Unauthorized");
            response.setMessage("Email hoặc mật khẩu không đúng");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Tạo JWT token
        // Token chứa claim "scope" = "ROLE_USER" hoặc "ROLE_ADMIN"
        String accessToken = securityUtil.generateAccessToken(user);
        String refreshToken = securityUtil.generateRefreshToken(user);

        LoginResponse loginResponse = new LoginResponse(
                accessToken,
                refreshToken,
                new UserDTO(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getRole(),
                        user.getStatus()));

        RestResponse<LoginResponse> response = new RestResponse<>();
        response.setStatus(200);
        response.setMessage("Đăng nhập thành công");
        response.setData(loginResponse);

        return ResponseEntity.ok(response);
    }

    // ===== DTO Classes =====

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        private String email;
        private String password;
        private String fullName;
        private String phone;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private String accessToken;
        private String refreshToken;
        private UserDTO user;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDTO {
        private Integer id;
        private String fullName;
        private String email;
        private String phone;
        private String role;
        private String status;
    }
}
