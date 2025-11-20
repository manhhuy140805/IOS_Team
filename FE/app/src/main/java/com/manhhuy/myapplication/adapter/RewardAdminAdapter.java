package com.manhhuy.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.model.RewardItem;

import java.util.List;

public class RewardAdminAdapter extends RecyclerView.Adapter<RewardAdminAdapter.RewardViewHolder> {

    private Context context;
    private List<RewardItem> rewardList;
    private OnRewardActionListener listener;

    public interface OnRewardActionListener {
        void onEditClick(RewardItem reward, int position);
        void onPauseClick(RewardItem reward, int position);
        void onDeleteClick(RewardItem reward, int position);
    }

    public RewardAdminAdapter(Context context, List<RewardItem> rewardList, OnRewardActionListener listener) {
        this.context = context;
        this.rewardList = rewardList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reward_admin, parent, false);
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        RewardItem reward = rewardList.get(position);

        holder.tvRewardName.setText(reward.getName());
        holder.tvRewardCategory.setText(getCategoryText(reward.getCategoryType()));
        holder.tvRewardPoints.setText("⭐ " + reward.getPoints() + " điểm");
        holder.tvStock.setText("Còn: " + reward.getStock());
        
        // Calculate redeemed count (mock data - should come from backend)
        int redeemed = calculateRedeemed(reward);
        holder.tvRedeemed.setText("Đã đổi: " + redeemed);

        // Set status
        setStatusBadge(holder, reward);

        // Set icon based on category
        setRewardIcon(holder.ivRewardImage, reward);

        // Button listeners
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(reward, position);
            }
        });

        holder.btnPause.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPauseClick(reward, position);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(reward, position);
            }
        });
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
            holder.tvStatus.setText("Tạm ngưng");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_button_lock);
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.status_rejected));
            holder.btnPause.setText("Kích hoạt");
        } else if (stock <= 5) {
            holder.tvStatus.setText("Sắp hết hàng");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_button_delete);
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.status_rejected));
        } else {
            holder.tvStatus.setText("Đang hoạt động");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_active_reward);
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.app_green_primary));
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
        ImageView ivRewardImage;
        TextView tvRewardName, tvRewardCategory, tvRewardPoints;
        TextView tvStock, tvRedeemed, tvStatus;
        Button btnEdit, btnPause, btnDelete;

        public RewardViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRewardImage = itemView.findViewById(R.id.ivRewardImage);
            tvRewardName = itemView.findViewById(R.id.tvRewardName);
            tvRewardCategory = itemView.findViewById(R.id.tvRewardCategory);
            tvRewardPoints = itemView.findViewById(R.id.tvRewardPoints);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvRedeemed = itemView.findViewById(R.id.tvRedeemed);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnPause = itemView.findViewById(R.id.btnPause);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
