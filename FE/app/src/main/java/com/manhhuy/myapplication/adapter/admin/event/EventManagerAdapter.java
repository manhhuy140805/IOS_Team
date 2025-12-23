package com.manhhuy.myapplication.adapter.admin.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ItemEventManagerBinding;
import com.manhhuy.myapplication.helper.ApiConfig;
import com.manhhuy.myapplication.helper.response.EventResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventManagerAdapter extends RecyclerView.Adapter<EventManagerAdapter.EventViewHolder> {

    private final Context context;
    private final List<EventResponse> eventList;
    private final List<EventResponse> eventListFull;
    private final OnEventActionListenerInterface listener;

    public EventManagerAdapter(Context context, List<EventResponse> eventList, OnEventActionListenerInterface listener) {
        this.context = context;
        this.eventList = eventList;
        this.eventListFull = new ArrayList<>(eventList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventManagerBinding binding = ItemEventManagerBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new EventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventResponse event = eventList.get(position);

        // Load image with Glide
        if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(event.getImageUrl())
                    .placeholder(R.drawable.logokhoa)
                    .error(R.drawable.logokhoa)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.1f)  // Load thumbnail trước
                    .centerCrop()
                    .into(holder.binding.ivEventImage);
        } else {
            holder.binding.ivEventImage.setImageResource(R.drawable.logokhoa);
        }

        // Bind data
        holder.binding.tvEventTitle.setText(event.getTitle());
        holder.binding.tvOrganization.setText(event.getCreatorName());
        holder.binding.tvPoints.setText(event.getRewardPoints() + " điểm");
        holder.binding.tvLocation.setText("📍 " + event.getLocation());

        // Format date
        if (event.getEventStartTime() != null) {
            holder.binding.tvDate.setText("📅 " + event.getEventStartTime());
        }

        // Set participants
        holder.binding.tvParticipants.setText("👥 " + event.getCurrentParticipants() + "/" + event.getNumOfVolunteers() + " người");

        // Set status
        String status = event.getStatus();
        if (status != null) {
            if ("active".equalsIgnoreCase(status)) {
                holder.binding.tvStatus.setText("Đang hoạt động");
                holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_status_active_event);
                holder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.app_green_primary));
            } else if ("completed".equalsIgnoreCase(status)) {
                holder.binding.tvStatus.setText("Hoàn thành");
                holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_rounded_lite);
                holder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.button_blue));
            } else if ("pending".equalsIgnoreCase(status)) {
                holder.binding.tvStatus.setText("Chờ duyệt");
                holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                holder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.status_pending));
            } else if ("rejected".equalsIgnoreCase(status)) {
                holder.binding.tvStatus.setText("Đã từ chối");
                holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_status_rejected);
                holder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.status_rejected));
            } else {
                holder.binding.tvStatus.setText(status);
                holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_rounded_lite);
                holder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.text_secondary));
            }
        } else {
            holder.binding.tvStatus.setVisibility(View.GONE);
        }

        String category = event.getCategory();
        if (category != null && !category.isEmpty()) {
            holder.binding.tvTag1.setVisibility(View.VISIBLE);
            holder.binding.tvTag1.setText(category);
        } else {
            holder.binding.tvTag1.setVisibility(View.GONE);
        }

        // Button listeners
        holder.binding.btnNotification.setVisibility(View.GONE);
        holder.binding.btnView.setOnClickListener(v -> listener.onViewClick(event));
        
        // Admin: chỉ có nút Delete (không có Edit)
        // Organizer: có cả Edit và Delete (chỉ với events của mình)
        if (ApiConfig.isAdmin()) {
            holder.binding.btnEdit.setVisibility(View.GONE);
            holder.binding.btnDelete.setVisibility(View.VISIBLE);
            holder.binding.btnDelete.setOnClickListener(v -> listener.onDeleteClick(event));
        } else if (ApiConfig.isOrganizer() && isMyEvent(event)) {
            holder.binding.btnEdit.setVisibility(View.VISIBLE);
            holder.binding.btnDelete.setVisibility(View.VISIBLE);
            holder.binding.btnEdit.setOnClickListener(v -> listener.onEditClick(event));
            holder.binding.btnDelete.setOnClickListener(v -> listener.onDeleteClick(event));
        } else {
            holder.binding.btnEdit.setVisibility(View.GONE);
            holder.binding.btnDelete.setVisibility(View.GONE);
        }
        
        holder.itemView.setOnClickListener(v -> listener.onViewClick(event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
    
    /**
     * Kiểm tra xem event có phải do user hiện tại tạo không
     */
    private boolean isMyEvent(EventResponse event) {
        Integer currentUserId = ApiConfig.getUserId();
        return currentUserId != null && currentUserId.equals(event.getCreatorId());
    }
 
    public void updateList(List<EventResponse> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return eventList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return eventList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                EventResponse oldEvent = eventList.get(oldItemPosition);
                EventResponse newEvent = newList.get(newItemPosition);
                return oldEvent.getTitle().equals(newEvent.getTitle()) &&
                       oldEvent.getStatus().equals(newEvent.getStatus());
            }
        });
        
        eventList.clear();
        eventList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    public void filterByStatus(String status) {
        eventList.clear();
        if ("all".equals(status)) {
            eventList.addAll(eventListFull);
        } else {
            for (EventResponse event : eventListFull) {
                if (status.equals(event.getStatus())) {
                    eventList.add(event);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterByCategory(String category) {
        eventList.clear();
        if ("all".equals(category)) {
            eventList.addAll(eventListFull);
        } else {
            for (EventResponse event : eventListFull) {
                if (event.getCategory() != null && event.getCategory().equals(category)) {
                    eventList.add(event);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        ItemEventManagerBinding binding;

        public EventViewHolder(@NonNull ItemEventManagerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
