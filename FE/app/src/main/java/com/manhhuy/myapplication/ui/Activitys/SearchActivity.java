package com.manhhuy.myapplication.ui.Activitys;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.FilterCategoryAdapter;
import com.manhhuy.myapplication.adapter.SearchResultAdapter;
import com.manhhuy.myapplication.databinding.ActivitySearchBinding;
import com.manhhuy.myapplication.model.FilterCategory;
import com.manhhuy.myapplication.model.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private SearchResultAdapter adapter;
    private FilterCategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupCategoryFiltersRecyclerView();
        setupRecyclerView();
        setupFilters();
        loadSearchResults();
    }

    private void setupCategoryFiltersRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.categoryFiltersRecyclerView.setLayoutManager(layoutManager);
        
        List<FilterCategory> filterCategories = new ArrayList<>();
        filterCategories.add(new FilterCategory("Environment", false));
        filterCategories.add(new FilterCategory("Education", false));
        filterCategories.add(new FilterCategory("Healthcare", false));
        filterCategories.add(new FilterCategory("Animal Care", false));
        filterCategories.add(new FilterCategory("Technology", false));
        
        categoryAdapter = new FilterCategoryAdapter(filterCategories);
        categoryAdapter.setListener((category, isSelected) -> {
            // Handle category selection
        });
        binding.categoryFiltersRecyclerView.setAdapter(categoryAdapter);
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.searchResultsRecyclerView.setLayoutManager(layoutManager);
        
        adapter = new SearchResultAdapter(new ArrayList<>());
        binding.searchResultsRecyclerView.setAdapter(adapter);

        adapter.setListener(result -> {
            // Handle result click - navigate to detail activity
        });
    }

    private void setupFilters() {
        // Filter button - toggle filter panel visibility
        binding.filterButton.setOnClickListener(v -> {
            if (binding.filterPanel.getVisibility() == View.GONE) {
                binding.filterPanel.setVisibility(View.VISIBLE);
            } else {
                binding.filterPanel.setVisibility(View.GONE);
            }
        });
        
        // Apply filter button
        binding.applyFilterBtn.setOnClickListener(v -> {
            String keyword = binding.searchKeyword.getText().toString();
            applyFilters(keyword);
            // Optionally hide filter panel after applying
            binding.filterPanel.setVisibility(View.GONE);
        });
        
        // Clear filters button
        binding.clearFiltersBtn.setOnClickListener(v -> {
            clearAllFilters();
        });
        
        // Reset filter button
        binding.resetFilterBtn.setOnClickListener(v -> {
            clearAllFilters();
        });
    }

    private void clearAllFilters() {
        binding.searchKeyword.setText("");
        binding.startDate.setText("");
        binding.endDate.setText("");
        
        // Clear category selections
        if (categoryAdapter != null) {
            List<FilterCategory> categories = new ArrayList<>();
            categories.add(new FilterCategory("Environment", false));
            categories.add(new FilterCategory("Education", false));
            categories.add(new FilterCategory("Healthcare", false));
            categories.add(new FilterCategory("Animal Care", false));
            categories.add(new FilterCategory("Technology", false));
            categoryAdapter.setCategories(categories);
        }
        
        loadSearchResults();
    }

    private void loadSearchResults() {
        List<SearchResult> results = new ArrayList<>();
        
        results.add(new SearchResult(
                "Dọn sạch bờ biển",
                "Vung Tàu, Green Vietnam",
                "Vung Tau",
                R.drawable.ic_launcher_background,
                "Môi trường",
                "Ngoài trời",
                "Mô tả ngắn: Thu gom rác nhưa, phân loại và bàn giao cho đơn vị chế.",
                "30/10/2025",
                12,
                0,
                "1 ngày"
        ));

        // Item with image URL
        results.add(new SearchResult(
                "Dạy tiếng Anh cho trẻ em",
                "Education For All, Quận 1, HCM",
                "Ho Chi Minh City",
                "https://images2.thanhnien.vn/528068263637045248/2023/4/13/base64-16813649927941566404443.png",
                "Giáo dục",
                "Trẻ em",
                "Mô tả ngắn: Hỗ trợ hỏi tiếng Anh cấp độ cơ bản cho trẻ 6-10 tuổi.",
                "07/11/2025",
                6,
                44,
                "Linh hoạt"
        ));

        results.add(new SearchResult(
                "Hỗ trợ bệnh nhân tại bệnh viện",
                "Care & Share, Cần Thơ",
                "Can Tho",
                R.drawable.ic_launcher_background,
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
        String startDate = binding.startDate.getText().toString();
        String endDate = binding.endDate.getText().toString();

        // Apply filters and reload results
        loadSearchResults();
    }
}
