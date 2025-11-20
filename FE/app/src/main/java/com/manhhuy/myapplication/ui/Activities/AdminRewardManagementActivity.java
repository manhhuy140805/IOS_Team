package com.manhhuy.myapplication.ui.Activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.admin.reward.OnRewardActionListener;
import com.manhhuy.myapplication.adapter.admin.reward.RewardAdminAdapter;
import com.manhhuy.myapplication.databinding.ActivityAdminRewardManagementBinding;
import com.manhhuy.myapplication.model.RewardItem;

import java.util.ArrayList;
import java.util.List;

public class AdminRewardManagementActivity extends AppCompatActivity implements OnRewardActionListener {

    private ActivityAdminRewardManagementBinding binding;
    private RewardAdminAdapter adapter;

    private final List<RewardItem> rewardList = new ArrayList<>();
    private final List<RewardItem> filteredList = new ArrayList<>();

    private int selectedCategory = 0; // 0=all, 1=voucher, 2=gift, 3=experience, 4=low stock

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminRewardManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();
        setupRecyclerView();
        loadMockData();
        setupListeners();
    }

    private void setupViews() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.fabAddReward.setOnClickListener(v -> {
            Toast.makeText(this, "Thêm quà mới", Toast.LENGTH_SHORT).show();
            // TODO: Open add reward dialog/activity
        });
    }

    private void setupRecyclerView() {
        adapter = new RewardAdminAdapter(this, filteredList, this);
        binding.recyclerViewRewards.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewRewards.setAdapter(adapter);
    }

    private void loadMockData() {
        rewardList.clear();

        // Mock data based on Figma design
        rewardList.add(new RewardItem(
                "Voucher The Coffee House 50K",
                "The Coffee House",
                "Voucher • Đồ uống",
                "500",
                "45",
                "31/12/2025",
                1, // voucher
                "Đồ uống",
                "Phổ biến",
                0 // purple
        ));

        rewardList.add(new RewardItem(
                "Áo Volunteer Limited Edition",
                "Volunteer Impact",
                "Vật phẩm • Thời trang",
                "2000",
                "12",
                "31/12/2025",
                2, // gift
                "Thời trang",
                "Giới hạn",
                1 // pink
        ));

        rewardList.add(new RewardItem(
                "Vé Workshop Kỹ Năng Mềm",
                "Skill Academy",
                "Trải nghiệm • Đào tạo",
                "1500",
                "3",
                "15/12/2025",
                3, // experience
                "Đào tạo",
                "Sắp hết",
                2 // orange
        ));

        rewardList.add(new RewardItem(
                "Sách Phát Triển Bản Thân",
                "Nhà xuất bản Trẻ",
                "Vật phẩm • Sách",
                "800",
                "28",
                "31/12/2025",
                2, // gift
                "Sách",
                "Tri thức",
                3 // cyan
        ));

        rewardList.add(new RewardItem(
                "Voucher Shopee 100K",
                "Shopee",
                "Voucher • Mua sắm",
                "1000",
                "0",
                "31/01/2026",
                1, // voucher
                "Mua sắm",
                "Hết hàng",
                0 // purple
        ));

        rewardList.add(new RewardItem(
                "Balo Du Lịch Cao Cấp",
                "Travel Gear",
                "Vật phẩm • Phụ kiện",
                "3500",
                "20",
                "31/12/2025",
                2, // gift
                "Phụ kiện",
                "Cao cấp",
                1 // pink
        ));

        filterRewards(selectedCategory);
    }

    private void setupListeners() {
        binding.tabAll.setOnClickListener(v -> selectCategory(0, binding.tabAll));
        binding.tabVoucher.setOnClickListener(v -> selectCategory(1, binding.tabVoucher));
        binding.tabGift.setOnClickListener(v -> selectCategory(2, binding.tabGift));
        binding.tabExperience.setOnClickListener(v -> selectCategory(3, binding.tabExperience));
        binding.tabLowStock.setOnClickListener(v -> selectCategory(4, binding.tabLowStock));
    }

    private void selectCategory(int category, TextView selectedTab) {
        // Reset tất cả tabs
        resetTabStyle(binding.tabAll);
        resetTabStyle(binding.tabVoucher);
        resetTabStyle(binding.tabGift);
        resetTabStyle(binding.tabExperience);
        resetTabStyle(binding.tabLowStock);

        // Set selected tab style
        setSelectedTabStyle(selectedTab);

        selectedCategory = category;
        filterRewards(category);
    }

    private void resetTabStyle(TextView tab) {
        tab.setBackgroundResource(R.drawable.bg_category_tab_unselected_reward);
        tab.setTextColor(getResources().getColor(R.color.text_secondary));
    }

    private void setSelectedTabStyle(TextView tab) {
        tab.setBackgroundResource(R.drawable.bg_category_tab_selected_reward);
        tab.setTextColor(getResources().getColor(R.color.app_green_primary));
    }

    private void filterRewards(int category) {
        filteredList.clear();

        if (category == 0) {
            // All
            filteredList.addAll(rewardList);
        } else if (category == 4) {
            // Low stock (<=5)
            for (RewardItem reward : rewardList) {
                if (Integer.parseInt(reward.getStock()) <= 5) {
                    filteredList.add(reward);
                }
            }
        } else {
            // Filter by category type
            for (RewardItem reward : rewardList) {
                if (reward.getCategoryType() == category) {
                    filteredList.add(reward);
                }
            }
        }

        adapter.updateList(filteredList);
    }

    // Implement OnRewardActionListener interface
    @Override
    public void onEditClick(RewardItem reward, int position) {
        Toast.makeText(this, "Sửa: " + reward.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Open edit dialog
    }

    @Override
    public void onPauseClick(RewardItem reward, int position) {
        Toast.makeText(this, "Tạm ngưng: " + reward.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Toggle pause status
    }

    @Override
    public void onDeleteClick(RewardItem reward, int position) {
        Toast.makeText(this, "Xóa: " + reward.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Show confirmation dialog and delete
    }
}
