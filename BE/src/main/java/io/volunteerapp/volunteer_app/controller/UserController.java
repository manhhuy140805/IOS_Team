package io.volunteerapp.volunteer_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import io.volunteerapp.volunteer_app.DTO.requeset.UpdateUserRequest;
import io.volunteerapp.volunteer_app.DTO.response.UserResponse;
import io.volunteerapp.volunteer_app.Util.RestResponse;
import io.volunteerapp.volunteer_app.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ROLE_VOLUNTEER', 'ROLE_ADMIN', 'ROLE_ORGANIZATION')")
    public ResponseEntity<RestResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal Jwt jwt) {

        // Lấy userId từ token - JWT lưu số dưới dạng Long
        Object userIdClaim = jwt.getClaim("userId");
        Integer userId;
        if (userIdClaim instanceof Long) {
            userId = ((Long) userIdClaim).intValue();
        } else if (userIdClaim instanceof Integer) {
            userId = (Integer) userIdClaim;
        } else {
            userId = Integer.parseInt(userIdClaim.toString());
        }

        // Gọi service để lấy thông tin
        return userService.getUserById(userId);
    }

    @GetMapping("")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RestResponse<List<UserResponse>>> getAllUsers(
    /* @AuthenticationPrincipal Jwt jwt */) {

        // Log để debug (optional)
        // System.out.println("Admin " + jwt.getSubject() + " đang xem danh sách user");

        // Gọi service để lấy danh sách
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_VOLUNTEER', 'ROLE_ADMIN', 'ROLE_ORGANIZATION')")
    public ResponseEntity<RestResponse<UserResponse>> getUserById(
            @PathVariable Integer id,
            @AuthenticationPrincipal Jwt jwt) {

        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RestResponse<UserResponse>> updateUser(
            @PathVariable Integer id,
            @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RestResponse<Void>> deleteUser(
            @PathVariable Integer id
    /* , @AuthenticationPrincipal Jwt jwt */) {

        return userService.deleteUser(id);
    }

    @PostMapping("/{id}/promote-to-admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RestResponse<UserResponse>> promoteToAdmin(
            @PathVariable Integer id,
            @AuthenticationPrincipal Jwt jwt) {

        // Log admin đang thực hiện hành động
        String adminEmail = jwt.getSubject();
        System.out.println("Admin " + adminEmail + " đang thăng cấp user " + id + " lên ADMIN");

        return userService.promoteToAdmin(id);
    }
}
