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
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body { 
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
                        line-height: 1.6; 
                        color: #333; 
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        padding: 40px 20px;
                    }
                    .email-wrapper { 
                        max-width: 600px; 
                        margin: 0 auto; 
                        background: white;
                        border-radius: 16px;
                        overflow: hidden;
                        box-shadow: 0 20px 60px rgba(0,0,0,0.3);
                    }
                    .header { 
                        background: linear-gradient(135deg, #00C997 0%%, #00A67E 100%%); 
                        color: white; 
                        padding: 40px 30px; 
                        text-align: center;
                    }
                    .header h1 { 
                        font-size: 28px; 
                        margin-bottom: 10px;
                        font-weight: 700;
                    }
                    .header p { 
                        font-size: 16px; 
                        opacity: 0.95;
                    }
                    .content { 
                        padding: 40px 30px; 
                        background: #ffffff;
                    }
                    .greeting { 
                        font-size: 20px; 
                        font-weight: 600; 
                        color: #2c3e50;
                        margin-bottom: 20px;
                    }
                    .message { 
                        font-size: 15px; 
                        color: #555;
                        margin-bottom: 30px;
                        line-height: 1.8;
                    }
                    .otp-container { 
                        background: linear-gradient(135deg, #f5f7fa 0%%, #c3cfe2 100%%);
                        padding: 30px; 
                        text-align: center; 
                        border-radius: 12px;
                        margin: 30px 0;
                        border: 3px solid #00C997;
                    }
                    .otp-label { 
                        font-size: 14px; 
                        color: #666;
                        margin-bottom: 15px;
                        text-transform: uppercase;
                        letter-spacing: 1px;
                        font-weight: 600;
                    }
                    .otp-code { 
                        font-size: 42px; 
                        font-weight: 800; 
                        color: #00C997; 
                        letter-spacing: 8px;
                        font-family: 'Courier New', monospace;
                        text-shadow: 2px 2px 4px rgba(0,0,0,0.1);
                        margin: 10px 0;
                    }
                    .expiry-warning { 
                        background: #fff3cd;
                        color: #856404;
                        padding: 12px 20px;
                        border-radius: 8px;
                        font-size: 14px;
                        margin-top: 20px;
                        border-left: 4px solid #ffc107;
                        display: inline-block;
                    }
                    .security-notice { 
                        background: #f8f9fa;
                        padding: 25px;
                        border-radius: 10px;
                        margin-top: 30px;
                        border-left: 4px solid #00C997;
                    }
                    .security-notice h3 { 
                        color: #2c3e50;
                        font-size: 16px;
                        margin-bottom: 15px;
                        display: flex;
                        align-items: center;
                    }
                    .security-notice ul { 
                        list-style: none;
                        padding: 0;
                    }
                    .security-notice li { 
                        padding: 8px 0;
                        color: #555;
                        font-size: 14px;
                        position: relative;
                        padding-left: 25px;
                    }
                    .security-notice li:before { 
                        content: "✓";
                        position: absolute;
                        left: 0;
                        color: #00C997;
                        font-weight: bold;
                        font-size: 16px;
                    }
                    .signature { 
                        margin-top: 35px;
                        padding-top: 25px;
                        border-top: 2px solid #e9ecef;
                        color: #666;
                        font-size: 15px;
                    }
                    .signature strong { 
                        color: #00C997;
                    }
                    .footer { 
                        background: #2c3e50;
                        color: #ecf0f1;
                        text-align: center; 
                        padding: 30px;
                        font-size: 13px;
                    }
                    .footer p { 
                        margin: 8px 0;
                        opacity: 0.9;
                    }
                    .footer a { 
                        color: #00C997;
                        text-decoration: none;
                    }
                    @media only screen and (max-width: 600px) {
                        .email-wrapper { 
                            border-radius: 0;
                        }
                        .header h1 { 
                            font-size: 24px;
                        }
                        .otp-code { 
                            font-size: 36px;
                            letter-spacing: 6px;
                        }
                        .content { 
                            padding: 30px 20px;
                        }
                    }
                </style>
            </head>
            <body>
                <div class="email-wrapper">
                    <div class="header">
                        <h1>🌟 Volunteer App</h1>
                        <p>Nền tảng kết nối tình nguyện viên</p>
                    </div>
                    
                    <div class="content">
                        <div class="greeting">Xin chào! 👋</div>
                        
                        <p class="message">
                            Bạn đã yêu cầu mã OTP để xác thực tài khoản của mình. 
                            Vui lòng sử dụng mã bảo mật dưới đây để tiếp tục:
                        </p>
                        
                        <div class="otp-container">
                            <div class="otp-label">Mã OTP của bạn</div>
                            <div class="otp-code">%s</div>
                            <div class="expiry-warning">
                                ⏰ Mã này sẽ hết hạn sau <strong>5 phút</strong>
                            </div>
                        </div>
                        
                        <div class="security-notice">
                            <h3>🔒 Lưu ý bảo mật</h3>
                            <ul>
                                <li>Không chia sẻ mã OTP này với bất kỳ ai, kể cả nhân viên Volunteer App</li>
                                <li>Volunteer App sẽ không bao giờ yêu cầu mã OTP qua điện thoại hoặc tin nhắn</li>
                                <li>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email và đổi mật khẩu ngay</li>
                                <li>Mỗi mã OTP chỉ có thể sử dụng một lần duy nhất</li>
                            </ul>
                        </div>
                        
                        <div class="signature">
                            <p>Trân trọng,</p>
                            <p><strong>Đội ngũ Volunteer App</strong></p>
                            <p style="font-size: 13px; color: #999; margin-top: 10px;">
                                Cùng nhau tạo nên sự khác biệt 💚
                            </p>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p><strong>© 2024 Volunteer App</strong></p>
                        <p>Email này được gửi tự động, vui lòng không trả lời.</p>
                        <p style="margin-top: 15px;">
                            Cần hỗ trợ? Liên hệ: <a href="mailto:support@volunteerapp.com">support@volunteerapp.com</a>
                        </p>
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
