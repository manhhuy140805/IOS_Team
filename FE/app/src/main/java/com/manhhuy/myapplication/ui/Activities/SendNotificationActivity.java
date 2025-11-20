package com.manhhuy.myapplication.ui.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.manhhuy.myapplication.R;

public class SendNotificationActivity extends AppCompatActivity {

    // UI Components
    private ImageView btnBack;
    private TextView tvEventTitle, tvEventDate, tvEventTime, tvEventLocation, tvRegisteredCount;
    private CardView btnChangeSchedule, btnCancelEvent, btnGeneralInfo;
    private CardView cardTemplate1, cardTemplate2, cardTemplate3;
    private CardView radioAllUsers, radioConfirmedUsers, radioUnconfirmedUsers;
    private RadioButton rbAllUsers, rbConfirmedUsers, rbUnconfirmedUsers;
    private EditText etNotificationTitle, etNotificationContent;
    private TextView tvCharCount, tvPreviewTitle, tvPreviewContent;
    private Button btnCancel, btnSendNotification;

    private int selectedRecipientType = 0; // 0: All, 1: Confirmed, 2: Unconfirmed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        initViews();
        setupListeners();
        setupTextWatchers();
    }

    private void initViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);

        // Event Info
        tvEventTitle = findViewById(R.id.tvEventTitle);
        tvEventDate = findViewById(R.id.tvEventDate);
        tvEventTime = findViewById(R.id.tvEventTime);
        tvEventLocation = findViewById(R.id.tvEventLocation);
        tvRegisteredCount = findViewById(R.id.tvRegisteredCount);

        // Action Buttons
        btnChangeSchedule = findViewById(R.id.btnChangeSchedule);
        btnCancelEvent = findViewById(R.id.btnCancelEvent);
        btnGeneralInfo = findViewById(R.id.btnGeneralInfo);

        // Templates
        cardTemplate1 = findViewById(R.id.cardTemplate1);
        cardTemplate2 = findViewById(R.id.cardTemplate2);
        cardTemplate3 = findViewById(R.id.cardTemplate3);

        // Recipients
        radioAllUsers = findViewById(R.id.radioAllUsers);
        radioConfirmedUsers = findViewById(R.id.radioConfirmedUsers);
        radioUnconfirmedUsers = findViewById(R.id.radioUnconfirmedUsers);
        rbAllUsers = findViewById(R.id.rbAllUsers);
        rbConfirmedUsers = findViewById(R.id.rbConfirmedUsers);
        rbUnconfirmedUsers = findViewById(R.id.rbUnconfirmedUsers);

        // Input Fields
        etNotificationTitle = findViewById(R.id.etNotificationTitle);
        etNotificationContent = findViewById(R.id.etNotificationContent);
        tvCharCount = findViewById(R.id.tvCharCount);

        // Preview
        tvPreviewTitle = findViewById(R.id.tvPreviewTitle);
        tvPreviewContent = findViewById(R.id.tvPreviewContent);

        // Bottom Buttons
        btnCancel = findViewById(R.id.btnCancel);
        btnSendNotification = findViewById(R.id.btnSendNotification);
    }

    private void setupListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Action Buttons
        btnChangeSchedule.setOnClickListener(v -> {
            selectTemplate("Thay đổi giờ tập trung",
                    "Xin chào các bạn,\n\nDo có sự thay đổi đột xuất, giờ tập trung hoạt động Beach Cleanup đã được điều chỉnh từ 8:00 sang 9:00 sáng.\n\nVui lòng cập nhật lịch trình để tránh nhầm lẫn.");
        });

        btnCancelEvent.setOnClickListener(v -> {
            selectTemplate("Hủy hoạt động Beach Cleanup",
                    "Xin chào các bạn,\n\nDo điều kiện thời tiết không thuận lợi, chúng tôi buộc phải hủy hoạt động Beach Cleanup đã được lên lịch.\n\nChúng tôi rất tiếc về sự bất tiện này và sẽ thông báo lịch mới sớm nhất.");
        });

        btnGeneralInfo.setOnClickListener(v -> {
            selectTemplate("Thông tin hoạt động Beach Cleanup",
                    "Xin chào các bạn,\n\nHoạt động Beach Cleanup sẽ diễn ra vào Chủ nhật, 27/10/2025 từ 8:00 - 12:00 tại Bãi Sậu, Vũng Tàu.\n\nVui lòng tập trung đúng giờ và mang theo nước uống, găng tay.");
        });

        // Template Cards
        cardTemplate1.setOnClickListener(v -> {
            selectTemplate("Thay đổi giờ tập trung",
                    "Xin lỗi vì sự thay đổi đột xuất. Giờ tập trung mới là 9:00 sáng thay vì 8:00 sáng như dự kiến ban đầu.");
        });

        cardTemplate2.setOnClickListener(v -> {
            selectTemplate("Hủy do thời tiết",
                    "Do điều kiện thời tiết không thuận lợi, chúng tôi buộc phải hủy hoạt động. Chúng tôi sẽ thông báo lịch mới sớm nhất có thể.");
        });

        cardTemplate3.setOnClickListener(v -> {
            selectTemplate("Nhắc nhở chuẩn bị",
                    "Nhắc nhở: Hoạt động sẽ diễn ra vào ngày mai. Vui lòng mang theo nước uống, găng tay và tinh thần nhiệt huyết!");
        });

        // Recipient Selection
        radioAllUsers.setOnClickListener(v -> selectRecipient(0));
        radioConfirmedUsers.setOnClickListener(v -> selectRecipient(1));
        radioUnconfirmedUsers.setOnClickListener(v -> selectRecipient(2));

        rbAllUsers.setOnClickListener(v -> selectRecipient(0));
        rbConfirmedUsers.setOnClickListener(v -> selectRecipient(1));
        rbUnconfirmedUsers.setOnClickListener(v -> selectRecipient(2));

        // Bottom Buttons
        btnCancel.setOnClickListener(v -> finish());

        btnSendNotification.setOnClickListener(v -> sendNotification());
    }

    private void setupTextWatchers() {
        // Character count for content
        etNotificationContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                tvCharCount.setText(length + " / 500 ký tự");

                if (length > 500) {
                    tvCharCount.setTextColor(
                            ContextCompat.getColor(SendNotificationActivity.this, android.R.color.holo_red_dark));
                } else {
                    tvCharCount.setTextColor(
                            ContextCompat.getColor(SendNotificationActivity.this, android.R.color.darker_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Preview update for title
        etNotificationTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    tvPreviewTitle.setText(s.toString());
                    tvPreviewTitle
                            .setTextColor(ContextCompat.getColor(SendNotificationActivity.this, android.R.color.black));
                } else {
                    tvPreviewTitle.setText("Tiêu đề thông báo sẽ hiển thị ở đây");
                    tvPreviewTitle.setTextColor(
                            ContextCompat.getColor(SendNotificationActivity.this, android.R.color.darker_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Preview update for content
        etNotificationContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    tvPreviewContent.setText(s.toString());
                    tvPreviewContent
                            .setTextColor(ContextCompat.getColor(SendNotificationActivity.this, android.R.color.black));
                } else {
                    tvPreviewContent.setText("Nội dung thông báo sẽ hiển thị ở đây");
                    tvPreviewContent.setTextColor(
                            ContextCompat.getColor(SendNotificationActivity.this, android.R.color.darker_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void selectTemplate(String title, String content) {
        etNotificationTitle.setText(title);
        etNotificationContent.setText(content);
        Toast.makeText(this, "Đã chọn mẫu", Toast.LENGTH_SHORT).show();
    }

    private void selectRecipient(int type) {
        selectedRecipientType = type;

        // Update radio buttons
        rbAllUsers.setChecked(type == 0);
        rbConfirmedUsers.setChecked(type == 1);
        rbUnconfirmedUsers.setChecked(type == 2);

        // Update card backgrounds
        updateRecipientCardBackground(radioAllUsers, type == 0);
        updateRecipientCardBackground(radioConfirmedUsers, type == 1);
        updateRecipientCardBackground(radioUnconfirmedUsers, type == 2);
    }

    private void updateRecipientCardBackground(CardView card, boolean isSelected) {
        int backgroundRes = isSelected ? R.drawable.bg_radio_selected : R.drawable.bg_radio_normal;
        card.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        // The background is set via the LinearLayout inside the CardView
    }

    private void sendNotification() {
        String title = etNotificationTitle.getText().toString().trim();
        String content = etNotificationContent.getText().toString().trim();

        // Validation
        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề thông báo", Toast.LENGTH_SHORT).show();
            etNotificationTitle.requestFocus();
            return;
        }

        if (content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung thông báo", Toast.LENGTH_SHORT).show();
            etNotificationContent.requestFocus();
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