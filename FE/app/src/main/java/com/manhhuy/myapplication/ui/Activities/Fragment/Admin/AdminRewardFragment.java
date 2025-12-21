package com.manhhuy.myapplication.ui.Activities.Fragment.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.admin.reward.OnRewardActionListener;
import com.manhhuy.myapplication.adapter.admin.reward.RewardAdminAdapter;
import com.manhhuy.myapplication.databinding.ActivityAdminRewardManagementBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.ApiEndpoints;
import com.manhhuy.myapplication.helper.response.PageResponse;
import com.manhhuy.myapplication.helper.response.RewardResponse;
import com.manhhuy.myapplication.helper.response.RestResponse;
import com.manhhuy.myapplication.model.RewardItem;
import com.manhhuy.myapplication.ui.Activities.Admin.AddRewardActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class AdminRewardFragment extends Fragment implements OnRewardActionListener {

    private static final String TAG = "AdminRewardFragment";
    private ActivityAdminRewardManagementBinding binding;
    private RewardAdminAdapter adapter;
    private ApiEndpoints apiEndpoints;
    private final List<RewardItem> allRewards = new ArrayList<>();
    private final List<RewardItem> displayedRewards = new ArrayList<>();
    private int selectedCategory = 0;
    private boolean isLoading = false;

    private final ActivityResultLauncher<Intent> addRewardLauncher = 
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                    loadRewards(); // Reload list after adding
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        binding = ActivityAdminRewardManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiEndpoints = ApiConfig.getClient().create(ApiEndpoints.class);
        initViews();
        loadRewards();
    }

    private void initViews() {
        binding.btnBack.setVisibility(View.GONE);
        binding.fabAddReward.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddRewardActivity.class);
            addRewardLauncher.launch(intent);
        });
        
        adapter = new RewardAdminAdapter(requireContext(), displayedRewards, this);
        binding.recyclerViewRewards.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewRewards.setAdapter(adapter);
        
        setupCategoryTabs();
    }

    private void setupCategoryTabs() {
        binding.tabAll.setOnClickListener(v -> selectCategory(0, binding.tabAll));
        binding.tabVoucher.setOnClickListener(v -> selectCategory(1, binding.tabVoucher));
        binding.tabGift.setOnClickListener(v -> selectCategory(2, binding.tabGift));
        binding.tabExperience.setOnClickListener(v -> selectCategory(3, binding.tabExperience));
        binding.tabLowStock.setOnClickListener(v -> selectCategory(4, binding.tabLowStock));
    }

    private void loadRewards() {
        if (isLoading) return;
        isLoading = true;
        binding.recyclerViewRewards.setVisibility(View.GONE);

        apiEndpoints.getAllRewards(null, 0, 100, "createdAt", "desc")
                .enqueue(new Callback<RestResponse<PageResponse<RewardResponse>>>() {
                    @Override
                    public void onResponse(Call<RestResponse<PageResponse<RewardResponse>>> call,
                                           Response<RestResponse<PageResponse<RewardResponse>>> response) {
                        isLoading = false;
                        binding.recyclerViewRewards.setVisibility(View.VISIBLE);

                        if (response.isSuccessful() && response.body() != null 
                            && response.body().getStatusCode() == 200 
                            && response.body().getData() != null) {
                            allRewards.clear();
                            for (RewardResponse r : response.body().getData().getContent()) {
                                allRewards.add(mapRewardItem(r));
                            }
                            applyFilter();
                        } else {
                            showToast("Không thể tải dữ liệu");
                        }
                    }

                    @Override
                    public void onFailure(Call<RestResponse<PageResponse<RewardResponse>>> call, Throwable t) {
                        isLoading = false;
                        binding.recyclerViewRewards.setVisibility(View.VISIBLE);
                        Log.e(TAG, "Load rewards failed", t);
                        showToast("Lỗi kết nối");
                    }
                });
    }

    private RewardItem mapRewardItem(RewardResponse r) {
        String type = r.getType() != null ? r.getType().toLowerCase() : "";
        int categoryType = type.contains("voucher") ? 1 
                         : type.contains("gift") || type.contains("vật phẩm") ? 2
                         : type.contains("experience") || type.contains("trải nghiệm") ? 3 : 1;

        int qty = r.getQuantity() != null ? r.getQuantity() : 0;
        String tag = qty == 0 ? "Hết hàng" : qty <= 5 ? "Sắp hết" : qty >= 50 ? "Phổ biến" : "Còn hàng";

        RewardItem item = new RewardItem(
                r.getName(),
                r.getProviderName() != null ? r.getProviderName() : "Unknown",
                r.getType() != null ? r.getType() : "Reward",
                String.valueOf(r.getPointsRequired() != null ? r.getPointsRequired() : 0),
                String.valueOf(qty),
                r.getExpiryDate() != null ? r.getExpiryDate() : "N/A",
                categoryType, r.getType(), tag, categoryType - 1
        );
        item.setId(r.getId());
        item.setStatus(r.getStatus() != null ? r.getStatus() : "ACTIVE");
        item.setImageUrl(r.getImageUrl());
        return item;
    }

    private void selectCategory(int category, TextView selectedTab) {
        TextView[] tabs = {binding.tabAll, binding.tabVoucher, binding.tabGift, 
                          binding.tabExperience, binding.tabLowStock};
        for (TextView tab : tabs) {
            boolean isSelected = tab == selectedTab;
            tab.setBackgroundResource(isSelected ? R.drawable.bg_category_tab_selected_reward 
                                                 : R.drawable.bg_category_tab_unselected_reward);
            tab.setTextColor(getResources().getColor(isSelected ? R.color.app_green_primary 
                                                                : R.color.text_secondary));
        }
        selectedCategory = category;
        applyFilter();
    }

    private void applyFilter() {
        displayedRewards.clear();
        for (RewardItem reward : allRewards) {
            boolean matches = selectedCategory == 0 // All
                    || (selectedCategory == 4 && Integer.parseInt(reward.getStock()) <= 5) // Low stock
                    || reward.getCategoryType() == selectedCategory; // By category
            if (matches) displayedRewards.add(reward);
        }
        adapter.notifyDataSetChanged();
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditClick(RewardItem reward, int position) {
        showToast("Sửa: " + reward.getName());
    }

    @Override
    public void onPauseClick(RewardItem reward, int position) {
        if (reward.getId() == null) return;

        String newStatus = "ACTIVE".equals(reward.getStatus()) ? "INACTIVE" : "ACTIVE";
        apiEndpoints.updateRewardStatus(reward.getId(), newStatus)
                .enqueue(new Callback<RestResponse<RewardResponse>>() {
                    @Override
                    public void onResponse(Call<RestResponse<RewardResponse>> call, 
                                          Response<RestResponse<RewardResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            reward.setStatus(newStatus);
                            adapter.notifyItemChanged(position);
                            showToast("ACTIVE".equals(newStatus) ? "Đã kích hoạt" : "Đã tạm dừng");
                        } else {
                            showToast("Cập nhật thất bại");
                        }
                    }

                    @Override
                    public void onFailure(Call<RestResponse<RewardResponse>> call, Throwable t) {
                        showToast("Lỗi kết nối");
                    }
                });
    }

    @Override
    public void onDeleteClick(RewardItem reward, int position) {
        if (reward.getId() == null) return;
        
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Xóa \"" + reward.getName() + "\"?")
                .setPositiveButton("Xóa", (d, w) -> deleteReward(reward))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteReward(RewardItem reward) {
        apiEndpoints.deleteReward(reward.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    allRewards.remove(reward);
                    applyFilter();
                    showToast("Đã xóa");
                } else {
                    showToast("Xóa thất bại");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showToast("Lỗi kết nối");
            }
        });
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
