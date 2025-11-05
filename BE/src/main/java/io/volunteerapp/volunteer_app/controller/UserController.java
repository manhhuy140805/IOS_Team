package io.volunteerapp.volunteer_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import io.volunteerapp.volunteer_app.Util.RestResponse;
import io.volunteerapp.volunteer_app.model.User;
import io.volunteerapp.volunteer_app.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller quản lý User với phân quyền
 * 
 * PHÂN QUYỀN:
 * - ROLE_USER: Có thể xem thông tin của chính mình
 * - ROLE_ADMIN: Có thể CRUD tất cả user
 * 
 * CÁCH LẤY THÔNG TIN USER TỪ TOKEN:
 * - Dùng @AuthenticationPrincipal Jwt jwt
 * - jwt.getSubject() -> email
 * - jwt.getClaim("userId") -> id
 * - jwt.getClaim("scope") -> role (VD: "ROLE_ADMIN")
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * LẤY THÔNG TIN CỦA CHÍNH MÌNH
     * 
     * PHÂN QUYỀN: Cả USER và ADMIN đều có thể gọi
     * 
     * LUỒNG HOẠT ĐỘNG:
     * 1. Client gửi request kèm token trong header: Authorization: Bearer <token>
     * 2. Spring Security decode token và inject vào @AuthenticationPrincipal Jwt
     * jwt
     * 3. Lấy userId từ jwt.getClaim("userId")
     * 4. Tìm user trong database
     * 5. Trả về thông tin user
     * 
     * @param jwt - JWT token tự động inject bởi Spring Security
     * @return Thông tin user hiện tại
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')") // Cả USER và ADMIN đều được
    public ResponseEntity<RestResponse<UserDTO>> getCurrentUser(
            @AuthenticationPrincipal Jwt jwt) {

        // Lấy userId từ token
        Integer userId = jwt.getClaim("userId");
        // String email = jwt.getSubject(); // hoặc lấy email
        // String role = jwt.getClaim("scope"); // VD: "ROLE_ADMIN"

        // Tìm user trong database
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            RestResponse<UserDTO> response = new RestResponse<>();
            response.setStatus(404);
            response.setError("Not Found");
            response.setMessage("Không tìm thấy user");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User user = userOptional.get();
        UserDTO userDTO = convertToDTO(user);

        RestResponse<UserDTO> response = new RestResponse<>();
        response.setStatus(200);
        response.setMessage("Lấy thông tin thành công");
        response.setData(userDTO);

        return ResponseEntity.ok(response);
    }

    /**
     * LẤY DANH SÁCH TẤT CẢ USER
     * 
     * PHÂN QUYỀN: Chỉ ADMIN mới được gọi
     * 
     * HOẠT ĐỘNG:
     * - Nếu user có role ROLE_ADMIN -> cho phép
     * - Nếu user có role ROLE_USER -> trả về 403 Forbidden
     * 
     * @param jwt - JWT token
     * @return Danh sách tất cả user
     */
    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Chỉ ADMIN
    public ResponseEntity<RestResponse<List<UserDTO>>> getAllUsers(
            @AuthenticationPrincipal Jwt jwt) {

        // Log để debug (optional)
        System.out.println("Admin đang xem danh sách user. Email: " + jwt.getSubject());

        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        RestResponse<List<UserDTO>> response = new RestResponse<>();
        response.setStatus(200);
        response.setMessage("Lấy danh sách user thành công");
        response.setData(userDTOs);

        return ResponseEntity.ok(response);
    }

    /**
     * LẤY THÔNG TIN USER THEO ID
     * 
     * PHÂN QUYỀN: Chỉ ADMIN
     * 
     * @param id  - User ID
     * @param jwt - JWT token
     * @return Thông tin user
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Chỉ ADMIN
    public ResponseEntity<RestResponse<UserDTO>> getUserById(
            @PathVariable Integer id,
            @AuthenticationPrincipal Jwt jwt) {

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            RestResponse<UserDTO> response = new RestResponse<>();
            response.setStatus(404);
            response.setError("Not Found");
            response.setMessage("Không tìm thấy user với ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        UserDTO userDTO = convertToDTO(userOptional.get());

        RestResponse<UserDTO> response = new RestResponse<>();
        response.setStatus(200);
        response.setMessage("Lấy thông tin user thành công");
        response.setData(userDTO);

        return ResponseEntity.ok(response);
    }

    /**
     * CẬP NHẬT THÔNG TIN USER
     * 
     * PHÂN QUYỀN: Chỉ ADMIN
     * 
     * HOẠT ĐỘNG:
     * 1. Kiểm tra quyền ROLE_ADMIN
     * 2. Tìm user theo ID
     * 3. Cập nhật thông tin
     * 4. Lưu vào database
     * 5. Trả về user đã cập nhật
     * 
     * @param id      - User ID
     * @param request - Thông tin cập nhật
     * @param jwt     - JWT token
     * @return User đã cập nhật
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Chỉ ADMIN
    public ResponseEntity<RestResponse<UserDTO>> updateUser(
            @PathVariable Integer id,
            @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            RestResponse<UserDTO> response = new RestResponse<>();
            response.setStatus(404);
            response.setError("Not Found");
            response.setMessage("Không tìm thấy user với ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User user = userOptional.get();

        // Cập nhật thông tin
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        User updatedUser = userRepository.save(user);
        UserDTO userDTO = convertToDTO(updatedUser);

        RestResponse<UserDTO> response = new RestResponse<>();
        response.setStatus(200);
        response.setMessage("Cập nhật user thành công");
        response.setData(userDTO);

        return ResponseEntity.ok(response);
    }

    /**
     * XÓA USER
     * 
     * PHÂN QUYỀN: Chỉ ADMIN
     * 
     * @param id  - User ID
     * @param jwt - JWT token
     * @return Thông báo xóa thành công
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Chỉ ADMIN
    public ResponseEntity<RestResponse<Void>> deleteUser(
            @PathVariable Integer id,
            @AuthenticationPrincipal Jwt jwt) {

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            RestResponse<Void> response = new RestResponse<>();
            response.setStatus(404);
            response.setError("Not Found");
            response.setMessage("Không tìm thấy user với ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        userRepository.deleteById(id);

        RestResponse<Void> response = new RestResponse<>();
        response.setStatus(200);
        response.setMessage("Xóa user thành công");

        return ResponseEntity.ok(response);
    }

    /**
     * THĂNG CẤP USER LÊN ADMIN
     * 
     * PHÂN QUYỀN: Chỉ ADMIN
     * 
     * VÍ DỤ: Endpoint đặc biệt chỉ ADMIN có thể gọi
     * 
     * @param id  - User ID
     * @param jwt - JWT token
     * @return User đã thăng cấp
     */
    @PostMapping("/{id}/promote-to-admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Chỉ ADMIN
    public ResponseEntity<RestResponse<UserDTO>> promoteToAdmin(
            @PathVariable Integer id,
            @AuthenticationPrincipal Jwt jwt) {

        // Log admin đang thực hiện hành động
        String adminEmail = jwt.getSubject();
        System.out.println("Admin " + adminEmail + " đang thăng cấp user " + id + " lên ADMIN");

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            RestResponse<UserDTO> response = new RestResponse<>();
            response.setStatus(404);
            response.setError("Not Found");
            response.setMessage("Không tìm thấy user với ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User user = userOptional.get();
        user.setRole("ADMIN"); // Thăng cấp lên ADMIN
        User updatedUser = userRepository.save(user);

        UserDTO userDTO = convertToDTO(updatedUser);

        RestResponse<UserDTO> response = new RestResponse<>();
        response.setStatus(200);
        response.setMessage("Thăng cấp user lên ADMIN thành công");
        response.setData(userDTO);

        return ResponseEntity.ok(response);
    }

    // ===== Helper Method =====

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getStatus());
    }

    // ===== DTO Classes =====

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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateUserRequest {
        private String fullName;
        private String phone;
        private String role;
        private String status;
    }
}
