package com.manhhuy.myapplication.adapter.admin.reward;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ItemRewardAdminBinding;
import com.manhhuy.myapplication.model.RewardItem;

import java.util.List;

public class RewardAdminAdapter extends RecyclerView.Adapter<RewardAdminAdapter.RewardViewHolder> {

    private final Context context;
    private final List<RewardItem> rewardList;
    private final OnRewardActionListener listener;

    public RewardAdminAdapter(Context context, List<RewardItem> rewardList, OnRewardActionListener listener) {
        this.context = context;
        this.rewardList = rewardList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRewardAdminBinding binding = ItemRewardAdminBinding.inflate(LayoutInflater.from(context), parent, false);
        return new RewardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        RewardItem reward = rewardList.get(position);

        holder.binding.tvRewardName.setText(reward.getName());
        holder.binding.tvRewardCategory.setText(getCategoryText(reward.getCategoryType()));
        holder.binding.tvRewardPoints.setText("⭐ " + reward.getPoints() + " điểm");
        holder.binding.tvStock.setText("Còn: " + reward.getStock());

        // Calculate redeemed count (mock data - should come from backend)
        int redeemed = calculateRedeemed(reward);
        holder.binding.tvRedeemed.setText("Đã đổi: " + redeemed);

        // Set status
        setStatusBadge(holder, reward);

        // Set icon based on category
        setRewardIcon(holder.binding.ivRewardImage, reward);

        // Button listeners
        holder.binding.btnEdit.setOnClickListener(v -> listener.onEditClick(reward, position));
        holder.binding.btnPause.setOnClickListener(v -> listener.onPauseClick(reward, position));
        holder.binding.btnDelete.setOnClickListener(v -> listener.onDeleteClick(reward, position));
    }

    @Override
    public int getItemCount() {
        return rewardList.size();
    }

    private String getCategoryText(int categoryType) {
        switch (categoryType) {
            case 1:
                return "Voucher • Đồ uống";
            case 2:
                return "Vật phẩm • Thời trang";
            case 3:
                return "Trải nghiệm • Đào tạo";
            default:
                return "Quà tặng";
        }
    }

    private void setStatusBadge(RewardViewHolder holder, RewardItem reward) {
        String status = reward.getStatus() != null ? reward.getStatus() : "ACTIVE";
        int stock = Integer.parseInt(reward.getStock());

        // Check status first
        if ("INACTIVE".equals(status)) {
            holder.binding.tvStatus.setText("Tạm ngưng");
            holder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.status_rejected));
            // Change button to "Activate" icon (play)
            holder.binding.btnPause.setImageResource(R.drawable.ic_check);
            holder.binding.btnPause.setColorFilter(context.getResources().getColor(R.color.app_green_primary));
        } else if (stock == 0) {
            holder.binding.tvStatus.setText("Hết hàng");
            holder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.status_rejected));
            // Keep pause button
            holder.binding.btnPause.setImageResource(R.drawable.ic_pause);
            holder.binding.btnPause.setColorFilter(context.getResources().getColor(R.color.text_secondary));
        } else if (stock <= 5) {
            holder.binding.tvStatus.setText("Sắp hết hàng");
            holder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.status_rejected));
            // Keep pause button
            holder.binding.btnPause.setImageResource(R.drawable.ic_pause);
            holder.binding.btnPause.setColorFilter(context.getResources().getColor(R.color.text_secondary));
        } else {
            holder.binding.tvStatus.setText("Đang hoạt động");
            holder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.app_green_primary));
            // Keep pause button
            holder.binding.btnPause.setImageResource(R.drawable.ic_pause);
            holder.binding.btnPause.setColorFilter(context.getResources().getColor(R.color.text_secondary));
        }
        
        // Clear background that might have been set in previous version
        holder.binding.tvStatus.setBackground(null);
    }

    private void setRewardIcon(ImageView imageView, RewardItem reward) {
        // Set icon based on category type and icon color index
        int[] iconResources = {
                R.drawable.ic_coffee,
                R.drawable.ic_group,
                R.drawable.ic_certificate,
                R.drawable.ic_book,
                R.drawable.ic_voucher,
                R.drawable.ic_backpack
        };

        int iconIndex = reward.getIconColorIndex() % iconResources.length;
        imageView.setImageResource(iconResources[iconIndex]);
    }

    private int calculateRedeemed(RewardItem reward) {
        // Mock calculation - in real app, this should come from backend
        return (int) (Math.random() * 150);
    }

    public void updateList(List<RewardItem> newList) {
//        this.rewardList = newList;
        notifyDataSetChanged();
    }

    static class RewardViewHolder extends RecyclerView.ViewHolder {
        ItemRewardAdminBinding binding;

        public RewardViewHolder(@NonNull ItemRewardAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
