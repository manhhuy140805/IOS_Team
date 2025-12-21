package com.manhhuy.myapplication.ui.Activities.Fragment.Common;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.admin.event.EventManagerAdapter;
import com.manhhuy.myapplication.adapter.admin.event.OnEventActionListenerInterface;
import com.manhhuy.myapplication.databinding.FragmentEventManagerBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.response.EventResponse;
import com.manhhuy.myapplication.helper.response.EventTypeResponse;
import com.manhhuy.myapplication.helper.response.PageResponse;
import com.manhhuy.myapplication.helper.response.RestResponse;
import com.manhhuy.myapplication.model.EventPost;
import com.manhhuy.myapplication.ui.Activities.AddEventActivity;
import com.manhhuy.myapplication.ui.Activities.DetailEventActivity;
import com.manhhuy.myapplication.ui.Activities.SendNotificationActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment quản lý sự kiện cho Admin
 */
public class EventManageFragment extends Fragment implements OnEventActionListenerInterface {

    private static final String TAG = "EventManageFragment";
    
    // UI
    private FragmentEventManagerBinding binding;
    private EventManagerAdapter adapter;
    
    // Data
    private final List<EventPost> eventList = new ArrayList<>();
    private final List<EventPost> allEventsList = new ArrayList<>();
    private final List<EventTypeResponse> eventTypes = new ArrayList<>();
    
    // API
    private ApiEndpoints apiEndpoints;
    
    // Filter state
    private String currentCategoryFilter = "all";
    private TextView selectedCategoryChip;
    
    // Pagination
    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean hasMorePages = true;
    private static final int PAGE_SIZE = 20;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEventManagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        apiEndpoints = ApiConfig.getClient().create(ApiEndpoints.class);
        
        setupUI();
        loadData();
    }
    
    private void setupUI() {
        binding.btnBack.setVisibility(View.GONE);
        
        // RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new EventManagerAdapter(getContext(), eventList, this);
        binding.recyclerViewEvents.setLayoutManager(layoutManager);
        binding.recyclerViewEvents.setAdapter(adapter);
        
        // Scroll listener for pagination
        binding.recyclerViewEvents.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                if (!isLoading && hasMorePages && dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5) {
                        loadMoreEvents();
                    }
                }
            }
        });
        
        // Add event button - chỉ hiện cho ORGANIZER
        binding.btnAddReward.setOnClickListener(v -> startActivity(new Intent(getContext(), AddEventActivity.class)));
        checkUserRoleAndShowAddButton();
    }
    
    /**
     * Kiểm tra role của user và chỉ hiện nút thêm event nếu user là ORGANIZER
     */
    private void checkUserRoleAndShowAddButton() {
        if (ApiConfig.isOrganizer()) {
            binding.btnAddReward.setVisibility(View.VISIBLE);
        } else {
            binding.btnAddReward.setVisibility(View.GONE);
        }
    }
    
    private void loadData() {
        loadEventTypes();
        loadEvents();
    }

    
    // ==================== Event Actions ====================
    
    @Override
    public void onViewClick(EventPost event) {
        Intent intent = new Intent(getContext(), DetailEventActivity.class);
        intent.putExtra("eventPost", event);
        startActivity(intent);
    }

    @Override
    public void onEditClick(EventPost event) {
        Intent intent = new Intent(getContext(), AddEventActivity.class);
        intent.putExtra("EVENT_ID", event.getId());
        intent.putExtra("IS_EDIT_MODE", true);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(EventPost event) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc muốn xóa sự kiện \"" + event.getTitle() + "\"?\n\nLưu ý: Tất cả đăng ký liên quan sẽ bị xóa!")
            .setPositiveButton("Xóa", (dialog, which) -> {
                apiEndpoints.deleteEvent(event.getId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            allEventsList.remove(event);
                            applyFilters();
                            updateStatistics();
                            showToast("Đã xóa sự kiện và tất cả đăng ký liên quan");
                        } else {
                            showToast("Không thể xóa sự kiện");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "Failed to delete event", t);
                        showToast("Lỗi kết nối");
                    }
                });
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    @Override
    public void onNotificationClick(EventPost event) {
        Intent intent = new Intent(getContext(), SendNotificationActivity.class);
        intent.putExtra("EVENT_ID", event.getId());
        intent.putExtra("EVENT_TITLE", event.getTitle());
        startActivity(intent);
    }
    
    // ==================== Load Data from API ====================
    
    private void loadEventTypes() {
        apiEndpoints.getEventTypes().enqueue(new Callback<RestResponse<List<EventTypeResponse>>>() {
            @Override
            public void onResponse(Call<RestResponse<List<EventTypeResponse>>> call, Response<RestResponse<List<EventTypeResponse>>> response) {
                if (isResponseValid(response)) {
                    eventTypes.clear();
                    eventTypes.addAll(response.body().getData());
                    createCategoryChips();
                    Log.d(TAG, "Loaded " + eventTypes.size() + " event types");
                }
            }

            @Override
            public void onFailure(Call<RestResponse<List<EventTypeResponse>>> call, Throwable t) {
                Log.e(TAG, "Failed to load event types", t);
            }
        });
    }
    
    private void loadEvents() {
        currentPage = 0;
        hasMorePages = true;
        allEventsList.clear();
        loadEventsPage(currentPage);
    }
    
    private void loadMoreEvents() {
        if (!isLoading && hasMorePages) {
            currentPage++;
            loadEventsPage(currentPage);
        }
    }
    
    private void loadEventsPage(int page) {
        isLoading = true;
        showLoading(page == 0);
        
        apiEndpoints.getAllEvents(page, PAGE_SIZE, "createdAt", "DESC", null, null, null, null, null, null, null, null)
            .enqueue(new Callback<RestResponse<PageResponse<EventResponse>>>() {
                @Override
                public void onResponse(Call<RestResponse<PageResponse<EventResponse>>> call, Response<RestResponse<PageResponse<EventResponse>>> response) {
                    isLoading = false;
                    showLoading(false);
                    
                    if (isResponseValid(response)) {
                        PageResponse<EventResponse> pageData = response.body().getData();
                        
                        for (EventResponse er : pageData.getContent()) {
                            allEventsList.add(mapToEventPost(er));
                        }
                        
                        hasMorePages = !pageData.isLast();
                        applyFilters();
                        updateStatistics();
                        Log.d(TAG, "Loaded page " + page + ", total: " + allEventsList.size() + "/" + pageData.getTotalElements());
                    } else {
                        showToast("Đã xảy ra lỗi khi tải sự kiện");
                    }
                }

                @Override
                public void onFailure(Call<RestResponse<PageResponse<EventResponse>>> call, Throwable t) {
                    isLoading = false;
                    showLoading(false);
                    Log.e(TAG, "Failed to load events", t);
                    showToast("Lỗi kết nối");
                }
            });
    }
    
    // ==================== Category Chips ====================
    
    private void createCategoryChips() {
        if (getContext() == null) return;
        
        binding.categoryChipsLayout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        
        // "Tất cả" chip
        addChip(inflater, "Tất cả", "all", true);
        
        // Event type chips
        for (EventTypeResponse type : eventTypes) {
            addChip(inflater, type.getName(), type.getName(), false);
        }
    }
    
    private void addChip(LayoutInflater inflater, String text, String filterValue, boolean selected) {
        TextView chip = (TextView) inflater.inflate(R.layout.item_category_chip, binding.categoryChipsLayout, false);
        
        chip.setText(text);
        updateChipStyle(chip, selected);
        
        chip.setOnClickListener(v -> {
            if (selectedCategoryChip != null) {
                updateChipStyle(selectedCategoryChip, false);
            }
            updateChipStyle(chip, true);
            selectedCategoryChip = chip;
            currentCategoryFilter = filterValue;
            applyFilters();
        });
        
        binding.categoryChipsLayout.addView(chip);
        
        if (selected) {
            selectedCategoryChip = chip;
        }
    }
    
    private void updateChipStyle(TextView chip, boolean selected) {
        chip.setTextColor(getResources().getColor(selected ? R.color.app_green_primary : R.color.text_secondary));
        chip.setBackgroundResource(selected ? R.drawable.bg_chip_selected_event : R.drawable.bg_chip_unselected_event);
    }
    
    // ==================== Filter & Statistics ====================

    private void applyFilters() {
        List<EventPost> filteredList = new ArrayList<>();
        
        for (EventPost event : allEventsList) {
            if (currentCategoryFilter.equals("all") || 
                (event.getTags() != null && event.getTags().contains(currentCategoryFilter))) {
                filteredList.add(event);
            }
        }
        
        adapter.updateList(filteredList);
        eventList.clear();
        eventList.addAll(filteredList);
        updateStatistics();
    }

    private void updateStatistics() {
        int total = allEventsList.size();
        int active = 0;
        int completed = 0;

        for (EventPost event : allEventsList) {
            String status = event.getStatus();
            if ("active".equals(status)) active++;
            else if ("completed".equals(status)) completed++;
        }

        binding.tvTotalEvents.setText(String.valueOf(total));
        binding.tvActiveEvents.setText(String.valueOf(active));
        binding.tvCompletedEvents.setText(String.valueOf(completed));
    }
    
    // ==================== Mapping & Helpers ====================
    
    private EventPost mapToEventPost(EventResponse response) {
        EventPost event = new EventPost();
        
        event.setId(response.getId());
        event.setTitle(response.getTitle());
        event.setDescription(response.getDescription());
        event.setImageUrl(response.getImageUrl());
        event.setLocation(response.getLocation());
        event.setRewardPoints(response.getRewardPoints() != null ? response.getRewardPoints() : 0);
        event.setOrganizationName(response.getCreatorName() != null ? response.getCreatorName() : "Unknown");
        event.setCurrentParticipants(response.getCurrentParticipants() != null ? response.getCurrentParticipants() : 0);
        event.setMaxParticipants(response.getNumOfVolunteers() != null ? response.getNumOfVolunteers() : 0);
        
        // Status mapping
        event.setStatus(mapStatus(response.getStatus()));
        
        // Tags
        List<String> tags = new ArrayList<>();
        if (response.getCategory() != null) tags.add(response.getCategory());
        if (response.getEventTypeName() != null) tags.add(response.getEventTypeName());
        event.setTags(tags);
        
        // Date
        event.setEventDate(parseDate(response.getEventStartTime()));
        
        return event;
    }
    
    private String mapStatus(String backendStatus) {
        if ("APPROVED".equalsIgnoreCase(backendStatus)) return "active";
        if ("COMPLETED".equalsIgnoreCase(backendStatus)) return "completed";
        return "pending";
    }
    
    private Date parseDate(String dateString) {
        if (dateString == null) return new Date();
        
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing date: " + dateString, e);
            return new Date();
        }
    }
    
    private <T> boolean isResponseValid(Response<RestResponse<T>> response) {
        return response.isSuccessful() && 
               response.body() != null && 
               response.body().getData() != null;
    }
    
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    
    private void showLoading(boolean show) {
        if (binding != null) {
            binding.recyclerViewEvents.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
