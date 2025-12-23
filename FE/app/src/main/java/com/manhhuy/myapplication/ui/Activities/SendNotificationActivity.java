package com.manhhuy.myapplication.ui.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ActivitySendNotificationBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.request.SendNotificationRequest;
import com.manhhuy.myapplication.helper.response.RestResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotificationActivity extends AppCompatActivity {

    private static final String TAG = "SendNotificationActivity";
    private ActivitySendNotificationBinding binding;
    private int selectedRecipientType = 0; // 0: All, 1: Confirmed, 2: Unconfirmed
    private Integer eventId;
    private boolean isLoading = false;
    private Uri selectedImageUri;
    private String uploadedImageUrl; // Lưu URL sau khi upload
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupImagePicker();

        // Get event ID from intent
        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        String eventTitle = getIntent().getStringExtra("EVENT_TITLE");
        
        if (eventId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy sự kiện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        Log.d(TAG, "Event ID: " + eventId + ", Title: " + eventTitle);

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

        // File Selection
        binding.btnSelectFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        binding.ivRemoveFile.setOnClickListener(v -> {
            selectedImageUri = null;
            uploadedImageUrl = null;
            binding.tvFileName.setText("Chưa chọn tệp nào");
            binding.ivSelectedImage.setVisibility(View.GONE);
            binding.ivRemoveFile.setVisibility(View.GONE);
        });

        binding.btnSendNotification.setOnClickListener(v -> sendNotification());
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            binding.tvFileName.setText("Đang tải lên...");
                            binding.ivSelectedImage.setImageURI(selectedImageUri);
                            binding.ivSelectedImage.setVisibility(View.VISIBLE);
                            binding.ivRemoveFile.setVisibility(View.VISIBLE);
                            
                            // Upload ngay lập tức
                            uploadImageImmediately();
                        }
                    }
                });
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
        if (isLoading) return;
        
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

        // Map recipient type
        String recipientType;
        switch (selectedRecipientType) {
            case 1:
                recipientType = "APPROVED";
                break;
            case 2:
                recipientType = "PENDING";
                break;
            default:
                recipientType = "ALL";
                break;
        }
        
        // Call API
        isLoading = true;
        showLoading();
        
        ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
        
        // Tạo request với URL đã upload sẵn
        SendNotificationRequest request = new SendNotificationRequest(
                eventId,
                title,
                content,
                recipientType,
                uploadedImageUrl // Dùng URL đã upload
        );
        
        Call<RestResponse<Map<String, Object>>> call = apiService.sendNotification(request);
        
        call.enqueue(new Callback<RestResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<RestResponse<Map<String, Object>>> call, 
                                 Response<RestResponse<Map<String, Object>>> response) {
                isLoading = false;
                hideLoading();
                
                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<Map<String, Object>> restResponse = response.body();
                    Map<String, Object> data = restResponse.getData();
                    
                    int sentCount = data != null && data.get("sentCount") != null ? 
                        ((Number) data.get("sentCount")).intValue() : 0;
                    
                    Toast.makeText(SendNotificationActivity.this,
                        "Đã gửi thông báo đến " + sentCount + " người",
                        Toast.LENGTH_LONG).show();
                    
                    Log.d(TAG, "Notification sent successfully to " + sentCount + " users");
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? 
                            response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Error sending notification: " + errorBody);
                        Toast.makeText(SendNotificationActivity.this, 
                            "Không thể gửi thông báo: " + response.message(), 
                            Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(SendNotificationActivity.this, 
                            "Không thể gửi thông báo", 
                            Toast.LENGTH_SHORT).show();
                    }
                }
            }
            
            @Override
            public void onFailure(Call<RestResponse<Map<String, Object>>> call, Throwable t) {
                isLoading = false;
                hideLoading();
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(SendNotificationActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    // Method upload ảnh ngay khi chọn
    private void uploadImageImmediately() {
        if (selectedImageUri == null) return;
        
        try {
            File file = getFileFromUri(selectedImageUri);
            if (file == null) {
                Toast.makeText(this, "Lỗi khi đọc file", Toast.LENGTH_SHORT).show();
                binding.tvFileName.setText("Chưa chọn tệp nào");
                return;
            }
            
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            
            ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
            Call<RestResponse<Map<String, String>>> call = apiService.uploadNotificationAttachment(body);
            
            call.enqueue(new Callback<RestResponse<Map<String, String>>>() {
                @Override
                public void onResponse(Call<RestResponse<Map<String, String>>> call, Response<RestResponse<Map<String, String>>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        uploadedImageUrl = response.body().getData().get("url");
                        binding.tvFileName.setText("✅ Tải lên thành công");
                        Toast.makeText(SendNotificationActivity.this, "Tải ảnh thành công", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Image uploaded: " + uploadedImageUrl);
                    } else {
                        selectedImageUri = null;
                        uploadedImageUrl = null;
                        binding.tvFileName.setText("Tải lên thất bại");
                        binding.ivSelectedImage.setVisibility(View.GONE);
                        binding.ivRemoveFile.setVisibility(View.GONE);
                        Toast.makeText(SendNotificationActivity.this, "Tải ảnh thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
                
                @Override
                public void onFailure(Call<RestResponse<Map<String, String>>> call, Throwable t) {
                    selectedImageUri = null;
                    uploadedImageUrl = null;
                    binding.tvFileName.setText("Lỗi kết nối");
                    binding.ivSelectedImage.setVisibility(View.GONE);
                    binding.ivRemoveFile.setVisibility(View.GONE);
                    Toast.makeText(SendNotificationActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Upload failed: " + t.getMessage());
                }
            });
            
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error preparing upload: " + e.getMessage());
        }
    }

    private File getFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getCacheDir(), "temp_image_" + System.currentTimeMillis());
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void showLoading() {
        binding.btnSendNotification.setEnabled(false);
        binding.btnSendNotification.setText("Đang gửi...");
    }
    
    private void hideLoading() {
        binding.btnSendNotification.setEnabled(true);
        binding.btnSendNotification.setText("Gửi thông báo");
    }
}