package com.manhhuy.myapplication.ui.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ActivitySendNotificationBinding;

public class SendNotificationActivity extends AppCompatActivity {

    private ActivitySendNotificationBinding binding;
    private int selectedRecipientType = 0; // 0: All, 1: Confirmed, 2: Unconfirmed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupListeners();
        setupTextWatchers();
    }

    private void setupListeners() {
        // Back button
        binding.btnBack.setOnClickListener(v -> finish());

        // Action Buttons
        binding.btnChangeSchedule.setOnClickListener(v -> {
            selectTemplate("Thay đổi giờ tập trung",
                    "Xin chào các bạn,\n\nDo có sự thay đổi đột xuất, giờ tập trung hoạt động Beach Cleanup đã được điều chỉnh từ 8:00 sang 9:00 sáng.\n\nVui lòng cập nhật lịch trình để tránh nhầm lẫn.");
        });

        binding.btnCancelEvent.setOnClickListener(v -> {
            selectTemplate("Hủy hoạt động Beach Cleanup",
                    "Xin chào các bạn,\n\nDo điều kiện thời tiết không thuận lợi, chúng tôi buộc phải hủy hoạt động Beach Cleanup đã được lên lịch.\n\nChúng tôi rất tiếc về sự bất tiện này và sẽ thông báo lịch mới sớm nhất.");
        });

        binding.btnGeneralInfo.setOnClickListener(v -> {
            selectTemplate("Thông tin hoạt động Beach Cleanup",
                    "Xin chào các bạn,\n\nHoạt động Beach Cleanup sẽ diễn ra vào Chủ nhật, 27/10/2025 từ 8:00 - 12:00 tại Bãi Sậu, Vũng Tàu.\n\nVui lòng tập trung đúng giờ và mang theo nước uống, găng tay.");
        });

        // Template Cards
        binding.cardTemplate1.setOnClickListener(v -> {
            selectTemplate("Thay đổi giờ tập trung",
                    "Xin lỗi vì sự thay đổi đột xuất. Giờ tập trung mới là 9:00 sáng thay vì 8:00 sáng như dự kiến ban đầu.");
        });

        binding.cardTemplate2.setOnClickListener(v -> {
            selectTemplate("Hủy do thời tiết",
                    "Do điều kiện thời tiết không thuận lợi, chúng tôi buộc phải hủy hoạt động. Chúng tôi sẽ thông báo lịch mới sớm nhất có thể.");
        });

        binding.cardTemplate3.setOnClickListener(v -> {
            selectTemplate("Nhắc nhở chuẩn bị",
                    "Nhắc nhở: Hoạt động sẽ diễn ra vào ngày mai. Vui lòng mang theo nước uống, găng tay và tinh thần nhiệt huyết!");
        });

        // Recipient Selection
        binding.radioAllUsers.setOnClickListener(v -> selectRecipient(0));
        binding.radioConfirmedUsers.setOnClickListener(v -> selectRecipient(1));
        binding.radioUnconfirmedUsers.setOnClickListener(v -> selectRecipient(2));

        binding.rbAllUsers.setOnClickListener(v -> selectRecipient(0));
        binding.rbConfirmedUsers.setOnClickListener(v -> selectRecipient(1));
        binding.rbUnconfirmedUsers.setOnClickListener(v -> selectRecipient(2));

        // Bottom Buttons
        binding.btnCancel.setOnClickListener(v -> finish());

        binding.btnSendNotification.setOnClickListener(v -> sendNotification());
    }

    private void setupTextWatchers() {
        // Character count for content
        binding.etNotificationContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                binding.tvCharCount.setText(length + " / 500 ký tự");

                if (length > 500) {
                    binding.tvCharCount.setTextColor(
                            ContextCompat.getColor(SendNotificationActivity.this, android.R.color.holo_red_dark));
                } else {
                    binding.tvCharCount.setTextColor(
                            ContextCompat.getColor(SendNotificationActivity.this, android.R.color.darker_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        binding.etNotificationTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    binding.tvPreviewTitle.setText(s.toString());
                    binding.tvPreviewTitle
                            .setTextColor(ContextCompat.getColor(SendNotificationActivity.this, android.R.color.black));
                } else {
                    binding.tvPreviewTitle.setText("Tiêu đề thông báo sẽ hiển thị ở đây");
                    binding.tvPreviewTitle.setTextColor(
                            ContextCompat.getColor(SendNotificationActivity.this, android.R.color.darker_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Preview update for content
        binding.etNotificationContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    binding.tvPreviewContent.setText(s.toString());
                    binding.tvPreviewContent
                            .setTextColor(ContextCompat.getColor(SendNotificationActivity.this, android.R.color.black));
                } else {
                    binding.tvPreviewContent.setText("Nội dung thông báo sẽ hiển thị ở đây");
                    binding.tvPreviewContent.setTextColor(
                            ContextCompat.getColor(SendNotificationActivity.this, android.R.color.darker_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void selectTemplate(String title, String content) {
        binding.etNotificationTitle.setText(title);
        binding.etNotificationContent.setText(content);
        Toast.makeText(this, "Đã chọn mẫu", Toast.LENGTH_SHORT).show();
    }

    private void selectRecipient(int type) {
        selectedRecipientType = type;

        // Update radio buttons
        binding.rbAllUsers.setChecked(type == 0);
        binding.rbConfirmedUsers.setChecked(type == 1);
        binding.rbUnconfirmedUsers.setChecked(type == 2);

        // Update card backgrounds
        updateRecipientCardBackground(binding.llAllUsersBackground, type == 0);
        updateRecipientCardBackground(binding.llConfirmedUsersBackground, type == 1);
        updateRecipientCardBackground(binding.llUnconfirmedUsersBackground, type == 2);
    }

    private void updateRecipientCardBackground(LinearLayout layout, boolean isSelected) {
        int backgroundRes = isSelected ? R.drawable.bg_radio_selected : R.drawable.bg_radio_normal;
        layout.setBackgroundResource(backgroundRes);
    }

    private void sendNotification() {
        String title = binding.etNotificationTitle.getText().toString().trim();
        String content = binding.etNotificationContent.getText().toString().trim();

        // Validation
        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề thông báo", Toast.LENGTH_SHORT).show();
            binding.etNotificationTitle.requestFocus();
            return;
        }

        if (content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung thông báo", Toast.LENGTH_SHORT).show();
            binding.etNotificationContent.requestFocus();
            return;
        }

        if (content.length() > 500) {
            Toast.makeText(this, "Nội dung không được vượt quá 500 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get recipient type
        String recipientType = "";
        switch (selectedRecipientType) {
            case 0:
                recipientType = "Tất cả người đăng ký (32 người)";
                break;
            case 1:
                recipientType = "Đã xác nhận tham gia (28 người)";
                break;
            case 2:
                recipientType = "Chưa xác nhận (4 người)";
                break;
        }

        // TODO: Implement actual notification sending logic here
        // For now, just show a success message
        Toast.makeText(this,
                "Đã gửi thông báo đến: " + recipientType,
                Toast.LENGTH_LONG).show();

        // Optionally clear fields or close activity
        finish();
    }
}