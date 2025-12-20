package com.manhhuy.myapplication.ui.Activities.Fragment.User;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.RewardAdapter;
import com.manhhuy.myapplication.databinding.FragmentRedeemBinding;

import com.manhhuy.myapplication.helper.ApiService;
import com.manhhuy.myapplication.helper.response.PageResponse;
import com.manhhuy.myapplication.helper.response.RestResponse;
import com.manhhuy.myapplication.helper.response.RewardResponse;
import com.manhhuy.myapplication.helper.response.RewardTypeResponse;
import com.manhhuy.myapplication.model.RewardItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RedeemFragment extends Fragment {

    private static final String TAG = "RedeemFragment";
    private FragmentRedeemBinding binding;
    private RewardAdapter rewardAdapter;

    private int currentUserPoints = 1250;
    private int selectedCategory = 0;
    private List<RewardTypeResponse> rewardTypes = new ArrayList<>();
    private List<TextView> categoryTabs = new ArrayList<>();

    public RedeemFragment() {
        // Required empty public constructor
    }

    public static RedeemFragment newInstance() {
        return new RedeemFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentRedeemBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // RecyclerView cho danh sách rewards
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        binding.rvRewards.setLayoutManager(layoutManager);

        // Khởi tạo adapter với list rỗng
        List<RewardItem> rewardList = new ArrayList<>();
        rewardAdapter = new RewardAdapter(requireContext(), rewardList, currentUserPoints);
        binding.rvRewards.setAdapter(rewardAdapter);

        binding.tvTotalPoints.setText(String.format("%,d", currentUserPoints));

        // Load reward types từ API
        loadRewardTypes();
        
        // Load rewards khi khởi tạo (tab "Tất cả")
        loadAllRewards();
    }

    private void loadAllRewards() {
        loadRewards(null);
    }

    private void loadRewards(Integer rewardTypeId) {
        ApiService.api().getAllRewards(rewardTypeId, 0, 100, "createdAt", "desc")
                .enqueue(new Callback<RestResponse<PageResponse<RewardResponse>>>() {
                    @Override
                    public void onResponse(Call<RestResponse<PageResponse<RewardResponse>>> call,
                            Response<RestResponse<PageResponse<RewardResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            RestResponse<PageResponse<RewardResponse>> restResponse = response.body();
                            if (restResponse.getData() != null && restResponse.getData().getContent() != null) {
                                List<RewardResponse> rewards = restResponse.getData().getContent();
                                Log.d(TAG, "Loaded " + rewards.size() + " rewards");
                                
                                // Để Adapter tự convert
                                rewardAdapter.setRewardsFromApi(rewards);
                                
                                // Ẩn/hiện empty state
                                if (rewards.isEmpty()) {
                                    binding.rvRewards.setVisibility(View.GONE);
                                    binding.emptyState.setVisibility(View.VISIBLE);
                                } else {
                                    binding.rvRewards.setVisibility(View.VISIBLE);
                                    binding.emptyState.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            Log.e(TAG, "Failed to load rewards: " + response.code());
                            Toast.makeText(requireContext(), "Không thể tải phần thưởng", Toast.LENGTH_SHORT).show();
                            showEmptyState();
                        }
                    }

                    @Override
                    public void onFailure(Call<RestResponse<PageResponse<RewardResponse>>> call, Throwable t) {
                        Log.e(TAG, "Error loading rewards", t);
                        Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                        showEmptyState();
                    }
                });
    }

    private void showEmptyState() {
        binding.rvRewards.setVisibility(View.GONE);
        binding.emptyState.setVisibility(View.VISIBLE);
    }

    private void loadRewardTypes() {
        ApiService.api().getRewardTypes().enqueue(new Callback<RestResponse<List<RewardTypeResponse>>>() {
            @Override
            public void onResponse(Call<RestResponse<List<RewardTypeResponse>>> call,
                    Response<RestResponse<List<RewardTypeResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RestResponse<List<RewardTypeResponse>> restResponse = response.body();
                    if (restResponse.getData() != null) {
                        rewardTypes = restResponse.getData();
                        Log.d(TAG, "Loaded " + rewardTypes.size() + " reward types");
                        
                        // Tạo tabs động từ dữ liệu API
                        createDynamicTabs();
                    }
                } else {
                    Log.e(TAG, "Failed to load reward types: " + response.code());
                    Toast.makeText(requireContext(), "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
                    // Fallback: tạo tabs mặc định
                    createDefaultTabs();
                }
            }

            @Override
            public void onFailure(Call<RestResponse<List<RewardTypeResponse>>> call, Throwable t) {
                Log.e(TAG, "Error loading reward types", t);
                Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                // Fallback: tạo tabs mặc định
                createDefaultTabs();
            }
        });
    }

    private void createDynamicTabs() {
        // Clear các tab cũ
        binding.categoryContainer.removeAllViews();
        categoryTabs.clear();

        // Tạo tab "Tất cả" đầu tiên
        TextView tabAll = createTabView("Tất cả", 0, true);
        binding.categoryContainer.addView(tabAll);
        categoryTabs.add(tabAll);

        // Tạo tabs từ API data
        for (int i = 0; i < rewardTypes.size(); i++) {
            RewardTypeResponse type = rewardTypes.get(i);
            final int index = i + 1; // +1 vì "Tất cả" là 0
            
            TextView tab = createTabView(type.getTitle(), index, false);
            binding.categoryContainer.addView(tab);
            categoryTabs.add(tab);
        }
    }

    private TextView createTabView(String title, int categoryIndex, boolean isSelected) {
        TextView tab = new TextView(requireContext());
        
        // Set layout params với margin
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        if (categoryIndex > 0) {
            params.setMarginStart(dpToPx(12));
        }
        tab.setLayoutParams(params);
        
        // Set các thuộc tính
        tab.setText(title);
        tab.setTextSize(14);
        tab.setGravity(Gravity.CENTER);
        tab.setPadding(dpToPx(20), dpToPx(10), dpToPx(20), dpToPx(10));
        tab.setClickable(true);
        tab.setFocusable(true);
        
        // Set style dựa trên trạng thái selected
        if (isSelected) {
            tab.setBackgroundResource(R.drawable.bg_category_tab_selected_reward);
            tab.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
            tab.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            tab.setBackgroundResource(R.drawable.bg_category_tab);
            tab.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
            tab.setTypeface(null, android.graphics.Typeface.BOLD);
        }
        
        // Set click listener
        tab.setOnClickListener(v -> {
            selectCategory(categoryIndex, tab);
            updateSectionTitle(categoryIndex);
            
            // Call API với rewardTypeId tương ứng
            if (categoryIndex == 0) {
                // Tab "Tất cả" - load tất cả rewards
                loadRewards(null);
            } else {
                // Tab category - load rewards theo rewardTypeId
                RewardTypeResponse selectedType = rewardTypes.get(categoryIndex - 1);
                loadRewards(selectedType.getId());
            }
        });
        
        return tab;
    }

    private int dpToPx(int dp) {
        float density = requireContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void createDefaultTabs() {
        // Tạo tabs mặc định nếu API fail
        binding.categoryContainer.removeAllViews();
        categoryTabs.clear();

        String[] defaultTabs = {"Tất cả", "Voucher", "Quà tặng", "Cơ hội"};
        for (int i = 0; i < defaultTabs.length; i++) {
            TextView tab = createTabView(defaultTabs[i], i, i == 0);
            binding.categoryContainer.addView(tab);
            categoryTabs.add(tab);
        }
    }

    private void selectCategory(int category, TextView selectedTab) {
        selectedCategory = category;

        // Reset tất cả tabs về trạng thái chưa chọn
        for (TextView tab : categoryTabs) {
            tab.setBackgroundResource(R.drawable.bg_category_tab);
            tab.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
            tab.setTypeface(null, android.graphics.Typeface.BOLD);
        }

        // Highlight tab được chọn
        selectedTab.setBackgroundResource(R.drawable.bg_category_tab_selected_reward);
        selectedTab.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
        selectedTab.setTypeface(null, android.graphics.Typeface.BOLD);
    }

    private void updateSectionTitle(int category) {
        if (category == 0) {
            // Tab "Tất cả"
            binding.tvSectionTitle.setText("Phổ biến nhất");
            binding.ivSectionIcon.setImageResource(R.drawable.ic_gift);
        } else if (category - 1 < rewardTypes.size()) {
            // Tabs từ API (category - 1 vì index 0 là "Tất cả")
            RewardTypeResponse type = rewardTypes.get(category - 1);
            binding.tvSectionTitle.setText(type.getTitle());
            
            // Set icon dựa trên tên loại phần thưởng
            String title = type.getTitle().toLowerCase();
            if (title.contains("voucher")) {
                binding.ivSectionIcon.setImageResource(R.drawable.ic_voucher);
            } else if (title.contains("quà") || title.contains("gift")) {
                binding.ivSectionIcon.setImageResource(R.drawable.ic_gift);
            } else if (title.contains("cơ hội") || title.contains("opportunity")) {
                binding.ivSectionIcon.setImageResource(R.drawable.ic_discount);
            } else {
                binding.ivSectionIcon.setImageResource(R.drawable.ic_gift);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}