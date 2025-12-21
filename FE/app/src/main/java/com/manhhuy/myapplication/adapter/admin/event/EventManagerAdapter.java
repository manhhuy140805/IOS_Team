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
import com.manhhuy.myapplication.model.EventPost;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventManagerAdapter extends RecyclerView.Adapter<EventManagerAdapter.EventViewHolder> {

    private final Context context;
    private final List<EventPost> eventList;
    private final List<EventPost> eventListFull;
    private final OnEventActionListenerInterface listener;

    public EventManagerAdapter(Context context, List<EventPost> eventList, OnEventActionListenerInterface listener) {
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
        EventPost event = eventList.get(position);

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
        holder.binding.tvOrganization.setText(event.getOrganizationName());
        holder.binding.tvPoints.setText(event.getRewardPoints() + " điểm");
        holder.binding.tvLocation.setText("📍 " + event.getLocation());

        // Format date
        if (event.getEventDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.binding.tvDate.setText("📅 " + sdf.format(event.getEventDate()));
        }

        // Set participants
        holder.binding.tvParticipants.setText("👥 " + event.getCurrentParticipants() + "/" + event.getMaxParticipants() + " người");

        // Set status
        String status = event.getStatus();
        if ("active".equals(status)) {
            holder.binding.tvStatus.setText("Đang hoạt động");
            holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_status_active_event);
            holder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.app_green_primary));
        } else if ("completed".equals(status)) {
            holder.binding.tvStatus.setText("Hoàn thành");
            holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_rounded_lite);
            holder.binding.tvStatus.setTextColor(context.getResources().getColor(R.color.button_blue));
        }

        List<String> tags = event.getTags();
        if (tags != null && !tags.isEmpty()) {
            holder.binding.tvTag1.setVisibility(View.VISIBLE);
            holder.binding.tvTag1.setText(tags.get(0));
        } else {
            holder.binding.tvTag1.setVisibility(View.GONE);
        }

        // Button listeners
        holder.binding.btnNotification.setVisibility(View.GONE); // Ẩn nút thông báo
        holder.binding.btnView.setOnClickListener(v -> listener.onViewClick(event));
        holder.binding.btnEdit.setOnClickListener(v -> listener.onEditClick(event));
        holder.binding.btnDelete.setOnClickListener(v -> listener.onDeleteClick(event));
        
        holder.itemView.setOnClickListener(v -> listener.onViewClick(event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
 
    public void updateList(List<EventPost> newList) {
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
                return eventList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                EventPost oldEvent = eventList.get(oldItemPosition);
                EventPost newEvent = newList.get(newItemPosition);
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
            for (EventPost event : eventListFull) {
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
            for (EventPost event : eventListFull) {
                if (event.getTags() != null && event.getTags().contains(category)) {
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
