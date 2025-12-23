package io.volunteerapp.volunteer_app.service;

import io.volunteerapp.volunteer_app.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    
    private final Map<String, OTPData> otpStorage = new HashMap<>();
    
    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXPIRY_MINUTES = 5;

    public String generateAndSendOTP(String email) throws MessagingException {
        // Check if email exists in database
        if (!userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email không tồn tại trong hệ thống");
        }
        
        // Generate 6-digit OTP
        String otp = generateOTP();
        
        // Store OTP with expiry time
        long expiryTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(OTP_EXPIRY_MINUTES);
        otpStorage.put(email, new OTPData(otp, expiryTime));
        
        // Send email
        sendOTPEmail(email, otp);
        
        log.info("OTP sent to email: {}", email);
        return otp; 
    }
    
    /**
     * Verify OTP
     */
    public boolean verifyOTP(String email, String otp) {
        OTPData otpData = otpStorage.get(email);
        
        if (otpData == null) {
            log.warn("No OTP found for email: {}", email);
            return false;
        }
        
        // Check if OTP expired
        if (System.currentTimeMillis() > otpData.getExpiryTime()) {
            otpStorage.remove(email);
            log.warn("OTP expired for email: {}", email);
            return false;
        }
        
        // Verify OTP
        if (otpData.getOtp().equals(otp)) {
            otpStorage.remove(email); // Remove after successful verification
            log.info("OTP verified successfully for email: {}", email);
            return true;
        }
        
        log.warn("Invalid OTP for email: {}", email);
        return false;
    }
    
    /**
     * Generate random 6-digit OTP
     */
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    
    /**
     * Send OTP email
     */
    private void sendOTPEmail(String to, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(to);
        helper.setSubject("Mã OTP xác thực - Volunteer App");
        
        String htmlContent = buildOTPEmailTemplate(otp);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }
    
    /**
     * Build HTML email template
     */
    private String buildOTPEmailTemplate(String otp) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #00C997 0%%, #00A67E 100%%); 
                              color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .otp-box { background: white; padding: 20px; text-align: center; 
                               border: 2px dashed #00C997; border-radius: 10px; margin: 20px 0; }
                    .otp-code { font-size: 32px; font-weight: bold; color: #00C997; 
                                letter-spacing: 5px; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                    .warning { color: #e74c3c; font-size: 14px; margin-top: 15px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎯 Volunteer App</h1>
                        <p>Xác thực tài khoản của bạn</p>
                    </div>
                    <div class="content">
                        <h2>Xin chào!</h2>
                        <p>Bạn đã yêu cầu mã OTP để xác thực tài khoản. Vui lòng sử dụng mã dưới đây:</p>
                        
                        <div class="otp-box">
                            <p style="margin: 0; color: #666;">Mã OTP của bạn là:</p>
                            <div class="otp-code">%s</div>
                            <p class="warning">⏰ Mã này sẽ hết hạn sau 5 phút</p>
                        </div>
                        
                        <p><strong>Lưu ý:</strong></p>
                        <ul>
                            <li>Không chia sẻ mã OTP này với bất kỳ ai</li>
                            <li>Volunteer App sẽ không bao giờ yêu cầu mã OTP qua điện thoại</li>
                            <li>Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email</li>
                        </ul>
                        
                        <p>Trân trọng,<br><strong>Đội ngũ Volunteer App</strong></p>
                    </div>
                    <div class="footer">
                        <p>© 2024 Volunteer App. All rights reserved.</p>
                        <p>Email này được gửi tự động, vui lòng không trả lời.</p>
                    </div>
                </div>
            </body>
            </html>
            """, otp);
    }
    
    /**
     * Inner class to store OTP data
     */
    private static class OTPData {
        private final String otp;
        private final long expiryTime;
        
        public OTPData(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
        
        public String getOtp() {
            return otp;
        }
        
        public long getExpiryTime() {
            return expiryTime;
        }
    }
}
