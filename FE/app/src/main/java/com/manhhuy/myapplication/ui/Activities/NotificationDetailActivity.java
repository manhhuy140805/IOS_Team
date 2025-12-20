package com.manhhuy.myapplication.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ActivityNotificationDetailBinding;
import com.manhhuy.myapplication.helper.ApiService;
import com.manhhuy.myapplication.helper.response.NotificationResponse;
import com.manhhuy.myapplication.helper.response.RestResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationDetailActivity extends AppCompatActivity {

    private static final String TAG = "NotificationDetail";
    private static final int CURRENT_USER_ID = 1; // TODO: Get from session
    
    private ActivityNotificationDetailBinding binding;
    private int notificationId;
    private String title;
    private String content;
    private String senderRole;
    private String type;
    private boolean isRead;
    private long createdAt;
    private long readAt;
    private String attached;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getDataFromIntent();
        setupToolbar();
        setupUI();
        setupClickListeners();
        
        // Call API to auto mark as read
        markNotificationAsRead();
    }
    
    private void markNotificationAsRead() {
        // Gọi API để tự động đánh dấu đã đọc khi xem chi tiết
        ApiService.api().getNotificationById(notificationId, CURRENT_USER_ID)
            .enqueue(new Callback<RestResponse<NotificationResponse>>() {
                @Override
                public void onResponse(Call<RestResponse<NotificationResponse>> call, 
                                     Response<RestResponse<NotificationResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        RestResponse<NotificationResponse> restResponse = response.body();
                        if (restResponse.getStatusCode() == 200) {
                            Log.d(TAG, "Notification marked as read");
                            // Update UI if needed
                            isRead = true;
                            readAt = System.currentTimeMillis();
                            updateReadStatus();
                        }
                    } else {
                        Log.e(TAG, "Failed to mark as read: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<RestResponse<NotificationResponse>> call, Throwable t) {
                    Log.e(TAG, "Error marking as read", t);
                }
            });
    }
    
    private void updateReadStatus() {
        if (isRead && readAt > 0) {
            binding.readStatusContainer.setVisibility(View.VISIBLE);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
            binding.tvReadStatus.setText("Đã đọc lúc " + sdf.format(new Date(readAt)));
        } else {
            binding.readStatusContainer.setVisibility(View.GONE);
        }
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        notificationId = intent.getIntExtra("notification_id", 0);
        title = intent.getStringExtra("notification_title");
        content = intent.getStringExtra("notification_content");
        senderRole = intent.getStringExtra("notification_sender_role");
        type = intent.getStringExtra("notification_type");
        isRead = intent.getBooleanExtra("notification_is_read", false);
        createdAt = intent.getLongExtra("notification_created_at", System.currentTimeMillis());
        readAt = intent.getLongExtra("notification_read_at", 0);
        attached = intent.getStringExtra("notification_attached");
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupUI() {
        // Set title and content
        binding.tvNotificationTitle.setText(title != null ? title : "Thông báo");
        binding.tvNotificationContent.setText(content != null ? content : "Không có nội dung");

        // Set time
        binding.tvNotificationTime.setText(getRelativeTimeString(new Date(createdAt)));

        // Set type
        binding.tvNotificationType.setText(getTypeText(type));

        // Set sender badge
        if (senderRole != null && !senderRole.isEmpty()) {
            binding.tvSenderBadge.setText(senderRole);
            int badgeColor = getBadgeColor(senderRole);
            binding.senderBadgeCard.setCardBackgroundColor(badgeColor);
        } else {
            binding.senderBadgeCard.setVisibility(View.GONE);
        }

        // Set icon
        int iconResource = getIconResource(senderRole, type);
        binding.ivNotificationIcon.setImageResource(iconResource);

        // Set icon background
        int iconBgColor = getIconBackgroundColor(senderRole);
        binding.iconContainer.setBackgroundTintList(
            ContextCompat.getColorStateList(this, iconBgColor)
        );

        // Show attached file if exists
        if (attached != null && !attached.isEmpty()) {
            binding.attachedCard.setVisibility(View.VISIBLE);
            binding.tvAttachedFile.setText(getFileNameFromUrl(attached));
        } else {
            binding.attachedCard.setVisibility(View.GONE);
        }

        // Initial read status display
        updateReadStatus();

        // Hide action buttons for now (can be customized based on notification type)
        binding.actionButtonsContainer.setVisibility(View.GONE);
    }

    private void setupClickListeners() {
        // Download attachment
        binding.btnDownloadAttachment.setOnClickListener(v -> {
            if (attached != null && !attached.isEmpty()) {
                Toast.makeText(this, "Đang tải xuống...", Toast.LENGTH_SHORT).show();
                // TODO: Implement download functionality
            }
        });

        // Action buttons
        binding.btnActionPrimary.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
        });

        binding.btnActionSecondary.setOnClickListener(v -> {
            Toast.makeText(this, "Đã lưu để xem sau", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Delete notification
        binding.btnDeleteNotification.setOnClickListener(v -> {
            // TODO: Implement delete functionality
            Toast.makeText(this, "Đã xóa thông báo", Toast.LENGTH_SHORT).show();
            
            // Return result to update the list
            Intent resultIntent = new Intent();
            resultIntent.putExtra("deleted_notification_id", notificationId);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private String getRelativeTimeString(Date date) {
        long now = System.currentTimeMillis();
        long time = date.getTime();
        long diff = now - time;

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (seconds < 60) {
            return "Vừa xong";
        } else if (minutes < 60) {
            return minutes + " phút trước";
        } else if (hours < 24) {
            return hours + " giờ trước";
        } else if (days < 7) {
            return days + " ngày trước";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdf.format(date);
        }
    }

    private String getTypeText(String type) {
        if (type == null) return "Thông báo";
        
        switch (type) {
            case "PERSONAL":
                return "Cá nhân";
            case "ORGANIZATION":
                return "Tổ chức";
            case "GLOBAL":
                return "Chung";
            default:
                return "Thông báo";
        }
    }

    private int getBadgeColor(String senderRole) {
        if (senderRole == null) return ContextCompat.getColor(this, R.color.gray_500);
        
        switch (senderRole) {
            case "ADMIN":
                return ContextCompat.getColor(this, R.color.button_red);
            case "ORGANIZATION":
                return ContextCompat.getColor(this, R.color.app_green_primary);
            case "SYSTEM":
                return ContextCompat.getColor(this, R.color.button_blue);
            default:
                return ContextCompat.getColor(this, R.color.gray_500);
        }
    }

    private int getIconResource(String senderRole, String type) {
        if (senderRole != null) {
            switch (senderRole) {
                case "ADMIN":
                    return R.drawable.ic_user;
                case "ORGANIZATION":
                    return R.drawable.ic_organization;
                case "SYSTEM":
                    return R.drawable.ic_info;
            }
        }
        
        if (type != null) {
            switch (type) {
                case "PERSONAL":
                    return R.drawable.ic_user;
                case "ORGANIZATION":
                    return R.drawable.ic_organization;
                case "GLOBAL":
                    return R.drawable.ic_notification;
            }
        }
        
        return R.drawable.ic_notification;
    }

    private int getIconBackgroundColor(String senderRole) {
        if (senderRole != null) {
            switch (senderRole) {
                case "ADMIN":
                    return R.color.button_red;
                case "ORGANIZATION":
                    return R.color.app_green_primary;
                case "SYSTEM":
                    return R.color.button_blue;
            }
        }
        return R.color.app_green_primary;
    }

    private String getFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) return "file";
        
        int lastSlash = url.lastIndexOf('/');
        if (lastSlash != -1 && lastSlash < url.length() - 1) {
            return url.substring(lastSlash + 1);
        }
        return "attachment.pdf";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
