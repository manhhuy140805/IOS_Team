package com.manhhuy.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.model.EventPost;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventManagerAdapter extends RecyclerView.Adapter<EventManagerAdapter.EventViewHolder> {

    private Context context;
    private List<EventPost> eventList;
    private List<EventPost> eventListFull;
    private OnEventActionListener listener;

    public interface OnEventActionListener {
        void onViewClick(EventPost event);
        void onEditClick(EventPost event);
        void onDeleteClick(EventPost event);
    }

    public EventManagerAdapter(Context context, List<EventPost> eventList, OnEventActionListener listener) {
        this.context = context;
        this.eventList = eventList;
        this.eventListFull = new ArrayList<>(eventList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_manager, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventPost event = eventList.get(position);

        holder.tvEventTitle.setText(event.getTitle());
        holder.tvOrganization.setText(event.getOrganizationName());
        holder.tvPoints.setText("⭐ " + event.getRewardPoints() + " điểm");
        holder.tvLocation.setText("📍 " + event.getLocation());

        // Format date
        if (event.getEventDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.tvDate.setText("📅 " + sdf.format(event.getEventDate()));
        }

        // Set participants
        holder.tvParticipants.setText("👥 " + event.getCurrentParticipants() + "/" + event.getMaxParticipants() + " người");

        // Set status
        String status = event.getStatus();
        if ("active".equals(status)) {
            holder.tvStatus.setText("Đang hoạt động");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_active_event);
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.primary_green));
        } else if ("completed".equals(status)) {
            holder.tvStatus.setText("Hoàn thành");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_completed_event);
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.button_blue));
        }

        // Set tags
        List<String> tags = event.getTags();
        if (tags != null && !tags.isEmpty()) {
            holder.tvTag1.setVisibility(View.VISIBLE);
            holder.tvTag1.setText(tags.get(0));
            
            if (tags.size() > 1) {
                holder.tvTag2.setVisibility(View.VISIBLE);
                holder.tvTag2.setText(tags.get(1));
            } else {
                holder.tvTag2.setVisibility(View.GONE);
            }
            
            if (tags.size() > 2) {
                holder.tvTag3.setVisibility(View.VISIBLE);
                holder.tvTag3.setText(tags.get(2));
            } else {
                holder.tvTag3.setVisibility(View.GONE);
            }
        } else {
            holder.tvTag1.setVisibility(View.GONE);
            holder.tvTag2.setVisibility(View.GONE);
            holder.tvTag3.setVisibility(View.GONE);
        }

        // Button listeners
        holder.btnView.setOnClickListener(v -> {
            if (listener != null) listener.onViewClick(event);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(event);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(event);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
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
        ImageView ivEventImage;
        TextView tvEventTitle, tvOrganization, tvStatus, tvPoints;
        TextView tvLocation, tvDate, tvParticipants;
        TextView tvTag1, tvTag2, tvTag3;
        AppCompatButton btnView, btnEdit, btnDelete;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            ivEventImage = itemView.findViewById(R.id.ivEventImage);
            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            tvOrganization = itemView.findViewById(R.id.tvOrganization);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPoints = itemView.findViewById(R.id.tvPoints);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvParticipants = itemView.findViewById(R.id.tvParticipants);
            tvTag1 = itemView.findViewById(R.id.tvTag1);
            tvTag2 = itemView.findViewById(R.id.tvTag2);
            tvTag3 = itemView.findViewById(R.id.tvTag3);
            btnView = itemView.findViewById(R.id.btnView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
