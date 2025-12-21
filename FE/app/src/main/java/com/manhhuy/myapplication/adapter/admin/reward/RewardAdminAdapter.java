package com.manhhuy.myapplication.adapter.admin.reward;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
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
        holder.bind(reward, position);
    }

    class RewardViewHolder extends RecyclerView.ViewHolder {
        final ItemRewardAdminBinding binding;

        RewardViewHolder(@NonNull ItemRewardAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(RewardItem reward, int position) {
            binding.tvRewardName.setText(reward.getName());
            binding.tvRewardCategory.setText(getCategoryText(reward.getCategoryType()));
            binding.tvRewardPoints.setText("⭐ " + reward.getPoints() + " điểm");
            binding.tvStock.setText("Còn: " + reward.getStock());
            binding.tvRedeemed.setText("Đã đổi: " + (int)(Math.random() * 150));

            updateStatusUI(reward);
            loadImage(reward);

            binding.btnEdit.setOnClickListener(v -> listener.onEditClick(reward, position));
            binding.btnPause.setOnClickListener(v -> listener.onPauseClick(reward, position));
            binding.btnDelete.setOnClickListener(v -> listener.onDeleteClick(reward, position));
        }

        void updateStatusUI(RewardItem reward) {
            String status = reward.getStatus() != null ? reward.getStatus() : "ACTIVE";
            int stock = Integer.parseInt(reward.getStock());
            int redColor = context.getResources().getColor(R.color.status_rejected);
            int greenColor = context.getResources().getColor(R.color.app_green_primary);
            int grayColor = context.getResources().getColor(R.color.text_secondary);

            if ("INACTIVE".equals(status)) {
                binding.tvStatus.setText("Tạm ngưng");
                binding.tvStatus.setTextColor(redColor);
                binding.btnPause.setImageResource(R.drawable.ic_check);
                binding.btnPause.setColorFilter(greenColor);
            } else if (stock == 0) {
                binding.tvStatus.setText("Hết hàng");
                binding.tvStatus.setTextColor(redColor);
                binding.btnPause.setImageResource(R.drawable.ic_pause);
                binding.btnPause.setColorFilter(grayColor);
            } else if (stock <= 5) {
                binding.tvStatus.setText("Sắp hết");
                binding.tvStatus.setTextColor(redColor);
                binding.btnPause.setImageResource(R.drawable.ic_pause);
                binding.btnPause.setColorFilter(grayColor);
            } else {
                binding.tvStatus.setText("Hoạt động");
                binding.tvStatus.setTextColor(greenColor);
                binding.btnPause.setImageResource(R.drawable.ic_pause);
                binding.btnPause.setColorFilter(grayColor);
            }
            binding.tvStatus.setBackground(null);
        }

        void loadImage(RewardItem reward) {
            String imageUrl = reward.getImageUrl();
            Log.d("RewardAdapter", "Loading image for: " + reward.getName() + ", URL: " + imageUrl);
            
            if (!TextUtils.isEmpty(imageUrl)) {
                Log.d("RewardAdapter", "Loading from URL: " + imageUrl);
                Glide.with(context)
                        .load(imageUrl)
                        .apply(new RequestOptions()
                                .transform(new RoundedCorners(16))
                                .placeholder(R.drawable.ic_voucher)
                                .error(R.drawable.ic_voucher))
                        .into(binding.ivRewardImage);
            } else {
                Log.d("RewardAdapter", "No image URL, using default icon");
                int[] icons = {R.drawable.ic_coffee, R.drawable.ic_group, R.drawable.ic_certificate,
                              R.drawable.ic_book, R.drawable.ic_voucher, R.drawable.ic_backpack};
                binding.ivRewardImage.setImageResource(icons[reward.getIconColorIndex() % icons.length]);
            }
        }
    }

    @Override
    public int getItemCount() {
        return rewardList.size();
    }

    private String getCategoryText(int categoryType) {
        switch (categoryType) {
            case 1: return "Voucher • Đồ uống";
            case 2: return "Vật phẩm • Thời trang";
            case 3: return "Trải nghiệm • Đào tạo";
            default: return "Quà tặng";
        }
    }
}
