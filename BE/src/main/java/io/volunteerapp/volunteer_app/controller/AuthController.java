package io.volunteerapp.volunteer_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.volunteerapp.volunteer_app.DTO.requeset.LoginRequest;
import io.volunteerapp.volunteer_app.DTO.requeset.RegisterRequest;
import io.volunteerapp.volunteer_app.DTO.response.LoginResponse;
import io.volunteerapp.volunteer_app.DTO.response.UserResponse;
import io.volunteerapp.volunteer_app.Util.RestResponse;
import io.volunteerapp.volunteer_app.service.AuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RestResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<RestResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        return authService.login(request);
    }
}
