package com.manhhuy.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.helper.response.MyRewardResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.bumptech.glide.Glide;

public class MyRewardAdapter extends RecyclerView.Adapter<MyRewardAdapter.ViewHolder> {

    private Context context;
    private List<MyRewardResponse> rewardList;

    public MyRewardAdapter(Context context) {
        this.context = context;
        this.rewardList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_reward, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyRewardResponse reward = rewardList.get(position);

        // Set reward name
        holder.tvRewardName.setText(reward.getRewardName() != null ? reward.getRewardName() : "Phần thưởng");

        // Set points spent
        Integer pointsSpent = reward.getPointsSpent();
        if (pointsSpent != null) {
            holder.tvPointsSpent.setText(String.format("-%,d điểm", pointsSpent));
        } else if (reward.getPointsRequired() != null) {
            holder.tvPointsSpent.setText(String.format("-%,d điểm", reward.getPointsRequired()));
        } else {
            holder.tvPointsSpent.setText("-0 điểm");
        }

        // Set date
        String createdAt = reward.getCreatedAt();
        if (createdAt != null && !createdAt.isEmpty()) {
            holder.tvDate.setText(formatDateTime(createdAt));
        } else {
            holder.tvDate.setText("");
        }

        // Load reward image
        String imageUrl = reward.getRewardImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_gift)
                    .error(R.drawable.ic_gift)
                    .centerCrop()
                    .into(holder.ivRewardIcon);
            holder.ivRewardIcon.setColorFilter(null); // Remove tint
        } else {
            holder.ivRewardIcon.setImageResource(R.drawable.ic_gift);
            holder.ivRewardIcon.setColorFilter(ContextCompat.getColor(context, android.R.color.white));
        }

        // Set status with color
        String status = reward.getStatus();
        String displayStatus = reward.getDisplayStatus();
        holder.tvStatus.setText(displayStatus);

        // Set status background color based on status
        int bgColor;
        int textColor = ContextCompat.getColor(context, android.R.color.white);

        if (status != null) {
            switch (status.toUpperCase()) {
                case "PENDING":
                    bgColor = ContextCompat.getColor(context, R.color.status_pending);
                    break;
                case "APPROVED":
                case "ACCEPTED":
                    bgColor = ContextCompat.getColor(context, R.color.status_approved);
                    break;
                case "DELIVERED":
                    bgColor = ContextCompat.getColor(context, R.color.app_green_primary);
                    break;
                case "REJECTED":
                case "CANCELLED":
                    bgColor = ContextCompat.getColor(context, R.color.status_rejected);
                    break;
                default:
                    bgColor = ContextCompat.getColor(context, R.color.gray_500);
                    break;
            }
        } else {
            bgColor = ContextCompat.getColor(context, R.color.gray_500);
        }

        holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(bgColor));
        holder.tvStatus.setTextColor(textColor);
    }

    @Override
    public int getItemCount() {
        return rewardList.size();
    }

    public void setRewards(List<MyRewardResponse> rewards) {
        this.rewardList.clear();
        if (rewards != null) {
            this.rewardList.addAll(rewards);
        }
        notifyDataSetChanged();
    }

    /**
     * Format ISO date time to readable format
     */
    private String formatDateTime(String isoDateTime) {
        try {
            // Parse ISO format: 2025-12-22T15:30:00Z or similar
            SimpleDateFormat inputFormat;
            if (isoDateTime.contains("T")) {
                if (isoDateTime.endsWith("Z")) {
                    inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                } else if (isoDateTime.contains("+") || isoDateTime.lastIndexOf("-") > 10) {
                    inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                } else {
                    inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                }
            } else {
                inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            }

            Date date = inputFormat.parse(isoDateTime.substring(0, Math.min(19, isoDateTime.length())));
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            // Return original string if parsing fails
            return isoDateTime;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRewardIcon;
        TextView tvRewardName;
        TextView tvPointsSpent;
        TextView tvDate;
        TextView tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRewardIcon = itemView.findViewById(R.id.ivRewardIcon);
            tvRewardName = itemView.findViewById(R.id.tvRewardName);
            tvPointsSpent = itemView.findViewById(R.id.tvPointsSpent);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
