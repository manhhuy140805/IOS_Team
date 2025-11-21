package com.manhhuy.myapplication.ui.Activities.Fragment.Common;

import android.content.Intent;
import android.os.Bundle;
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
import com.manhhuy.myapplication.databinding.FragmentHomeBinding;
import com.manhhuy.myapplication.model.SearchResult;
import com.manhhuy.myapplication.ui.Activities.DetailEventActivity;
import com.manhhuy.myapplication.ui.Activities.HomeActivity;
import com.manhhuy.myapplication.ui.Activities.UserActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SearchResultAdapter featuredAdapter;

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

        setupFeaturedRecyclerView();
        setupClickListeners();
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
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).switchToSearchTab();
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