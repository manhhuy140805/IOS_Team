package com.manhhuy.myapplication.ui.Activities.Fragment.Common;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.CategoryAdapter;
import com.manhhuy.myapplication.adapter.EventAdapter;
import com.manhhuy.myapplication.adapter.SearchResultAdapter;
import com.manhhuy.myapplication.databinding.FragmentHomeBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.JwtUtil;
import com.manhhuy.myapplication.helper.response.EventResponse;
import com.manhhuy.myapplication.helper.response.EventTypeResponse;
import com.manhhuy.myapplication.helper.response.PageResponse;
import com.manhhuy.myapplication.helper.response.RestResponse;
import com.manhhuy.myapplication.helper.response.UserResponse;
import com.manhhuy.myapplication.model.SearchResult;
import com.manhhuy.myapplication.ui.Activities.DetailEventActivity;
import com.manhhuy.myapplication.ui.Activities.AdminActivity;
import com.manhhuy.myapplication.ui.Activities.UserActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private SearchResultAdapter featuredAdapter;
    private EventAdapter eventAdapter;
    private CategoryAdapter categoryAdapter;
    private ApiEndpoints apiEndpoints;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize API client
        apiEndpoints = ApiConfig.getClient().create(ApiEndpoints.class);

        // Load user info first
        // Moved to onResume to update points when returning
        
        setupCategoriesRecyclerView();
        loadEventTypes();
        setupFeaturedRecyclerView();
        loadFeaturedEvents();
        setupClickListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo();
    }

    /**
     * Load user information from API using JWT token
     */
    private void loadUserInfo() {
        // Get token from SharedPreferences
        String token = ApiConfig.getToken();
        
        if (token == null || token.isEmpty()) {
            Log.w(TAG, "No token found, user not logged in");
            setDefaultUserInfo();
            return;
        }
        
        // Check if token is expired
        if (JwtUtil.isExpired(token)) {
            Log.w(TAG, "Token expired");
            setDefaultUserInfo();
            return;
        }
        
        Log.d(TAG, "Loading current user info");
        
        // Call API to get current user info
        Call<RestResponse<UserResponse>> call = apiEndpoints.getCurrentUser();
        
        call.enqueue(new Callback<RestResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<RestResponse<UserResponse>> call, 
                                 Response<RestResponse<UserResponse>> response) {
                if (binding == null) return; // Fragment destroyed
                
                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<UserResponse> restResponse = response.body();
                    
                    if (restResponse.getData() != null) {
                        UserResponse user = restResponse.getData();
                        updateUserInfo(user);
                        Log.d(TAG, "User info loaded successfully: " + user.getFullName());
                    } else {
                        Log.e(TAG, "API error: " + restResponse.getMessage());
                        setDefaultUserInfo();
                    }
                } else {
                    Log.e(TAG, "Response not successful: " + response.code());
                    setDefaultUserInfo();
                }
            }
            
            @Override
            public void onFailure(Call<RestResponse<UserResponse>> call, Throwable t) {
                if (binding == null) return; // Fragment destroyed
                Log.e(TAG, "Failed to load user info: " + t.getMessage(), t);
                setDefaultUserInfo();
            }
        });
    }
    
    /**
     * Update UI with user information
     */
    private void updateUserInfo(UserResponse user) {
        if (binding == null) return;
        
        // Update greeting with user's name
        String firstName = getFirstName(user.getFullName());
        binding.tvGreeting.setText("Xin chào, " + firstName + "! 👋");
        
        // Update points
        Integer points = user.getTotalPoints();
        if (points != null) {
            binding.impactPoints.setText(String.format("%,d", points));
        } else {
            binding.impactPoints.setText("0");
        }
        
        // Load avatar
        loadAvatar(user.getAvatarUrl());
    }
    
    /**
     * Set default user info when not logged in or error
     */
    private void setDefaultUserInfo() {
        if (binding == null) return;
        binding.tvGreeting.setText("Xin chào! 👋");
        binding.impactPoints.setText("0");
        
        // Set default avatar
        binding.profileIcon.setImageResource(R.drawable.ic_profile);
    }
    
    /**
     * Extract first name from full name
     */
    private String getFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Bạn";
        }
        
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length > 0) {
            return parts[parts.length - 1]; // Vietnamese names: last word is first name
        }
        return fullName;
    }
    
    /**
     * Load avatar image with proper URL handling
     */
    private void loadAvatar(String avatarUrl) {
        if (binding == null || !isAdded()) {
            return;
        }

        try {
            // Check if avatar URL is valid
            if (avatarUrl != null && !avatarUrl.isEmpty() && 
                !avatarUrl.equals("null") && !avatarUrl.equals("undefined")) {
                
                // Build full URL if needed
                String fullAvatarUrl = avatarUrl;
                if (!avatarUrl.startsWith("http://") && !avatarUrl.startsWith("https://")) {
                    // If relative URL, prepend base URL
                    fullAvatarUrl = "http://10.0.2.2:8081" + (avatarUrl.startsWith("/") ? "" : "/") + avatarUrl;
                }
                
                Log.d(TAG, "Loading avatar from: " + fullAvatarUrl);
                
                com.bumptech.glide.Glide.with(this)
                    .load(fullAvatarUrl)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .circleCrop()
                    .into(binding.profileIcon);
            } else {
                Log.d(TAG, "No valid avatar URL, using default icon");
                com.bumptech.glide.Glide.with(this)
                    .load(R.drawable.ic_profile)
                    .circleCrop()
                    .into(binding.profileIcon);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading avatar: " + e.getMessage(), e);
            // Fallback to default icon
            binding.profileIcon.setImageResource(R.drawable.ic_profile);
        }
    }

    private void setupCategoriesRecyclerView() {
        categoryAdapter = new CategoryAdapter(new ArrayList<>());
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), 
                LinearLayoutManager.HORIZONTAL, false);
        binding.categoriesRecyclerView.setLayoutManager(layoutManager);
        binding.categoriesRecyclerView.setAdapter(categoryAdapter);
        
        categoryAdapter.setListener(category -> {
            if (category.getId() == null) {
                // Load all events
                loadFeaturedEvents();
            } else {
                // Load events by type
                loadEventsByType(category.getId(), category.getName());
            }
        });
    }

    private void loadEventTypes() {
        Call<RestResponse<List<EventTypeResponse>>> call = apiEndpoints.getEventTypes();
        
        call.enqueue(new Callback<RestResponse<List<EventTypeResponse>>>() {
            @Override
            public void onResponse(Call<RestResponse<List<EventTypeResponse>>> call, 
                                 Response<RestResponse<List<EventTypeResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<List<EventTypeResponse>> restResponse = response.body();
                    
                    if (restResponse.getStatusCode() == 200 && restResponse.getData() != null) {
                        List<EventTypeResponse> eventTypes = new ArrayList<>(restResponse.getData());
                        
                        // Add "Tất cả" category at the beginning
                        EventTypeResponse allCategory = new EventTypeResponse();
                        allCategory.setId(null);
                        allCategory.setName("Tất cả");
                        eventTypes.add(0, allCategory);
                        
                        categoryAdapter.setCategories(eventTypes);
                    } else {
                        Toast.makeText(getContext(), 
                                "Không thể tải danh mục: " + restResponse.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), 
                            "Không thể tải danh mục", 
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RestResponse<List<EventTypeResponse>>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupFeaturedRecyclerView() {
        eventAdapter = new EventAdapter(new ArrayList<>());
        binding.eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.eventsRecyclerView.setAdapter(eventAdapter);
        
        eventAdapter.setListener(event -> {
            Intent intent = new Intent(getContext(), DetailEventActivity.class);
            intent.putExtra("eventData", event);
            startActivity(intent);
        });
    }

    private void loadFeaturedEvents() {
        // Load first page of events, sorted by createdAt desc, only ACTIVE events
        Call<RestResponse<PageResponse<EventResponse>>> call = apiEndpoints.getAllEvents(
                0, 10, "createdAt", "desc", 
                null, null, null, null, null, null, null, null
        );
        
        call.enqueue(new Callback<RestResponse<PageResponse<EventResponse>>>() {
            @Override
            public void onResponse(Call<RestResponse<PageResponse<EventResponse>>> call, 
                                 Response<RestResponse<PageResponse<EventResponse>>> response) {
                if (binding == null) return; // Fragment destroyed
                
                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<PageResponse<EventResponse>> restResponse = response.body();
                    
                    if (restResponse.getStatusCode() == 200 && restResponse.getData() != null) {
                        PageResponse<EventResponse> pageResponse = restResponse.getData();
                        List<EventResponse> events = pageResponse.getContent();
                        eventAdapter.setEvents(events);
                        
                        // Show/hide empty state
                        if (events.isEmpty()) {
                            binding.eventsRecyclerView.setVisibility(View.GONE);
                            binding.emptyStateLayout.setVisibility(View.VISIBLE);
                        } else {
                            binding.eventsRecyclerView.setVisibility(View.VISIBLE);
                            binding.emptyStateLayout.setVisibility(View.GONE);
                        }
                        
                    } else {
                        showEmptyState("Không thể tải sự kiện");
                    }
                } else {
                    showEmptyState("Không thể tải sự kiện");
                }
            }

            @Override
            public void onFailure(Call<RestResponse<PageResponse<EventResponse>>> call, Throwable t) {
                if (binding == null) return; // Fragment destroyed
                showEmptyState("Lỗi kết nối");
            }
        });
    }
    
    private void loadEventsByType(Integer eventTypeId, String typeName) {
        Call<RestResponse<PageResponse<EventResponse>>> call = apiEndpoints.getEventsByType(
                eventTypeId, 0, 10, "createdAt", "desc"
        );
        
        call.enqueue(new Callback<RestResponse<PageResponse<EventResponse>>>() {
            @Override
            public void onResponse(Call<RestResponse<PageResponse<EventResponse>>> call, 
                                 Response<RestResponse<PageResponse<EventResponse>>> response) {
                if (binding == null) return; // Fragment destroyed
                
                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<PageResponse<EventResponse>> restResponse = response.body();
                    
                    if (restResponse.getStatusCode() == 200 && restResponse.getData() != null) {
                        PageResponse<EventResponse> pageResponse = restResponse.getData();
                        List<EventResponse> events = pageResponse.getContent();
                        eventAdapter.setEvents(events);
                        
                        // Show/hide empty state
                        if (events.isEmpty()) {
                            binding.eventsRecyclerView.setVisibility(View.GONE);
                            binding.emptyStateLayout.setVisibility(View.VISIBLE);
                            binding.emptyStateText.setText("Không có sự kiện " + typeName);
                        } else {
                            binding.eventsRecyclerView.setVisibility(View.VISIBLE);
                            binding.emptyStateLayout.setVisibility(View.GONE);
                        }
                        
                    } else {
                        showEmptyState("Không thể tải sự kiện " + typeName);
                    }
                } else {
                    showEmptyState("Không thể tải sự kiện " + typeName);
                }
            }

            @Override
            public void onFailure(Call<RestResponse<PageResponse<EventResponse>>> call, Throwable t) {
                if (binding == null) return; // Fragment destroyed
                showEmptyState("Lỗi kết nối");
            }
        });
    }
    
    private void showEmptyState(String message) {
        if (binding == null) return; // Fragment destroyed
        binding.eventsRecyclerView.setVisibility(View.GONE);
        binding.emptyStateLayout.setVisibility(View.VISIBLE);
        binding.emptyStateText.setText(message);
    }

    private void setupClickListeners() {
        // Search bar click listener
        binding.searchContainer.setOnClickListener(v -> {
            if (getActivity() instanceof AdminActivity) {
                ((AdminActivity) getActivity()).switchToSearchTab();
            } else if (getActivity() instanceof UserActivity) {
                ((UserActivity) getActivity()).switchToSearchTab();
            } else {
                Toast.makeText(getContext(), "Chuyển đến trang tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });


        binding.viewAllFeatured.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Xem tất cả sự kiện nổi bật", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}