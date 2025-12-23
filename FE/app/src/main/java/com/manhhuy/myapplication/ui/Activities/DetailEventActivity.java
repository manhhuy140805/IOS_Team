package com.manhhuy.myapplication.ui.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ActivityDetailEventBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.request.EventRegistrationRequest;
import com.manhhuy.myapplication.helper.request.SendNotificationRequest;
import com.manhhuy.myapplication.helper.response.EventRegistrationResponse;
import com.manhhuy.myapplication.helper.response.EventResponse;
import com.manhhuy.myapplication.helper.response.RestResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailEventActivity extends AppCompatActivity {

    private static final String TAG = "DetailEventActivity";
    private ActivityDetailEventBinding binding;
    private EventResponse eventData;
    private ProgressDialog progressDialog;
    private boolean isRegistering = false;
    
    // Registration info when coming from MyEventsActivity
    private Integer registrationId;
    private boolean isRegistered = false;
    private String registrationStatus;
    
    // For sending certificate
    private Uri selectedFileUri;
    private TextView tvSelectedFileName;
    private ActivityResultLauncher<String> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Initialize file picker
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedFileUri = uri;
                        if (tvSelectedFileName != null) {
                            tvSelectedFileName.setText("Đã chọn file");
                            tvSelectedFileName.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

        // Get EventResponse from Intent
        eventData = (EventResponse) getIntent().getSerializableExtra("eventData");
        
        // Get registration info if coming from MyEventsActivity
        registrationId = getIntent().getIntExtra("registrationId", -1);
        isRegistered = getIntent().getBooleanExtra("isRegistered", false);
        registrationStatus = getIntent().getStringExtra("registrationStatus");
        
        if (registrationId == -1) {
            registrationId = null;
        }
        
        // If eventData is null, try to load by eventId
        if (eventData == null) {
            int eventId = getIntent().getIntExtra("eventId", -1);
            if (eventId != -1) {
                loadEventById(eventId);
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin sự kiện", Toast.LENGTH_SHORT).show();
                finish();
            }
            return;
        }

        setupUI();
    }
    
    private void loadEventById(int eventId) {
        showProgressDialog("Đang tải thông tin sự kiện...");
        
        ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
        Call<RestResponse<EventResponse>> call = apiService.getEventById(eventId);
        
        call.enqueue(new Callback<RestResponse<EventResponse>>() {
            @Override
            public void onResponse(Call<RestResponse<EventResponse>> call, 
                                 Response<RestResponse<EventResponse>> response) {
                dismissProgressDialog();
                
                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<EventResponse> restResponse = response.body();
                    eventData = restResponse.getData();
                    
                    if (eventData != null) {
                        setupUI();
                    } else {
                        Toast.makeText(DetailEventActivity.this, 
                            "Không có dữ liệu sự kiện", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(DetailEventActivity.this, 
                        "Không thể tải thông tin sự kiện", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            
            @Override
            public void onFailure(Call<RestResponse<EventResponse>> call, Throwable t) {
                dismissProgressDialog();
                Toast.makeText(DetailEventActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    
    private void setupUI() {
        setupClickListeners();
        displayEventInfo();
        loadImages();
        checkUserRoleAndShowRegisterButton();
        checkUserRoleAndSetupShareButton();
    }
    
    private void checkUserRoleAndSetupShareButton() {
        if (ApiConfig.isOrganizer()) {
            // Change share button to send certificate button
            binding.btnShare.setImageResource(R.drawable.ic_certificate);
            binding.btnShare.setOnClickListener(v -> {
                if (eventData != null && "ACTIVE".equals(eventData.getStatus())) {
                    showSendCertificateDialog();
                } else {
                    Toast.makeText(this, "Chỉ có thể gửi thông báo cho sự kiện đang diễn ra", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Default share behavior
            binding.btnShare.setImageResource(R.drawable.ic_share);
            binding.btnShare.setOnClickListener(v -> {
                Toast.makeText(this, "Chia sẻ sự kiện", Toast.LENGTH_SHORT).show();
            });
        }
    }
    

    private void checkUserRoleAndShowRegisterButton() {
        if (isRegistered && registrationId != null) {
            // User already registered - show cancel button
            binding.btnRegisterEvent.setVisibility(View.VISIBLE);
            binding.btnRegisterEvent.setText("Hủy đăng ký");
            binding.btnRegisterEvent.setBackgroundColor(getResources().getColor(R.color.status_rejected));
        } else if (ApiConfig.isVolunteer()) {
            // User not registered - show register button
            binding.btnRegisterEvent.setVisibility(View.VISIBLE);
            binding.btnRegisterEvent.setText("Đăng ký tham gia");
        } else {
            binding.btnRegisterEvent.setVisibility(View.GONE);
        }
    }
    
    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        
        binding.btnFavorite.setOnClickListener(v -> {
            Toast.makeText(this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
        });
        
        binding.btnShare.setOnClickListener(v -> {
            Toast.makeText(this, "Chia sẻ sự kiện", Toast.LENGTH_SHORT).show();
        });
        
        binding.btnRegisterEvent.setOnClickListener(v -> {
            if (eventData != null) {
                if (isRegistered && registrationId != null) {
                    // Show cancel confirmation
                    showCancelRegistrationDialog();
                } else {
                    // Show register confirmation
                    showRegistrationConfirmDialog();
                }
            }
        });
    }
    
    private void displayEventInfo() {
        // Basic info
        setText(binding.tvEventTitle, eventData.getTitle());
        setText(binding.tvEventDescription, eventData.getDescription());
        setText(binding.tvOrganizationName, eventData.getCreatorName());
        setText(binding.tvEventLocation, eventData.getLocation());
        setText(binding.tvMapLocation, eventData.getLocation());
        
        // Date and time
        if (eventData.getEventStartTime() != null) {
            binding.tvEventDate.setText(eventData.getEventStartTime());
        }
        binding.tvEventTime.setText("1 ngày");
        
        // Stats
        binding.tvEventParticipants.setText(eventData.getCurrentParticipants() + "/" + eventData.getNumOfVolunteers());
        binding.tvEventReward.setText(eventData.getRewardPoints() + " điểm");
        
        // Category
        if (eventData.getCategory() != null && !eventData.getCategory().isEmpty()) {
            binding.tvEventCategory.setText(eventData.getCategory());
        }
    }
    
    private void loadImages() {
        // Event banner
        String imageUrl = eventData.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.banner_event_default)
                .error(R.drawable.banner_event_default)
                .override(800, 400) // Limit size to prevent OOM
                .centerCrop()
                .into(binding.ivEventBanner);
        } else {
            binding.ivEventBanner.setImageResource(R.drawable.banner_event_default);
        }
        
        // Map preview
        Glide.with(this)
            .load("https://images.viblo.asia/01cb5447-ae32-46db-8224-6c7392202648.png")
            .placeholder(R.drawable.banner_event_default)
            .error(R.drawable.banner_event_default)
            .override(600, 400)
            .centerCrop()
            .into(binding.ivMapPreview);
    }
    
    private void setText(android.widget.TextView textView, String text) {
        if (text != null) textView.setText(text);
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Show confirmation dialog before registering for event
     */
    private void showRegistrationConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng ký tham gia sự kiện")
                .setMessage("Bạn có chắc chắn muốn đăng ký tham gia sự kiện \"" + eventData.getTitle() + "\"?")
                .setPositiveButton("Đăng ký", (dialog, which) -> {
                    registerForEvent();
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
    
    /**
     * Register user for the event via API
     */
    private void registerForEvent() {
        if (isRegistering) return;
        
        // Check if user is logged in
        String token = ApiConfig.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập để đăng ký sự kiện", Toast.LENGTH_LONG).show();
            return;
        }
        
        isRegistering = true;
        showProgressDialog("Đang đăng ký...");
        
        // Create registration request
        EventRegistrationRequest request = new EventRegistrationRequest(
                eventData.getId(),
                "" // notes - có thể để trống hoặc thêm dialog nhập ghi chú
        );
        
        ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
        Call<EventRegistrationResponse> call = apiService.registerForEvent(request);
        
        call.enqueue(new Callback<EventRegistrationResponse>() {
            @Override
            public void onResponse(Call<EventRegistrationResponse> call, 
                                 Response<EventRegistrationResponse> response) {
                isRegistering = false;
                dismissProgressDialog();
                
                if (response.isSuccessful() && response.body() != null) {
                    EventRegistrationResponse registration = response.body();
                    showSuccessDialog();
                } else {
                    String errorMsg = "Không thể đăng ký sự kiện";
                    if (response.code() == 400) {
                        errorMsg = "Bạn đã đăng ký sự kiện này rồi hoặc sự kiện đã đầy";
                    } else if (response.code() == 401) {
                        errorMsg = "Vui lòng đăng nhập lại";
                    } else if (response.code() == 403) {
                        errorMsg = "Bạn không có quyền đăng ký sự kiện này";
                    }
                    
                    Toast.makeText(DetailEventActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }
            
            @Override
            public void onFailure(Call<EventRegistrationResponse> call, Throwable t) {
                isRegistering = false;
                dismissProgressDialog();
                
                Toast.makeText(DetailEventActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), 
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng ký thành công!")
                .setMessage("Bạn đã đăng ký tham gia sự kiện \"" + eventData.getTitle() + "\" thành công. " +
                        "Vui lòng chờ ban tổ chức xác nhận.")
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }
    
    /**
     * Show confirmation dialog before canceling registration
     */
    private void showCancelRegistrationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hủy đăng ký")
                .setMessage("Bạn có chắc chắn muốn hủy đăng ký tham gia sự kiện \"" + eventData.getTitle() + "\"?")
                .setPositiveButton("Hủy đăng ký", (dialog, which) -> {
                    cancelRegistration();
                })
                .setNegativeButton("Không", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
    
    /**
     * Cancel registration via API
     */
    private void cancelRegistration() {
        if (isRegistering || registrationId == null) return;
        
        isRegistering = true;
        showProgressDialog("Đang hủy đăng ký...");
        
        ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
        Call<Void> call = apiService.cancelRegistration(registrationId);
        
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isRegistering = false;
                dismissProgressDialog();
                
                if (response.isSuccessful()) {
                    new AlertDialog.Builder(DetailEventActivity.this)
                            .setTitle("Thành công")
                            .setMessage("Đã hủy đăng ký tham gia sự kiện thành công")
                            .setPositiveButton("OK", (dialog, which) -> {
                                dialog.dismiss();
                                // Set result to notify MyEventsActivity to reload
                                setResult(RESULT_OK);
                                finish(); // Return to previous screen
                            })
                            .setCancelable(false)
                            .show();
                } else {
                    String errorMsg = "Không thể hủy đăng ký";
                    if (response.code() == 404) {
                        errorMsg = "Không tìm thấy đăng ký";
                    } else if (response.code() == 403) {
                        errorMsg = "Bạn không có quyền hủy đăng ký này";
                    }
                    Toast.makeText(DetailEventActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isRegistering = false;
                dismissProgressDialog();
                Toast.makeText(DetailEventActivity.this,
                        "Lỗi kết nối: " + t.getMessage(), 
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void showSendCertificateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_send_notification, null);
        builder.setView(dialogView);
        
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etContent = dialogView.findViewById(R.id.etContent);
        Button btnAttachFile = dialogView.findViewById(R.id.btnAttachFile);
        tvSelectedFileName = dialogView.findViewById(R.id.tvFileName);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSend = dialogView.findViewById(R.id.btnSend);
        
        // Reset selection
        selectedFileUri = null;
        
        btnAttachFile.setOnClickListener(v -> {
            filePickerLauncher.launch("*/*");
        });
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        btnSend.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();
            
            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tiêu đề và nội dung", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (selectedFileUri != null) {
                uploadFileAndSendNotification(title, content, dialog);
            } else {
                sendNotification(title, content, null, dialog);
            }
        });
        
        dialog.show();
    }
    
    private void uploadFileAndSendNotification(String title, String content, AlertDialog dialog) {
        showProgressDialog("Đang tải lên tệp...");
        
        try {
            File file = getFileFromUri(selectedFileUri);
            if (file == null) {
                dismissProgressDialog();
                Toast.makeText(this, "Lỗi khi đọc tệp", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Determine MIME type
            String mimeType = getContentResolver().getType(selectedFileUri);
            if (mimeType == null) {
                mimeType = "multipart/form-data";
            }
            
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            
            ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
            Call<Map<String, Object>> call = apiService.uploadImage(body);
            
            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String fileUrl = (String) response.body().get("imageUrl");
                        sendNotification(title, content, fileUrl, dialog);
                    } else {
                        dismissProgressDialog();
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                            Toast.makeText(DetailEventActivity.this, "Tải lên thất bại: " + errorBody, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(DetailEventActivity.this, "Tải lên thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                
                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    dismissProgressDialog();
                    Toast.makeText(DetailEventActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            
        } catch (Exception e) {
            dismissProgressDialog();
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void sendNotification(String title, String content, String attachmentUrl, AlertDialog dialog) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            showProgressDialog("Đang gửi thông báo...");
        } else {
            progressDialog.setMessage("Đang gửi thông báo...");
        }
        
        SendNotificationRequest request = new SendNotificationRequest(
                eventData.getId(),
                title,
                content,
                "ALL", // Send to all participants
                attachmentUrl
        );
        
        ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
        Call<RestResponse<Map<String, Object>>> call = apiService.sendNotification(request);
        
        call.enqueue(new Callback<RestResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<RestResponse<Map<String, Object>>> call, Response<RestResponse<Map<String, Object>>> response) {
                dismissProgressDialog();
                if (response.isSuccessful()) {
                    Toast.makeText(DetailEventActivity.this, "Đã gửi thông báo thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(DetailEventActivity.this, "Gửi thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<RestResponse<Map<String, Object>>> call, Throwable t) {
                dismissProgressDialog();
                Toast.makeText(DetailEventActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private File getFileFromUri(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        
        // Get file name
        String fileName = "temp_file";
        android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
            if (nameIndex != -1) {
                fileName = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        
        File file = new File(getCacheDir(), fileName);
        FileOutputStream outputStream = new FileOutputStream(file);
        
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        
        outputStream.close();
        inputStream.close();
        return file;
    }

    /**
     * Show progress dialog
     */
    private void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }
    
    /**
     * Dismiss progress dialog
     */
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
        
        if (!isFinishing()) {
            Glide.with(getApplicationContext()).pauseRequests();
        }
        
        binding = null;
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Glide.with(this).pauseRequests();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(this).resumeRequests();
    }
}
