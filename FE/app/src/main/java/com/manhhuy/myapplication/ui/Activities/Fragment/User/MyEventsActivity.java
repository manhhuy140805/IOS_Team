package com.manhhuy.myapplication.ui.Activities.Fragment.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.manhhuy.myapplication.adapter.EventRegistrationAdapter;
import com.manhhuy.myapplication.databinding.ActivityMyEventsBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.response.EventRegistrationResponse;
import com.manhhuy.myapplication.helper.response.PageResponse;
import com.manhhuy.myapplication.helper.response.RestResponse;
import com.manhhuy.myapplication.ui.Activities.DetailEventActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyEventsActivity extends AppCompatActivity {

    private static final String TAG = "MyEventsActivity";
    private ActivityMyEventsBinding binding;
    private EventRegistrationAdapter adapter;
    private List<EventRegistrationResponse> registrationList = new ArrayList<>();
    private int currentPage = 0;
    private static final int PAGE_SIZE = 10;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyEventsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        loadMyRegistrations();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new EventRegistrationAdapter(registrationList);
        binding.recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewEvents.setAdapter(adapter);

        // Set click listener for registration items
        adapter.setListener(registration -> {
            // Open event detail using eventId and pass registrationId for cancel option
            if (registration.getEventId() != null) {
                Intent intent = new Intent(MyEventsActivity.this, DetailEventActivity.class);
                intent.putExtra("eventId", registration.getEventId());
                intent.putExtra("registrationId", registration.getId());
                intent.putExtra("isRegistered", true);
                intent.putExtra("registrationStatus", registration.getStatus());
                startActivityForResult(intent, 100); // Use startActivityForResult to get callback
            }
        });
    }

    private void setupClickListeners() {
        binding.btnRetry.setOnClickListener(v -> {
            currentPage = 0;
            registrationList.clear();
            loadMyRegistrations();
        });
    }

    private void loadMyRegistrations() {
        if (isLoading) return;

        isLoading = true;
        showLoading();

        // Get token to ensure user is authenticated
        String token = ApiConfig.getToken();
        
        Log.d(TAG, "Token: " + (token != null ? "exists (length: " + token.length() + ")" : "null"));
        
        if (token == null || token.isEmpty()) {
            Log.w(TAG, "No token found, user not logged in");
            isLoading = false;
            showError("Vui lòng đăng nhập để xem sự kiện của bạn");
            return;
        }
        
        Log.d(TAG, "Loading my event registrations from: " + ApiConfig.getClient().baseUrl() + "event-registrations/my-registrations");

        ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
        
        // Call getMyRegistrations - returns event registrations of the current user
        Call<RestResponse<PageResponse<EventRegistrationResponse>>> call = apiService.getMyRegistrations(
                currentPage,
                PAGE_SIZE,
                null  // status filter (null = all statuses)
        );

        call.enqueue(new Callback<RestResponse<PageResponse<EventRegistrationResponse>>>() {
            @Override
            public void onResponse(Call<RestResponse<PageResponse<EventRegistrationResponse>>> call, 
                                 Response<RestResponse<PageResponse<EventRegistrationResponse>>> response) {
                isLoading = false;

                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response successful: " + response.isSuccessful());
                
                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<PageResponse<EventRegistrationResponse>> restResponse = response.body();
                    PageResponse<EventRegistrationResponse> pageResponse = restResponse.getData();
                    
                    if (pageResponse == null) {
                        Log.e(TAG, "PageResponse is null in RestResponse");
                        showError("Không có dữ liệu");
                        return;
                    }
                    
                    List<EventRegistrationResponse> registrations = pageResponse.getContent();

                    Log.d(TAG, "Page response: " + pageResponse);
                    Log.d(TAG, "Registrations count: " + (registrations != null ? registrations.size() : "null"));

                    if (registrations != null && !registrations.isEmpty()) {
                        registrationList.clear();
                        registrationList.addAll(registrations);
                        adapter.setRegistrations(registrationList);
                        showContent();
                        
                        Log.d(TAG, "Loaded " + registrations.size() + " registrations");
                        for (EventRegistrationResponse reg : registrations) {
                            Log.d(TAG, "Registration: " + reg.getEventTitle() + " - Status: " + reg.getStatus());
                        }
                    } else {
                        Log.d(TAG, "No registrations found, showing empty state");
                        if (currentPage == 0) {
                            showEmptyState();
                        }
                    }
                } else {
                    Log.e(TAG, "API Error: " + response.code() + " - " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    showError("Không thể tải dữ liệu: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<RestResponse<PageResponse<EventRegistrationResponse>>> call, Throwable t) {
                isLoading = false;
                Log.e(TAG, "Network Error: " + t.getMessage(), t);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerViewEvents.setVisibility(View.GONE);
        binding.emptyState.setVisibility(View.GONE);
        binding.errorState.setVisibility(View.GONE);
    }

    private void showContent() {
        binding.progressBar.setVisibility(View.GONE);
        binding.recyclerViewEvents.setVisibility(View.VISIBLE);
        binding.emptyState.setVisibility(View.GONE);
        binding.errorState.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        binding.progressBar.setVisibility(View.GONE);
        binding.recyclerViewEvents.setVisibility(View.GONE);
        binding.emptyState.setVisibility(View.VISIBLE);
        binding.errorState.setVisibility(View.GONE);
    }

    private void showError(String message) {
        binding.progressBar.setVisibility(View.GONE);
        binding.recyclerViewEvents.setVisibility(View.GONE);
        binding.emptyState.setVisibility(View.GONE);
        binding.errorState.setVisibility(View.VISIBLE);
        binding.tvErrorMessage.setText(message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data when returning to this activity
        if (registrationList.isEmpty()) {
            currentPage = 0;
            loadMyRegistrations();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // Reload data if returning from DetailEventActivity with success result
        if (requestCode == 100 && resultCode == RESULT_OK) {
            currentPage = 0;
            registrationList.clear();
            loadMyRegistrations();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
