package com.manhhuy.myapplication.ui.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.manhhuy.myapplication.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private LinearLayout layoutBack;
    private EditText edtEmail;
    private Button btnSendReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupListeners();
    }

    private void initViews() {
        layoutBack = findViewById(R.id.layout_back);
        edtEmail = findViewById(R.id.edit_text_email);
        btnSendReset = findViewById(R.id.btn_send_reset);
    }

    private void setupListeners() {
        layoutBack.setOnClickListener(v -> finish());

        btnSendReset.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            } else {
                // Mock sending reset link
                Toast.makeText(this, "Đã gửi hướng dẫn đặt lại mật khẩu tới " + email, Toast.LENGTH_LONG).show();
                // Optional: finish() to go back to login, or stay here. 
                // Usually stay here or show success dialog. I'll just show toast.
            }
        });
    }
}