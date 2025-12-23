package com.manhhuy.myapplication.ui.Activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.manhhuy.myapplication.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupListeners();
    }

    private void setupListeners() {
        binding.backButtonContainer.setOnClickListener(v -> finish());
        
        binding.btnRememberPassword.setOnClickListener(v -> finish());

        binding.btnSendResetLink.setOnClickListener(v -> {
            String email = binding.editTextEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            
            sendOTP(email);
        });
    }
    
    private void sendOTP(String email) {
        // Show loading
        binding.btnSendResetLink.setEnabled(false);
        binding.btnSendResetLink.setText("Đang gửi...");
        
        com.manhhuy.myapplication.helper.request.SendOTPRequest request = 
            new com.manhhuy.myapplication.helper.request.SendOTPRequest(email);
        
        com.manhhuy.myapplication.helper.ApiService.api().sendOTP(request)
            .enqueue(new retrofit2.Callback<com.manhhuy.myapplication.helper.response.RestResponse<String>>() {
                @Override
                public void onResponse(
                    retrofit2.Call<com.manhhuy.myapplication.helper.response.RestResponse<String>> call,
                    retrofit2.Response<com.manhhuy.myapplication.helper.response.RestResponse<String>> response) {
                    
                    binding.btnSendResetLink.setEnabled(true);
                    binding.btnSendResetLink.setText("Gửi mã OTP");
                    
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(ForgotPasswordActivity.this, 
                            "Mã OTP đã được gửi đến email của bạn", Toast.LENGTH_SHORT).show();
                        
                        // Navigate to OTP verification screen
                        android.content.Intent intent = new android.content.Intent(
                            ForgotPasswordActivity.this, OTPVerificationActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = "Không thể gửi OTP";
                        if (response.code() == 404) {
                            errorMsg = "Email không tồn tại trong hệ thống";
                        }
                        Toast.makeText(ForgotPasswordActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }
                
                @Override
                public void onFailure(
                    retrofit2.Call<com.manhhuy.myapplication.helper.response.RestResponse<String>> call,
                    Throwable t) {
                    
                    binding.btnSendResetLink.setEnabled(true);
                    binding.btnSendResetLink.setText("Gửi mã OTP");
                    Toast.makeText(ForgotPasswordActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}