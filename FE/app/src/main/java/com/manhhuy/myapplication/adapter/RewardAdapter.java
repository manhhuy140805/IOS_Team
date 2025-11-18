package com.manhhuy.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.model.RewardItem;

import java.util.ArrayList;
import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.RewardViewHolder> {

    private Context context;
    private List<RewardItem> rewardList;
    private List<RewardItem> rewardListFull; // For filtering
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_reward, parent, false);
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        RewardItem item = rewardList.get(position);

        // Set data
        holder.tvRewardName.setText(item.getName());
        holder.tvOrganization.setText(item.getOrganization());
        holder.tvDescription.setText(item.getDescription());
        holder.tvRewardPoints.setText(item.getPoints());
        holder.tvStock.setText(item.getStock());
        holder.tvExpiry.setText(item.getExpiry());

        // Set tags
        holder.tvTag1.setText(item.getTag1());
        if (item.getTag2() != null && !item.getTag2().isEmpty()) {
            holder.tvTag2.setVisibility(View.VISIBLE);
            holder.tvTag2.setText(item.getTag2());
        } else {
            holder.tvTag2.setVisibility(View.GONE);
        }

        // Set icon background color
        int backgroundRes;
        int iconRes;
        switch (item.getIconColorIndex()) {
            case 0: // Purple
                backgroundRes = R.drawable.bg_icon_purple;
                break;
            case 1: // Pink
                backgroundRes = R.drawable.bg_icon_pink;
                break;
            case 2: // Orange
                backgroundRes = R.drawable.bg_icon_orange;
                break;
            case 3: // Cyan
                backgroundRes = R.drawable.bg_icon_cyan;
                break;
            default:
                backgroundRes = R.drawable.bg_icon_purple;
        }
        holder.iconContainer.setBackgroundResource(backgroundRes);

        // Set icon based on category type
        switch (item.getCategoryType()) {
            case 1: // Voucher
                iconRes = R.drawable.ic_voucher;
                break;
            case 2: // Gift
                iconRes = R.drawable.ic_gift;
                break;
            case 3: // Opportunity
                iconRes = R.drawable.ic_discount;
                break;
            default:
                iconRes = R.drawable.ic_gift;
        }
        holder.ivRewardIcon.setImageResource(iconRes);

        // Check if user has enough points
        int itemPoints = parsePoints(item.getPoints());
        boolean canRedeem = userPoints >= itemPoints;

        // Update button state
        if (canRedeem) {
            holder.btnRedeem.setEnabled(true);
            holder.btnRedeem.setBackgroundResource(R.drawable.bg_redeem_button);
            holder.btnRedeem.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            holder.btnRedeem.setText("Đổi ngay");
        } else {
            holder.btnRedeem.setEnabled(false);
            holder.btnRedeem.setBackgroundResource(R.drawable.bg_disabled_button);
            holder.btnRedeem.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
            holder.btnRedeem.setText("Chưa đủ điểm");
        }

        // Handle expiry visibility
        if (item.getExpiry() != null && !item.getExpiry().isEmpty()) {
            holder.ivExpiry.setVisibility(View.VISIBLE);
            holder.tvExpiry.setVisibility(View.VISIBLE);
        } else {
            holder.ivExpiry.setVisibility(View.GONE);
            holder.tvExpiry.setVisibility(View.GONE);
        }

        // Click listener
        holder.btnRedeem.setOnClickListener(v -> {
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
        FrameLayout iconContainer;
        ImageView ivRewardIcon, ivExpiry;
        TextView tvRewardName, tvOrganization, tvDescription, tvRewardPoints;
        TextView tvStock, tvExpiry, tvTag1, tvTag2;
        Button btnRedeem;

        public RewardViewHolder(@NonNull View itemView) {
            super(itemView);

            iconContainer = itemView.findViewById(R.id.iconContainer);
            ivRewardIcon = itemView.findViewById(R.id.ivRewardIcon);
            ivExpiry = itemView.findViewById(R.id.ivExpiry);
            tvRewardName = itemView.findViewById(R.id.tvRewardName);
            tvOrganization = itemView.findViewById(R.id.tvOrganization);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvRewardPoints = itemView.findViewById(R.id.tvRewardPoints);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
            tvTag1 = itemView.findViewById(R.id.tvTag1);
            tvTag2 = itemView.findViewById(R.id.tvTag2);
            btnRedeem = itemView.findViewById(R.id.btnRedeem);
        }
    }
}
