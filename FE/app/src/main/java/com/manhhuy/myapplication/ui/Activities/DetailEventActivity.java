package com.manhhuy.myapplication.ui.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ActivityDetailEventBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.request.EventRegistrationRequest;
import com.manhhuy.myapplication.helper.response.EventRegistrationResponse;
import com.manhhuy.myapplication.helper.response.EventResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailEventActivity extends AppCompatActivity {

    private static final String TAG = "DetailEventActivity";
    private ActivityDetailEventBinding binding;
    private EventResponse eventData;
    private ProgressDialog progressDialog;
    private boolean isRegistering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get EventResponse from Intent
        eventData = (EventResponse) getIntent().getSerializableExtra("eventData");
        
        if (eventData == null) {
            Toast.makeText(this, "Không tìm thấy thông tin sự kiện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupUI();
    }
    
    private void setupUI() {
        setupClickListeners();
        displayEventInfo();
        loadImages();
        checkUserRoleAndShowRegisterButton();
    }
    

    private void checkUserRoleAndShowRegisterButton() {
        if (ApiConfig.isVolunteer()) {
            binding.btnRegisterEvent.setVisibility(View.VISIBLE);
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
                showRegistrationConfirmDialog();
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
