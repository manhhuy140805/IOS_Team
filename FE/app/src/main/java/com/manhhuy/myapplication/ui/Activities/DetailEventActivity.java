package com.manhhuy.myapplication.ui.Activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ActivityDetailEventBinding;
import com.manhhuy.myapplication.model.EventPost;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailEventActivity extends AppCompatActivity {

    private ActivityDetailEventBinding binding;
    private EventPost event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        event = (EventPost) getIntent().getSerializableExtra("eventPost");
        
        if (event == null) {
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
    }
    
    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnFavorite.setOnClickListener(v -> showToast("Đã thêm vào yêu thích"));
        binding.btnShare.setOnClickListener(v -> showToast("Chia sẻ sự kiện"));
        binding.btnRegisterEvent.setOnClickListener(v -> showToast("Đăng ký tham gia: " + event.getTitle()));
    }
    
    private void displayEventInfo() {
        // Basic info
        setText(binding.tvEventTitle, event.getTitle());
        setText(binding.tvEventDescription, event.getDescription());
        setText(binding.tvOrganizationName, event.getOrganizationName());
        setText(binding.tvEventLocation, event.getLocation());
        setText(binding.tvMapLocation, event.getLocation());
        
        // Date and time
        if (event.getEventDate() != null) {
            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(event.getEventDate());
            binding.tvEventDate.setText(date);
        }
        binding.tvEventTime.setText("1 ngày");
        
        // Stats
        binding.tvEventParticipants.setText(event.getCurrentParticipants() + "/" + event.getMaxParticipants());
        binding.tvEventReward.setText(event.getRewardPoints() + " điểm");
        
        // Category
        if (event.getTags() != null && !event.getTags().isEmpty()) {
            binding.tvEventCategory.setText(event.getTags().get(0));
        }
    }
    
    private void loadImages() {
        // Event banner
        String imageUrl = event.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.banner_event_default)
                .error(R.drawable.banner_event_default)
                .centerCrop()
                .into(binding.ivEventBanner);
        }
        
        // Map preview
        Glide.with(this)
            .load("https://images.viblo.asia/01cb5447-ae32-46db-8224-6c7392202648.png")
            .placeholder(R.drawable.banner_event_default)
            .centerCrop()
            .into(binding.ivMapPreview);
    }
    
    private void setText(android.widget.TextView textView, String text) {
        if (text != null) textView.setText(text);
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
