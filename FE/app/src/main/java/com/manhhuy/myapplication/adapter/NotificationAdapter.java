package com.manhhuy.myapplication.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ItemNotificationBinding;
import com.manhhuy.myapplication.model.NotificationItem;

import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<NotificationItem> notificationList;
    private OnNotificationClickListener clickListener;

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationItem notification);
    }


    public NotificationAdapter(List<NotificationItem> notificationList,
                              OnNotificationClickListener clickListener) {
        this.notificationList = notificationList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new NotificationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem item = notificationList.get(position);
        Context context = holder.itemView.getContext();

        // Set title and content
        holder.binding.tvNotificationTitle.setText(item.getTitle());
        holder.binding.tvNotificationContent.setText(item.getContent());

        // Set time
        holder.binding.tvNotificationTime.setText(getRelativeTimeString(item.getCreatedAt()));

        // Set type
        String typeText = getTypeText(item.getType());
        holder.binding.tvNotificationType.setText(typeText);


        // Set sender badge
        if (item.getSenderRole() != null && !item.getSenderRole().isEmpty()) {
            holder.binding.tvSenderBadge.setVisibility(View.VISIBLE);
            holder.binding.tvSenderBadge.setText(item.getSenderRole());
            
            // Set badge color based on sender role
            int badgeColor = getBadgeColor(context, item.getSenderRole());
            holder.binding.tvSenderBadge.setBackgroundColor(badgeColor);
        } else {
            holder.binding.tvSenderBadge.setVisibility(View.GONE);
        }

        // Set icon based on sender role or type
        int iconResource = getIconResource(item.getSenderRole(), item.getType());
        holder.binding.ivNotificationIcon.setImageResource(iconResource);
        
        // Set icon background color
        int iconBgColor = getIconBackgroundColor(context, item.getSenderRole());
        holder.binding.iconContainer.setBackgroundTintList(
            ContextCompat.getColorStateList(context, iconBgColor)
        );

        // Card opacity for read/unread
        holder.binding.cardNotification.setAlpha(item.isRead() ? 0.7f : 1.0f);

        // Click listener
        holder.binding.cardNotification.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onNotificationClick(item);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    private String getRelativeTimeString(Date date) {
        if (date == null) return "";
        
        long now = System.currentTimeMillis();
        long time = date.getTime();
        
        CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
            time,
            now,
            DateUtils.MINUTE_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE
        );
        
        return relativeTime.toString();
    }

    private String getTypeText(String type) {
        if (type == null) return "Thông báo";
        
        switch (type) {
            case "PERSONAL":
                return "Cá nhân";
            case "ORGANIZATION":
                return "Tổ chức";
            case "GLOBAL":
                return "Chung";
            default:
                return "Thông báo";
        }
    }

    private int getBadgeColor(Context context, String senderRole) {
        if (senderRole == null) return ContextCompat.getColor(context, R.color.gray_500);
        
        switch (senderRole) {
            case "ADMIN":
                return ContextCompat.getColor(context, R.color.button_red);
            case "ORGANIZATION":
                return ContextCompat.getColor(context, R.color.app_green_primary);
            case "SYSTEM":
                return ContextCompat.getColor(context, R.color.button_blue);
            default:
                return ContextCompat.getColor(context, R.color.gray_500);
        }
    }

    private int getIconResource(String senderRole, String type) {
        if (senderRole != null) {
            switch (senderRole) {
                case "ADMIN":
                    return R.drawable.ic_user;
                case "ORGANIZATION":
                    return R.drawable.ic_organization;
                case "SYSTEM":
                    return R.drawable.ic_info;
            }
        }
        
        // Fallback to type
        if (type != null) {
            switch (type) {
                case "PERSONAL":
                    return R.drawable.ic_user;
                case "ORGANIZATION":
                    return R.drawable.ic_organization;
                case "GLOBAL":
                    return R.drawable.ic_notification;
            }
        }
        
        return R.drawable.ic_notification;
    }

    private int getIconBackgroundColor(Context context, String senderRole) {
        if (senderRole != null) {
            switch (senderRole) {
                case "ADMIN":
                    return R.color.button_red;
                case "ORGANIZATION":
                    return R.color.app_green_primary;
                case "SYSTEM":
                    return R.color.button_blue;
            }
        }
        return R.color.app_green_primary;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ItemNotificationBinding binding;

        public NotificationViewHolder(@NonNull ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
