package com.manhhuy.myapplication.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
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
        Toast.makeText(this, "Đăng ký tài khoản tình nguyện viên thành công!", Toast.LENGTH_LONG).show();
        finish();
    }

    private void createOrganizationAccount() {
        String orgName = binding.editTextOrgName.getText().toString().trim();
        String orgEmail = binding.editTextOrgEmail.getText().toString().trim();
        String foundedDate = binding.editTextOrgFoundedDate.getText().toString().trim();
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

        if (foundedDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập năm thành lập", Toast.LENGTH_SHORT).show();
            binding.editTextOrgFoundedDate.requestFocus();
            return;
        }

        try {
            int year = Integer.parseInt(foundedDate);
            if (year < 1900 || year > 2025) {
                Toast.makeText(this, "Năm thành lập không hợp lệ", Toast.LENGTH_SHORT).show();
                binding.editTextOrgFoundedDate.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Năm thành lập phải là số", Toast.LENGTH_SHORT).show();
            binding.editTextOrgFoundedDate.requestFocus();
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

        Toast.makeText(this, "Đăng ký tổ chức thành công!", Toast.LENGTH_LONG).show();
        finish();
    }
}