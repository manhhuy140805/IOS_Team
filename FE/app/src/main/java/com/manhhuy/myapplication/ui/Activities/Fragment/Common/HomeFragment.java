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
import com.manhhuy.myapplication.adapter.SearchResultAdapter;
import com.manhhuy.myapplication.databinding.FragmentHomeBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.response.EventTypeResponse;
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
        setupClickListeners();
    }

    private void setupCategoriesRecyclerView() {
        categoryAdapter = new CategoryAdapter(new ArrayList<>());
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), 
                LinearLayoutManager.HORIZONTAL, false);
        binding.categoriesRecyclerView.setLayoutManager(layoutManager);
        binding.categoriesRecyclerView.setAdapter(categoryAdapter);
        
        categoryAdapter.setListener(category -> {
            Toast.makeText(getContext(), 
                    "Đã chọn: " + category.getName(), 
                    Toast.LENGTH_SHORT).show();
            // TODO: Navigate to filtered events list
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
                        List<EventTypeResponse> eventTypes = restResponse.getData();
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
        List<SearchResult> featuredEvents = new ArrayList<>();
        // Add some dummy data for featured events
        featuredEvents.add(new SearchResult(
                "Chiến dịch Mùa Hè Xanh 2025",
                "Đoàn Thanh niên TP.HCM",
                "TP. Hồ Chí Minh",
                R.drawable.banner_event_default,
                "Cộng đồng",
                "Tình nguyện",
                "Tham gia các hoạt động tình nguyện hè...",
                "30/06/2025",
                120,
                200,
                "1 tháng"
        ));
        
        featuredEvents.add(new SearchResult(
                "Hiến máu nhân đạo - Giọt hồng",
                "Hội Chữ Thập Đỏ",
                "Nhà Văn hóa Thanh niên",
                R.drawable.banner_event_default,
                "Y tế",
                "Hiến máu",
                "Ngày hội hiến máu tình nguyện...",
                "15/12/2024",
                85,
                100,
                "1 ngày"
        ));

        featuredAdapter = new SearchResultAdapter(featuredEvents);
        binding.eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.eventsRecyclerView.setAdapter(featuredAdapter);
        
        featuredAdapter.setListener(result -> {
            Intent intent = new Intent(getContext(), DetailEventActivity.class);
            startActivity(intent);
        });
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

        binding.viewAllCategories.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Xem tất cả danh mục", Toast.LENGTH_SHORT).show()
        );

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