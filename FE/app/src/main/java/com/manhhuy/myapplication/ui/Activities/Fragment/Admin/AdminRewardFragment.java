package com.manhhuy.myapplication.ui.Activities.Fragment.Admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

    private final List<RewardItem> rewardList = new ArrayList<>();
    private final List<RewardItem> filteredList = new ArrayList<>();

    private int selectedCategory = 0; // 0=all, 1=voucher, 2=gift, 3=experience, 4=low stock
    private boolean isLoading = false;

    public AdminRewardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        binding = ActivityAdminRewardManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiEndpoints = ApiConfig.getClient().create(ApiEndpoints.class);
        setupViews();
        setupRecyclerView();
        loadRewardsFromAPI();
        setupListeners();
    }

    private void setupViews() {
        binding.btnBack.setVisibility(View.GONE);
        binding.fabAddReward.setOnClickListener(v -> showToast("Thêm quà mới - Coming soon"));
    }

    private void setupRecyclerView() {
        adapter = new RewardAdminAdapter(getContext(), filteredList, this);
        binding.recyclerViewRewards.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewRewards.setAdapter(adapter);
    }

    private void loadRewardsFromAPI() {
        if (isLoading) return;
        isLoading = true;
        showLoading(true);

        apiEndpoints.getAllRewards(null, 0, 100, "createdAt", "desc")
                .enqueue(new Callback<RestResponse<PageResponse<RewardResponse>>>() {
                    @Override
                    public void onResponse(Call<RestResponse<PageResponse<RewardResponse>>> call,
                                           Response<RestResponse<PageResponse<RewardResponse>>> response) {
                        isLoading = false;
                        showLoading(false);

                        if (response.isSuccessful() && response.body() != null) {
                            RestResponse<PageResponse<RewardResponse>> restResponse = response.body();
                            if (restResponse.getStatusCode() == 200 && restResponse.getData() != null) {
                                rewardList.clear();
                                for (RewardResponse reward : restResponse.getData().getContent()) {
                                    rewardList.add(mapToRewardItem(reward));
                                }
                                filterRewards(selectedCategory);
                            } else {
                                showToast("Không thể tải danh sách phần thưởng: " + restResponse.getMessage());
                            }
                        } else {
                            showToast("Lỗi tải dữ liệu: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<RestResponse<PageResponse<RewardResponse>>> call, Throwable t) {
                        isLoading = false;
                        showLoading(false);
                        Log.e(TAG, "Failed to load rewards", t);
                        showToast("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    private RewardItem mapToRewardItem(RewardResponse r) {
        String type = r.getType() != null ? r.getType() : "";
        int categoryType = type.toLowerCase().contains("voucher") ? 1 
                         : type.toLowerCase().contains("gift") || type.contains("Vật phẩm") ? 2
                         : type.toLowerCase().contains("experience") || type.contains("Trải nghiệm") ? 3 : 1;

        int quantity = r.getQuantity() != null ? r.getQuantity() : 0;
        String tag = quantity == 0 ? "Hết hàng" : quantity <= 5 ? "Sắp hết" : quantity >= 50 ? "Phổ biến" : "Còn hàng";

        RewardItem item = new RewardItem(
                r.getName(),
                r.getProviderName() != null ? r.getProviderName() : "Unknown",
                r.getType() != null ? r.getType() : "Reward",
                String.valueOf(r.getPointsRequired() != null ? r.getPointsRequired() : 0),
                String.valueOf(quantity),
                r.getExpiryDate() != null ? r.getExpiryDate() : "N/A",
                categoryType,
                r.getType(),
                tag,
                categoryType - 1
        );
        item.setId(r.getId());
        item.setStatus(r.getStatus() != null ? r.getStatus() : "ACTIVE");
        return item;
    }

    private void showLoading(boolean show) {
        if (binding != null) {
            binding.recyclerViewRewards.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        binding.tabAll.setOnClickListener(v -> selectCategory(0, binding.tabAll));
        binding.tabVoucher.setOnClickListener(v -> selectCategory(1, binding.tabVoucher));
        binding.tabGift.setOnClickListener(v -> selectCategory(2, binding.tabGift));
        binding.tabExperience.setOnClickListener(v -> selectCategory(3, binding.tabExperience));
        binding.tabLowStock.setOnClickListener(v -> selectCategory(4, binding.tabLowStock));
    }

    private void selectCategory(int category, TextView selectedTab) {
        TextView[] tabs = {binding.tabAll, binding.tabVoucher, binding.tabGift, binding.tabExperience, binding.tabLowStock};
        for (TextView tab : tabs) {
            tab.setBackgroundResource(R.drawable.bg_category_tab_unselected_reward);
            tab.setTextColor(getResources().getColor(R.color.text_secondary));
        }
        
        selectedTab.setBackgroundResource(R.drawable.bg_category_tab_selected_reward);
        selectedTab.setTextColor(getResources().getColor(R.color.app_green_primary));
        
        selectedCategory = category;
        filterRewards(category);
    }

    private void filterRewards(int category) {
        filteredList.clear();
        
        if (category == 0) {
            filteredList.addAll(rewardList);
        } else if (category == 4) {
            for (RewardItem reward : rewardList) {
                if (Integer.parseInt(reward.getStock()) <= 5) filteredList.add(reward);
            }
        } else {
            for (RewardItem reward : rewardList) {
                if (reward.getCategoryType() == category) filteredList.add(reward);
            }
        }
        
        adapter.updateList(filteredList);
    }

    // Implement OnRewardActionListener interface
    @Override
    public void onEditClick(RewardItem reward, int position) {
        showToast("Sửa: " + reward.getName());
    }

    @Override
    public void onPauseClick(RewardItem reward, int position) {
        if (reward.getId() == null) {
            showToast("Không tìm thấy ID phần thưởng");
            return;
        }

        // Toggle status between ACTIVE and INACTIVE
        String currentStatus = reward.getStatus() != null ? reward.getStatus() : "ACTIVE";
        String newStatus = "ACTIVE".equals(currentStatus) ? "INACTIVE" : "ACTIVE";
        
        apiEndpoints.updateRewardStatus(reward.getId(), newStatus).enqueue(new Callback<RestResponse<RewardResponse>>() {
            @Override
            public void onResponse(Call<RestResponse<RewardResponse>> call, Response<RestResponse<RewardResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    reward.setStatus(newStatus);
                    adapter.notifyItemChanged(position);
                    showToast("Đã chuyển sang " + ("ACTIVE".equals(newStatus) ? "đang hoạt động" : "tạm dừng"));
                } else {
                    showToast("Không thể cập nhật: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RestResponse<RewardResponse>> call, Throwable t) {
                Log.e(TAG, "Failed to update reward status", t);
                showToast("Lỗi kết nối");
            }
        });
    }

    @Override
    public void onDeleteClick(RewardItem reward, int position) {
        if (reward.getId() == null) {
            showToast("Không tìm thấy ID phần thưởng");
            return;
        }
        
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa phần thưởng \"" + reward.getName() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    apiEndpoints.deleteReward(reward.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                rewardList.remove(reward);
                                filterRewards(selectedCategory);
                                showToast("Đã xóa phần thưởng");
                            } else {
                                showToast("Không thể xóa: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e(TAG, "Failed to delete reward", t);
                            showToast("Lỗi kết nối");
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
