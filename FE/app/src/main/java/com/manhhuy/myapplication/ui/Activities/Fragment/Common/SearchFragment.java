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
import com.manhhuy.myapplication.adapter.EventAdapter;
import com.manhhuy.myapplication.databinding.FragmentSearchBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.response.EventResponse;
import com.manhhuy.myapplication.helper.response.PageResponse;
import com.manhhuy.myapplication.helper.response.RestResponse;
import com.manhhuy.myapplication.ui.Activities.DetailEventActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private EventAdapter adapter;
    private List<EventResponse> allResults;
    private ApiEndpoints apiEndpoints;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiEndpoints = ApiConfig.getClient().create(ApiEndpoints.class);
        
        setupRecyclerView();
        setupSearchFunctionality();
        setupFilterButtons();
        showEmptyState();
    }

    private void setupRecyclerView() {
        allResults = new ArrayList<>();
        adapter = new EventAdapter(allResults);

        binding.searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.searchResultsRecyclerView.setAdapter(adapter);

        adapter.setListener(event -> {
            Intent intent = new Intent(getContext(), DetailEventActivity.class);
            intent.putExtra("eventData", event);
            startActivity(intent);
        });
    }

    private void setupSearchFunctionality() {
        // Back button
        binding.backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
        
        // Filter button - trigger search when clicked
        binding.filterButton.setOnClickListener(v -> {
            String query = binding.searchKeyword.getText().toString().trim();
            if (!query.isEmpty()) {
                searchEvents(query);
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupFilterButtons() {
        // Apply filters button click listener
        binding.applyFilterBtn.setOnClickListener(v -> {
            String keyword = binding.searchKeyword.getText().toString().trim();
            if (!keyword.isEmpty()) {
                searchEvents(keyword);
                Toast.makeText(getContext(), "Đã áp dụng bộ lọc", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });

        // Clear filters button
        binding.clearFiltersBtn.setOnClickListener(v -> {
            binding.categoryEnvironment.setChecked(false);
            binding.categoryEducation.setChecked(false);
            binding.categoryHealth.setChecked(false);
            binding.startDate.setText("");
            binding.endDate.setText("");
        });

        // Reset filter button
        binding.resetFilterBtn.setOnClickListener(v -> {
            binding.categoryEnvironment.setChecked(false);
            binding.categoryEducation.setChecked(false);
            binding.categoryHealth.setChecked(false);
            binding.startDate.setText("");
            binding.endDate.setText("");
        });
    }

    private void searchEvents(String query) {
        Call<RestResponse<PageResponse<EventResponse>>> call = apiEndpoints.searchEvents(
                query,
                0,      // page
                100,    // size - lấy nhiều để có đủ kết quả
                "createdAt",
                "desc"
        );

        call.enqueue(new Callback<RestResponse<PageResponse<EventResponse>>>() {
            @Override
            public void onResponse(Call<RestResponse<PageResponse<EventResponse>>> call, 
                                 Response<RestResponse<PageResponse<EventResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<PageResponse<EventResponse>> restResponse = response.body();
                    if (restResponse.getStatusCode() == 200 && restResponse.getData() != null) {
                        List<EventResponse> events = restResponse.getData().getContent();
                        allResults.clear();
                        allResults.addAll(events);
                        updateSearchResults(events);
                        updateResultCount(events.size());
                    } else {
                        showEmptyState();
                        updateResultCount(0);
                    }
                } else {
                    showEmptyState();
                    updateResultCount(0);
                    Toast.makeText(getContext(), "Không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RestResponse<PageResponse<EventResponse>>> call, Throwable t) {
                Log.e("SearchFragment", "Error searching events", t);
                showEmptyState();
                updateResultCount(0);
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSearchResults(List<EventResponse> results) {
        if (results.isEmpty()) {
            showEmptyState();
        } else {
            binding.noResultsContainer.setVisibility(View.GONE);
            binding.searchResultsRecyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    private void showEmptyState() {
        binding.noResultsContainer.setVisibility(View.VISIBLE);
        binding.searchResultsRecyclerView.setVisibility(View.GONE);
    }

    private void updateResultCount(int count) {
        binding.viewMoreFilters.setText(count + " cơ hội được tìm thấy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}