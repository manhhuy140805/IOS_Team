package com.manhhuy.myapplication.ui.Activities.Fragment.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.manhhuy.myapplication.adapter.EventAdapter;
import com.manhhuy.myapplication.databinding.ActivityMyEventsBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.response.EventResponse;
import com.manhhuy.myapplication.helper.response.PageResponse;
import com.manhhuy.myapplication.ui.Activities.DetailEventActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyEventsActivity extends AppCompatActivity {

    private static final String TAG = "MyEventsActivity";
    private ActivityMyEventsBinding binding;
    private EventAdapter eventAdapter;
    private List<EventResponse> eventList = new ArrayList<>();
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
        loadMyEvents();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        eventAdapter = new EventAdapter(eventList);
        binding.recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewEvents.setAdapter(eventAdapter);

        // Set click listener for event items
        eventAdapter.setListener(event -> {
            Intent intent = new Intent(MyEventsActivity.this, DetailEventActivity.class);
            intent.putExtra("event", event);
            startActivity(intent);
        });
    }

    private void setupClickListeners() {
        binding.btnRetry.setOnClickListener(v -> {
            currentPage = 0;
            eventList.clear();
            loadMyEvents();
        });
    }

    private void loadMyEvents() {
        if (isLoading) return;

        isLoading = true;
        showLoading();

        ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
        
        // Using userId = 1 as sample since user info is not saved yet
        Call<PageResponse<EventResponse>> call = apiService.getEventsByUserId(
                1, // Sample user ID
                currentPage,
                PAGE_SIZE
        );

        call.enqueue(new Callback<PageResponse<EventResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<EventResponse>> call, Response<PageResponse<EventResponse>> response) {
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    PageResponse<EventResponse> pageResponse = response.body();
                    List<EventResponse> events = pageResponse.getContent();

                    if (events != null && !events.isEmpty()) {
                        eventList.clear();
                        eventList.addAll(events);
                        eventAdapter.setEvents(eventList);
                        showContent();
                        
                        Log.d(TAG, "Loaded " + events.size() + " events");
                    } else {
                        if (currentPage == 0) {
                            showEmptyState();
                        }
                    }
                } else {
                    Log.e(TAG, "API Error: " + response.code() + " - " + response.message());
                    showError("Không thể tải dữ liệu: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PageResponse<EventResponse>> call, Throwable t) {
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
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
