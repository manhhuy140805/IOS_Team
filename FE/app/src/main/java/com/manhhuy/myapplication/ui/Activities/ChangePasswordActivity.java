package com.manhhuy.myapplication.ui.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.manhhuy.myapplication.databinding.ActivityChangePasswordBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.request.ChangePasswordRequest;
import com.manhhuy.myapplication.helper.response.RestResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = "ChangePasswordActivity";
    private ActivityChangePasswordBinding binding;
    private ProgressDialog progressDialog;
    private boolean isChanging = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupClickListeners();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupClickListeners() {
        binding.btnChangePassword.setOnClickListener(v -> {
            if (validateInput()) {
                changePassword();
            }
        });
    }

    private boolean validateInput() {
        String currentPassword = binding.etCurrentPassword.getText().toString().trim();
        String newPassword = binding.etNewPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        if (currentPassword.isEmpty()) {
            binding.etCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
            binding.etCurrentPassword.requestFocus();
            return false;
        }

        if (newPassword.isEmpty()) {
            binding.etNewPassword.setError("Vui lòng nhập mật khẩu mới");
            binding.etNewPassword.requestFocus();
            return false;
        }

        if (newPassword.length() < 6) {
            binding.etNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            binding.etNewPassword.requestFocus();
            return false;
        }

        if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.setError("Vui lòng xác nhận mật khẩu mới");
            binding.etConfirmPassword.requestFocus();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            binding.etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            binding.etConfirmPassword.requestFocus();
            return false;
        }

        if (currentPassword.equals(newPassword)) {
            binding.etNewPassword.setError("Mật khẩu mới phải khác mật khẩu hiện tại");
            binding.etNewPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void changePassword() {
        if (isChanging) return;

        String currentPassword = binding.etCurrentPassword.getText().toString().trim();
        String newPassword = binding.etNewPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        isChanging = true;
        showProgressDialog("Đang đổi mật khẩu...");

        ChangePasswordRequest request = new ChangePasswordRequest(
            currentPassword, newPassword, confirmPassword
        );

        ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
        Call<RestResponse<Void>> call = apiService.changePassword(request);

        call.enqueue(new Callback<RestResponse<Void>>() {
            @Override
            public void onResponse(Call<RestResponse<Void>> call,
                                 Response<RestResponse<Void>> response) {
                isChanging = false;
                dismissProgressDialog();

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ChangePasswordActivity.this,
                        "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMsg = "Không thể đổi mật khẩu";
                    if (response.code() == 400) {
                        try {
                            String errorBody = response.errorBody() != null ? 
                                response.errorBody().string() : "";
                            if (errorBody.contains("không đúng")) {
                                errorMsg = "Mật khẩu hiện tại không đúng";
                            } else if (errorBody.contains("không khớp")) {
                                errorMsg = "Mật khẩu mới và xác nhận không khớp";
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                    }
                    Toast.makeText(ChangePasswordActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RestResponse<Void>> call, Throwable t) {
                isChanging = false;
                dismissProgressDialog();
                Log.e(TAG, "Change password failed: " + t.getMessage(), t);
                Toast.makeText(ChangePasswordActivity.this,
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
        binding = null;
    }
}
