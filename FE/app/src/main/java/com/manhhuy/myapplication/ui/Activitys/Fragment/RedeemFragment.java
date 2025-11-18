package com.manhhuy.myapplication.ui.Activitys.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.RewardAdapter;
import com.manhhuy.myapplication.databinding.FragmentRedeemBinding;

import com.manhhuy.myapplication.model.RewardItem;

import java.util.ArrayList;
import java.util.List;

public class RedeemFragment extends Fragment {

    private FragmentRedeemBinding binding;
    private RecyclerView rvRewards;
    private RewardAdapter rewardAdapter;
    private TextView tvTotalPoints;
    private TextView tvViewHistory;
    private TextView tabAll, tabVoucher, tabGift, tabOpportunity;
    private ImageView ivSectionIcon;
    private TextView tvSectionTitle;
    private LinearLayout emptyState;

    private int currentUserPoints = 1250;
    private int selectedCategory = 0; // 0=all, 1=voucher, 2=gift, 3=opportunity

    public RedeemFragment() {
        // Required empty public constructor
    }

    public static RedeemFragment newInstance() {
        return new RedeemFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // GIẢI THÍCH: Inflate layout fragment_redeem.xml
        return inflater.inflate(R.layout.fragment_redeem, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // GIẢI THÍCH: Khởi tạo các view
        initViews(view);

        // RecyclerView cho danh sách rewards
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvRewards.setLayoutManager(layoutManager);

        // GIẢI THÍCH: Khởi tạo adapter với list rỗng
        List<RewardItem> rewardList = new ArrayList<>();
        rewardAdapter = new RewardAdapter(getContext(), rewardList, currentUserPoints);
        rvRewards.setAdapter(rewardAdapter);
        // hết
        tvTotalPoints.setText(String.format("%,d", currentUserPoints));

        // GIẢI THÍCH: Setup category tabs
        setupCategoryTabs();

        // GIẢI THÍCH: Load dữ liệu rewards mẫu
        loadSampleRewards();
    }

    private void initViews(View view) {
        rvRewards = view.findViewById(R.id.rvRewards);
        tvTotalPoints = view.findViewById(R.id.tvTotalPoints);
        tvViewHistory = view.findViewById(R.id.tvViewHistory);
        tabAll = view.findViewById(R.id.tabAll);
        tabVoucher = view.findViewById(R.id.tabVoucher);
        tabGift = view.findViewById(R.id.tabGift);
        tabOpportunity = view.findViewById(R.id.tabOpportunity);
        ivSectionIcon = view.findViewById(R.id.ivSectionIcon);
        tvSectionTitle = view.findViewById(R.id.tvSectionTitle);
        emptyState = view.findViewById(R.id.emptyState);
    }

    private void setupCategoryTabs() {

        tabAll.setOnClickListener(v -> {
            selectCategory(0, tabAll);
            updateSectionTitle(0);
            rewardAdapter.filterByCategory(0);
        });

        tabVoucher.setOnClickListener(v -> {
            selectCategory(1, tabVoucher);
            updateSectionTitle(1);
            rewardAdapter.filterByCategory(1);
        });

        tabGift.setOnClickListener(v -> {
            selectCategory(2, tabGift);
            updateSectionTitle(2);
            rewardAdapter.filterByCategory(2);
        });

        tabOpportunity.setOnClickListener(v -> {
            selectCategory(3, tabOpportunity);
            updateSectionTitle(3);
            rewardAdapter.filterByCategory(3);
        });
    }

    private void selectCategory(int category, TextView selectedTab) {
        selectedCategory = category;

        // Reset tất cả tabs về trạng thái chưa chọn
        resetTab(tabAll);
        resetTab(tabVoucher);
        resetTab(tabGift);
        resetTab(tabOpportunity);

        // Highlight tab được chọn
        selectedTab.setBackgroundResource(R.drawable.bg_category_tab_selected);
        selectedTab.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

        // Update drawable tint cho selected tab
        if (selectedTab.getCompoundDrawables()[0] != null) {
            selectedTab.getCompoundDrawables()[0].setTint(
                    ContextCompat.getColor(requireContext(), android.R.color.white));
        }
    }

    private void resetTab(TextView tab) {
        tab.setBackgroundResource(R.drawable.bg_category_tab);

        // Set màu text và icon theo từng tab
        if (tab == tabAll) {
            tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_primary));
        } else if (tab == tabVoucher) {
            tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.cyan));
            if (tab.getCompoundDrawables()[0] != null) {
                tab.getCompoundDrawables()[0].setTint(
                        ContextCompat.getColor(requireContext(), R.color.cyan));
            }
        } else if (tab == tabGift) {
            tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.pink));
            if (tab.getCompoundDrawables()[0] != null) {
                tab.getCompoundDrawables()[0].setTint(
                        ContextCompat.getColor(requireContext(), R.color.pink));
            }
        } else if (tab == tabOpportunity) {
            tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange));
            if (tab.getCompoundDrawables()[0] != null) {
                tab.getCompoundDrawables()[0].setTint(
                        ContextCompat.getColor(requireContext(), R.color.orange));
            }
        }
    }

    private void updateSectionTitle(int category) {
        switch (category) {
            case 0:
                tvSectionTitle.setText("Phổ biến nhất");
                ivSectionIcon.setImageResource(R.drawable.ic_gift);
                break;
            case 1:
                tvSectionTitle.setText("Voucher");
                ivSectionIcon.setImageResource(R.drawable.ic_voucher);
                break;
            case 2:
                tvSectionTitle.setText("Quà tặng");
                ivSectionIcon.setImageResource(R.drawable.ic_gift);
                break;
            case 3:
                tvSectionTitle.setText("Cơ hội");
                ivSectionIcon.setImageResource(R.drawable.ic_discount);
                break;
        }
    }

    private void loadSampleRewards() {
        // GIẢI THÍCH: Tạo dữ liệu mẫu cho rewards
        List<RewardItem> rewards = new ArrayList<>();

        // Voucher items
        rewards.add(new RewardItem(
                "Voucher Grab 50k",
                "Grab Vietnam",
                "Giảm 50k cho chuyến đi tiếp theo",
                "500",
                "Còn 23",
                "31/12/2025",
                1, // voucher
                "Giao thông",
                "Hot",
                3 // cyan
        ));

        rewards.add(new RewardItem(
                "Voucher Shopee 100k",
                "Shopee",
                "Giảm 100k cho đơn hàng từ 300k",
                "800",
                "Còn 15",
                "25/12/2025",
                1, // voucher
                "Mua sắm",
                "Mới",
                0 // purple
        ));

        // Gift items
        rewards.add(new RewardItem(
                "Balo sinh viên",
                "VolunConnect",
                "Balo thời trang dành cho sinh viên",
                "1200",
                "Còn 8",
                "",
                2, // gift
                "Thời trang",
                "",
                1 // pink
        ));

        rewards.add(new RewardItem(
                "Áo phông TNV",
                "VolunConnect",
                "Áo phông dành cho tình nguyện viên",
                "600",
                "Còn 45",
                "",
                2, // gift
                "Thời trang",
                "Hot",
                2 // orange
        ));

        // Opportunity items
        rewards.add(new RewardItem(
                "Khóa học Excel",
                "FUNiX",
                "Khóa học Excel cơ bản miễn phí",
                "1000",
                "Còn 30",
                "15/01/2026",
                3, // opportunity
                "Học tập",
                "Mới",
                0 // purple
        ));

        rewards.add(new RewardItem(
                "Workshop Soft Skills",
                "VolunConnect",
                "Workshop kỹ năng mềm cho TNV",
                "400",
                "Còn 50",
                "20/12/2025",
                3, // opportunity
                "Kỹ năng",
                "",
                3 // cyan
        ));

        rewards.add(new RewardItem(
                "Voucher Highlands 30k",
                "Highlands Coffee",
                "Giảm 30k cho hóa đơn từ 100k",
                "300",
                "Còn 100",
                "31/12/2025",
                1, // voucher
                "Đồ uống",
                "Hot",
                1 // pink
        ));

        rewards.add(new RewardItem(
                "Sách kỹ năng sống",
                "Nhà xuất bản Trẻ",
                "Bộ sách phát triển bản thân",
                "900",
                "Còn 12",
                "",
                2, // gift
                "Sách",
                "Mới",
                2 // orange
        ));

        // GIẢI THÍCH: Cập nhật adapter với dữ liệu mới
        rewardAdapter.setCategories(rewards);

        // GIẢI THÍCH: Ẩn/hiện empty state
        if (rewards.isEmpty()) {
            rvRewards.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvRewards.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}