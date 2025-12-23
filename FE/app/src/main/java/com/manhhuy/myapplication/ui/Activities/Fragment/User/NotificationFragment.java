package com.manhhuy.myapplication.ui.Activities.Fragment.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.NotificationAdapter;
import com.manhhuy.myapplication.databinding.FragmentNotificationBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiService;
import com.manhhuy.myapplication.helper.JwtUtil;
import com.manhhuy.myapplication.helper.response.NotificationResponse;
import com.manhhuy.myapplication.helper.response.RestResponse;
import com.manhhuy.myapplication.helper.response.UserNotificationResponse;
import com.manhhuy.myapplication.model.NotificationItem;
import com.manhhuy.myapplication.ui.Activities.NotificationDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    private static final String TAG = "NotificationFragment";
    private FragmentNotificationBinding binding;
    private NotificationAdapter notificationAdapter;

    private List<NotificationItem> allNotifications = new ArrayList<>();
    private List<NotificationItem> filteredNotifications = new ArrayList<>();
    private String currentFilter = "ALL"; // ALL, UNREAD, READ
    
    private int currentUserId = -1;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ApiConfig
        ApiConfig.init(requireContext());
        
        // Get current user ID
        currentUserId = getUserId();
        if (currentUserId == -1) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            // Optional: Navigate to login
            return;
        }
        
        setupRecyclerView();
        setupFilterButtons();
        setupClickListeners();
        setupSwipeRefresh();
        loadNotifications(); // Gọi API thay vì mock data
    }

    private int getUserId() {
        String token = ApiConfig.getToken();
        if (token != null) {
            Integer userId = JwtUtil.getUserId(token);
            if (userId != null) {
                return userId;
            }
        }
        return -1;
    }

    private void setupRecyclerView() {
        notificationAdapter = new NotificationAdapter(
            filteredNotifications,
            this::onNotificationClick
        );
        
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvNotifications.setAdapter(notificationAdapter);
    }

    private void setupFilterButtons() {
        // Set initial filter (All)
        updateFilterButton(binding.btnFilterAll, true);
        
        binding.btnFilterAll.setOnClickListener(v -> {
            currentFilter = "ALL";
            updateFilterUI(binding.btnFilterAll);
            loadNotifications(); // Call API
        });

        binding.btnFilterUnread.setOnClickListener(v -> {
            currentFilter = "UNREAD";
            updateFilterUI(binding.btnFilterUnread);
            loadNotifications(); // Call API
        });

        binding.btnFilterRead.setOnClickListener(v -> {
            currentFilter = "READ";
            updateFilterUI(binding.btnFilterRead);
            loadNotifications(); // Call API
        });
    }

    private void updateFilterUI(MaterialButton selectedButton) {
        // Reset all buttons
        updateFilterButton(binding.btnFilterAll, false);
        updateFilterButton(binding.btnFilterUnread, false);
        updateFilterButton(binding.btnFilterRead, false);
        
        // Highlight selected
        updateFilterButton(selectedButton, true);
    }

    private void updateFilterButton(MaterialButton button, boolean isSelected) {
        if (isSelected) {
            button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
            button.setStrokeColorResource(R.color.app_green_primary);
            button.setStrokeWidth(4);
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_green_primary));
        } else {
            button.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            button.setStrokeColorResource(R.color.gray_300);
            button.setStrokeWidth(2);
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        }
    }

    private void setupClickListeners() {
        binding.btnMarkAllRead.setOnClickListener(v -> markAllAsRead());
    }

    private void setupSwipeRefresh() {
        // Setup SwipeRefreshLayout
        binding.swipeRefreshLayout.setColorSchemeResources(
            R.color.app_green_primary,
            R.color.cyan,
            R.color.md_theme_light_primary
        );
        
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            // Reload notifications when user swipes down
            loadNotifications();
        });
    }

    // Load notifications từ API
    private void loadNotifications() {
        if (currentUserId == -1) return;

        showLoading();
        
        Call<RestResponse<List<UserNotificationResponse>>> call;
        
        switch (currentFilter) {
            case "UNREAD":
                call = ApiService.api().getUnreadNotifications(currentUserId);
                break;
            case "READ":
                call = ApiService.api().getReadNotifications(currentUserId);
                break;
            case "ALL":
            default:
                call = ApiService.api().getUserNotifications(currentUserId);
                break;
        }
        
        call.enqueue(new Callback<RestResponse<List<UserNotificationResponse>>>() {
            @Override
            public void onResponse(Call<RestResponse<List<UserNotificationResponse>>> call, 
                                 Response<RestResponse<List<UserNotificationResponse>>> response) {
                hideLoading();
                
                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<List<UserNotificationResponse>> restResponse = response.body();
                    if (restResponse.getData() != null) {
                        convertToNotificationItems(restResponse.getData());
                    } else {
                        updateEmptyState();
                    }
                } else {
                    Log.e(TAG, "Error loading notifications: " + response.code());
                    Toast.makeText(getContext(), "Không thể tải thông báo", Toast.LENGTH_SHORT).show();
                    updateEmptyState();
                }
            }

            @Override
            public void onFailure(Call<RestResponse<List<UserNotificationResponse>>> call, Throwable t) {
                hideLoading();
                Log.e(TAG, "Failed to load notifications", t);
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                updateEmptyState();
            }
        });
    }

    // Convert API response sang NotificationItem
    private void convertToNotificationItems(List<UserNotificationResponse> userNotifications) {
        filteredNotifications.clear();
        
        for (UserNotificationResponse userNotif : userNotifications) {
            NotificationResponse notif = userNotif.getNotification();
            
            NotificationItem item = new NotificationItem(
                notif.getId(),
                notif.getTitle(),
                notif.getContent(),
                notif.getSenderRole(),
                notif.getType(),
                userNotif.getIsRead() != null ? userNotif.getIsRead() : false,
                parseDate(notif.getCreatedAt()),
                parseDate(userNotif.getReadAt()),
                notif.getAttached()
            );
            
            filteredNotifications.add(item);
        }
        
        notificationAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    // Parse ISO 8601 date string
    private Date parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.parse(dateString);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date: " + dateString, e);
            return null;
        }
    }

    private void showLoading() {
        if (binding != null) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.rvNotifications.setVisibility(View.GONE);
            binding.emptyState.setVisibility(View.GONE);
        }
    }

    private void hideLoading() {
        if (binding != null) {
            binding.progressBar.setVisibility(View.GONE);
            binding.swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void updateEmptyState() {
        if (filteredNotifications.isEmpty()) {
            binding.rvNotifications.setVisibility(View.GONE);
            binding.emptyState.setVisibility(View.VISIBLE);
        } else {
            binding.rvNotifications.setVisibility(View.VISIBLE);
            binding.emptyState.setVisibility(View.GONE);
        }
    }

    private void onNotificationClick(NotificationItem notification) {
        // Mở màn hình chi tiết thông báo (API sẽ tự động đánh dấu đã đọc)
        Intent intent = new Intent(getContext(), NotificationDetailActivity.class);
        intent.putExtra("notification_id", notification.getId());
        intent.putExtra("notification_title", notification.getTitle());
        intent.putExtra("notification_content", notification.getContent());
        intent.putExtra("notification_sender_role", notification.getSenderRole());
        intent.putExtra("notification_type", notification.getType());
        intent.putExtra("notification_is_read", notification.isRead());
        intent.putExtra("notification_created_at", notification.getCreatedAt() != null ? notification.getCreatedAt().getTime() : System.currentTimeMillis());
        intent.putExtra("notification_read_at", notification.getReadAt() != null ? notification.getReadAt().getTime() : 0);
        intent.putExtra("notification_attached", notification.getAttached());
        startActivity(intent);
    }


    private void markAllAsRead() {
        if (currentUserId == -1) return;

        // Call API để đánh dấu tất cả đã đọc
        ApiService.api().markAllAsRead(currentUserId)
            .enqueue(new Callback<RestResponse<Void>>() {
                @Override
                public void onResponse(Call<RestResponse<Void>> call, Response<RestResponse<Void>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), 
                            "Đã đánh dấu tất cả thông báo là đã đọc", 
                            Toast.LENGTH_SHORT).show();
                        // Reload notifications
                        loadNotifications();
                    } else {
                        Toast.makeText(getContext(), 
                            "Không thể đánh dấu tất cả đã đọc", 
                            Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<RestResponse<Void>> call, Throwable t) {
                    Log.e(TAG, "Failed to mark all as read", t);
                    Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
