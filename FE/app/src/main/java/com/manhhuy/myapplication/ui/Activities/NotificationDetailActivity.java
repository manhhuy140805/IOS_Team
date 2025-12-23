package com.manhhuy.myapplication.ui.Activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ActivityNotificationDetailBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
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
    private static final int PERMISSION_REQUEST_CODE = 1001;
    
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
        // Lấy userId từ token
        Integer userId = ApiConfig.getUserId();
        if (userId == null) {
            Log.e(TAG, "User ID is null, cannot mark as read");
            return;
        }
        
        // Gọi API để tự động đánh dấu đã đọc khi xem chi tiết
        ApiService.api().getNotificationById(notificationId, userId)
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
    }

    private void setupClickListeners() {
        // Download attachment
        binding.btnDownloadAttachment.setOnClickListener(v -> {
            if (attached != null && !attached.isEmpty()) {
                downloadFile(attached);
            } else {
                Toast.makeText(this, "Không có file đính kèm", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete notification
        binding.btnDeleteNotification.setOnClickListener(v -> deleteNotification());
    }
    
    private void deleteNotification() {
        // Lấy userId từ token
        Integer userId = ApiConfig.getUserId();
        if (userId == null) {
            Toast.makeText(this, "Không thể xóa thông báo", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Hiển thị loading (có thể thêm progress dialog nếu cần)
        binding.btnDeleteNotification.setEnabled(false);
        
        // Gọi API xóa thông báo
        ApiService.api().deleteUserNotification(userId, notificationId)
            .enqueue(new Callback<RestResponse<Void>>() {
                @Override
                public void onResponse(Call<RestResponse<Void>> call, 
                                     Response<RestResponse<Void>> response) {
                    binding.btnDeleteNotification.setEnabled(true);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        RestResponse<Void> restResponse = response.body();
                        if (restResponse.getStatusCode() == 200) {
                            Toast.makeText(NotificationDetailActivity.this, 
                                "Đã xóa thông báo", Toast.LENGTH_SHORT).show();
                            
                            // Return result to update the list
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("deleted_notification_id", notificationId);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } else {
                            Toast.makeText(NotificationDetailActivity.this, 
                                "Không thể xóa thông báo: " + restResponse.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(NotificationDetailActivity.this, 
                            "Không thể xóa thông báo", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Delete failed: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<RestResponse<Void>> call, Throwable t) {
                    binding.btnDeleteNotification.setEnabled(true);
                    Toast.makeText(NotificationDetailActivity.this, 
                        "Lỗi khi xóa thông báo", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Delete error", t);
                }
            });
    }
    
    private void downloadFile(String fileUrl) {
        // Check permission for Android 9 and below
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
                return;
            }
        }
        
        try {
            String fileName = getFileNameFromUrl(fileUrl);
            
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
            request.setTitle("Tải xuống file đính kèm");
            request.setDescription("Đang tải: " + fileName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            request.setAllowedOverMetered(true);
            request.setAllowedOverRoaming(true);
            
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            if (downloadManager != null) {
                long downloadId = downloadManager.enqueue(request);
                Toast.makeText(this, "Đang tải xuống file...", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Download started with ID: " + downloadId);
            } else {
                Toast.makeText(this, "Không thể tải file", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error downloading file", e);
            Toast.makeText(this, "Lỗi khi tải file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (attached != null && !attached.isEmpty()) {
                    downloadFile(attached);
                }
            } else {
                Toast.makeText(this, "Cần cấp quyền để tải file", Toast.LENGTH_SHORT).show();
            }
        }
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
