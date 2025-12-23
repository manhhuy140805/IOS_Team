package com.manhhuy.myapplication.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.manhhuy.myapplication.databinding.ActivityOtpVerificationBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiService;
import com.manhhuy.myapplication.helper.request.SendOTPRequest;
import com.manhhuy.myapplication.helper.request.VerifyOTPRequest;
import com.manhhuy.myapplication.helper.response.RestResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPVerificationActivity extends AppCompatActivity {

    private ActivityOtpVerificationBinding binding;
    private String userEmail;
    private CountDownTimer countDownTimer;
    private boolean canResend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get email from intent (passed from previous screen)
        userEmail = getIntent().getStringExtra("email");
        
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy email", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.tvEmail.setText(userEmail);
        
        setupOTPInputs();
        setupListeners();
        startResendTimer();
    }

    private void setupOTPInputs() {
        // Auto focus next input
        binding.etOtp1.addTextChangedListener(new OTPTextWatcher(binding.etOtp1, binding.etOtp2));
        binding.etOtp2.addTextChangedListener(new OTPTextWatcher(binding.etOtp2, binding.etOtp3));
        binding.etOtp3.addTextChangedListener(new OTPTextWatcher(binding.etOtp3, binding.etOtp4));
        binding.etOtp4.addTextChangedListener(new OTPTextWatcher(binding.etOtp4, binding.etOtp5));
        binding.etOtp5.addTextChangedListener(new OTPTextWatcher(binding.etOtp5, binding.etOtp6));
        binding.etOtp6.addTextChangedListener(new OTPTextWatcher(binding.etOtp6, null));
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnVerify.setOnClickListener(v -> verifyOTP());
        binding.btnResend.setOnClickListener(v -> resendOTP());
        binding.tvBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(OTPVerificationActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void startResendTimer() {
        canResend = false;
        binding.btnResend.setEnabled(false);
        binding.btnResend.setAlpha(0.5f);

        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.tvResendTimer.setText("Gửi lại sau " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                canResend = true;
                binding.btnResend.setEnabled(true);
                binding.btnResend.setAlpha(1.0f);
                binding.tvResendTimer.setText("Bạn có thể gửi lại mã");
            }
        }.start();
    }

    private String getOTPCode() {
        return binding.etOtp1.getText().toString() +
               binding.etOtp2.getText().toString() +
               binding.etOtp3.getText().toString() +
               binding.etOtp4.getText().toString() +
               binding.etOtp5.getText().toString() +
               binding.etOtp6.getText().toString();
    }

    private void verifyOTP() {
        String otp = getOTPCode();
        
        if (otp.length() != 6) {
            Toast.makeText(this, "Vui lòng nhập đủ 6 số", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnVerify.setEnabled(false);

        VerifyOTPRequest request = new VerifyOTPRequest(userEmail, otp);
        
        ApiService.api().verifyOTP(request).enqueue(new Callback<RestResponse<Boolean>>() {
            @Override
            public void onResponse(Call<RestResponse<Boolean>> call, Response<RestResponse<Boolean>> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnVerify.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<Boolean> result = response.body();
                    if (result.getData() != null && result.getData()) {
                        Toast.makeText(OTPVerificationActivity.this, 
                            "Xác thực thành công!", Toast.LENGTH_SHORT).show();
                        
                        // Navigate to reset password screen
                        Intent intent = new Intent(OTPVerificationActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("email", userEmail);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(OTPVerificationActivity.this, 
                            result.getMessage(), Toast.LENGTH_SHORT).show();
                        clearOTP();
                    }
                } else {
                    Toast.makeText(OTPVerificationActivity.this, 
                        "OTP không hợp lệ", Toast.LENGTH_SHORT).show();
                    clearOTP();
                }
            }

            @Override
            public void onFailure(Call<RestResponse<Boolean>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnVerify.setEnabled(true);
                Toast.makeText(OTPVerificationActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resendOTP() {
        if (!canResend) {
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        
        SendOTPRequest request = new SendOTPRequest(userEmail);
        
        ApiService.api().sendOTP(request).enqueue(new Callback<RestResponse<String>>() {
            @Override
            public void onResponse(Call<RestResponse<String>> call, Response<RestResponse<String>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(OTPVerificationActivity.this, 
                        "Đã gửi lại mã OTP", Toast.LENGTH_SHORT).show();
                    clearOTP();
                    startResendTimer();
                } else {
                    Toast.makeText(OTPVerificationActivity.this, 
                        "Không thể gửi lại OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RestResponse<String>> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(OTPVerificationActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearOTP() {
        binding.etOtp1.setText("");
        binding.etOtp2.setText("");
        binding.etOtp3.setText("");
        binding.etOtp4.setText("");
        binding.etOtp5.setText("");
        binding.etOtp6.setText("");
        binding.etOtp1.requestFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    // TextWatcher for auto-focus
    private class OTPTextWatcher implements TextWatcher {
        private final View currentView;
        private final View nextView;

        OTPTextWatcher(View currentView, View nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 1 && nextView != null) {
                nextView.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
