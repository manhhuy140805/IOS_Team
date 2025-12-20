package com.manhhuy.myapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ItemRewardBinding;
import com.manhhuy.myapplication.helper.response.RewardResponse;
import com.manhhuy.myapplication.model.RewardItem;

import java.util.ArrayList;
import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.RewardViewHolder> {

    private static final String TAG = "RewardAdapter";
    private Context context;
    private List<RewardItem> rewardList;
    private List<RewardItem> rewardListFull;
    private int userPoints;

    public RewardAdapter(Context context, List<RewardItem> rewardList, int userPoints) {
        this.context = context;
        this.rewardList = rewardList;
        this.rewardListFull = new ArrayList<>(rewardList);
        this.userPoints = userPoints;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRewardBinding binding = ItemRewardBinding.inflate(LayoutInflater.from(context), parent, false);
        return new RewardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        RewardItem item = rewardList.get(position);

        // Set data
        holder.binding.tvRewardName.setText(item.getName());
        holder.binding.tvOrganization.setText(item.getOrganization());
        holder.binding.tvDescription.setText(item.getDescription());
        holder.binding.tvRewardPoints.setText(item.getPoints());
        holder.binding.tvStock.setText(item.getStock());
        holder.binding.tvExpiry.setText(item.getExpiry());

        // Set tags
        holder.binding.tvTag1.setText(item.getTag1());
        if (item.getTag2() != null && !item.getTag2().isEmpty()) {
            holder.binding.tvTag2.setVisibility(View.VISIBLE);
            holder.binding.tvTag2.setText(item.getTag2());
        } else {
            holder.binding.tvTag2.setVisibility(View.GONE);
        }

        // Load image từ URL bằng Glide
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.ic_gift)
                .error(R.drawable.ic_gift)
                .centerCrop()
                .into(holder.binding.ivRewardIcon);

        // Check if user has enough points
        int itemPoints = parsePoints(item.getPoints());
        boolean canRedeem = userPoints >= itemPoints;

        // Update button state
        if (canRedeem) {
            holder.binding.btnRedeem.setEnabled(true);
            holder.binding.btnRedeem.setBackgroundResource(R.drawable.bg_redeem_button);
            holder.binding.btnRedeem.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            holder.binding.btnRedeem.setText("Đổi ngay");
        } else {
            holder.binding.btnRedeem.setEnabled(false);
            holder.binding.btnRedeem.setBackgroundResource(R.drawable.bg_button_white);
            holder.binding.btnRedeem.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
            holder.binding.btnRedeem.setText("Chưa đủ điểm");
        }

        // Handle expiry visibility
        if (item.getExpiry() != null && !item.getExpiry().isEmpty()) {
            holder.binding.ivExpiry.setVisibility(View.VISIBLE);
            holder.binding.tvExpiry.setVisibility(View.VISIBLE);
        } else {
            holder.binding.ivExpiry.setVisibility(View.GONE);
            holder.binding.tvExpiry.setVisibility(View.GONE);
        }

        // Click listener
        holder.binding.btnRedeem.setOnClickListener(v -> {
            if (canRedeem) {
                Toast.makeText(context,
                        "Đổi thưởng: " + item.getName() + " (-" + item.getPoints() + " điểm)",
                        Toast.LENGTH_LONG).show();
                // TODO: Implement actual redeem logic
            }
        });

        // Item click listener
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context,
                    "Chi tiết: " + item.getName(),
                    Toast.LENGTH_SHORT).show();
            // TODO: Open detail page
        });
    }

    @Override
    public int getItemCount() {
        return rewardList.size();
    }

    // Set categories/rewards list
    public void setCategories(List<RewardItem> rewards) {
        this.rewardList = rewards;
        this.rewardListFull = new ArrayList<>(rewards);
        notifyDataSetChanged();
    }

    // Set rewards from API response
    public void setRewardsFromApi(List<RewardResponse> rewards) {
        List<RewardItem> items = convertToRewardItems(rewards);
        setCategories(items);
    }

    // Convert RewardResponse to RewardItem
    private List<RewardItem> convertToRewardItems(List<RewardResponse> rewards) {
        List<RewardItem> items = new ArrayList<>();
        
        for (RewardResponse reward : rewards) {
            int categoryType = getCategoryTypeFromRewardType(reward.getType());
            int iconColorIndex = categoryType % 4;
            String expiry = reward.getExpiryDate() != null ? 
                    formatExpiryDate(reward.getExpiryDate()) : "";
            
            RewardItem item = new RewardItem(
                    reward.getName(),
                    reward.getProviderName() != null ? reward.getProviderName() : "VolunConnect",
                    reward.getDescription(),
                    String.valueOf(reward.getPointsRequired()),
                    "Còn " + reward.getQuantity(),
                    expiry,
                    categoryType,
                    reward.getType(),
                    reward.getStatus().equals("AVAILABLE") ? "Còn hàng" : "",
                    iconColorIndex,
                    reward.getImageUrl()
            );
            
            items.add(item);
        }
        
        return items;
    }

    private int getCategoryTypeFromRewardType(String type) {
        if (type == null) return 0;
        
        String lowerType = type.toLowerCase();
        if (lowerType.contains("voucher") || lowerType.contains("giảm giá")) {
            return 1;
        } else if (lowerType.contains("quà") || lowerType.contains("gift") || 
                   lowerType.contains("mua sắm")) {
            return 2;
        } else if (lowerType.contains("cơ hội") || lowerType.contains("học") || 
                   lowerType.contains("giáo dục") || lowerType.contains("kỹ năng")) {
            return 3;
        }
        return 0;
    }

    private String formatExpiryDate(String date) {
        if (date == null || date.isEmpty()) return "";
        
        try {
            String[] parts = date.split("-");
            if (parts.length == 3) {
                return parts[2] + "/" + parts[1] + "/" + parts[0];
            }
        } catch (Exception e) {
            Log.e(TAG, "Error formatting date", e);
        }
        return date;
    }

    // Filter by category
    public void filterByCategory(int categoryType) {
        rewardList.clear();
        if (categoryType == 0) {
            // Show all
            rewardList.addAll(rewardListFull);
        } else {
            for (RewardItem item : rewardListFull) {
                if (item.getCategoryType() == categoryType) {
                    rewardList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    // Update user points
    public void updateUserPoints(int points) {
        this.userPoints = points;
        notifyDataSetChanged();
    }

    // Helper method to parse points string
    private int parsePoints(String pointsStr) {
        try {
            return Integer.parseInt(pointsStr.replace(",", "").replace(".", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    static class RewardViewHolder extends RecyclerView.ViewHolder {
        ItemRewardBinding binding;

        public RewardViewHolder(@NonNull ItemRewardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
