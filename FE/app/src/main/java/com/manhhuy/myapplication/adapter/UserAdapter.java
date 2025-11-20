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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
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
        private ShapeableImageView ivUserAvatar;
        private TextView tvUserName;
        private TextView tvUserEmail;
        private TextView tvUserStatus;
        private TextView tvJoinDate;
        private TextView tvActivityCount;
        private TextView tvLastActive;
        private LinearLayout llViolationWarning;
        private TextView tvViolationType;
        private TextView btnView;
        private TextView btnLockUnlock;
        private TextView btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserStatus = itemView.findViewById(R.id.tvUserStatus);
            tvJoinDate = itemView.findViewById(R.id.tvJoinDate);
            tvActivityCount = itemView.findViewById(R.id.tvActivityCount);
            tvLastActive = itemView.findViewById(R.id.tvLastActive);
            llViolationWarning = itemView.findViewById(R.id.llViolationWarning);
            tvViolationType = itemView.findViewById(R.id.tvViolationType);
            btnView = itemView.findViewById(R.id.btnView);
            btnLockUnlock = itemView.findViewById(R.id.btnLockUnlock);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(User user, OnUserActionListener listener) {
            // Set basic info
            tvUserName.setText(user.getName());
            tvUserEmail.setText(user.getEmail());
            tvJoinDate.setText("Tham gia: " + user.getJoinDate());
            tvActivityCount.setText("Hoạt động: " + user.getActivityCount());
            tvLastActive.setText("Giờ: " + user.getLastActive());

            // Set status badge
            tvUserStatus.setText(user.getStatus());
            switch (user.getStatus()) {
                case "Hoạt động":
                    tvUserStatus.setBackgroundResource(R.drawable.bg_status_active);
                    tvUserStatus.setTextColor(itemView.getContext().getColor(R.color.app_green_primary));
                    btnLockUnlock.setText("Khóa");
                    break;
                case "Bị khóa":
                    tvUserStatus.setBackgroundResource(R.drawable.bg_button_lock);
                    tvUserStatus.setTextColor(itemView.getContext().getColor(R.color.pink));
                    btnLockUnlock.setText("Mở khóa");
                    break;
                case "Chờ xác thực":
                    tvUserStatus.setBackgroundResource(R.drawable.bg_status_pending);
                    tvUserStatus.setTextColor(itemView.getContext().getColor(R.color.orange));
                    btnLockUnlock.setText("Khóa");
                    break;
            }

            // Show/hide violation warning
            if (user.getViolationType() != null && !user.getViolationType().isEmpty()) {
                llViolationWarning.setVisibility(View.VISIBLE);
                tvViolationType.setText("Vi phạm: " + user.getViolationType());
            } else {
                llViolationWarning.setVisibility(View.GONE);
            }

            // Set click listeners
            btnView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewClick(user);
                }
            });

            btnLockUnlock.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLockUnlockClick(user);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(user);
                }
            });
        }
    }
}
