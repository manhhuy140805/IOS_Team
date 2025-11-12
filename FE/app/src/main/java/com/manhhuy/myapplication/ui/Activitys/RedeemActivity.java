package com.manhhuy.myapplication.ui.Activitys;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.adapter.RewardAdapter;
import com.manhhuy.myapplication.model.RewardItem;

import java.util.ArrayList;
import java.util.List;

public class RedeemActivity extends AppCompatActivity {

    // UI Components
    private TextView tvTotalPoints, tvViewHistory;
    private TextView tabAll, tabVoucher, tabGift, tabOpportunity;
    private ImageView ivSectionIcon;
    private TextView tvSectionTitle;
    private RecyclerView rvRewards;
    private LinearLayout emptyState;

    // Data
    private List<RewardItem> rewardList;
    private RewardAdapter rewardAdapter;
    private int currentCategory = 0; // 0=all, 1=voucher, 2=gift, 3=opportunity
    private int userPoints = 1250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);

        initViews();
        loadMockData();
        setupRecyclerView();
        setupCategoryTabs();
        setupListeners();
    }

    private void initViews() {
        tvTotalPoints = findViewById(R.id.tvTotalPoints);
        tvViewHistory = findViewById(R.id.tvViewHistory);
        tabAll = findViewById(R.id.tabAll);
        tabVoucher = findViewById(R.id.tabVoucher);
        tabGift = findViewById(R.id.tabGift);
        tabOpportunity = findViewById(R.id.tabOpportunity);
        ivSectionIcon = findViewById(R.id.ivSectionIcon);
        tvSectionTitle = findViewById(R.id.tvSectionTitle);
        rvRewards = findViewById(R.id.rvRewards);
        emptyState = findViewById(R.id.emptyState);

        // Set user points
        tvTotalPoints.setText(formatPoints(userPoints));
    }

    private void loadMockData() {
        rewardList = new ArrayList<>();
        Resources res = getResources();

        String[] names = res.getStringArray(R.array.reward_names);
        String[] organizations = res.getStringArray(R.array.reward_organizations);
        String[] descriptions = res.getStringArray(R.array.reward_descriptions);
        String[] points = res.getStringArray(R.array.reward_points);
        String[] stock = res.getStringArray(R.array.reward_stock);
        String[] expiry = res.getStringArray(R.array.reward_expiry);
        int[] categoryTypes = res.getIntArray(R.array.reward_category_types);
        String[] tag1 = res.getStringArray(R.array.reward_tag1);
        String[] tag2 = res.getStringArray(R.array.reward_tag2);
        int[] iconColors = res.getIntArray(R.array.reward_icon_colors);

        for (int i = 0; i < names.length; i++) {
            RewardItem item = new RewardItem(
                    names[i],
                    organizations[i],
                    descriptions[i],
                    points[i],
                    stock[i],
                    expiry[i],
                    categoryTypes[i],
                    tag1[i],
                    tag2[i],
                    iconColors[i]);
            rewardList.add(item);
        }
    }

    private void setupRecyclerView() {
        rewardAdapter = new RewardAdapter(this, rewardList, userPoints);
        rvRewards.setLayoutManager(new LinearLayoutManager(this));
        rvRewards.setAdapter(rewardAdapter);

        updateEmptyState();
    }

    private void setupCategoryTabs() {
        selectTab(tabAll, 0);
    }

    private void setupListeners() {
        // View history
        tvViewHistory.setOnClickListener(v -> {
            Toast.makeText(this, "Xem lịch sử đổi thưởng", Toast.LENGTH_SHORT).show();
            // TODO: Open history activity
        });

        // Category tabs
        tabAll.setOnClickListener(v -> selectTab(tabAll, 0));
        tabVoucher.setOnClickListener(v -> selectTab(tabVoucher, 1));
        tabGift.setOnClickListener(v -> selectTab(tabGift, 2));
        tabOpportunity.setOnClickListener(v -> selectTab(tabOpportunity, 3));
    }

    private void selectTab(TextView selectedTab, int categoryType) {
        currentCategory = categoryType;

        // Reset all tabs
        resetTab(tabAll);
        resetTab(tabVoucher);
        resetTab(tabGift);
        resetTab(tabOpportunity);

        // Highlight selected tab
        selectedTab.setBackgroundResource(R.drawable.bg_category_tab_selected);
        selectedTab.setTextColor(ContextCompat.getColor(this, android.R.color.white));

        // Update section title and icon
        switch (categoryType) {
            case 0: // All
                tvSectionTitle.setText("Phổ biến nhất");
                ivSectionIcon.setImageResource(R.drawable.ic_gift);
                break;
            case 1: // Voucher
                tvSectionTitle.setText("Voucher & Giảm giá");
                ivSectionIcon.setImageResource(R.drawable.ic_voucher);
                break;
            case 2: // Gift
                tvSectionTitle.setText("Quà tặng");
                ivSectionIcon.setImageResource(R.drawable.ic_gift);
                break;
            case 3: // Opportunity
                tvSectionTitle.setText("Phần thưởng đặc biệt");
                ivSectionIcon.setImageResource(R.drawable.ic_discount);
                break;
        }

        // Filter rewards
        rewardAdapter.filterByCategory(categoryType);
        updateEmptyState();
    }

    private void resetTab(TextView tab) {
        tab.setBackgroundResource(R.drawable.bg_category_tab);

        // Set color based on tab
        if (tab == tabVoucher) {
            tab.setTextColor(ContextCompat.getColor(this, R.color.cyan));
        } else if (tab == tabGift) {
            tab.setTextColor(ContextCompat.getColor(this, R.color.pink));
        } else if (tab == tabOpportunity) {
            tab.setTextColor(ContextCompat.getColor(this, R.color.orange));
        } else {
            tab.setTextColor(ContextCompat.getColor(this, R.color.cyan));
        }
    }

    private void updateEmptyState() {
        if (rewardAdapter.getItemCount() == 0) {
            rvRewards.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvRewards.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    private String formatPoints(int points) {
        return String.format("%,d", points).replace(",", ".");
    }
}