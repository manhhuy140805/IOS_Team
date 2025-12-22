package io.volunteerapp.volunteer_app.service;

import io.volunteerapp.volunteer_app.DTO.requeset.LoginRequest;
import io.volunteerapp.volunteer_app.DTO.requeset.RegisterRequest;
import io.volunteerapp.volunteer_app.DTO.response.LoginResponse;
import io.volunteerapp.volunteer_app.DTO.response.UserResponse;
import io.volunteerapp.volunteer_app.Util.RestResponse;
import io.volunteerapp.volunteer_app.Util.SecurityUtil;
import io.volunteerapp.volunteer_app.model.User;
import io.volunteerapp.volunteer_app.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final SecurityUtil securityUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, SecurityUtil securityUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityUtil = securityUtil;
    }

    public ResponseEntity<RestResponse<UserResponse>> register(RegisterRequest request) {

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            RestResponse<UserResponse> response = new RestResponse<>();
            response.setStatus(400);
            response.setError("Bad Request");
            response.setMessage("Email đã được sử dụng");
            return ResponseEntity.badRequest().body(response);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole("VOLUNTEER"); // Mặc định là VOLUNTEER
        user.setStatus("ACTIVE");
        user.setUpdatedAt(Instant.now());

        User savedUser = userRepository.save(user);

        UserResponse userResponse = convertToUserResponse(savedUser);

        RestResponse<UserResponse> response = new RestResponse<>();
        response.setStatus(201);
        response.setMessage("Đăng ký thành công");
        response.setData(userResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<RestResponse<LoginResponse>> login(LoginRequest request) {

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            RestResponse<LoginResponse> response = new RestResponse<>();
            response.setStatus(401);
            response.setError("Unauthorized");
            response.setMessage("Email hoặc mật khẩu không đúng");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            RestResponse<LoginResponse> response = new RestResponse<>();
            response.setStatus(401);
            response.setError("Unauthorized");
            response.setMessage("Email hoặc mật khẩu không đúng");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String accessToken = securityUtil.generateAccessToken(user);
        String refreshToken = securityUtil.generateRefreshToken(user);

        LoginResponse loginResponse = new LoginResponse(
                accessToken,
                refreshToken,
                convertToUserResponse(user));

        RestResponse<LoginResponse> response = new RestResponse<>();
        response.setStatus(200);
        response.setMessage("Đăng nhập thành công");
        response.setData(loginResponse);

        return ResponseEntity.ok(response);
    }

    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getAvatarUrl(),
                user.getRole(),
                user.getStatus(),
                user.getTotalPoints(),
                user.getAddress(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getViolation(),
                0  
        );
    }
}
