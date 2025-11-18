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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventPost post = eventPosts.get(position);
        
        // Set title
        holder.tvTitle.setText(post.getTitle());
        
        // Set organization
        holder.tvOrgInitials.setText(post.getOrganizationInitials());
        holder.tvOrgName.setText(post.getOrganizationName());
        
        // Set organization color
        if (post.getOrganizationColor() != null) {
            holder.tvOrgInitials.setBackgroundColor(Color.parseColor(post.getOrganizationColor()));
        }
        
        // Set date
        if (post.getEventDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.tvDate.setText(sdf.format(post.getEventDate()));
        }
        
        // Set location
        holder.tvLocation.setText(post.getLocation());
        
        // Set reward
        holder.tvReward.setText(post.getRewardPoints() + "đ");
        
        // Set posted info
        String postedInfo = "Đăng bởi: " + post.getPostedBy() + " • " + post.getPostedTime();
        holder.tvPostedInfo.setText(postedInfo);
        
        // Set status and buttons based on post status
        String status = post.getStatus();
        if ("pending".equals(status)) {
            holder.tvStatus.setText("⏳ Chờ duyệt");
            holder.tvStatus.setBackgroundResource(R.drawable.status_pending);
            holder.layoutActionButtons.setVisibility(View.VISIBLE);
            holder.layoutApprovedButtons.setVisibility(View.GONE);
            holder.layoutRejectionReason.setVisibility(View.GONE);
            holder.btnReview.setVisibility(View.GONE);
            
        } else if ("approved".equals(status)) {
            holder.tvStatus.setText("✓ Đã duyệt");
            holder.tvStatus.setBackgroundResource(R.drawable.status_approved);
            holder.layoutActionButtons.setVisibility(View.GONE);
            holder.layoutApprovedButtons.setVisibility(View.VISIBLE);
            holder.layoutRejectionReason.setVisibility(View.GONE);
            holder.btnReview.setVisibility(View.GONE);
            
            // Update posted info for approved posts
            String reviewedInfo = "Duyệt bởi: " + post.getReviewedBy() + " • " + post.getReviewedTime();
            holder.tvPostedInfo.setText(reviewedInfo);
            
        } else if ("rejected".equals(status)) {
            holder.tvStatus.setText("✕ Từ chối");
            holder.tvStatus.setBackgroundResource(R.drawable.status_rejected);
            holder.layoutActionButtons.setVisibility(View.GONE);
            holder.layoutApprovedButtons.setVisibility(View.GONE);
            holder.layoutRejectionReason.setVisibility(View.VISIBLE);
            holder.btnReview.setVisibility(View.VISIBLE);
            
            // Set rejection reason
            holder.tvRejectionReason.setText(post.getRejectionReason());
            
            // Update posted info for rejected posts
            String rejectedInfo = "Từ chối bởi: " + post.getReviewedBy() + " • " + post.getReviewedTime();
            holder.tvPostedInfo.setText(rejectedInfo);
        }
        
        // Set click listeners
        holder.btnApprove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onApproveClick(post, position);
            }
        });
        
        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRejectClick(post, position);
            }
        });
        
        holder.btnStatistics.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStatisticsClick(post, position);
            }
        });
        
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(post, position);
            }
        });
        
        holder.btnReview.setOnClickListener(v -> {
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
        ImageView imgEvent;
        TextView tvStatus, tvTitle, tvOrgInitials, tvOrgName;
        TextView tvDate, tvLocation, tvReward, tvPostedInfo;
        TextView tvRejectionReason;
        LinearLayout layoutActionButtons, layoutApprovedButtons, layoutRejectionReason;
        CardView btnApprove, btnReject, btnStatistics, btnEdit, btnReview;

        ViewHolder(View itemView) {
            super(itemView);
            imgEvent = itemView.findViewById(R.id.imgEvent);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOrgInitials = itemView.findViewById(R.id.tvOrgInitials);
            tvOrgName = itemView.findViewById(R.id.tvOrgName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvReward = itemView.findViewById(R.id.tvReward);
            tvPostedInfo = itemView.findViewById(R.id.tvPostedInfo);
            tvRejectionReason = itemView.findViewById(R.id.tvRejectionReason);
            layoutActionButtons = itemView.findViewById(R.id.layoutActionButtons);
            layoutApprovedButtons = itemView.findViewById(R.id.layoutApprovedButtons);
            layoutRejectionReason = itemView.findViewById(R.id.layoutRejectionReason);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnStatistics = itemView.findViewById(R.id.btnStatistics);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnReview = itemView.findViewById(R.id.btnReview);
        }
    }
}
