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

        // 2. Cập nhật thông tin sử dụng mapper (chỉ cập nhật field không null)
        userMapper.updateEntityFromRequest(request, user);

        // 3. Lưu vào database
        User updatedUser = userRepository.save(user);

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
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getAvatarUrl(),
                user.getRole(),
                user.getStatus(),
                user.getTotalPoints(),
                user.getAddress());
    }
}
