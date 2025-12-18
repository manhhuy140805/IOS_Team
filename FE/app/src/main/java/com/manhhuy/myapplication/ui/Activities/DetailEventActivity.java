package com.manhhuy.myapplication.ui.Activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ActivityDetailEventBinding;
import com.manhhuy.myapplication.helper.response.EventResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailEventActivity extends AppCompatActivity {

    private ActivityDetailEventBinding binding;
    private EventResponse eventData;

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
                Toast.makeText(this, "Đăng ký tham gia: " + eventData.getTitle(), Toast.LENGTH_SHORT).show();
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
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
