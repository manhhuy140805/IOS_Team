package com.manhhuy.myapplication.ui.Activities.Fragment.Common;

import android.content.Intent;
import android.os.Bundle;
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
import com.manhhuy.myapplication.ui.Activities.AddEventActivity;
import com.manhhuy.myapplication.ui.Activities.DetailEventActivity;
import com.manhhuy.myapplication.ui.Activities.SendNotificationActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Quản lý events: Admin (xem/xóa all), Organizer (thêm/sửa/xóa own)
 */
public class EventManageFragment extends Fragment implements OnEventActionListenerInterface {
    
    // UI
    private FragmentEventManagerBinding binding;
    private EventManagerAdapter adapter;
    
    // Data
    private final List<EventResponse> eventList = new ArrayList<>();
    private final List<EventResponse> allEventsList = new ArrayList<>();
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

    @Override
    public void onResume() {
        super.onResume();
        // Reload data when returning to this fragment (e.g. from Approve Posts tab)
        loadEvents();
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
        
        // Chỉ Organizer có nút Add
        binding.btnAddReward.setOnClickListener(v -> startActivity(new Intent(getContext(), AddEventActivity.class)));
        checkUserRoleAndShowAddButton();
    }
    
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

    
    // ========== Event Actions ==========
    
    @Override
    public void onViewClick(EventResponse event) {
        Intent intent = new Intent(getContext(), DetailEventActivity.class);
        intent.putExtra("eventData", event);
        startActivity(intent);
    }

    @Override
    public void onEditClick(EventResponse event) {
        Intent intent = new Intent(getContext(), AddEventActivity.class);
        intent.putExtra("EVENT_ID", event.getId());
        intent.putExtra("IS_EDIT_MODE", true);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(EventResponse event) {
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
                        showToast("Lỗi kết nối");
                    }
                });
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    @Override
    public void onNotificationClick(EventResponse event) {
        Intent intent = new Intent(getContext(), SendNotificationActivity.class);
        intent.putExtra("EVENT_ID", event.getId());
        intent.putExtra("EVENT_TITLE", event.getTitle());
        startActivity(intent);
    }
    
    // ========== Load Data ==========
    
    private void loadEventTypes() {
        apiEndpoints.getEventTypes().enqueue(new Callback<RestResponse<List<EventTypeResponse>>>() {
            @Override
            public void onResponse(Call<RestResponse<List<EventTypeResponse>>> call, Response<RestResponse<List<EventTypeResponse>>> response) {
                if (isResponseValid(response)) {
                    eventTypes.clear();
                    eventTypes.addAll(response.body().getData());
                    createCategoryChips();
                }
            }

            @Override
            public void onFailure(Call<RestResponse<List<EventTypeResponse>>> call, Throwable t) {
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

        Call<RestResponse<PageResponse<EventResponse>>> call;

        if (ApiConfig.isAdmin()) {
            // Admin: Chỉ load events ACTIVE
            call = apiEndpoints.getAllEvents(page, PAGE_SIZE, "createdAt", "DESC", null, null, "ACTIVE", null, null, null, null, null);
        } else if (ApiConfig.isOrganizer()) {
            // Organizer: Load own events
            call = apiEndpoints.getMyEvents(page, PAGE_SIZE, "createdAt", "DESC");
        } else {
            isLoading = false;
            showLoading(false);
            showToast("Bạn không có quyền truy cập");
            return;
        }

        call.enqueue(new Callback<RestResponse<PageResponse<EventResponse>>>() {
            @Override
            public void onResponse(Call<RestResponse<PageResponse<EventResponse>>> call, Response<RestResponse<PageResponse<EventResponse>>> response) {
                isLoading = false;
                showLoading(false);

                if (isResponseValid(response)) {
                    PageResponse<EventResponse> pageData = response.body().getData();

                    if (page == 0) {
                        allEventsList.clear();
                    }

                    allEventsList.addAll(pageData.getContent());

                    hasMorePages = !pageData.isLast();
                    applyFilters();
                    updateStatistics();
                } else {
                    showToast("Đã xảy ra lỗi khi tải sự kiện");
                }
            }

            @Override
            public void onFailure(Call<RestResponse<PageResponse<EventResponse>>> call, Throwable t) {
                isLoading = false;
                showLoading(false);
                showToast("Lỗi kết nối");
            }
        });
    }
    
    // ========== Category Chips ==========
    
    private void createCategoryChips() {
        if (getContext() == null) return;
        
        binding.categoryChipsLayout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        
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
    
    // ========== Filter & Statistics ==========

    private void applyFilters() {
        List<EventResponse> filteredList = new ArrayList<>();
        
        for (EventResponse event : allEventsList) {
            if (currentCategoryFilter.equals("all") || 
                (event.getCategory() != null && event.getCategory().equals(currentCategoryFilter))) {
                filteredList.add(event);
            }
        }
        
        adapter.updateList(filteredList);
        eventList.clear();
        eventList.addAll(filteredList);
        updateStatistics();
        
        if (eventList.isEmpty() && !allEventsList.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy sự kiện phù hợp", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateStatistics() {
        int total = allEventsList.size();
        int active = 0;
        int completed = 0;

        for (EventResponse event : allEventsList) {
            String status = event.getStatus();
            if ("active".equals(status)) active++;
            else if ("completed".equals(status)) completed++;
        }

        binding.tvTotalEvents.setText(String.valueOf(total));
        binding.tvActiveEvents.setText(String.valueOf(active));
        binding.tvCompletedEvents.setText(String.valueOf(completed));
    }
    
    // ========== Helpers ==========
    
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
