package com.manhhuy.myapplication.adapter.admin.post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ItemEventPostBinding;
import com.manhhuy.myapplication.helper.response.EventResponse;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventPostAdapter extends RecyclerView.Adapter<EventPostAdapter.ViewHolder> {

    private final Context context;
    private final List<EventResponse> events;
    private final OnItemClickListenerInterface listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public EventPostAdapter(Context context, List<EventResponse> events, OnItemClickListenerInterface listener) {
        this.context = context;
        this.events = events;
        this.listener = listener;
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemEventPostBinding binding;

        ViewHolder(ItemEventPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventPostBinding binding = ItemEventPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//        View view = inflater.inflate(R.layout.item_event_post, parent, false); // vì khai báo trong recycle view rồi nên có thể làm theo cách ở trên
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventResponse event = events.get(position);
        
        holder.binding.tvTitle.setText(event.getTitle());
        holder.binding.tvLocation.setText("📍 " + (event.getLocation() != null ? event.getLocation() : ""));
        holder.binding.tvDate.setText("📅 " + (event.getEventStartTime() != null ? event.getEventStartTime() : "N/A"));
        
        if (event.getRewardPoints() != null && event.getRewardPoints() > 0) {
            holder.binding.tvReward.setText(event.getRewardPoints() + "đ");
        } else {
            holder.binding.tvReward.setText("0đ");
        }
        
        // Image
        if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
            Glide.with(context)
                .load(event.getImageUrl())
                .placeholder(R.drawable.logokhoa)
                .error(R.drawable.logokhoa)
                .centerCrop()
                .into(holder.binding.imgEvent);
        } else {
            holder.binding.imgEvent.setImageResource(R.drawable.logokhoa);
        }
        
        // Org info - hide nếu không có
        holder.binding.tvOrgInitials.setVisibility(View.GONE);
        holder.binding.tvOrgName.setVisibility(View.GONE);
        
        // Posted info - hide vì không có data
        holder.binding.tvPostedInfo.setVisibility(View.GONE);
        
        String status = event.getStatus() != null ? event.getStatus().toUpperCase() : "PENDING";
        if ("PENDING".equals(status)) {
            holder.binding.tvStatus.setText("⏳ Chờ duyệt");
            holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_status_pending_light);
            holder.binding.layoutActionButtons.setVisibility(View.VISIBLE);
            holder.binding.layoutApprovedButtons.setVisibility(View.GONE);
            holder.binding.layoutRejectionReason.setVisibility(View.GONE);
        } else if ("ACTIVE".equals(status) || "APPROVED".equals(status)) {
            holder.binding.tvStatus.setText("✓ Đã duyệt");
            holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_status_approved_light);
            holder.binding.layoutActionButtons.setVisibility(View.GONE);
            holder.binding.layoutApprovedButtons.setVisibility(View.VISIBLE);
            holder.binding.layoutRejectionReason.setVisibility(View.GONE);
        } else if ("REJECTED".equals(status)) {
            holder.binding.tvStatus.setText("✕ Từ chối");
            holder.binding.tvStatus.setBackgroundResource(R.drawable.bg_status_rejected_light);
            holder.binding.layoutActionButtons.setVisibility(View.GONE);
            holder.binding.layoutApprovedButtons.setVisibility(View.GONE);
            holder.binding.layoutRejectionReason.setVisibility(View.VISIBLE);
            holder.binding.tvRejectionReason.setText("Admin đã từ chối sự kiện này");
        } else {
            holder.binding.tvStatus.setText(status);
            holder.binding.layoutActionButtons.setVisibility(View.GONE);
            holder.binding.layoutApprovedButtons.setVisibility(View.GONE);
            holder.binding.layoutRejectionReason.setVisibility(View.GONE);
        }
        
        holder.binding.getRoot().setOnClickListener(v -> listener.onViewClick(event));
        holder.binding.btnApprove.setOnClickListener(v -> listener.onApproveClick(event));
        holder.binding.btnReject.setOnClickListener(v -> listener.onRejectClick(event));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
