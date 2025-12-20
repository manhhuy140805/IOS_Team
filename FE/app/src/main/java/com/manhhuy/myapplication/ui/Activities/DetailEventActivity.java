package com.manhhuy.myapplication.ui.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
import java.util.Date;
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
        EdgeToEdge.enable(this);
        binding = ActivityDetailEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get event data from intent
        if (getIntent().hasExtra("eventData")) {
            eventData = getIntent().getParcelableExtra("eventData");
        }

        setupClickListeners();
        
        if (eventData != null) {
            displayEventData();
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin sự kiện", Toast.LENGTH_SHORT).show();
            finish();
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
    
    private void displayEventData() {
        // Set title
        if (eventData.getTitle() != null) {
            binding.tvEventTitle.setText(eventData.getTitle());
        }
        
        // Set description
        if (eventData.getDescription() != null) {
            binding.tvEventDescription.setText(eventData.getDescription());
        }
        
        // Set organization name
        if (eventData.getCreatorName() != null) {
            binding.tvOrganizationName.setText(eventData.getCreatorName());
        }
        
        // Set event date
        if (eventData.getEventStartTime() != null) {
            binding.tvEventDate.setText(formatDate(eventData.getEventStartTime()));
        }
        
        // Set time range
        if (eventData.getEventStartTime() != null && eventData.getEventEndTime() != null) {
            binding.tvEventTime.setText(formatDateRange(eventData.getEventStartTime(), eventData.getEventEndTime()));
        }
        
        // Set location
        if (eventData.getLocation() != null) {
            binding.tvEventLocation.setText(eventData.getLocation());
        }
        
        // Set participants
        int current = eventData.getCurrentParticipants() != null ? eventData.getCurrentParticipants() : 0;
        int total = eventData.getNumOfVolunteers() != null ? eventData.getNumOfVolunteers() : 0;
        binding.tvEventParticipants.setText(current + "/" + total);
        
        // Set reward points
        if (eventData.getRewardPoints() != null) {
            binding.tvEventReward.setText(eventData.getRewardPoints() + " điểm");
        }
        
        // Set category/event type
        if (eventData.getEventTypeName() != null) {
            binding.tvEventCategory.setText(eventData.getEventTypeName());
        }
        
        // Load event banner image
        if (eventData.getImageUrl() != null && !eventData.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(eventData.getImageUrl())
                    .placeholder(R.drawable.banner_event_default)
                    .error(R.drawable.banner_event_default)
                    .centerCrop()
                    .into(binding.ivEventBanner);
        } else {
            binding.ivEventBanner.setImageResource(R.drawable.banner_event_default);
        }
        
        // Load map preview image
        Glide.with(this)
                .load("https://images.viblo.asia/01cb5447-ae32-46db-8224-6c7392202648.png")
                .placeholder(R.drawable.banner_event_default)
                .error(R.drawable.banner_event_default)
                .centerCrop()
                .into(binding.ivMapPreview);
        
        // Update map location text
        if (eventData.getLocation() != null) {
            binding.tvMapLocation.setText(eventData.getLocation());
        }
    }
    
    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }
    
    private String formatDateRange(String startTime, String endTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date startDate = inputFormat.parse(startTime);
            Date endDate = inputFormat.parse(endTime);
            
            // Calculate difference in days
            long diffInMillis = endDate.getTime() - startDate.getTime();
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
            
            if (diffInDays == 0) {
                return "1 ngày";
            } else if (diffInDays == 1) {
                return "2 ngày";
            } else {
                return (diffInDays + 1) + " ngày";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "Cả ngày";
        }
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
                    Log.d(TAG, "Registration successful: " + registration.getId());
                    
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
                    
                    Log.e(TAG, "Registration failed: " + response.code() + " - " + response.message());
                    Toast.makeText(DetailEventActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }
            
            @Override
            public void onFailure(Call<EventRegistrationResponse> call, Throwable t) {
                isRegistering = false;
                dismissProgressDialog();
                
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(DetailEventActivity.this, 
                        "Lỗi kết nối: " + t.getMessage(), 
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    
    /**
     * Show success dialog after successful registration
     */
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng ký thành công!")
                .setMessage("Bạn đã đăng ký tham gia sự kiện \"" + eventData.getTitle() + "\" thành công. " +
                        "Vui lòng chờ ban tổ chức xác nhận.")
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    // Optionally: finish() to go back
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
        binding = null;
    }
}
