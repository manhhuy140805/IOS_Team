package com.manhhuy.myapplication.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.manhhuy.myapplication.databinding.ActivityMainBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiService;
import com.manhhuy.myapplication.helper.JwtUtil;
import com.manhhuy.myapplication.helper.request.LoginRequest;
import com.manhhuy.myapplication.helper.response.LoginResponse;
import com.manhhuy.myapplication.helper.response.RestResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Khởi tạo ApiConfig
        ApiConfig.init(this);


        // Kiểm tra token đã lưu
        if (checkSavedToken()) {
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnSignIn.setOnClickListener(v -> {
            String email = binding.editTextEmail.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call API login
            login(email, password);
        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        binding.tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private boolean checkSavedToken() {
        // Kiểm tra token có hợp lệ không
        if (!ApiConfig.isTokenValid()) {
            Log.d(TAG, "No valid token found");
            ApiConfig.clearToken(); // Xóa token không hợp lệ
            return false;
        }
        
        // Token còn hạn, tự động đăng nhập
        String token = ApiConfig.getToken();
        Log.d(TAG, "Valid token found, auto login");
        
        // Lấy role từ token
        String role = JwtUtil.getRole(token);
        String roleSimple = JwtUtil.getRoleSimple(token);
        Integer userId = JwtUtil.getUserId(token);
        

        // Chuyển đến Activity phù hợp dựa trên role
        Intent intent;
        if ("ROLE_ADMIN".equals(role) || "ADMIN".equalsIgnoreCase(roleSimple)) {
            intent = new Intent(MainActivity.this, AdminActivity.class);
        } else if ("ROLE_ORGANIZATION".equals(role) || "ORGANIZATION".equalsIgnoreCase(roleSimple)) {
            intent = new Intent(MainActivity.this, OrganizationActivity.class);
        } else {
            // Mặc định là USER/VOLUNTEER
            intent = new Intent(MainActivity.this, UserActivity.class);
        }
        
        startActivity(intent);
        finish();
        return true;
    }

    /**
     * Call API login
     */
    private void login(String email, String password) {
        // Disable button để tránh click nhiều lần
        binding.btnSignIn.setEnabled(false);
        binding.btnSignIn.setText("Đang đăng nhập...");

        // Tạo LoginRequest
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Call API
        Call<RestResponse<LoginResponse>> call = ApiService.api().login(loginRequest);

        call.enqueue(new Callback<RestResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<RestResponse<LoginResponse>> call,
                    Response<RestResponse<LoginResponse>> response) {
                binding.btnSignIn.setEnabled(true);
                binding.btnSignIn.setText("Đăng nhập");

                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<LoginResponse> restResponse = response.body();

                    if (restResponse.getStatusCode() == 200 && restResponse.getData() != null) {
                        LoginResponse loginResponse = restResponse.getData();
                        String status = loginResponse.getUser().getStatus();
                        if(!"ACTIVE".equalsIgnoreCase(status))
                        {
                            Toast.makeText(MainActivity.this, "Tài khoản của bạn đã bị khóa", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        // Lấy token
                        String token = loginResponse.getAccessToken();

                        // Lưu token
                        ApiConfig.saveToken(token);
                        String role = JwtUtil.getRole(token);
                        String roleSimple = JwtUtil.getRoleSimple(token);

                        // Chuyển đến Activity phù hợp dựa trên role
                        Intent intent;
                        if ("ROLE_ADMIN".equals(role) || "ADMIN".equalsIgnoreCase(roleSimple)) {
                            intent = new Intent(MainActivity.this, AdminActivity.class);
                        } else if ("ROLE_ORGANIZATION".equals(role) || "ORGANIZATION".equalsIgnoreCase(roleSimple)) {
                            intent = new Intent(MainActivity.this, OrganizationActivity.class);
                        } else {
                            // Mặc định là USER
                            intent = new Intent(MainActivity.this, UserActivity.class);
                        }

                        startActivity(intent);
                        finish();

                    } else {
                        // Lỗi từ server
                        String errorMessage = restResponse.getMessage() != null
                                ? restResponse.getMessage()
                                : "Đăng nhập thất bại";
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Response không thành công
                    Toast.makeText(MainActivity.this, "Đăng nhập thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<RestResponse<LoginResponse>> call, Throwable t) {
                binding.btnSignIn.setEnabled(true);
                binding.btnSignIn.setText("Đăng nhập");

                Log.e(TAG, "Login error", t);
                Toast.makeText(MainActivity.this,
                        "Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Vui lòng kiểm tra kết nối mạng"),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}