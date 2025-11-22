package com.manhhuy.myapplication.ui.Activities.Fragment.Common;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.SearchResultAdapter;
import com.manhhuy.myapplication.databinding.FragmentSearchBinding;
import com.manhhuy.myapplication.model.SearchResult;
import com.manhhuy.myapplication.ui.Activities.DetailEventActivity;
import com.manhhuy.myapplication.ui.Activities.MainActivity;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private SearchResultAdapter adapter;
    private List<SearchResult> allResults;

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

        setupRecyclerView();
        setupSearchFunctionality();
        setupFilterButtons();
        loadInitialData();
    }

    private void setupRecyclerView() {
        allResults = new ArrayList<>();
        adapter = new SearchResultAdapter(allResults);

        binding.searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.searchResultsRecyclerView.setAdapter(adapter);

        adapter.setListener(result -> {
            Intent intent = new Intent(getContext(), DetailEventActivity.class);
            startActivity(intent);
        });
    }

    private void setupSearchFunctionality() {
        // Setup search keyword input with text change listener
        binding.searchKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupFilterButtons() {
        // Apply filters button click listener
        binding.applyFilterBtn.setOnClickListener(v -> {
            String keyword = binding.searchKeyword.getText().toString();
            applyFilters(keyword);
            Toast.makeText(getContext(), "Đã áp dụng bộ lọc", Toast.LENGTH_SHORT).show();
        });

        // Category checkboxes listeners
        binding.categoryEnvironment.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String keyword = binding.searchKeyword.getText().toString();
            applyFilters(keyword);
        });

        binding.categoryEducation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String keyword = binding.searchKeyword.getText().toString();
            applyFilters(keyword);
        });

        binding.categoryHealth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String keyword = binding.searchKeyword.getText().toString();
            applyFilters(keyword);
        });
    }

    private void loadInitialData() {
        allResults.clear();
        allResults.add(new SearchResult(
                "Dọn rác bãi biển Vũng Tàu",
                "Nhóm Môi trường xanh",
                "Vũng Tàu",
                R.drawable.bg_rounded_orange,
                "Môi trường",
                "Dọn dẹp",
                "Mô tả ngắn: Dọn rác, phân loại rác thải, trồng cây ven biển.",
                "20/11/2025",
                45,
                50,
                "1 ngày"
        ));

        allResults.add(new SearchResult(
                "Dạy học cho trẻ em vùng cao",
                "Hội từ thiện Ánh Sáng",
                "Sapa, Lào Cai",
                R.drawable.bg_rounded_orange,
                "Giáo dục",
                "Dạy học",
                "Mô tả ngắn: Giảng dạy, tặng sách vở, tổ chức hoạt động ngoại khóa.",
                "25/11/2025",
                15,
                20,
                "3 ngày"
        ));

        allResults.add(new SearchResult(
                "Chăm sóc bệnh nhân tại bệnh viện",
                "Câu lạc bộ Tình nguyện Y tế",
                "Bệnh viện Trung ương, Hà Nội",
                R.drawable.bg_rounded_orange,
                "Y tế",
                "Tâm lý",
                "Mô tả ngắn: Thăm hỏi, đọc sách, hỗ trợ thủ tục hành chính cho bệnh nhân.",
                "15/12/2025",
                20,
                30,
                "Tuần"
        ));

        allResults.add(new SearchResult(
                "Trồng cây xanh tại công viên",
                "Nhóm Xanh hóa đô thị",
                "Công viên Cầu Giấy, Hà Nội",
                R.drawable.bg_rounded_orange,
                "Môi trường",
                "Trồng cây",
                "Mô tả ngắn: Trồng cây, chăm sóc cây xanh, làm sạch khu vực công viên.",
                "22/11/2025",
                30,
                40,
                "0.5 ngày"
        ));

        updateSearchResults(allResults);
        updateResultCount(allResults.size());
    }

    private void applyFilters(String keyword) {
        // Filter results based on keyword and category filters
        List<SearchResult> filteredResults = new ArrayList<>();

        // Get selected categories
        List<String> selectedCategories = new ArrayList<>();
        if (binding.categoryEnvironment.isChecked()) {
            selectedCategories.add("Môi trường");
        }
        if (binding.categoryEducation.isChecked()) {
            selectedCategories.add("Giáo dục");
        }
        if (binding.categoryHealth.isChecked()) {
            selectedCategories.add("Y tế");
        }

        // Filter by keyword and category
        for (SearchResult result : allResults) {
            boolean matchKeyword = keyword.isEmpty() ||
                    result.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                    result.getDescription().toLowerCase().contains(keyword.toLowerCase()) ||
                    result.getOrganization().toLowerCase().contains(keyword.toLowerCase());

            boolean matchCategory = selectedCategories.isEmpty() ||
                    selectedCategories.contains(result.getCategory());

            if (matchKeyword && matchCategory) {
                filteredResults.add(result);
            }
        }

        updateSearchResults(filteredResults);
        updateResultCount(filteredResults.size());
    }

    private void updateSearchResults(List<SearchResult> results) {
        if (results.isEmpty()) {
            binding.noResultsMessage.setVisibility(View.VISIBLE);
            binding.searchResultsRecyclerView.setVisibility(View.GONE);
        } else {
            binding.noResultsMessage.setVisibility(View.GONE);
            binding.searchResultsRecyclerView.setVisibility(View.VISIBLE);
            adapter.setResults(results);
        }
    }

    private void updateResultCount(int count) {
        // Use string resource with placeholder to support localization and avoid lint warnings
        binding.viewMoreFilters.setText(getString(R.string.opportunities_found, count));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}