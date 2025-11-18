package com.manhhuy.myapplication.ui.Activitys.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manhhuy.myapplication.R;

/**
 * GIẢI THÍCH: SearchFragment - Trang tìm kiếm sự kiện tình nguyện
 * Fragment này cho phép người dùng search và filter các sự kiện
 * Hiện tại đang để placeholder, bạn có thể thêm SearchView và RecyclerView sau
 */
public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // GIẢI THÍCH: Inflate layout fragment_search.xml
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // GIẢI THÍCH: Setup search functionality, filters, và RecyclerView ở đây

        // TODO: Setup SearchView
        // TODO: Setup Filter buttons
        // TODO: Setup RecyclerView cho search results
        // TODO: Load initial data
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up resources
    }
}
                "Y tế",
                "Tâm lý",
                "Mô tả ngắn: Thăm hỏi, độc sách, hỗ trợ thử tục hành chính cho bệnh nhân.",
                "15/11/2025",
                20,
                30,
                "Tuần"
        ));

        if (results.isEmpty()) {
            binding.noResultsMessage.setVisibility(View.VISIBLE);
            binding.searchResultsRecyclerView.setVisibility(View.GONE);
        } else {
            binding.noResultsMessage.setVisibility(View.GONE);
            binding.searchResultsRecyclerView.setVisibility(View.VISIBLE);
            adapter.setResults(results);
        }
    }

    private void applyFilters(String keyword) {
        // Filter results based on keyword and other filters
        String category = "";
        if (binding.categoryEnvironment.isChecked()) {
            category = "Môi trường";
        } else if (binding.categoryEducation.isChecked()) {
            category = "Giáo dục";
        } else if (binding.categoryHealth.isChecked()) {
            category = "Y tế";
        }

        String startDate = binding.startDate.getText().toString();
        String endDate = binding.endDate.getText().toString();

        // Apply filters and reload results
        loadSearchResults();
    }

}