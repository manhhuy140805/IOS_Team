package com.manhhuy.myapplication.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.ItemEventPostBinding;
import com.manhhuy.myapplication.model.EventPost;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventPostAdapter extends RecyclerView.Adapter<EventPostAdapter.ViewHolder> {

    private List<EventPost> eventPosts;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onApproveClick(EventPost post, int position);
        void onRejectClick(EventPost post, int position);
        void onStatisticsClick(EventPost post, int position);
        void onEditClick(EventPost post, int position);
        void onReviewClick(EventPost post, int position);
    }

    public EventPostAdapter(List<EventPost> eventPosts, OnItemClickListener listener) {
        this.eventPosts = eventPosts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventPostBinding binding = ItemEventPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventPost post = eventPosts.get(position);
        
        // Set title
        holder.binding.tvTitle.setText(post.getTitle());
        
        // Set organization
        holder.binding.tvOrgInitials.setText(post.getOrganizationInitials());
        holder.binding.tvOrgName.setText(post.getOrganizationName());
        
        // Set organization color
        if (post.getOrganizationColor() != null) {
            holder.binding.tvOrgInitials.setBackgroundColor(Color.parseColor(post.getOrganizationColor()));
        }
        
        // Set date
        if (post.getEventDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.binding.tvDate.setText(sdf.format(post.getEventDate()));
        }
        
        // Set location
        holder.binding.tvLocation.setText(post.getLocation());
        
        // Set reward
        holder.binding.tvReward.setText(post.getRewardPoints() + "đ");
        
        // Set posted info
        String postedInfo = "Đăng bởi: " + post.getPostedBy() + " • " + post.getPostedTime();
        holder.binding.tvPostedInfo.setText(postedInfo);
        
        // Set status and buttons based on post status
        String status = post.getStatus();
        if ("pending".equals(status)) {
            holder.binding.tvStatus.setText("⏳ Chờ duyệt");
            holder.binding.tvStatus.setBackgroundResource(R.drawable.status_pending);
            holder.binding.layoutActionButtons.setVisibility(View.VISIBLE);
            holder.binding.layoutApprovedButtons.setVisibility(View.GONE);
            holder.binding.layoutRejectionReason.setVisibility(View.GONE);
            holder.binding.btnReview.setVisibility(View.GONE);
            
        } else if ("approved".equals(status)) {
            holder.binding.tvStatus.setText("✓ Đã duyệt");
            holder.binding.tvStatus.setBackgroundResource(R.drawable.status_approved);
            holder.binding.layoutActionButtons.setVisibility(View.GONE);
            holder.binding.layoutApprovedButtons.setVisibility(View.VISIBLE);
            holder.binding.layoutRejectionReason.setVisibility(View.GONE);
            holder.binding.btnReview.setVisibility(View.GONE);
            
            // Update posted info for approved posts
            String reviewedInfo = "Duyệt bởi: " + post.getReviewedBy() + " • " + post.getReviewedTime();
            holder.binding.tvPostedInfo.setText(reviewedInfo);
            
        } else if ("rejected".equals(status)) {
            holder.binding.tvStatus.setText("✕ Từ chối");
            holder.binding.tvStatus.setBackgroundResource(R.drawable.status_rejected);
            holder.binding.layoutActionButtons.setVisibility(View.GONE);
            holder.binding.layoutApprovedButtons.setVisibility(View.GONE);
            holder.binding.layoutRejectionReason.setVisibility(View.VISIBLE);
            holder.binding.btnReview.setVisibility(View.VISIBLE);
            
            // Set rejection reason
            holder.binding.tvRejectionReason.setText(post.getRejectionReason());
            
            // Update posted info for rejected posts
            String rejectedInfo = "Từ chối bởi: " + post.getReviewedBy() + " • " + post.getReviewedTime();
            holder.binding.tvPostedInfo.setText(rejectedInfo);
        }
        
        // Set click listeners
        holder.binding.btnApprove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onApproveClick(post, position);
            }
        });
        
        holder.binding.btnReject.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRejectClick(post, position);
            }
        });
        
        holder.binding.btnStatistics.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStatisticsClick(post, position);
            }
        });
        
        holder.binding.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(post, position);
            }
        });
        
        holder.binding.btnReview.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReviewClick(post, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventPosts.size();
    }

    public void updateData(List<EventPost> newPosts) {
        this.eventPosts = newPosts;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemEventPostBinding binding;

        ViewHolder(ItemEventPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
