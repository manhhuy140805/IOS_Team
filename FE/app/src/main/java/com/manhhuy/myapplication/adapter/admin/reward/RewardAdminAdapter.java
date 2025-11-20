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
        int stock = Integer.parseInt(reward.getStock());
        
        if (stock == 0) {
            holder.binding.tvStatus.setText("Tạm ngưng");
            holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_button_lock);
            holder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.status_rejected));
            holder.binding.btnPause.setText("Kích hoạt");
        } else if (stock <= 5) {
            holder.binding.tvStatus.setText("Sắp hết hàng");
            holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_button_delete);
            holder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.status_rejected));
        } else {
            holder.binding.tvStatus.setText("Đang hoạt động");
            holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_status_active_reward);
            holder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.app_green_primary));
        }
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
        this.rewardList = newList;
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
