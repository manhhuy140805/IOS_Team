package io.volunteerapp.volunteer_app.service;

import io.volunteerapp.volunteer_app.DTO.requeset.UpdateUserRequest;
import io.volunteerapp.volunteer_app.DTO.response.UserResponse;
import io.volunteerapp.volunteer_app.Util.RestResponse;
import io.volunteerapp.volunteer_app.mapper.UserMapper;
import io.volunteerapp.volunteer_app.model.User;
import io.volunteerapp.volunteer_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public ResponseEntity<RestResponse<UserResponse>> getUserById(Integer userId) {

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            RestResponse<UserResponse> response = new RestResponse<>();
            response.setStatus(404);
            response.setError("Not Found");
            response.setMessage("Không tìm thấy user với ID: " + userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        UserResponse userResponse = convertToUserResponse(userOptional.get());

        RestResponse<UserResponse> response = new RestResponse<>();
        response.setStatus(200);
        response.setMessage("Lấy thông tin user thành công");
        response.setData(userResponse);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<RestResponse<List<UserResponse>>> getAllUsers() {

        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = users.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());

        RestResponse<List<UserResponse>> response = new RestResponse<>();
        response.setStatus(200);
        response.setMessage("Lấy danh sách user thành công");
        response.setData(userResponses);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<RestResponse<UserResponse>> updateUser(Integer userId, UpdateUserRequest request) {

        // 1. Kiểm tra user tồn tại
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            RestResponse<UserResponse> response = new RestResponse<>();
            response.setStatus(404);
            response.setError("Not Found");
            response.setMessage("Không tìm thấy user với ID: " + userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User user = userOptional.get();
        
        // Log request data
        System.out.println("=== UPDATE USER REQUEST ===");
        System.out.println("User ID: " + userId);
        System.out.println("Full Name: " + request.getFullName());
        System.out.println("Phone: " + request.getPhone());
        System.out.println("Address: " + request.getAddress());
        System.out.println("Avatar URL: " + request.getAvatarUrl());
        System.out.println("Date of Birth: " + request.getDateOfBirth());
        System.out.println("Gender: " + request.getGender());
        System.out.println("Role: " + request.getRole());
        System.out.println("Status: " + request.getStatus());
        System.out.println("===========================");

        // 2. Cập nhật thông tin sử dụng mapper (chỉ cập nhật field không null)
        userMapper.updateEntityFromRequest(request, user);
        
        // Log after mapping
        System.out.println("=== AFTER MAPPING ===");
        System.out.println("User Date of Birth: " + user.getDateOfBirth());
        System.out.println("User Gender: " + user.getGender());
        System.out.println("=====================");

        // 3. Lưu vào database
        User updatedUser = userRepository.save(user);
        
        // Log after save
        System.out.println("=== AFTER SAVE ===");
        System.out.println("Saved Date of Birth: " + updatedUser.getDateOfBirth());
        System.out.println("Saved Gender: " + updatedUser.getGender());
        System.out.println("==================");

        // 4. Trả về response
        UserResponse userResponse = convertToUserResponse(updatedUser);

        RestResponse<UserResponse> response = new RestResponse<>();
        response.setStatus(200);
        response.setMessage("Cập nhật user thành công");
        response.setData(userResponse);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<RestResponse<Void>> deleteUser(Integer userId) {

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            RestResponse<Void> response = new RestResponse<>();
            response.setStatus(404);
            response.setError("Not Found");
            response.setMessage("Không tìm thấy user với ID: " + userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        userRepository.deleteById(userId);

        RestResponse<Void> response = new RestResponse<>();
        response.setStatus(200);
        response.setMessage("Xóa user thành công");

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<RestResponse<UserResponse>> promoteToAdmin(Integer userId) {

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            RestResponse<UserResponse> response = new RestResponse<>();
            response.setStatus(404);
            response.setError("Not Found");
            response.setMessage("Không tìm thấy user với ID: " + userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User user = userOptional.get();
        user.setRole("ADMIN");
        User updatedUser = userRepository.save(user);

        UserResponse userResponse = convertToUserResponse(updatedUser);

        RestResponse<UserResponse> response = new RestResponse<>();
        response.setStatus(200);
        response.setMessage("Thăng cấp user lên ADMIN thành công");
        response.setData(userResponse);

        return ResponseEntity.ok(response);
    }

    private UserResponse convertToUserResponse(User user) {
        // Count activities: only for volunteers (event registrations)
        int activityCount = 0;
        if ("ROLE_VOLUNTEER".equals(user.getRole()) || "VOLUNTEER".equals(user.getRole()) || "ROLE_USER".equals(user.getRole())) {
            activityCount = user.getUserEventRegistrations() != null ? user.getUserEventRegistrations().size() : 0;
        }
        // Organizations: set to 0 (bỏ member count)
        
        UserResponse response = new UserResponse(
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
                activityCount,
                user.getDateOfBirth(),
                user.getGender()
        );
        
        return response;
    }
    
    public ResponseEntity<RestResponse<Void>> changePassword(Integer userId, 
            io.volunteerapp.volunteer_app.DTO.requeset.ChangePasswordRequest request) {
        
        // 1. Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            RestResponse<Void> response = new RestResponse<>();
            response.setStatus(400);
            response.setError("Bad Request");
            response.setMessage("Mật khẩu mới và xác nhận mật khẩu không khớp");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 2. Find user
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            RestResponse<Void> response = new RestResponse<>();
            response.setStatus(404);
            response.setError("Not Found");
            response.setMessage("Không tìm thấy người dùng");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        User user = userOptional.get();
        
        // 3. Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            RestResponse<Void> response = new RestResponse<>();
            response.setStatus(400);
            response.setError("Bad Request");
            response.setMessage("Mật khẩu hiện tại không đúng");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 4. Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // 5. Return success
        RestResponse<Void> response = new RestResponse<>();
        response.setStatus(200);
        response.setMessage("Đổi mật khẩu thành công");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Reset password (for forgot password flow - no current password needed)
     */
    public ResponseEntity<RestResponse<Void>> resetPassword(String email, String newPassword) {
        
        // 1. Find user by email
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            RestResponse<Void> response = new RestResponse<>();
            response.setStatus(404);
            response.setError("Not Found");
            response.setMessage("Không tìm thấy người dùng với email: " + email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        User user = userOptional.get();
        
        // 2. Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // 3. Return success
        RestResponse<Void> response = new RestResponse<>();
        response.setStatus(200);
        response.setMessage("Đặt lại mật khẩu thành công");
        return ResponseEntity.ok(response);
    }
}
