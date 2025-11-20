package com.manhhuy.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ItemUserBinding;
import com.manhhuy.myapplication.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onViewClick(User user);
        void onLockUnlockClick(User user);
        void onDeleteClick(User user);
    }

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
        this.userList = newList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private ItemUserBinding binding;

        public UserViewHolder(@NonNull ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(User user, OnUserActionListener listener) {
            // Set basic info
            binding.tvUserName.setText(user.getName());
            binding.tvUserEmail.setText(user.getEmail());
            binding.tvJoinDate.setText("Tham gia: " + user.getJoinDate());
            binding.tvActivityCount.setText("Hoạt động: " + user.getActivityCount());
            binding.tvLastActive.setText("Giờ: " + user.getLastActive());

            // Set status badge
            binding.tvUserStatus.setText(user.getStatus());
            switch (user.getStatus()) {
                case "Hoạt động":
                    binding.tvUserStatus.setBackgroundResource(R.drawable.bg_status_active);
                    binding.tvUserStatus.setTextColor(itemView.getContext().getColor(R.color.app_green_primary));
                    binding.btnLockUnlock.setText("Khóa");
                    break;
                case "Bị khóa":
                    binding.tvUserStatus.setBackgroundResource(R.drawable.bg_button_lock);
                    binding.tvUserStatus.setTextColor(itemView.getContext().getColor(R.color.pink));
                    binding.btnLockUnlock.setText("Mở khóa");
                    break;
                case "Chờ xác thực":
                    binding.tvUserStatus.setBackgroundResource(R.drawable.bg_status_pending);
                    binding.tvUserStatus.setTextColor(itemView.getContext().getColor(R.color.orange));
                    binding.btnLockUnlock.setText("Khóa");
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
            binding.btnView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewClick(user);
                }
            });

            binding.btnLockUnlock.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLockUnlockClick(user);
                }
            });

            binding.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(user);
                }
            });
        }
    }
}
