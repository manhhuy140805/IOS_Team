package com.manhhuy.myapplication.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ActivityRegisterBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.request.RegisterRequest;
import com.manhhuy.myapplication.helper.response.RestResponse;
import com.manhhuy.myapplication.helper.response.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private ActivityRegisterBinding binding;
    private ApiEndpoints apiEndpoints;
    private boolean isOrganization = false; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize API client
        apiEndpoints = ApiConfig.getClient().create(ApiEndpoints.class);

        setupListeners();
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnRoleVolunteer.setOnClickListener(v -> selectRole(false));
        binding.btnRoleOrganization.setOnClickListener(v -> selectRole(true));

        binding.btnCreateAccount.setOnClickListener(v -> handleCreateAccount());

        binding.tvLoginLink.setOnClickListener(v -> {
            finish();
        });
    }

    private void selectRole(boolean organization) {
        isOrganization = organization;

        if (organization) {
            binding.layoutUserForm.setVisibility(View.GONE);
            binding.layoutOrganizationForm.setVisibility(View.VISIBLE);

            binding.btnRoleVolunteer.setBackgroundResource(R.drawable.bg_custom_edit_text);
            binding.btnRoleVolunteer.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_user, 0, 0);
            binding.btnRoleOrganization.setBackgroundResource(R.drawable.bg_category_tab_selected_reward);
            binding.btnRoleOrganization.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_organization, 0, 0);
        } else {
            binding.layoutUserForm.setVisibility(View.VISIBLE);
            binding.layoutOrganizationForm.setVisibility(View.GONE);

            binding.btnRoleVolunteer.setBackgroundResource(R.drawable.bg_category_tab_selected_reward);
            binding.btnRoleVolunteer.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_user, 0, 0);
            binding.btnRoleOrganization.setBackgroundResource(R.drawable.bg_custom_edit_text);
            binding.btnRoleOrganization.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_organization, 0, 0);
        }
    }

    private void handleCreateAccount() {
        if (isOrganization) {
            createOrganizationAccount();
        } else {
            createUserAccount();
        }
    }

    private void createUserAccount() {
        String fullname = binding.editTextFullname.getText().toString().trim();
        String email = binding.editTextEmail.getText().toString().trim();
        String phone = binding.editTextPhone.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();
        String location = binding.editTextLocation.getText().toString().trim();

        // Validation
        if (fullname.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show();
            binding.editTextFullname.requestFocus();
            return;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Vui lòng nhập email hợp lệ", Toast.LENGTH_SHORT).show();
            binding.editTextEmail.requestFocus();
            return;
        }

        if (phone.isEmpty() || phone.length() < 10) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại hợp lệ", Toast.LENGTH_SHORT).show();
            binding.editTextPhone.requestFocus();
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            binding.editTextPassword.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
            binding.editTextLocation.requestFocus();
            return;
        }

        // Disable button to prevent double-click
        binding.btnCreateAccount.setEnabled(false);

        // Create register request
        RegisterRequest request = new RegisterRequest();
        request.setEmail(email);
        request.setPassword(password);
        request.setFullName(fullname);
        request.setPhone(phone);
        request.setRole("VOLUNTEER");
        request.setAddress(location);

        Log.d(TAG, "Registering volunteer account: " + email);

        // Call API
        Call<RestResponse<UserResponse>> call = apiEndpoints.register(request);
        call.enqueue(new Callback<RestResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<RestResponse<UserResponse>> call,
                    Response<RestResponse<UserResponse>> response) {
                binding.btnCreateAccount.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<UserResponse> restResponse = response.body();

                    if (restResponse.getStatusCode() == 201 || restResponse.getStatusCode() == 200) {
                        Toast.makeText(RegisterActivity.this,
                                "Đăng ký tài khoản tình nguyện viên thành công!",
                                Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Registration successful");
                        finish();
                    } else {
                        String errorMessage = restResponse.getMessage() != null
                                ? restResponse.getMessage()
                                : "Đăng ký thất bại";
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Registration failed: " + errorMessage);
                    }
                } else {
                    Toast.makeText(RegisterActivity.this,
                            "Đăng ký thất bại. Vui lòng thử lại!",
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RestResponse<UserResponse>> call, Throwable t) {
                binding.btnCreateAccount.setEnabled(true);
                Toast.makeText(RegisterActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Registration API call failed", t);
            }
        });
    }

    private void createOrganizationAccount() {
        String orgName = binding.editTextOrgName.getText().toString().trim();
        String orgEmail = binding.editTextOrgEmail.getText().toString().trim();
        String password = binding.editTextOrgPassword.getText().toString().trim();
        String location = binding.editTextOrgLocation.getText().toString().trim();
        String phone = binding.editTextOrgPhone.getText().toString().trim();

        // Validation
        if (orgName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên tổ chức", Toast.LENGTH_SHORT).show();
            binding.editTextOrgName.requestFocus();
            return;
        }

        if (orgEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(orgEmail).matches()) {
            Toast.makeText(this, "Vui lòng nhập email hợp lệ", Toast.LENGTH_SHORT).show();
            binding.editTextOrgEmail.requestFocus();
            return;
        }



        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            binding.editTextOrgPassword.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ văn phòng", Toast.LENGTH_SHORT).show();
            binding.editTextOrgLocation.requestFocus();
            return;
        }

        if (phone.isEmpty() || phone.length() < 10) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại hợp lệ", Toast.LENGTH_SHORT).show();
            binding.editTextOrgPhone.requestFocus();
            return;
        }

        // Disable button to prevent double-click
        binding.btnCreateAccount.setEnabled(false);

        // Create register request for organization
        RegisterRequest request = new RegisterRequest();
        request.setEmail(orgEmail);
        request.setPassword(password);
        request.setFullName(orgName);
        request.setPhone(phone);
        request.setRole("ORGANIZATION");
        request.setAddress(location);

        Log.d(TAG, "Registering organization account: " + orgEmail);

        // Call API
        Call<RestResponse<UserResponse>> call = apiEndpoints.register(request);
        call.enqueue(new Callback<RestResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<RestResponse<UserResponse>> call,
                    Response<RestResponse<UserResponse>> response) {
                binding.btnCreateAccount.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<UserResponse> restResponse = response.body();

                    if (restResponse.getStatusCode() == 201 || restResponse.getStatusCode() == 200) {
                        Toast.makeText(RegisterActivity.this,
                                "Đăng ký tổ chức thành công!",
                                Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Organization registration successful");
                        finish();
                    } else {
                        String errorMessage = restResponse.getMessage() != null
                                ? restResponse.getMessage()
                                : "Đăng ký thất bại";
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Organization registration failed: " + errorMessage);
                    }
                } else {
                    Toast.makeText(RegisterActivity.this,
                            "Đăng ký thất bại. Vui lòng thử lại!",
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RestResponse<UserResponse>> call, Throwable t) {
                binding.btnCreateAccount.setEnabled(true);
                Toast.makeText(RegisterActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Organization registration API call failed", t);
            }
        });
    }
}