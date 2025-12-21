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
import com.manhhuy.myapplication.adapter.admin.event.EventManagerAdapter;
import com.manhhuy.myapplication.adapter.admin.event.OnEventActionListenerInterface;
import com.manhhuy.myapplication.databinding.FragmentEventManagerBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.response.EventResponse;
import com.manhhuy.myapplication.helper.response.PageResponse;
import com.manhhuy.myapplication.helper.response.RestResponse;
import com.manhhuy.myapplication.model.EventPost;
import com.manhhuy.myapplication.ui.Activities.AddEventActivity;
import com.manhhuy.myapplication.ui.Activities.DetailEventActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventManageFragment extends Fragment implements OnEventActionListenerInterface {

    private static final String TAG = "EventManageFragment";
    private FragmentEventManagerBinding binding;
    private EventManagerAdapter adapter;
    private List<EventPost> eventList;
    private List<EventPost> allEventsList; // Store all events for filtering

    private String currentStatusFilter = "all";
    private String currentCategoryFilter = "all";
    
    private boolean isLoading = false;

    public EventManageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        binding = FragmentEventManagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupListeners();
        loadMyEvents();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload events when returning to fragment
        loadMyEvents();
    }

    private void setupRecyclerView() {
        eventList = new ArrayList<>();
        allEventsList = new ArrayList<>();
        adapter = new EventManagerAdapter(getContext(), eventList, this);
        binding.recyclerViewEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewEvents.setAdapter(adapter);
    }

    private void loadMyEvents() {
        if (isLoading) return;
        
        isLoading = true;
        showLoading();
        
        ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
        Call<RestResponse<PageResponse<EventResponse>>> call = apiService.getMyEvents(
                0,    // page
                100,  // size - load all events
                "createdAt",  // sortBy
                "desc"        // sortDirection
        );
        
        call.enqueue(new Callback<RestResponse<PageResponse<EventResponse>>>() {
            @Override
            public void onResponse(Call<RestResponse<PageResponse<EventResponse>>> call, 
                                 Response<RestResponse<PageResponse<EventResponse>>> response) {
                isLoading = false;
                
                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<PageResponse<EventResponse>> restResponse = response.body();
                    PageResponse<EventResponse> pageResponse = restResponse.getData();
                    
                    if (pageResponse == null) {
                        Log.e(TAG, "PageResponse is null in RestResponse");
                        hideLoading();
                        Toast.makeText(getContext(), "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    List<EventResponse> events = pageResponse.getContent();
                    
                    if (events != null && !events.isEmpty()) {
                        allEventsList.clear();
                        for (EventResponse event : events) {
                            allEventsList.add(convertToEventPost(event));
                        }
                        applyFilters();
                        hideLoading();
                        Log.d(TAG, "Loaded " + events.size() + " events");
                    } else {
                        allEventsList.clear();
                        applyFilters();
                        hideLoading();
                        Log.d(TAG, "No events found");
                    }
                } else {
                    hideLoading();
                    Log.e(TAG, "API Error: " + response.code());
                    
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error";
                        Log.e(TAG, "Error body: " + errorBody);
                        Toast.makeText(getContext(), "Lỗi: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Không thể tải sự kiện", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            
            @Override
            public void onFailure(Call<RestResponse<PageResponse<EventResponse>>> call, Throwable t) {
                isLoading = false;
                hideLoading();
                Log.e(TAG, "Network Error: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private EventPost convertToEventPost(EventResponse event) {
        EventPost post = new EventPost();
        post.setId(event.getId());
        post.setTitle(event.getTitle());
        post.setLocation(event.getLocation());
        post.setRewardPoints(event.getRewardPoints() != null ? event.getRewardPoints() : 0);
        
        // Set organization name from creator
        if (event.getCreatorName() != null) {
            post.setOrganizationName(event.getCreatorName());
        }
        
        // Convert status
        String status = "active";
        if ("COMPLETED".equals(event.getStatus())) {
            status = "completed";
        } else if ("CANCELLED".equals(event.getStatus()) || "REJECTED".equals(event.getStatus())) {
            status = "cancelled";
        }
        post.setStatus(status);
        
        // Parse date
        if (event.getEventStartTime() != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = sdf.parse(event.getEventStartTime());
                post.setEventDate(date);
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date: " + e.getMessage());
            }
        }
        
        // Set tags based on event type
        List<String> tags = new ArrayList<>();
        if (event.getEventTypeName() != null) {
            tags.add(event.getEventTypeName());
        }
        post.setTags(tags);
        
        // Set participants
        post.setMaxParticipants(event.getNumOfVolunteers() != null ? event.getNumOfVolunteers() : 0);
        post.setCurrentParticipants(event.getCurrentParticipants() != null ? event.getCurrentParticipants() : 0);
        
        return post;
    }
    
    private void showLoading() {
        if (binding != null) {
            binding.recyclerViewEvents.setVisibility(View.GONE);
        }
    }
    
    private void hideLoading() {
        if (binding != null) {
            binding.recyclerViewEvents.setVisibility(View.VISIBLE);
        }
    }

    private void setupListeners() {
        binding.btnBack.setVisibility(View.GONE);

        // Add New Event Button
        binding.btnAddReward.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddEventActivity.class);
            startActivity(intent);
        });

        // Status Tabs
        binding.tabAll.setOnClickListener(v -> {
            currentStatusFilter = "all";
            updateTabUI(binding.tabAll);
            applyFilters();
        });

        binding.tabActivity.setOnClickListener(v -> {
            currentStatusFilter = "active";
            updateTabUI(binding.tabActivity);
            applyFilters();
        });

        binding.tabComplete.setOnClickListener(v -> {
            currentStatusFilter = "completed";
            updateTabUI(binding.tabComplete);
            applyFilters();
        });

        // Category Chips - Add "All" chip and reset functionality
        binding.chipAll.setOnClickListener(v -> {
            currentCategoryFilter = "all";
            updateChipUI(binding.chipAll);
            applyFilters();
        });

        binding.chipEnvironment.setOnClickListener(v -> {
            currentCategoryFilter = "Môi trường";
            updateChipUI(binding.chipEnvironment);
            applyFilters();
        });

        binding.chipEducation.setOnClickListener(v -> {
            currentCategoryFilter = "Giáo dục";
            updateChipUI(binding.chipEducation);
            applyFilters();
        });

        binding.chipHealth.setOnClickListener(v -> {
            currentCategoryFilter = "Y tế";
            updateChipUI(binding.chipHealth);
            applyFilters();
        });
    }

    // --- Navigation & Actions ---

    @Override
    public void onViewClick(EventPost event) {
        Intent intent = new Intent(getContext(), DetailEventActivity.class);
        intent.putExtra("eventId", event.getId());
        startActivity(intent);
    }

    @Override
    public void onEditClick(EventPost event) {
        Intent intent = new Intent(getContext(), AddEventActivity.class); // Reusing AddEvent for Edit
        intent.putExtra("EVENT_ID", event.getId());
        intent.putExtra("IS_EDIT_MODE", true);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(EventPost event) {
        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa sự kiện \"" + event.getTitle() + "\"?")
            .setPositiveButton("Xóa", (dialog, which) -> deleteEvent(event))
            .setNegativeButton("Hủy", null)
            .show();
    }
    
    private void deleteEvent(EventPost event) {
        if (isLoading) return;
        
        isLoading = true;
        Toast.makeText(getContext(), "Đang xóa sự kiện...", Toast.LENGTH_SHORT).show();
        
        ApiEndpoints apiService = ApiConfig.getClient().create(ApiEndpoints.class);
        Call<Void> call = apiService.deleteEvent(event.getId());
        
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                isLoading = false;
                
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã xóa sự kiện thành công", Toast.LENGTH_SHORT).show();
                    
                    // Remove from lists
                    allEventsList.remove(event);
                    eventList.remove(event);
                    adapter.notifyDataSetChanged();
                    updateStatistics();
                    
                    Log.d(TAG, "Event deleted successfully: " + event.getId());
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? 
                            response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Error deleting event: " + errorBody);
                        Toast.makeText(getContext(), "Không thể xóa sự kiện: " + response.message(), 
                            Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Không thể xóa sự kiện", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isLoading = false;
                Log.e(TAG, "Network error deleting event: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNotificationClick(EventPost event) {
        Intent intent = new Intent(getContext(), com.manhhuy.myapplication.ui.Activities.SendNotificationActivity.class);
        intent.putExtra("EVENT_ID", event.getId());
        intent.putExtra("EVENT_TITLE", event.getTitle());
        startActivity(intent);
    }

    // --- UI Helpers ---

    private void updateTabUI(android.widget.TextView selectedTab) {
        resetTabStyle(binding.tabAll);
        resetTabStyle(binding.tabActivity);
        resetTabStyle(binding.tabComplete);

        selectedTab.setBackgroundResource(R.drawable.bg_category_tab_selected_reward);
        selectedTab.setTextColor(getResources().getColor(R.color.app_green_primary));
    }

    private void resetTabStyle(android.widget.TextView tab) {
        tab.setBackgroundResource(R.drawable.bg_category_tab_unselected_reward);
        tab.setTextColor(getResources().getColor(R.color.text_secondary));
    }

    private void updateChipUI(android.widget.TextView selectedChip) {
        resetChipStyle(binding.chipAll);
        resetChipStyle(binding.chipEnvironment);
        resetChipStyle(binding.chipEducation);
        resetChipStyle(binding.chipHealth);

        selectedChip.setBackgroundResource(R.drawable.bg_chip_selected_event);
        selectedChip.setTextColor(getResources().getColor(R.color.app_green_primary));
    }

    private void resetChipStyle(android.widget.TextView chip) {
        chip.setBackgroundResource(R.drawable.bg_chip_unselected_event);
        chip.setTextColor(getResources().getColor(R.color.text_primary));
    }
    
    private void resetAllChipStyles() {
        resetChipStyle(binding.chipAll);
        resetChipStyle(binding.chipEnvironment);
        resetChipStyle(binding.chipEducation);
        resetChipStyle(binding.chipHealth);
        // Set "All" as selected
        binding.chipAll.setBackgroundResource(R.drawable.bg_chip_selected_event);
        binding.chipAll.setTextColor(getResources().getColor(R.color.app_green_primary));
    }

    // --- Data & Logic ---

    private void applyFilters() {
        eventList.clear();
        
        for (EventPost event : allEventsList) {
            boolean matchesStatus = currentStatusFilter.equals("all") ||
                    event.getStatus().equals(currentStatusFilter);
            
            boolean matchesCategory = currentCategoryFilter.equals("all");
            if (!matchesCategory && event.getTags() != null) {
                // Check if any tag contains the category filter
                for (String tag : event.getTags()) {
                    if (tag != null && tag.contains(currentCategoryFilter)) {
                        matchesCategory = true;
                        break;
                    }
                }
            }

            if (matchesStatus && matchesCategory) {
                eventList.add(event);
            }
        }
        
        adapter.notifyDataSetChanged();
        updateStatistics();
        
        // Show empty state if no results
        if (eventList.isEmpty() && !allEventsList.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy sự kiện phù hợp", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateStatistics() {
        int total = allEventsList.size();
        int active = 0;
        int completed = 0;

        for (EventPost event : allEventsList) {
            if ("active".equals(event.getStatus())) {
                active++;
            } else if ("completed".equals(event.getStatus())) {
                completed++;
            }
        }

        if (binding != null) {
            binding.tvTotalEvents.setText(String.valueOf(total));
            binding.tvActiveEvents.setText(String.valueOf(active));
            binding.tvCompletedEvents.setText(String.valueOf(completed));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
