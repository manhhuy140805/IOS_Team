package com.manhhuy.myapplication.adapter.admin.userOrganations;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ItemUserBinding;
import com.manhhuy.myapplication.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> userList;
    private final OnUserActionListener listener;

    public UserAdapter(List<User> userList, OnUserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public void updateList(List<User> newList) {
//        this.userList = newList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private ItemUserBinding binding;

        public UserViewHolder(@NonNull ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(User user, OnUserActionListener listener) {
            // Load avatar
            if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(user.getAvatarUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(binding.ivUserAvatar);
            } else {
                binding.ivUserAvatar.setImageResource(R.mipmap.ic_launcher);
            }
            
            // Set basic info
            binding.tvUserName.setText(user.getName());
            binding.tvUserEmail.setText(user.getEmail());
            binding.tvJoinDate.setText(user.getJoinDate());
            binding.tvActivityCount.setText(String.valueOf(user.getActivityCount()));
            binding.tvLastActive.setText(String.valueOf(user.getPointsCount()));

            // Set status badge
            binding.tvUserStatus.setText(user.getStatus());
            switch (user.getStatus()) {
                case "Hoạt động":
                    binding.tvUserStatus.setBackgroundResource(R.drawable.bg_status_active_event);
                    binding.tvUserStatus.setTextColor(itemView.getContext().getColor(R.color.app_green_primary));
                    binding.btnLockUnlock.setImageResource(R.drawable.ic_lock);
                    binding.btnLockUnlock.setColorFilter(Color.parseColor("#FF9800")); // Orange for lock action
                    break;
                case "Bị khóa":
                    binding.tvUserStatus.setBackgroundResource(R.drawable.bg_status_rejected_light);
                    binding.tvUserStatus.setTextColor(Color.parseColor("#C62828"));
                    binding.btnLockUnlock.setImageResource(R.drawable.ic_lock_open);
                    binding.btnLockUnlock.setColorFilter(Color.parseColor("#4CAF50")); // Green for unlock action
                    break;
                case "Chờ xác thực":
                    binding.tvUserStatus.setBackgroundResource(R.drawable.bg_status_pending_light);
                    binding.tvUserStatus.setTextColor(Color.parseColor("#EF6C00"));
                    binding.btnLockUnlock.setImageResource(R.drawable.ic_lock);
                    binding.btnLockUnlock.setColorFilter(Color.parseColor("#FF9800"));
                    break;
            }

            // Show/hide violation warning
            if (user.getViolationType() != null && !user.getViolationType().isEmpty()) {
                binding.llViolationWarning.setVisibility(View.VISIBLE);
                binding.tvViolationType.setText("Vi phạm: " + user.getViolationType());
            } else {
                binding.llViolationWarning.setVisibility(View.GONE);
            }

            // Set click listeners
            binding.btnView.setOnClickListener(v -> listener.onViewClick(user));
            binding.btnLockUnlock.setOnClickListener(v -> listener.onLockUnlockClick(user));
            binding.btnDelete.setOnClickListener(v -> listener.onDeleteClick(user));
        }
    }
}
