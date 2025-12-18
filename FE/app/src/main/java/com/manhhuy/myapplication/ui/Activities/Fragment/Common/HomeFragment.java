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
import com.manhhuy.myapplication.helper.response.EventResponse;
import com.manhhuy.myapplication.helper.response.EventTypeResponse;
import com.manhhuy.myapplication.helper.response.PageResponse;
import com.manhhuy.myapplication.helper.response.RestResponse;
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

        setupCategoriesRecyclerView();
        loadEventTypes();
        setupFeaturedRecyclerView();
        loadFeaturedEvents();
        setupClickListeners();
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
                        Log.d(TAG, "Loaded " + eventTypes.size() + " event types");
                    } else {
                        Log.e(TAG, "API returned error: " + restResponse.getMessage());
                        Toast.makeText(getContext(), 
                                "Không thể tải danh mục: " + restResponse.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Response not successful: " + response.code());
                    Toast.makeText(getContext(), 
                            "Không thể tải danh mục", 
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RestResponse<List<EventTypeResponse>>> call, Throwable t) {
                Log.e(TAG, "Failed to load event types", t);
                Toast.makeText(getContext(), 
                        "Lỗi kết nối: " + t.getMessage(), 
                        Toast.LENGTH_SHORT).show();
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
                null, null, "ACTIVE", null, null, null, null, null
        );
        
        call.enqueue(new Callback<RestResponse<PageResponse<EventResponse>>>() {
            @Override
            public void onResponse(Call<RestResponse<PageResponse<EventResponse>>> call, 
                                 Response<RestResponse<PageResponse<EventResponse>>> response) {
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
                        
                        Log.d(TAG, "Loaded " + events.size() + " events");
                    } else {
                        Log.e(TAG, "API returned error: " + restResponse.getMessage());
                        showEmptyState("Không thể tải sự kiện");
                    }
                } else {
                    Log.e(TAG, "Response not successful: " + response.code());
                    showEmptyState("Không thể tải sự kiện");
                }
            }

            @Override
            public void onFailure(Call<RestResponse<PageResponse<EventResponse>>> call, Throwable t) {
                Log.e(TAG, "Failed to load events", t);
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
                        
                        Log.d(TAG, "Loaded " + events.size() + " events for type: " + typeName);
                    } else {
                        Log.e(TAG, "API returned error: " + restResponse.getMessage());
                        showEmptyState("Không thể tải sự kiện " + typeName);
                    }
                } else {
                    Log.e(TAG, "Response not successful: " + response.code());
                    showEmptyState("Không thể tải sự kiện " + typeName);
                }
            }

            @Override
            public void onFailure(Call<RestResponse<PageResponse<EventResponse>>> call, Throwable t) {
                Log.e(TAG, "Failed to load events by type", t);
                showEmptyState("Lỗi kết nối");
            }
        });
    }
    
    private void showEmptyState(String message) {
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