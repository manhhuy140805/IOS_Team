package com.manhhuy.myapplication.ui.Activities.Fragment.Organization;

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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment quản lý sự kiện cho Organization
 * Hiển thị các events mà organization đã tạo
 */
public class OrganizationEventFragment extends Fragment implements OnEventActionListenerInterface {

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
        // loadData(); // Removed to avoid double loading with onResume
    }

    @Override
    public void onResume() {
        super.onResume();
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

        // Organization có thể thêm event
        binding.btnAddReward.setVisibility(View.VISIBLE);
        binding.btnAddReward.setOnClickListener(v -> startActivity(new Intent(getContext(), AddEventActivity.class)));
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
                .setMessage("Bạn có chắc muốn xóa sự kiện \"" + event.getTitle()
                        + "\"?\n\nLưu ý: Tất cả đăng ký liên quan sẽ bị xóa!")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    apiEndpoints.deleteEvent(event.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                // Remove by ID
                                for (int i = 0; i < allEventsList.size(); i++) {
                                    if (allEventsList.get(i).getId().equals(event.getId())) {
                                        allEventsList.remove(i);
                                        break;
                                    }
                                }
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
            public void onResponse(Call<RestResponse<List<EventTypeResponse>>> call,
                    Response<RestResponse<List<EventTypeResponse>>> response) {
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

        android.util.Log.d("OrgEventFragment", "Calling getOrganizationEvents API, page=" + page);

        // Call API to get organization events - KHÔNG check role ở FE, để BE xử lý
        apiEndpoints.getOrganizationEvents(page, PAGE_SIZE, "createdAt", "DESC")
                .enqueue(new Callback<RestResponse<PageResponse<EventResponse>>>() {
                    @Override
                    public void onResponse(Call<RestResponse<PageResponse<EventResponse>>> call,
                            Response<RestResponse<PageResponse<EventResponse>>> response) {
                        isLoading = false;
                        showLoading(false);

                        android.util.Log.d("OrgEventFragment", "API Response code: " + response.code());

                        if (response.isSuccessful() && response.body() != null) {
                            android.util.Log.d("OrgEventFragment", "Response body: " + response.body());

                            if (response.body().getData() != null) {
                                PageResponse<EventResponse> pageData = response.body().getData();
                                android.util.Log.d("OrgEventFragment", "Events count: " + pageData.getContent().size());

                                allEventsList.addAll(pageData.getContent());
                                hasMorePages = !pageData.isLast();

                                applyFilters();
                                updateStatistics();
                            } else {
                                android.util.Log.e("OrgEventFragment", "Response data is null");
                                showToast("Dữ liệu trống");
                            }
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string()
                                        : "Unknown error";
                                android.util.Log.e("OrgEventFragment", "Error: " + errorBody);
                                showToast("Lỗi: " + response.code() + " - " + errorBody);
                            } catch (Exception e) {
                                showToast("Đã xảy ra lỗi khi tải sự kiện");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RestResponse<PageResponse<EventResponse>>> call, Throwable t) {
                        isLoading = false;
                        showLoading(false);
                        android.util.Log.e("OrgEventFragment", "API Failure: " + t.getMessage(), t);
                        showToast("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    // ========== Category Chips ==========

    private void createCategoryChips() {
        if (getContext() == null)
            return;

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
            if ("ACTIVE".equalsIgnoreCase(status))
                active++;
            else if ("COMPLETED".equalsIgnoreCase(status))
                completed++;
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
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
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
