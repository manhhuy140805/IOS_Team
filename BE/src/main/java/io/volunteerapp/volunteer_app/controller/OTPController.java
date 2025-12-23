package io.volunteerapp.volunteer_app.controller;

import io.volunteerapp.volunteer_app.DTO.requeset.ResetPasswordRequest;
import io.volunteerapp.volunteer_app.DTO.requeset.SendOTPRequest;
import io.volunteerapp.volunteer_app.DTO.requeset.VerifyOTPRequest;
import io.volunteerapp.volunteer_app.Util.RestResponse;
import io.volunteerapp.volunteer_app.service.EmailService;
import io.volunteerapp.volunteer_app.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
public class OTPController {

    private final EmailService emailService;
    private final UserService userService;

    /**
     * Send OTP to email
     */
    @PostMapping("/send")
    public ResponseEntity<RestResponse<String>> sendOTP(@Valid @RequestBody SendOTPRequest request) {
        RestResponse<String> response = new RestResponse<>();
        
        try {
            emailService.generateAndSendOTP(request.getEmail());
            
            response.setStatus(200);
            response.setMessage("OTP đã được gửi đến email: " + request.getEmail());
            response.setData("OTP sent successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.setStatus(404);
            response.setMessage(e.getMessage());
            response.setError("Email not found");
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (MessagingException e) {
            response.setStatus(500);
            response.setMessage("Không thể gửi email. Vui lòng thử lại sau.");
            response.setError(e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Verify OTP
     */
    @PostMapping("/verify")
    public ResponseEntity<RestResponse<Boolean>> verifyOTP(@Valid @RequestBody VerifyOTPRequest request) {
        RestResponse<Boolean> response = new RestResponse<>();
        
        boolean isValid = emailService.verifyOTP(request.getEmail(), request.getOtp());
        
        if (isValid) {
            response.setStatus(200);
            response.setMessage("OTP hợp lệ");
            response.setData(true);
            return ResponseEntity.ok(response);
        } else {
            response.setStatus(400);
            response.setMessage("OTP không hợp lệ hoặc đã hết hạn");
            response.setData(false);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<RestResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return userService.resetPassword(request.getEmail(), request.getNewPassword());
    }
}
