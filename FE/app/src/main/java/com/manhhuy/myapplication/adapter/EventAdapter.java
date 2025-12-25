package com.manhhuy.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.manhhuy.myapplication.R;
import com.manhhuy.myapplication.databinding.EventItemBinding;
import com.manhhuy.myapplication.helper.response.EventResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<EventResponse> events;
    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(EventResponse event);
    }

    public EventAdapter(List<EventResponse> events) {
        this.events = events;
    }

    public void setListener(OnEventClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        EventItemBinding binding = EventItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new EventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventResponse event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }

    public void setEvents(List<EventResponse> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        private final EventItemBinding binding;

        public EventViewHolder(EventItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(EventResponse event) {
            binding.eventTitle.setText(event.getTitle());
            binding.eventOrganization.setText(event.getCreatorName() != null ? event.getCreatorName() : "Tổ chức");
            binding.eventLocation.setText(event.getLocation());

            // Load image
            if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(event.getImageUrl())
                        .placeholder(R.drawable.banner_event_default)
                        .error(R.drawable.banner_event_default)
                        .centerCrop()
                        .into(binding.eventImage);
            } else {
                binding.eventImage.setImageResource(R.drawable.banner_event_default);
            }

            // Set category
            if (event.getEventTypeName() != null) {
                binding.eventCategory.setText(event.getEventTypeName());
                binding.eventCategory.setVisibility(android.view.View.VISIBLE);
            } else {
                binding.eventCategory.setVisibility(android.view.View.GONE);
            }

            // Set deadline (event start date)
            if (event.getEventStartTime() != null) {
                binding.eventDeadline.setText(formatDate(event.getEventStartTime()));
            } else {
                binding.eventDeadline.setText("N/A");
            }

            // Check if event is expired
            boolean isExpired = Boolean.TRUE.equals(event.getIsExpired());

            // Show/hide expired badge
            if (binding.expiredBadge != null) {
                binding.expiredBadge.setVisibility(isExpired ? android.view.View.VISIBLE : android.view.View.GONE);
            }

            // Set available slots - show "Đã hết hạn" if expired
            if (isExpired) {
                binding.eventSlots.setText("Đã hết hạn");
                binding.eventSlots.setTextColor(
                        binding.getRoot().getContext().getResources().getColor(android.R.color.holo_red_dark));
                // Dim the card slightly for expired events
                itemView.setAlpha(0.7f);
            } else {
                int availableSlots = event.getAvailableSlots() != null ? event.getAvailableSlots() : 0;
                String slotsText = availableSlots > 0 ? "Còn " + availableSlots + " chỗ" : "Hết chỗ";
                binding.eventSlots.setText(slotsText);
                binding.eventSlots.setTextColor(
                        binding.getRoot().getContext().getResources().getColor(R.color.app_green_primary));
                itemView.setAlpha(1.0f);
            }

            // Set reward points
            if (event.getRewardPoints() != null) {
                binding.eventRewardPoints.setText(String.valueOf(event.getRewardPoints()) + " điểm");
            } else {
                binding.eventRewardPoints.setText("0");
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventClick(event);
                }
            });
        }

        private String formatDate(String dateString) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                return date != null ? outputFormat.format(date) : dateString;
            } catch (Exception e) {
                return dateString;
            }
        }
    }
}
